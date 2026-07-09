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
 *
 * <p>This is a weapon <em>component</em> — not an Item. It lives in the plane's
 * {@code weapons} list and is updated every game tick via the plane's {@code onUpdate()}.</p>
 */
public class LightMachineGun implements IPlaneWeapon {

    // ── Stats ─────────────────────────────────────────────────────────────────

    private static final int   FIRE_RATE_TICKS = 5;    // 4 shots/second
    private static final float BASE_DAMAGE      = 4f;
    private static final int   ARMOR_PENETRATION = 12;
    private static final int   DEFAULT_AMMO      = 500;

    // ── State ─────────────────────────────────────────────────────────────────

    private int cooldownTicks = 0;
    private int ammo          = DEFAULT_AMMO;

    // ── IPlaneWeapon ──────────────────────────────────────────────────────────

    /**
     * Attempts to fire one round.
     *
     * <ol>
     *   <li>If on cooldown: decrement timer, return false.</li>
     *   <li>If ammo == 0 (not infinite): return false.</li>
     *   <li>Spawn a {@link BulletEntity} 1.8 blocks ahead of the shooter.</li>
     *   <li>Set bullet velocity to {@code direction × 3.5}.</li>
     *   <li>Decrement ammo if not infinite (ammo != -1).</li>
     *   <li>Reset cooldown to {@value #FIRE_RATE_TICKS} ticks.</li>
     * </ol>
     *
     * @param world     The world.
     * @param shooter   The plane entity (provides position + heading).
     * @param direction Normalized look vector from {@code Entity.getLookVec()}.
     * @return {@code true} if a bullet was actually fired.
     */
    @Override
    public boolean tryFire(World world, Entity shooter, Vec3d direction) {
        // Tick cooldown down
        if (cooldownTicks > 0) {
            cooldownTicks--;
            return false;
        }

        // Check ammo (ammo == -1 means infinite)
        if (ammo == 0) {
            return false; // empty
        }

        if (!world.isRemote) {
            // Spawn bullet 1.8 blocks ahead of the shooter's nose
            double spawnX = shooter.posX + direction.x * 1.8;
            double spawnY = shooter.posY + 0.5 + direction.y * 1.8;
            double spawnZ = shooter.posZ + direction.z * 1.8;

            BulletEntity bullet = new BulletEntity(world, shooter, BASE_DAMAGE, ARMOR_PENETRATION);
            bullet.setPosition(spawnX, spawnY, spawnZ);

            // Velocity = direction (already normalised) × 3.5 blocks/tick
            bullet.motionX = direction.x * 3.5;
            bullet.motionY = direction.y * 3.5;
            bullet.motionZ = direction.z * 3.5;

            world.spawnEntity(bullet);
        }

        // Consume one round (skip if infinite)
        if (ammo != -1) {
            ammo--;
        }

        // Reset fire-rate cooldown
        cooldownTicks = FIRE_RATE_TICKS;
        return true;
    }

    @Override
    public String getDisplayName() {
        return "Light Machine Gun";
    }

    // ── Ammo management (used by future HUD / Chunk 5) ────────────────────────

    public int getAmmo()               { return ammo;          }
    public void setAmmo(int ammo)      { this.ammo = ammo;     }
    public void setInfiniteAmmo()      { this.ammo = -1;       }
    public boolean isInfiniteAmmo()    { return ammo == -1;    }
    public int getArmorPenetration()   { return ARMOR_PENETRATION; }
    public float getBaseDamage()       { return BASE_DAMAGE;   }
}
