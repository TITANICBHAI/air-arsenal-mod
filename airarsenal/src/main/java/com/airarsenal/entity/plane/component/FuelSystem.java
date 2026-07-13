package com.airarsenal.entity.plane.component;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Fuel tank attached to every {@link com.airarsenal.entity.plane.BasePlaneEntity} (Chunk 9).
 *
 * <p>Older prop planes (Wood Biplane, Iron Monoplane) also carry a tank for
 * consistency, but their {@code fuelConsumption} is set to {@code 0} so it never
 * depletes — fuel is a mechanic introduced for the advanced Chunk 9 aircraft.</p>
 *
 * <p>Refuelled by {@link com.airarsenal.item.ItemJetFuel} (+25 per use).</p>
 */
public class FuelSystem {

    public static final float MAX_FUEL = 100f;

    private float fuelLevel = MAX_FUEL;

    // ── Consumption ───────────────────────────────────────────────────────────

    /**
     * Consumes {@code tickConsumption} fuel, clamped at zero.
     *
     * @return {@code false} if the tank is already empty or was just emptied by
     *         this call — the caller should treat the engine as failed.
     */
    public boolean consumeFuel(float tickConsumption) {
        if (fuelLevel <= 0f) return false;
        fuelLevel = Math.max(0f, fuelLevel - tickConsumption);
        return fuelLevel > 0f;
    }

    /** Adds fuel, capped at {@link #MAX_FUEL}. */
    public void refuel(float amount) {
        fuelLevel = Math.min(MAX_FUEL, fuelLevel + amount);
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public float getFuelLevel()   { return fuelLevel; }
    public float getFuelPercent() { return fuelLevel / MAX_FUEL; }
    public boolean isEmpty()      { return fuelLevel <= 0f; }

    // ── NBT persistence ───────────────────────────────────────────────────────

    public void writeToNBT(NBTTagCompound compound) {
        compound.setFloat("FuelLevel", fuelLevel);
    }

    public void readFromNBT(NBTTagCompound compound) {
        if (compound.hasKey("FuelLevel")) {
            fuelLevel = compound.getFloat("FuelLevel");
        }
    }
}
