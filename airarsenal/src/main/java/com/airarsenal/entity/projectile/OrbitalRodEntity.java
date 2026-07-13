package com.airarsenal.entity.projectile;

import com.airarsenal.config.AirArsenalConfig;
import com.airarsenal.network.ModNetwork;
import com.airarsenal.network.PacketOrbitalImpact;
import com.airarsenal.network.PacketScreenShake;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.List;

/**
 * Kinetic tungsten rod dropped by the Orbital Cannon. Spawns at y=300 above
 * the target X/Z, falls straight down at a constant 8 blocks/tick (no
 * horizontal drift, no gravity acceleration — it's already at terminal
 * "orbital" velocity), and detonates on the first solid block it reaches or
 * once it passes the recorded ground height.
 */
public class OrbitalRodEntity extends Entity {

    private double targetX;
    private double targetZ;
    private double targetGroundY;
    private int lifetimeTicks = 0;
    private static final int MAX_LIFETIME = 80; // 640 blocks of fall as a failsafe
    private static final double FALL_SPEED = 8.0;

    /** Required by Forge entity registration. */
    public OrbitalRodEntity(World world) {
        super(world);
        setSize(0.2f, 2.0f);
        noClip = true;
    }

    public OrbitalRodEntity(World world, double x, double y, double z, int targetGroundY) {
        this(world);
        this.targetX = x;
        this.targetZ = z;
        this.targetGroundY = targetGroundY;
        setPosition(x, y, z);
        motionX = 0;
        motionY = -FALL_SPEED;
        motionZ = 0;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (world.isRemote) return;

        posY += motionY;
        setPosition(posX, posY, posZ);
        lifetimeTicks++;

        BlockPos here = new BlockPos(posX, posY, posZ);
        boolean hitBlock = world.getBlockState(here).getMaterial() != Material.AIR;
        boolean pastGround = posY <= targetGroundY;

        if (hitBlock || pastGround) {
            onImpact();
            return;
        }

        if (lifetimeTicks > MAX_LIFETIME) {
            setDead();
        }
    }

    private void onImpact() {
        int radius = AirArsenalConfig.orbitalBlastRadius;
        boolean destroyBlocks = AirArsenalConfig.orbitalBlockDestruction;

        if (destroyBlocks) {
            generateCrater(radius);
        }

        world.createExplosion(null, posX, posY, posZ, radius, destroyBlocks);

        // Fire at crater rim.
        for (int i = 0; i < 20; i++) {
            double angle = world.rand.nextDouble() * Math.PI * 2;
            double dist = world.rand.nextDouble() * 20;
            BlockPos firePos = new BlockPos(
                posX + Math.cos(angle) * dist,
                posY,
                posZ + Math.sin(angle) * dist);
            if (world.isAirBlock(firePos) && world.isAirBlock(firePos.down())) continue;
            if (world.isAirBlock(firePos)) {
                world.setBlockState(firePos, Blocks.FIRE.getDefaultState());
            }
        }

        sendImpactPackets();
        setDead();
    }

    private void generateCrater(int radius) {
        BlockPos center = new BlockPos(posX, posY, posZ);

        // Hollow out the crater sphere (non-bedrock blocks only).
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    double distSq = dx * dx + dy * dy + dz * dz;
                    if (distSq > radius * radius) continue;

                    BlockPos p = center.add(dx, dy, dz);
                    if (world.getBlockState(p).getBlock() != Blocks.BEDROCK) {
                        world.setBlockToAir(p);
                    }
                }
            }
        }

        // Obsidian ring at the outermost 8-block-radius shell, 1 block thick.
        int ringRadius = 8;
        for (int dx = -ringRadius - 1; dx <= ringRadius + 1; dx++) {
            for (int dz = -ringRadius - 1; dz <= ringRadius + 1; dz++) {
                double dist = Math.sqrt(dx * dx + dz * dz);
                if (dist >= ringRadius - 0.5 && dist <= ringRadius + 0.5) {
                    BlockPos ringPos = center.add(dx, 0, dz);
                    if (world.getBlockState(ringPos).getBlock() != Blocks.BEDROCK) {
                        world.setBlockState(ringPos, Blocks.OBSIDIAN.getDefaultState());
                    }
                }
            }
        }
    }

    private void sendImpactPackets() {
        if (!(world instanceof WorldServer)) return;
        WorldServer server = (WorldServer) world;
        BlockPos impactPos = new BlockPos(posX, posY, posZ);
        AxisAlignedBB area = new AxisAlignedBB(impactPos).grow(300);
        List<net.minecraft.entity.player.EntityPlayer> nearby =
            world.getEntitiesWithinAABB(net.minecraft.entity.player.EntityPlayer.class, area);
        for (net.minecraft.entity.player.EntityPlayer p : nearby) {
            if (p instanceof EntityPlayerMP) {
                ModNetwork.CHANNEL.sendTo(new PacketOrbitalImpact(impactPos), (EntityPlayerMP) p);
                ModNetwork.CHANNEL.sendTo(new PacketScreenShake(1.5f, 30), (EntityPlayerMP) p);
            }
        }
    }

    @Override
    protected void entityInit() {}

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        targetX = compound.getDouble("TargetX");
        targetZ = compound.getDouble("TargetZ");
        targetGroundY = compound.getDouble("TargetGroundY");
        lifetimeTicks = compound.getInteger("LifetimeTicks");
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setDouble("TargetX", targetX);
        compound.setDouble("TargetZ", targetZ);
        compound.setDouble("TargetGroundY", targetGroundY);
        compound.setInteger("LifetimeTicks", lifetimeTicks);
    }

    @Override
    public boolean canBeCollidedWith() { return false; }

    @Override
    public boolean isImmuneToExplosions() { return true; }
}
