package com.airarsenal.combat.weapon;

import com.airarsenal.entity.projectile.BulletEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Nose-mounted Light Machine Gun fitted to the Iron Monoplane.
 *
 * <ul>
 *   <li>Fire rate: 4 rounds/second (fires every 5 ticks).</li>
 *   <li>Base damage: 4 HP per round.</li>
 *   <li>Armor penetration: 12.</li>
 *   <li>Default ammo: 500 rounds. Set to -1 for infinite.</li>
 * </ul>
 */
public class LightMachineGun implements IPlaneWeapon {

    private static final int   FIRE_RATE_TICKS  = 5;
    private static final float BASE_DAMAGE       = 4f;
    private static final int   ARMOR_PENETRATION = 12;
    private static final int   DEFAULT_AMMO      = 500;

    private int cooldownTicks = 0;
    private int ammo          = DEFAULT_AMMO;

    // ── IPlaneWeapon ──────────────────────────────────────────────────────────

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
            double spawnX = shooter.posX + direction.x * 1.8;
            double spawnY = shooter.posY + 0.5 + direction.y * 1.8;
            double spawnZ = shooter.posZ + direction.z * 1.8;

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
    public String getDisplayName() {
        return "Light Machine Gun";
    }

    @Override
    public int getAmmoCount() {
        return ammo; // -1 = infinite (displayed as ∞ in HUD)
    }

    // ── Ammo management ───────────────────────────────────────────────────────

    public void setAmmo(int ammo)    { this.ammo = ammo; }
    public void setInfiniteAmmo()    { this.ammo = -1;   }
    public boolean isInfiniteAmmo()  { return ammo == -1;}
    public int getArmorPenetration() { return ARMOR_PENETRATION; }
    public float getBaseDamage()     { return BASE_DAMAGE;       }
}
