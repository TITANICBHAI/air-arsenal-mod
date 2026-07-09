package com.airarsenal.entity.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.List;

/**
 * Smoke Bomb — persistent smoke cloud.
 *
 * <ul>
 *   <li>Duration: 400 ticks (20 s)</li>
 *   <li>Spawns {@code SMOKE_LARGE} particles every 5 ticks (client side)</li>
 *   <li>Applies Blindness II to any entity within 15 blocks while active</li>
 * </ul>
 *
 * <p>{@code hasDetonated} is synced via {@link EntityDataManager} so the client
 * can switch from flight visuals to smoke-cloud visuals without packet-level
 * custom messaging.</p>
 *
 * Not craftable — loot only.
 */
public class SmokeBombEntity extends BaseBombEntity {

    // ── DataManager sync ──────────────────────────────────────────────────────
    private static final DataParameter<Boolean> DETONATED =
        EntityDataManager.createKey(SmokeBombEntity.class, DataSerializers.BOOLEAN);

    private static final int SMOKE_DURATION    = 400;
    private static final int PARTICLE_INTERVAL = 5;
    private static final int BLINDNESS_RADIUS  = 15;

    /** Server-authoritative tick counter. Not synced — clients use ticks-elapsed. */
    private int smokeTicksLeft = 0;
    private int particleTimer  = 0;

    public SmokeBombEntity(World world) { super(world); }

    public SmokeBombEntity(World world, Entity dropper) { super(world, dropper); }

    // ── DataManager registration ──────────────────────────────────────────────

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(DETONATED, false);
    }

    private boolean isDetonated() {
        return this.dataManager.get(DETONATED);
    }

    // ── Impact: start smoke phase ─────────────────────────────────────────────

    @Override
    protected void onImpact() {
        if (isDetonated()) return;
        if (!world.isRemote) {
            this.dataManager.set(DETONATED, true);
            smokeTicksLeft = SMOKE_DURATION;
            // Stop all motion — entity stays at impact point
            motionX = 0; motionY = 0; motionZ = 0;
        }
        // Do NOT call setDead — entity persists for the smoke duration
    }

    // ── Per-tick update ───────────────────────────────────────────────────────

    @Override
    public void onUpdate() {
        if (!isDetonated()) {
            // Still in flight — use normal bomb physics
            super.onUpdate();
            return;
        }

        // ── Smoke phase ───────────────────────────────────────────────────────
        if (!world.isRemote) {
            smokeTicksLeft--;
        }
        particleTimer++;

        // Client: spawn SMOKE_LARGE particles around entity position
        if (world.isRemote && particleTimer >= PARTICLE_INTERVAL) {
            particleTimer = 0;
            for (int i = 0; i < 12; i++) {
                double ox = (world.rand.nextDouble() - 0.5) * 6.0;
                double oy = world.rand.nextDouble() * 3.0;
                double oz = (world.rand.nextDouble() - 0.5) * 6.0;
                world.spawnParticle(EnumParticleTypes.SMOKE_LARGE,
                    posX + ox, posY + oy, posZ + oz,
                    0, 0.04, 0);
            }
        }

        // Server: apply Blindness II to nearby entities every 20 ticks
        if (!world.isRemote && particleTimer % 20 == 0) {
            AxisAlignedBB aabb = new AxisAlignedBB(
                posX - BLINDNESS_RADIUS, posY - BLINDNESS_RADIUS, posZ - BLINDNESS_RADIUS,
                posX + BLINDNESS_RADIUS, posY + BLINDNESS_RADIUS, posZ + BLINDNESS_RADIUS
            );
            List<EntityLivingBase> nearby = world.getEntitiesWithinAABB(EntityLivingBase.class, aabb);
            for (EntityLivingBase e : nearby) {
                e.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 100, 1));
            }
        }

        // Server: expire
        if (!world.isRemote && smokeTicksLeft <= 0) {
            setDead();
        }
    }

    // ── NBT ───────────────────────────────────────────────────────────────────

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("HasDetonated",   isDetonated());
        compound.setInteger("SmokeTicksLeft", smokeTicksLeft);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (compound.getBoolean("HasDetonated")) {
            this.dataManager.set(DETONATED, true);
        }
        smokeTicksLeft = compound.getInteger("SmokeTicksLeft");
    }
}
