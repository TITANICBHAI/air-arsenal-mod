package com.airarsenal.combat.weapon;

import com.airarsenal.entity.projectile.BulletEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Door/nose-mounted Minigun fitted to the Attack Helicopter (Chunk 9).
 * Very high fire rate, low per-round damage — a light suppression weapon
 * rather than a precision gun.
 *
 * <ul>
 *   <li>Fire rate: 10 rounds/second (fires every 2 ticks).</li>
 *   <li>Base damage: 2.5 HP per round.</li>
 *   <li>Armor penetration: 8.</li>
 *   <li>Default ammo: 800 rounds.</li>
 * </ul>
 */
public class Minigun implements IPlaneWeapon {

    private static final int   FIRE_RATE_TICKS  = 2;
    private static final float BASE_DAMAGE       = 2.5f;
    private static final int   ARMOR_PENETRATION = 8;
    private static final int   DEFAULT_AMMO      = 800;

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
            double spawnX = shooter.posX + direction.x * 1.5;
            double spawnY = shooter.posY + 0.5 + direction.y * 1.5;
            double spawnZ = shooter.posZ + direction.z * 1.5;

            BulletEntity bullet = new BulletEntity(world, shooter, BASE_DAMAGE, ARMOR_PENETRATION);
            bullet.setPosition(spawnX, spawnY, spawnZ);
            bullet.motionX = direction.x * 3.5;
            bullet.motionY = direction.y * 3.5;
            bullet.motionZ = direction.z * 3.5;
            world.spawnEntity(bullet);
        }

        if (ammo != -1) ammo--;
        cooldownTicks = FIRE_RATE_TICKS;
        return true;
    }

    @Override
    public String getDisplayName() { return "Minigun"; }

    @Override
    public int getAmmoCount() { return ammo; }

    public void setAmmo(int ammo) { this.ammo = ammo; }
}
