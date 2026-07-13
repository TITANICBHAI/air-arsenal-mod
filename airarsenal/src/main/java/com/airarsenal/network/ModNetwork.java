package com.airarsenal.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Registers all Air Arsenal network packets via Forge's {@link SimpleNetworkWrapper}.
 * Call {@link #register()} once in {@code FMLPreInitializationEvent}.
 *
 * Discriminator map:
 * <pre>
 *  1 → PacketTacModeToggle      (C→S)   Chunk 5
 *  2 → PacketWeaponFire         (C→S)   Chunk 5
 *  3 → PacketPredatorCameraStart (S→C)  Chunk 7
 *  4 → PacketPredatorCameraEnd   (S→C)  Chunk 7
 *  5 → PacketMissileSteer        (C→S)  Chunk 7
 * </pre>
 */
public final class ModNetwork {

    public static SimpleNetworkWrapper CHANNEL;
    private static final String CHANNEL_NAME = "airarsenal";

    private ModNetwork() {}

    public static void register() {
        CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(CHANNEL_NAME);

        // ── Chunk 5 ───────────────────────────────────────────────────────────
        CHANNEL.registerMessage(
            PacketTacModeToggle.Handler.class,
            PacketTacModeToggle.class, 1, Side.SERVER);

        CHANNEL.registerMessage(
            PacketWeaponFire.Handler.class,
            PacketWeaponFire.class, 2, Side.SERVER);

        // ── Chunk 7 ───────────────────────────────────────────────────────────
        CHANNEL.registerMessage(
            PacketPredatorCameraStart.Handler.class,
            PacketPredatorCameraStart.class, 3, Side.CLIENT);

        CHANNEL.registerMessage(
            PacketPredatorCameraEnd.Handler.class,
            PacketPredatorCameraEnd.class, 4, Side.CLIENT);

        CHANNEL.registerMessage(
            PacketMissileSteer.Handler.class,
            PacketMissileSteer.class, 5, Side.SERVER);
    }
}
