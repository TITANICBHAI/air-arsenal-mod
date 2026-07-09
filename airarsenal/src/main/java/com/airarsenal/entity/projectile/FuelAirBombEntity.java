package com.airarsenal.entity.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

/**
 * Fuel-Air Bomb — thermobaric weapon with a two-phase detonation.
 *
 * <ol>
 *   <li><b>DISPERSAL</b> (2 ticks): fuel-cloud particles spread across a 20-block sphere.</li>
 *   <li><b>IGNITION</b>: 20-block shockwave — entity damage only, block breaking always off.</li>
 * </ol>
 *
 * <p>Phase is encoded as an {@code int} DataParameter (0=FLIGHT, 1=DISPERSAL, 2=IGNITION)
 * so the client can switch from flight to cloud visuals without a custom packet.</p>
 *
 * Not craftable — loot only.
 */
public class FuelAirBombEntity extends BaseBombEntity {

    // ── Phase constants (DataManager-compatible int) ───────────────────────────
    private static final int PHASE_FLIGHT    = 0;
    private static final int PHASE_DISPERSAL = 1;
    private static final int PHASE_IGNITION  = 2;

    private static final DataParameter<Integer> PHASE =
        EntityDataManager.createKey(FuelAirBombEntity.class, DataSerializers.VARINT);

    private static final int   DISPERSAL_TICKS     = 2;
    private static final float SHOCKWAVE_RADIUS     = 20f;
    private static final int   CLOUD_RADIUS_BLOCKS  = 20;

    /** Server-only: counts ticks spent in DISPERSAL before igniting. */
    private int dispersalAge = 0;

    public FuelAirBombEntity(World world) { super(world); }

    public FuelAirBombEntity(World world, Entity dropper) { super(world, dropper); }

    // ── DataManager ───────────────────────────────────────────────────────────

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(PHASE, PHASE_FLIGHT);
    }

    private int getPhase()           { return this.dataManager.get(PHASE); }
    private void setPhase(int phase) { this.dataManager.set(PHASE, phase); }

    // ── Impact: begin dispersal ───────────────────────────────────────────────

    @Override
    protected void onImpact() {
        if (getPhase() != PHASE_FLIGHT) return;
        if (!world.isRemote) {
            setPhase(PHASE_DISPERSAL);
            motionX = 0; motionY = 0; motionZ = 0;
        }
        // Stay alive through dispersal
    }

    // ── Per-tick update ───────────────────────────────────────────────────────

    @Override
    public void onUpdate() {
        int phase = getPhase();

        if (phase == PHASE_FLIGHT) {
            super.onUpdate(); // normal bomb physics
            return;
        }

        // ── DISPERSAL: emit cloud particles ───────────────────────────────────
        if (phase == PHASE_DISPERSAL) {
            if (world.isRemote) {
                // Dense fuel-cloud particle burst
                for (int i = 0; i < 30; i++) {
                    double ox = (world.rand.nextDouble() - 0.5) * CLOUD_RADIUS_BLOCKS;
                    double oy = (world.rand.nextDouble() - 0.5) * CLOUD_RADIUS_BLOCKS;
                    double oz = (world.rand.nextDouble() - 0.5) * CLOUD_RADIUS_BLOCKS;
                    world.spawnParticle(EnumParticleTypes.CLOUD,
                        posX + ox, posY + oy, posZ + oz,
                        0, 0.01, 0);
                }
            }

            if (!world.isRemote) {
                dispersalAge++;
                if (dispersalAge >= DISPERSAL_TICKS) {
                    setPhase(PHASE_IGNITION);
                }
            }
            return;
        }

        // ── IGNITION: pressure-wave explosion ────────────────────────────────
        if (phase == PHASE_IGNITION && !world.isRemote) {
            world.createExplosion(
                droppedByEntity,
                posX, posY, posZ,
                SHOCKWAVE_RADIUS,
                false   // shockwave never breaks blocks, regardless of config
            );
            setDead();
        } else if (phase == PHASE_IGNITION && world.isRemote) {
            // Client just waits for server to send the setDead packet
        }
    }

    // ── NBT ───────────────────────────────────────────────────────────────────

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("Phase",        getPhase());
        compound.setInteger("DispersalAge", dispersalAge);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        setPhase(compound.getInteger("Phase"));
        dispersalAge = compound.getInteger("DispersalAge");
    }
}
