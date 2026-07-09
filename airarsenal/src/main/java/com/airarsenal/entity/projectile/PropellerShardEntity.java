package com.airarsenal.entity.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

/**
 * A propeller fragment ejected when a plane's propeller is destroyed.
 *
 * <ul>
 *   <li>Flies in a random direction within a 60° forward cone.</li>
 *   <li>On entity hit: deals 6 HP <em>true damage</em> (bypasses all armor).</li>
 *   <li>On block hit: drops an {@link Items#IRON_NUGGET} (scrap) and despawns.</li>
 *   <li>Despawns automatically after 60 ticks if nothing was hit.</li>
 * </ul>
 */
public class PropellerShardEntity extends EntityThrowable {

    /** True-damage source — unblockable, bypasses armor reduction. */
    public static final DamageSource SHARD_DAMAGE = new DamageSource("propeller_shard") {
        @Override
        public boolean isUnblockable() { return true; }
        @Override
        public boolean isDamageAbsolute() { return true; }
    };

    private static final int MAX_LIFETIME_TICKS = 60;
    private int ticksAlive = 0;

    // ── Construction ──────────────────────────────────────────────────────────

    /** Required by Forge entity registration. */
    public PropellerShardEntity(World world) {
        super(world);
    }

    /**
     * Spawn a shard from a plane, fired in a random direction within a 60° cone
     * pointing in the plane's forward direction.
     *
     * @param world     World to spawn in.
     * @param thrower   The plane entity (used for position and heading).
     */
    public PropellerShardEntity(World world, Entity thrower) {
        super(world, thrower.posX, thrower.posY + 0.5, thrower.posZ, null);

        // Plane's forward unit vector (yaw only — ignore pitch for cone origin)
        double yawRad = Math.toRadians(thrower.rotationYaw);
        double fwdX = -Math.sin(yawRad);
        double fwdZ =  Math.cos(yawRad);

        // Random deviation within ±30° (total 60° cone)
        double devDeg = (world.rand.nextDouble() - 0.5) * 60.0;
        double devRad = Math.toRadians(devDeg);

        // Rotate forward vector by deviation around the Y axis
        double cos = Math.cos(devRad);
        double sin = Math.sin(devRad);
        double vx  = fwdX * cos - fwdZ * sin;
        double vz  = fwdX * sin + fwdZ * cos;

        // Slight vertical scatter
        double vy = (world.rand.nextDouble() - 0.3) * 0.6;

        // Scale to random magnitude in [0.8, 1.4]
        float magnitude = 0.8f + world.rand.nextFloat() * 0.6f;
        double len = Math.sqrt(vx * vx + vy * vy + vz * vz);
        if (len > 0) {
            vx = (vx / len) * magnitude;
            vy = (vy / len) * magnitude;
            vz = (vz / len) * magnitude;
        }

        this.motionX = vx;
        this.motionY = vy;
        this.motionZ = vz;
    }

    // ── Projectile impact ─────────────────────────────────────────────────────

    @Override
    protected void onImpact(RayTraceResult result) {
        if (world.isRemote) return;

        if (result.typeOfHit == RayTraceResult.Type.ENTITY && result.entityHit != null) {
            // True damage — bypasses armor
            result.entityHit.attackEntityFrom(SHARD_DAMAGE, 6.0f);
        } else if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
            // Drop iron nugget scrap at impact site
            EnumFacing face = result.sideHit;
            double dropX = result.hitVec.x + face.getXOffset() * 0.1;
            double dropY = result.hitVec.y + face.getYOffset() * 0.1;
            double dropZ = result.hitVec.z + face.getZOffset() * 0.1;
            entityDropItem(new ItemStack(Items.IRON_NUGGET, 1), 0.1f);
        }

        setDead();
    }

    // ── Lifetime ──────────────────────────────────────────────────────────────

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!world.isRemote) {
            ticksAlive++;
            if (ticksAlive >= MAX_LIFETIME_TICKS) {
                setDead();
            }
        }
    }

    // ── EntityThrowable gravity — shards arc like debris ─────────────────────

    @Override
    protected float getGravityVelocity() {
        return 0.05f; // moderate arc
    }
}
