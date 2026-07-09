package com.airarsenal.entity.projectile;

import com.airarsenal.combat.AirArsenalDamageSource;
import com.airarsenal.combat.DamageCalculator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * A single bullet projectile fired by any plane weapon.
 *
 * <ul>
 *   <li>Speed: 3.5 blocks/tick (set by the spawning weapon, not the entity).</li>
 *   <li>Lifetime: 80 ticks (~64 blocks at full speed) before auto-despawn.</li>
 *   <li>Entity hits: AP/DR + location multiplier; damage source chosen by AP rating.</li>
 *   <li>Block hits: BLOCK_CRACK particle, no block damage (optional in Chunk 10).</li>
 * </ul>
 */
public class BulletEntity extends EntityThrowable {

    public float baseDamage       = 4f;
    public int   armorPenetration = 12;
    public Entity shooterEntity;

    private int ticksAlive = 0;
    private static final int MAX_LIFETIME = 80;

    // ── Construction ──────────────────────────────────────────────────────────

    /** Required by Forge entity registration. */
    public BulletEntity(World world) {
        super(world);
        setSize(0.25f, 0.25f);
    }

    /**
     * Spawn a bullet from a plane entity.
     *
     * @param world            World to spawn in.
     * @param shooter          The firing plane entity.
     * @param baseDamage       Raw bullet damage before AP/DR.
     * @param armorPenetration AP rating — determines which damage source is used.
     */
    public BulletEntity(World world, Entity shooter,
                        float baseDamage, int armorPenetration) {
        super(world);
        setSize(0.25f, 0.25f);
        this.shooterEntity    = shooter;
        this.baseDamage       = baseDamage;
        this.armorPenetration = armorPenetration;
        // Spawn at the plane's nose (same offset used for propeller)
        setPosition(shooter.posX, shooter.posY + 0.5, shooter.posZ);
    }

    // ── Impact handling ───────────────────────────────────────────────────────

    @Override
    protected void onImpact(RayTraceResult result) {
        if (result.typeOfHit == RayTraceResult.Type.ENTITY && result.entityHit != null) {
            Entity target = result.entityHit;

            // Skip the plane that fired this bullet
            if (target == shooterEntity) return;

            if (!world.isRemote) {
                // ── Armor + location calculation ──────────────────────────────
                int dr = (target instanceof EntityPlayer)
                    ? DamageCalculator.getArmorDR((EntityPlayer) target)
                    : 0;

                Vec3d hitPos = result.hitVec != null ? result.hitVec
                    : new Vec3d(target.posX, target.posY + target.height * 0.5, target.posZ);

                float locationMult = DamageCalculator.getLocationMultiplier(target, hitPos);
                float finalDamage  = DamageCalculator.calculateBulletDamage(
                    baseDamage, armorPenetration, dr, locationMult);

                AirArsenalDamageSource src = damageSourceForAP(armorPenetration);
                target.attackEntityFrom(src, finalDamage);
            }

            // CRIT spark at hit position (client only)
            if (world.isRemote && result.hitVec != null) {
                for (int i = 0; i < 6; i++) {
                    world.spawnParticle(EnumParticleTypes.CRIT,
                        result.hitVec.x, result.hitVec.y, result.hitVec.z,
                        (world.rand.nextDouble() - 0.5) * 0.3,
                        (world.rand.nextDouble() - 0.5) * 0.3,
                        (world.rand.nextDouble() - 0.5) * 0.3);
                }
            }
            setDead();

        } else if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
            // Block crack particles (client only)
            if (world.isRemote && result.hitVec != null) {
                world.spawnParticle(EnumParticleTypes.BLOCK_CRACK,
                    result.hitVec.x, result.hitVec.y, result.hitVec.z,
                    0, 0, 0,
                    net.minecraft.block.Block.getStateId(
                        world.getBlockState(result.getBlockPos())));
            }
            setDead();
        }
    }

    // ── Lifetime ──────────────────────────────────────────────────────────────

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!world.isRemote) {
            ticksAlive++;
            if (ticksAlive >= MAX_LIFETIME) {
                setDead();
            }
        }
    }

    // ── Gravity — bullets travel nearly flat ─────────────────────────────────

    @Override
    protected float getGravityVelocity() {
        return 0.005f; // minimal drop over 80 ticks
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    /** Returns the pre-built damage source whose AP matches this bullet's AP. */
    private static AirArsenalDamageSource damageSourceForAP(int ap) {
        if (ap >= 48) return AirArsenalDamageSource.BULLET_AP;
        if (ap >= 28) return AirArsenalDamageSource.BULLET_HEAVY;
        return AirArsenalDamageSource.BULLET_LIGHT;
    }
}
