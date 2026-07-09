package com.airarsenal.entity.plane.component;

import net.minecraft.nbt.NBTTagCompound;

import java.util.Random;

/**
 * Data object attached to every {@link com.airarsenal.entity.plane.BasePlaneEntity}.
 * Tracks the propeller's structural health, damage state, and derived penalties.
 *
 * <p>Not an Entity — it has no world presence of its own. The parent plane entity
 * is responsible for spawning {@link com.airarsenal.entity.projectile.PropellerShardEntity}
 * when this component reaches {@link PropellerState#DESTROYED}.</p>
 */
public class PropellerComponent {

    private final float maxHealth;
    private float currentHealth;
    private final Random rand = new Random();

    // ── Construction ──────────────────────────────────────────────────────────

    public PropellerComponent(float maxHealth) {
        this.maxHealth     = maxHealth;
        this.currentHealth = maxHealth;
    }

    // ── State query ───────────────────────────────────────────────────────────

    /**
     * Returns the current damage state based on the HP percentage.
     *
     * <ul>
     *   <li>&gt; 75% → INTACT</li>
     *   <li>&gt; 50% → DAMAGED</li>
     *   <li>&gt; 25% → HEAVY_DAMAGE</li>
     *   <li>&gt;  0% → CRITICAL</li>
     *   <li>  = 0% → DESTROYED</li>
     * </ul>
     */
    public PropellerState getDamageState() {
        if (currentHealth <= 0f)                         return PropellerState.DESTROYED;
        float pct = currentHealth / maxHealth;
        if (pct > 0.75f)                                 return PropellerState.INTACT;
        if (pct > 0.50f)                                 return PropellerState.DAMAGED;
        if (pct > 0.25f)                                 return PropellerState.HEAVY_DAMAGE;
        /* pct > 0 */                                    return PropellerState.CRITICAL;
    }

    /**
     * Speed multiplier the parent plane should apply this tick.
     */
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

    /**
     * Random yaw drift (degrees) to add this tick. Zero when intact or destroyed.
     * Called once per tick; each call produces an independent random value.
     */
    public float getYawDrift() {
        switch (getDamageState()) {
            case HEAVY_DAMAGE:
                // ±3° range
                return (rand.nextFloat() * 6f) - 3f;
            case CRITICAL:
                // ±7° range
                return (rand.nextFloat() * 14f) - 7f;
            default:
                return 0f;
        }
    }

    // ── Damage intake ─────────────────────────────────────────────────────────

    /**
     * Reduces propeller health by {@code amount}.
     * Clamps to zero; the parent entity must poll {@link #getDamageState()} to
     * detect the DESTROYED transition and spawn the shard.
     *
     * @param amount positive damage value
     * @return {@code true} if the propeller just transitioned to DESTROYED
     */
    public boolean takeDamage(float amount) {
        boolean wasAlive = currentHealth > 0f;
        currentHealth = Math.max(0f, currentHealth - amount);
        return wasAlive && currentHealth <= 0f; // true only on the killing hit
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public float getCurrentHealth() { return currentHealth; }
    public float getMaxHealth()     { return maxHealth;      }
    public boolean isDestroyed()    { return currentHealth <= 0f; }

    // ── NBT persistence ───────────────────────────────────────────────────────

    public void writeToNBT(NBTTagCompound compound) {
        compound.setFloat("PropellerHealth",    currentHealth);
        compound.setFloat("PropellerMaxHealth", maxHealth);
    }

    public void readFromNBT(NBTTagCompound compound) {
        // maxHealth is final — only restore currentHealth
        currentHealth = compound.getFloat("PropellerHealth");
    }
}
