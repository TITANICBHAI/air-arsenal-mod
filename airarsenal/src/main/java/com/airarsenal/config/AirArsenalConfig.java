package com.airarsenal.config;

import com.airarsenal.AirArsenal;
import net.minecraftforge.common.config.Config;

/**
 * Central gameplay configuration for Air Arsenal.
 * Generates {@code config/airarsenal.cfg} on first launch via the {@code @Config}
 * annotation — Forge handles file creation and loading automatically.
 *
 * The in-game config GUI sync listener is registered server-safely in
 * {@link com.airarsenal.ClientProxy#init} (client side only) to avoid
 * classloading {@code ConfigChangedEvent} — a client-only class — on a
 * dedicated server.
 *
 * Access values as plain static fields:
 * <pre>
 *   if (AirArsenalConfig.allowBlockDestruction) { ... }
 * </pre>
 */
@Config(modid = AirArsenal.MODID, category = "gameplay")
public final class AirArsenalConfig {

    @Config.Comment("If false, bombs damage entities but do not break blocks.")
    @Config.LangKey("config.airarsenal.allowBlockDestruction")
    public static boolean allowBlockDestruction = true;

    @Config.Comment("If false, bombs do not damage the plane or player that dropped them.")
    @Config.LangKey("config.airarsenal.allowFriendlyFire")
    public static boolean allowFriendlyFire = true;

    // ── Chunk 11 — Orbital Cannon ────────────────────────────────────────────

    @Config.Comment("Cooldown between orbital strikes in ticks (default 6000 = 5 minutes)")
    @Config.LangKey("config.airarsenal.orbitalCooldownTicks")
    public static int orbitalCooldownTicks = 6000;

    @Config.Comment("Blast radius of orbital rod impact in blocks")
    @Config.LangKey("config.airarsenal.orbitalBlastRadius")
    public static int orbitalBlastRadius = 15;

    @Config.Comment("Ticks of charging before rod fires (default 200 = 10s)")
    @Config.LangKey("config.airarsenal.orbitalChargingTicks")
    public static int orbitalChargingTicks = 200;

    @Config.Comment("Ticks of warning before impact (default 100 = 5s)")
    @Config.LangKey("config.airarsenal.orbitalWarningTicks")
    public static int orbitalWarningTicks = 100;

    @Config.Comment("If false, orbital strike does not destroy blocks (entity damage still applies)")
    @Config.LangKey("config.airarsenal.orbitalBlockDestruction")
    public static boolean orbitalBlockDestruction = true;

    @Config.Comment("Max distance in blocks from cannon the designator can paint a target")
    @Config.LangKey("config.airarsenal.orbitalMaxRange")
    public static int orbitalMaxRange = 500;

    private AirArsenalConfig() {}
}
