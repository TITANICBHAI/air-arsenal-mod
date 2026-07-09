package com.airarsenal.combat;

import net.minecraft.util.DamageSource;

/**
 * Custom damage source that carries an armor-penetration (AP) rating.
 *
 * <p>When an entity is hit, its {@code attackEntityFrom} override can cast
 * the incoming {@link DamageSource} to this type and read {@link #armorPenetration}
 * to look up the correct DR and run the Air Arsenal damage formula.</p>
 *
 * <p>Pre-built static instances cover every bullet / projectile tier in the mod.
 * Create additional instances for any future damage type that needs AP.</p>
 */
public class AirArsenalDamageSource extends DamageSource {

    /** Armor penetration rating of this damage type. Higher = punches through more armor. */
    public final int armorPenetration;

    public AirArsenalDamageSource(String name, int ap) {
        super(name);
        this.armorPenetration = ap;
    }

    // ── Pre-built instances (extend as needed in future chunks) ───────────────

    /** Light round — Iron Monoplane nose gun (Chunk 4). */
    public static final AirArsenalDamageSource BULLET_LIGHT =
        new AirArsenalDamageSource("airarsenal.bullet.light",  12);

    /** Heavy round — Propeller Fighter (Chunk 9). */
    public static final AirArsenalDamageSource BULLET_HEAVY =
        new AirArsenalDamageSource("airarsenal.bullet.heavy",  28);

    /** AP round — Fighter Jet (Chunk 9). */
    public static final AirArsenalDamageSource BULLET_AP =
        new AirArsenalDamageSource("airarsenal.bullet.ap",     48);

    /** Guided missile (Chunk 7). */
    public static final AirArsenalDamageSource MISSILE_GUIDED =
        new AirArsenalDamageSource("airarsenal.missile.guided",70);

    /** Tank shell — bypasses all armour (Chunk future). */
    public static final AirArsenalDamageSource SHELL_TANK =
        (AirArsenalDamageSource) new AirArsenalDamageSource("airarsenal.shell.tank", 80)
            .setDamageBypassesArmor(); // additionally uses Minecraft's bypass flag
}
