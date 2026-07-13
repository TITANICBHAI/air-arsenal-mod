package com.airarsenal.entity.plane;

import com.airarsenal.AirArsenal;
import com.airarsenal.entity.plane.component.IEngineComponent;
import com.airarsenal.entity.plane.component.PropellerComponent;
import com.airarsenal.entity.plane.component.PropellerState;
import com.airarsenal.registry.ModSounds;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

/**
 * Stealth Bomber — 4-engine, radar-invisible heavy bomber (Chunk 9).
 * Not craftable — creative tab / loot only.
 *
 * Stats:
 * <ul>
 *   <li>Top speed:          70 blocks/second</li>
 *   <li>Structural health:  120 HP</li>
 *   <li>Engines:            4× 40 HP {@link PropellerComponent}, tracked independently</li>
 *   <li>Fuel consumption:   0.08/tick (2× while stealth is active)</li>
 *   <li>Hardpoints:         3 + a bomb bay (up to 4 bombs — bomb-bay loading is
 *       out of scope for this chunk; the bay capacity is exposed for a future
 *       loadout UI)</li>
 * </ul>
 *
 * <h3>Stealth</h3>
 * Toggled with the shared special-action key ({@code G}, routed through
 * {@code PacketSpecialAction}). While {@link #isStealthActive} is true, ground
 * AA/Flak auto-targeting code must skip this entity — see
 * {@code AACannonTileEntity} / {@code FlakBatteryTileEntity}'s target scan.
 */
public class StealthBomberEntity extends BasePlaneEntity {

    public static final int BOMB_BAY_CAPACITY = 4;

    private final MultiEngineAggregate engines = new MultiEngineAggregate(40f, 4);

    private boolean isStealthActive = false;

    public StealthBomberEntity(World world) {
        super(world);
        this.maxSpeed        = 70f;
        this.maxPlaneHealth  = 120f;
        this.planeHealth     = this.maxPlaneHealth;
        this.propeller       = engines; // aggregate of the 4 engines drives base-class physics/HUD
        this.hasSpinningPropeller = false; // rear engine pods, not a front-mounted blade
        this.fuelConsumption = 0.08f;

        // 3 general hardpoints — no weapons pre-loaded per spec (bomb bay is primary armament)
    }

    @Override
    public String getPlaneType() { return "stealth_bomber"; }

    @Override
    public void onUpdate() {
        super.onUpdate(); // fuel tick + particles
        applyFlightPhysics();
        playEngineLoopSound(ModSounds.PLANE_ENGINE_JET);
        if (world.isRemote && speed > 0.5f) {
            AirArsenal.proxy.spawnJetExhaust(world, posX, posY, posZ);
        }

        if (!world.isRemote && isStealthActive) {
            // Stealth costs an extra 1x fuel per tick (2x total) on top of the
            // base tickFuel() drain already applied by BasePlaneEntity#onUpdate.
            fuelSystem.consumeFuel(fuelConsumption);
            if (fuelSystem.isEmpty()) {
                isStealthActive = false; // can't sustain stealth with an empty tank
            }
        }
    }

    /** Called server-side by {@code PacketSpecialAction} when the pilot presses G. */
    public void toggleStealth() {
        if (world.isRemote) return;
        isStealthActive = !isStealthActive;
        AirArsenal.LOGGER.debug("Stealth Bomber stealth → {}", isStealthActive);
    }

    /** Ground AA/Flak targeting scans must exclude this entity while true. */
    public boolean isStealthActive() { return isStealthActive; }

    public int getBombBayCapacity() { return BOMB_BAY_CAPACITY; }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("StealthActive", isStealthActive);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        isStealthActive = compound.getBoolean("StealthActive");
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Multi-engine aggregate
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Wraps 4 independent {@link PropellerComponent}s so {@link BasePlaneEntity}
     * (which only knows about a single {@link IEngineComponent}) can drive flight
     * physics and the HUD bar off a combined health pool, while each engine still
     * tracks its own damage for flavor/future per-engine-fire visuals.
     *
     * <p>Damage taken by the aggregate (fuel exhaustion, direct engine damage) is
     * spread evenly across the still-alive engines. The aggregate is DESTROYED
     * only once all 4 engines are DESTROYED.</p>
     */
    private static class MultiEngineAggregate implements IEngineComponent {

        private final PropellerComponent[] engines;
        private final float maxHealthEach;

        MultiEngineAggregate(float maxHealthEach, int count) {
            this.maxHealthEach = maxHealthEach;
            this.engines = new PropellerComponent[count];
            for (int i = 0; i < count; i++) {
                engines[i] = new PropellerComponent(maxHealthEach);
            }
        }

        @Override
        public PropellerState getDamageState() {
            float pct = getCurrentHealth() / getMaxHealth();
            if (pct <= 0f)      return PropellerState.DESTROYED;
            if (pct > 0.75f)    return PropellerState.INTACT;
            if (pct > 0.50f)    return PropellerState.DAMAGED;
            if (pct > 0.25f)    return PropellerState.HEAVY_DAMAGE;
            return PropellerState.CRITICAL;
        }

        @Override
        public float getSpeedMultiplier() {
            switch (getDamageState()) {
                case INTACT:       return 1.00f;
                case DAMAGED:      return 0.90f;
                case HEAVY_DAMAGE: return 0.65f;
                case CRITICAL:     return 0.35f;
                case DESTROYED:    return 0.00f;
                default:           return 1.00f;
            }
        }

        @Override
        public float getYawDrift() {
            // Average the drift of whichever engines are still contributing
            float total = 0f;
            for (PropellerComponent e : engines) {
                total += e.getYawDrift();
            }
            return total / engines.length;
        }

        @Override
        public boolean takeDamage(float amount) {
            boolean wasAlive = !isDestroyed();
            float remaining = amount;
            int aliveCount = aliveCount();
            if (aliveCount == 0) return false;

            float perEngine = remaining / aliveCount;
            for (PropellerComponent e : engines) {
                if (!e.isDestroyed()) {
                    e.takeDamage(perEngine);
                }
            }
            return wasAlive && isDestroyed();
        }

        @Override
        public float getCurrentHealth() {
            float total = 0f;
            for (PropellerComponent e : engines) total += e.getCurrentHealth();
            return total;
        }

        @Override
        public float getMaxHealth() { return maxHealthEach * engines.length; }

        @Override
        public boolean isDestroyed() {
            for (PropellerComponent e : engines) {
                if (!e.isDestroyed()) return false;
            }
            return true;
        }

        private int aliveCount() {
            int count = 0;
            for (PropellerComponent e : engines) if (!e.isDestroyed()) count++;
            return count;
        }

        @Override
        public void writeToNBT(NBTTagCompound compound) {
            NBTTagList list = new NBTTagList();
            for (PropellerComponent e : engines) {
                NBTTagCompound engineNBT = new NBTTagCompound();
                e.writeToNBT(engineNBT);
                list.appendTag(engineNBT);
            }
            compound.setTag("Engines", list);
        }

        @Override
        public void readFromNBT(NBTTagCompound compound) {
            if (!compound.hasKey("Engines")) return;
            NBTTagList list = compound.getTagList("Engines", 10); // 10 = TAG_COMPOUND
            for (int i = 0; i < list.tagCount() && i < engines.length; i++) {
                engines[i].readFromNBT(list.getCompoundTagAt(i));
            }
        }
    }
}
