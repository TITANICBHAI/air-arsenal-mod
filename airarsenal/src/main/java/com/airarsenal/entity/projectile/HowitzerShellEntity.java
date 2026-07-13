package com.airarsenal.entity.projectile;

import com.airarsenal.config.AirArsenalConfig;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Howitzer shell — flies straight to a designated {@link BlockPos} at a fixed speed,
 * same waypoint-flight pattern as {@link BrahMosEntity} but shorter range and blast.
 *
 * Speed: 4 blocks/tick. Blast: 10-block radius, always breaks blocks.
 */
public class HowitzerShellEntity extends Entity {

    private static final float SPEED        = 4f;
    private static final float BLAST_RADIUS = 10f;
    private static final float ARRIVAL_DIST = 2.5f;
    private static final int   MAX_LIFETIME = 400;

    private BlockPos waypoint;
    private Entity firer;
    private int lifetimeTicks = 0;

    public HowitzerShellEntity(World world) {
        super(world);
        setSize(0.5f, 0.5f);
    }

    public HowitzerShellEntity(World world, Entity firer, double muzzleX, double muzzleY,
                               double muzzleZ, BlockPos waypoint) {
        this(world);
        this.firer = firer;
        this.waypoint = waypoint;
        setPosition(muzzleX, muzzleY, muzzleZ);
        aimAtWaypoint();
    }

    private void aimAtWaypoint() {
        Vec3d dir = new Vec3d(
            waypoint.getX() + 0.5 - posX,
            waypoint.getY() + 1.0 - posY,
            waypoint.getZ() + 0.5 - posZ
        ).normalize();
        motionX = dir.x * SPEED;
        motionY = dir.y * SPEED;
        motionZ = dir.z * SPEED;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!world.isRemote) {
            lifetimeTicks++;
            if (waypoint != null) {
                double dist = Math.sqrt(getDistanceSq(
                    waypoint.getX() + 0.5, waypoint.getY() + 1.0, waypoint.getZ() + 0.5));
                if (dist <= ARRIVAL_DIST) { detonate(); return; }
            }
            if (lifetimeTicks >= MAX_LIFETIME) { detonate(); return; }
        }
        moveEntity(motionX, motionY, motionZ);
    }

    private void detonate() {
        if (!world.isRemote) {
            // Howitzer shells always break blocks — heavy indirect artillery.
            world.createExplosion(firer, posX, posY, posZ, BLAST_RADIUS, true);
        }
        setDead();
    }

    @Override protected void entityInit() {}

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        if (waypoint != null) {
            compound.setInteger("WPX", waypoint.getX());
            compound.setInteger("WPY", waypoint.getY());
            compound.setInteger("WPZ", waypoint.getZ());
        }
        compound.setInteger("LifetimeTicks", lifetimeTicks);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        if (compound.hasKey("WPX")) {
            waypoint = new BlockPos(
                compound.getInteger("WPX"), compound.getInteger("WPY"), compound.getInteger("WPZ"));
        }
        lifetimeTicks = compound.getInteger("LifetimeTicks");
    }

    @Override public boolean canBePushed()       { return false; }
    @Override public boolean canBeCollidedWith() { return false; }
}
