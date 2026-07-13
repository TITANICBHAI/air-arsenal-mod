package com.airarsenal.block.artillery;

import com.airarsenal.entity.projectile.MLRSRocketEntity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * MLRS (Multiple Launch Rocket System) — loads an {@link com.airarsenal.item.ItemRocketPod}
 * of 8 rockets, then bursts all 8 over 3 seconds (1 every 5 ticks) with a ±8-block
 * random lateral spread. Reload lockout: 15 seconds (300 ticks) after the burst completes.
 */
public class MLRSTileEntity extends BaseArtilleryTileEntity {

    private static final int ROCKETS_PER_BURST = 8;
    private static final int TICKS_BETWEEN_SHOTS = 5;   // 8 shots × 5 ticks ≈ 3 seconds (40 ticks)
    private static final int RELOAD_TICKS = 300;         // 15 seconds
    private static final double SPREAD = 8.0;
    private static final float ROCKET_SPEED = 3.5f;

    /** Rockets remaining to fire in the current burst; 0 = idle/no burst in progress. */
    private int burstRemaining = 0;
    private int burstTickTimer = 0;

    @Override
    protected void tickArtillery() {
        if (burstRemaining > 0) {
            if (burstTickTimer > 0) {
                burstTickTimer--;
            } else {
                fireOneRocket();
                burstRemaining--;
                burstTickTimer = TICKS_BETWEEN_SHOTS;
                if (burstRemaining == 0) {
                    cooldownTicks = RELOAD_TICKS;
                }
            }
            markDirty();
        }
    }

    /** True if a pod can be loaded right now (no ammo, no burst in progress, reload elapsed). */
    public boolean canLoad() {
        return ammoCount <= 0 && burstRemaining == 0 && cooldownTicks <= 0;
    }

    public void load() {
        ammoCount = ROCKETS_PER_BURST;
        markDirty();
    }

    /** True if loaded and idle (not already mid-burst). */
    public boolean canFireBurst() {
        return ammoCount > 0 && burstRemaining == 0;
    }

    public void startBurst() {
        if (!canFireBurst()) return;
        burstRemaining = ammoCount;
        burstTickTimer = 0;
        ammoCount = 0;
        markDirty();
    }

    private void fireOneRocket() {
        EnumFacing facing = world.getBlockState(pos).getValue(net.minecraft.block.BlockHorizontal.FACING);
        double spreadAngle = (world.rand.nextDouble() - 0.5) * 2 * Math.toRadians(SPREAD);

        double baseYaw = facing.getHorizontalAngle();
        double yaw = Math.toRadians(baseYaw) + spreadAngle;
        Vec3d dir = new Vec3d(-Math.sin(yaw), 0.05, Math.cos(yaw)).normalize();

        MLRSRocketEntity rocket = new MLRSRocketEntity(world, null);
        rocket.setPosition(pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5);
        rocket.motionX = dir.x * ROCKET_SPEED;
        rocket.motionY = dir.y * ROCKET_SPEED;
        rocket.motionZ = dir.z * ROCKET_SPEED;
        world.spawnEntity(rocket);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("BurstRemaining", burstRemaining);
        compound.setInteger("BurstTickTimer", burstTickTimer);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        burstRemaining = compound.getInteger("BurstRemaining");
        burstTickTimer = compound.getInteger("BurstTickTimer");
    }
}
