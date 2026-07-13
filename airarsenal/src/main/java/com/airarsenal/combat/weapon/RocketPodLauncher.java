package com.airarsenal.combat.weapon;

import com.airarsenal.entity.projectile.MLRSRocketEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Under-wing Rocket Pod fitted to the Attack Helicopter (Chunk 9).
 * Fires one dumb-fire {@link MLRSRocketEntity} per shot with a small random
 * spread, reusing the Chunk 8 MLRS rocket projectile.
 *
 * <ul>
 *   <li>Fire rate: 1 rocket every 15 ticks.</li>
 *   <li>Default ammo: 8 rockets (one pod, see {@link com.airarsenal.item.ItemRocketPod}).</li>
 * </ul>
 */
public class RocketPodLauncher implements IPlaneWeapon {

    private static final int FIRE_RATE_TICKS = 15;
    private static final int DEFAULT_AMMO    = 8;
    private static final float SPEED         = 3.0f;

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
            double spreadX = (world.rand.nextDouble() - 0.5) * 0.08;
            double spreadZ = (world.rand.nextDouble() - 0.5) * 0.08;

            MLRSRocketEntity rocket = new MLRSRocketEntity(world, shooter);
            rocket.setPosition(
                shooter.posX + direction.x * 1.8,
                shooter.posY + 0.3 + direction.y * 1.8,
                shooter.posZ + direction.z * 1.8);
            rocket.motionX = (direction.x + spreadX) * SPEED;
            rocket.motionY = direction.y * SPEED;
            rocket.motionZ = (direction.z + spreadZ) * SPEED;
            world.spawnEntity(rocket);
        }

        if (ammo != -1) ammo--;
        cooldownTicks = FIRE_RATE_TICKS;
        return true;
    }

    @Override
    public String getDisplayName() { return "Rocket Pod"; }

    @Override
    public int getAmmoCount() { return ammo; }

    public void setAmmo(int ammo) { this.ammo = ammo; }
}
