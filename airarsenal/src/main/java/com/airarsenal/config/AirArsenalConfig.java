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

    private AirArsenalConfig() {}
}
