package com.airarsenal.entity.projectile;

import com.airarsenal.combat.AirArsenalDamageSource;
import com.airarsenal.config.AirArsenalConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

/**
 * Hellfire missile — laser-guided, fire-and-steer.
 *
 * <ul>
 *   <li>Speed: 3.2 blocks/tick (≈ 160 b/s at 50 TPS)</li>
 *   <li>Guidance: steers toward {@link #targetEntity} via lerp factor 0.15 each tick</li>
 *   <li>If target lost (dead or null): missile goes ballistic and self-destructs at 8000 ticks</li>
 *   <li>Warhead: 6-block explosion; applies AP-100 unblockable damage to all living entities in radius</li>
 * </ul>
 *
 * Note: extends {@code Entity} directly (not EntityThrowable) to fully control motion
 * without EntityThrowable's gravity step interfering with guidance updates.
 */
public class HellfireEntity extends Entity {

    private static final float SPEED         = 3.2f;
    private static final int   MAX_LIFETIME  = 8000; // ticks ≈ 6.7 minutes
    private static final float LERP_FACTOR   = 0.15f;
    private static final float BLAST_RADIUS  = 6f;

    /** The locked-on target. Null = ballistic flight. */
    public Entity targetEntity;

    private int lifetimeTicks = 0;

    /** Entity that fired this missile (plane or player). Not persisted across restarts. */
    private Entity shooter;

    // ── Construction ──────────────────────────────────────────────────────────

    public HellfireEntity(World world) {
        super(world);
        setSize(0.5f, 0.5f);
    }

    public HellfireEntity(World world, Entity shooter, Entity target) {
        this(world);
        this.shooter      = shooter;
        this.targetEntity = target;
        setPosition(shooter.posX, shooter.posY, shooter.posZ);
        // Initial velocity — straight ahead at full speed
        Vec3d look = shooter.getLookVec();
        motionX = look.x * SPEED;
        motionY = look.y * SPEED;
        motionZ = look.z * SPEED;
    }

    // ── Per-tick update ───────────────────────────────────────────────────────

    @Override
    public void onUpdate() {
        super.onUpdate();

        // ── Guidance ──────────────────────────────────────────────────────────
        if (!world.isRemote && targetEntity != null && targetEntity.isEntityAlive()) {
            Vec3d toTarget = new Vec3d(
                targetEntity.posX - posX,
                targetEntity.posY + targetEntity.height * 0.5 - posY,
                targetEntity.posZ - posZ
            ).normalize().scale(SPEED);

            // Smooth lerp toward target vector
            motionX = lerp(motionX, toTarget.x, LERP_FACTOR);
            motionY = lerp(motionY, toTarget.y, LERP_FACTOR);
            motionZ = lerp(motionZ, toTarget.z, LERP_FACTOR);
        }

        // ── Lifetime ──────────────────────────────────────────────────────────
        lifetimeTicks++;
        if (lifetimeTicks >= MAX_LIFETIME) {
            setDead();
            return;
        }

        if (!world.isRemote) {
            // ── Entity collision scan ─────────────────────────────────────────
            AxisAlignedBB sweptBox = getEntityBoundingBox()
                .expand(motionX, motionY, motionZ).grow(0.5);
            List<Entity> candidates = world.getEntitiesInAABBexcluding(this, sweptBox,
                e -> e != shooter && e.canBeCollidedWith() && e.isEntityAlive());

            if (!candidates.isEmpty()) {
                triggerExplosion();
                return;
            }
        }

        // ── Move ──────────────────────────────────────────────────────────────
        Vec3d start = new Vec3d(posX, posY, posZ);
        moveEntity(motionX, motionY, motionZ);
        Vec3d end = new Vec3d(posX, posY, posZ);

        // ── Block collision ───────────────────────────────────────────────────
        if (!world.isRemote) {
            RayTraceResult hit = world.rayTraceBlocks(start, end, false, true, false);
            if (hit != null && hit.typeOfHit == RayTraceResult.Type.BLOCK) {
                triggerExplosion();
            }
        }
    }

    // ── Warhead ───────────────────────────────────────────────────────────────

    private void triggerExplosion() {
        if (!world.isRemote) {
            // Standard explosion for block damage (respects config)
            world.createExplosion(shooter, posX, posY, posZ,
                BLAST_RADIUS, AirArsenalConfig.allowBlockDestruction);

            // Additionally apply AP-unblockable damage to living entities in radius
            // (bypasses isImmuneToExplosions, e.g. withers, ender dragons)
            AxisAlignedBB aabb = new AxisAlignedBB(
                posX - BLAST_RADIUS, posY - BLAST_RADIUS, posZ - BLAST_RADIUS,
                posX + BLAST_RADIUS, posY + BLAST_RADIUS, posZ + BLAST_RADIUS
            );
            List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, aabb);
            for (EntityLivingBase e : entities) {
                if (e == shooter && !AirArsenalConfig.allowFriendlyFire) continue;
                // True damage: 30 HP, bypasses armor and explosion immunity
                e.attackEntityFrom(AirArsenalDamageSource.MISSILE_GUIDED, 30f);
            }
        }
        setDead();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static float lerp(double current, double target, float t) {
        return (float) (current + (target - current) * t);
    }

    // ── Entity framework ──────────────────────────────────────────────────────

    @Override protected void entityInit() {}

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        compound.setInteger("LifetimeTicks", lifetimeTicks);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        lifetimeTicks = compound.getInteger("LifetimeTicks");
        // targetEntity and shooter are transient — missile goes ballistic after reload
    }

    @Override public boolean canBePushed()       { return false; }
    @Override public boolean canBeCollidedWith() { return true;  }
}
