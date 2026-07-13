package com.airarsenal.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

/**
 * Invisible marker entity that lives exactly 100 ticks at the Orbital Cannon's
 * target position, spawning a red particle ring each tick so nearby players
 * get a clear visual warning before the rod lands.
 *
 * <p>Uses {@link WorldServer#spawnParticle} (not {@code World#spawnParticle},
 * which is client-only and silently does nothing on a dedicated server) so
 * the ring is actually broadcast to nearby clients.</p>
 */
public class OrbitalWarningMarkerEntity extends Entity {

    private int lifetimeTicks = 0;
    private static final int MAX_LIFETIME = 100;
    private static final int RING_POINTS = 16;
    private static final double RING_RADIUS = 10.0;

    /** Required by Forge entity registration. */
    public OrbitalWarningMarkerEntity(World world) {
        super(world);
        setSize(0.1f, 0.1f);
        noClip = true;
        setInvisible(true);
    }

    public OrbitalWarningMarkerEntity(World world, BlockPos targetPos) {
        this(world);
        setPosition(targetPos.getX() + 0.5, targetPos.getY() + 1, targetPos.getZ() + 0.5);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!world.isRemote) {
            if (world instanceof WorldServer) {
                WorldServer ws = (WorldServer) world;
                for (int i = 0; i < RING_POINTS; i++) {
                    double angle = (2 * Math.PI / RING_POINTS) * i;
                    double px = posX + RING_RADIUS * Math.cos(angle);
                    double pz = posZ + RING_RADIUS * Math.sin(angle);
                    ws.spawnParticle(EnumParticleTypes.REDSTONE, px, posY + 1, pz, 1, 0, 0, 0, 0);
                    ws.spawnParticle(EnumParticleTypes.FLAME, px, posY + 1, pz, 1, 0, 0, 0, 0);
                }
            }

            lifetimeTicks++;
            if (lifetimeTicks >= MAX_LIFETIME) setDead();
        }
    }

    @Override
    protected void entityInit() {}

    @Override
    protected void readEntityFromNBT(net.minecraft.nbt.NBTTagCompound compound) {
        lifetimeTicks = compound.getInteger("LifetimeTicks");
    }

    @Override
    protected void writeEntityToNBT(net.minecraft.nbt.NBTTagCompound compound) {
        compound.setInteger("LifetimeTicks", lifetimeTicks);
    }

    @Override
    public boolean canBeCollidedWith() { return false; }

    @Override
    public boolean isImmuneToExplosions() { return true; }
}
