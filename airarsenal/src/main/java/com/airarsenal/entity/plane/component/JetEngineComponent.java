package com.airarsenal.entity.plane.component;

import net.minecraft.nbt.NBTTagCompound;

import java.util.Random;

/**
 * Jet-engine equivalent of {@link PropellerComponent} (Chunk 9).
 *
 * <p>Mirrors the propeller's health/damage-state/speed-multiplier/yaw-drift math
 * exactly, but differs in two ways the parent plane must handle itself:</p>
 * <ul>
 *   <li><b>No contact hazard</b> — jets have no exposed spinning blade, so
 *       {@link com.airarsenal.entity.plane.BasePlaneEntity} skips propeller-contact
 *       checks for jet-engined planes (see {@code hasSpinningPropeller}).</li>
 *   <li><b>Destruction consequence</b> — when this reaches {@link PropellerState#DESTROYED}
 *       the parent plane spawns fire behind the airframe and applies
 *       {@link net.minecraft.potion.MobEffects#WITHER} to the pilot, instead of
 *       ejecting a propeller shard.</li>
 * </ul>
 */
public class JetEngineComponent implements IEngineComponent {

    private final float maxHealth;
    private float currentHealth;
    private final Random rand = new Random();

    public JetEngineComponent(float maxHealth) {
        this.maxHealth     = maxHealth;
        this.currentHealth = maxHealth;
    }

    @Override
    public PropellerState getDamageState() {
        if (currentHealth <= 0f)                         return PropellerState.DESTROYED;
        float pct = currentHealth / maxHealth;
        if (pct > 0.75f)                                 return PropellerState.INTACT;
        if (pct > 0.50f)                                 return PropellerState.DAMAGED;
        if (pct > 0.25f)                                 return PropellerState.HEAVY_DAMAGE;
        /* pct > 0 */                                    return PropellerState.CRITICAL;
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
        switch (getDamageState()) {
            case HEAVY_DAMAGE:
                return (rand.nextFloat() * 6f) - 3f;
            case CRITICAL:
                return (rand.nextFloat() * 14f) - 7f;
            default:
                return 0f;
        }
    }

    @Override
    public boolean takeDamage(float amount) {
        boolean wasAlive = currentHealth > 0f;
        currentHealth = Math.max(0f, currentHealth - amount);
        return wasAlive && currentHealth <= 0f;
    }

    @Override public float getCurrentHealth() { return currentHealth; }
    @Override public float getMaxHealth()     { return maxHealth;      }
    @Override public boolean isDestroyed()    { return currentHealth <= 0f; }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        compound.setFloat("JetEngineHealth",    currentHealth);
        compound.setFloat("JetEngineMaxHealth", maxHealth);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        if (compound.hasKey("JetEngineHealth")) {
            currentHealth = compound.getFloat("JetEngineHealth");
        }
    }
}
