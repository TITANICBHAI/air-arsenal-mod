package com.airarsenal.combat.weapon;

import com.airarsenal.entity.projectile.BulletEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Wing-mounted Heavy Round Gun fitted to the Fighter Jet (Chunk 9).
 * Same delivery mechanism as {@link LightMachineGun} but with a much heavier
 * round — the AP rating (28) selects {@link com.airarsenal.combat.AirArsenalDamageSource#BULLET_HEAVY}
 * in {@link BulletEntity#writeEntityToNBT}'s damage-source lookup.
 *
 * <ul>
 *   <li>Fire rate: 3 rounds/second (fires every 7 ticks).</li>
 *   <li>Base damage: 8 HP per round.</li>
 *   <li>Armor penetration: 28.</li>
 *   <li>Default ammo: 300 rounds.</li>
 * </ul>
 */
public class HeavyRoundGun implements IPlaneWeapon {

    private static final int   FIRE_RATE_TICKS  = 7;
    private static final float BASE_DAMAGE       = 8f;
    private static final int   ARMOR_PENETRATION = 28;
    private static final int   DEFAULT_AMMO      = 300;

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
            double spawnX = shooter.posX + direction.x * 1.8;
            double spawnY = shooter.posY + 0.5 + direction.y * 1.8;
            double spawnZ = shooter.posZ + direction.z * 1.8;

            BulletEntity bullet = new BulletEntity(world, shooter, BASE_DAMAGE, ARMOR_PENETRATION);
            bullet.setPosition(spawnX, spawnY, spawnZ);
            bullet.motionX = direction.x * 4.5;
            bullet.motionY = direction.y * 4.5;
            bullet.motionZ = direction.z * 4.5;
            world.spawnEntity(bullet);
        }

        if (ammo != -1) ammo--;
        cooldownTicks = FIRE_RATE_TICKS;
        return true;
    }

    @Override
    public String getDisplayName() { return "Heavy Round Gun"; }

    @Override
    public int getAmmoCount() { return ammo; }

    public void setAmmo(int ammo) { this.ammo = ammo; }
}
