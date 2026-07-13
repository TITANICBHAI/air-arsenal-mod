package com.airarsenal.entity.plane.component;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Shared contract for a plane's motive-power component — either a
 * {@link PropellerComponent} (prop-driven planes) or a {@link JetEngineComponent}
 * (jet-driven planes, Chunk 9). {@link com.airarsenal.entity.plane.BasePlaneEntity}
 * only ever talks to this interface so flight physics code is identical for both.
 */
public interface IEngineComponent {

    /** Damage state derived from the current HP percentage. */
    PropellerState getDamageState();

    /** Speed multiplier the parent plane should apply this tick. */
    float getSpeedMultiplier();

    /** Random yaw drift (degrees) to add this tick. Zero when intact or destroyed. */
    float getYawDrift();

    /**
     * Reduces health by {@code amount}, clamped to zero.
     *
     * @return {@code true} if this call just transitioned the component to DESTROYED.
     */
    boolean takeDamage(float amount);

    float getCurrentHealth();

    float getMaxHealth();

    boolean isDestroyed();

    void writeToNBT(NBTTagCompound compound);

    void readFromNBT(NBTTagCompound compound);
}
