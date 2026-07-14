package com.airarsenal.combat.weapon;

import com.airarsenal.entity.projectile.HellfireEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

/**
 * Hardpoint-mounted Hellfire missile launcher fitted to the Fighter Jet and
 * Predator Drone (Chunk 9). On fire, scans a forward cone for the nearest
 * living target and launches a self-guiding {@link HellfireEntity} at it. If
 * nothing is found in the cone, the missile still launches and goes ballistic
 * (matches {@link HellfireEntity}'s no-target behaviour).
 *
 * <ul>
 *   <li>Fire rate: 1 missile every 40 ticks (2 seconds).</li>
 *   <li>Lock cone: 30° half-angle, 60-block range.</li>
 *   <li>Default ammo: 4 missiles per hardpoint.</li>
 * </ul>
 */
public class HellfireLauncher implements IPlaneWeapon {

    private static final int   FIRE_RATE_TICKS = 40;
    private static final int   DEFAULT_AMMO    = 4;
    private static final double LOCK_RANGE     = 60.0;
    private static final double LOCK_COS       = Math.cos(Math.toRadians(30.0));

    private int cooldownTicks = 0;
    private int ammo          = DEFAULT_AMMO;

    @Override
    public boolean tryFire(World world, Entity shooter, Vec3d direction) {
        if (cooldownTicks > 0) {
            cooldownTicks--;
            return false;
        }
        if (ammo == 0) {
            return false;
        }

        if (!world.isRemote) {
            Entity target = findLockTarget(world, shooter, direction);
            HellfireEntity missile = new HellfireEntity(world, shooter, target);
            world.spawnEntity(missile);
        }

        if (ammo != -1) ammo--;
        cooldownTicks = FIRE_RATE_TICKS;
        return true;
    }

    private Entity findLockTarget(World world, Entity shooter, Vec3d direction) {
        AxisAlignedBB scanBox = shooter.getEntityBoundingBox().grow(LOCK_RANGE);
        List<Entity> nearby = world.getEntitiesWithinAABBExcludingEntity(shooter, scanBox);

        Entity best = null;
        double bestDist = Double.MAX_VALUE;

        for (Entity e : nearby) {
            if (!(e instanceof EntityLivingBase) || !e.isEntityAlive()) continue;

            Vec3d toEntity = new Vec3d(
                e.posX - shooter.posX,
                e.posY + e.height * 0.5 - shooter.posY,
                e.posZ - shooter.posZ
            );
            double dist = toEntity.length();
            if (dist > LOCK_RANGE || dist < 0.001) continue;

            Vec3d toEntityNorm = toEntity.scale(1.0 / dist);
            if (direction.dotProduct(toEntityNorm) >= LOCK_COS && dist < bestDist) {
                bestDist = dist;
                best = e;
            }
        }

        return best;
    }

    @Override
    public String getDisplayName() { return "Hellfire Launcher"; }

    @Override
    public int getAmmoCount() { return ammo; }

    public void setAmmo(int ammo) { this.ammo = ammo; }
}
