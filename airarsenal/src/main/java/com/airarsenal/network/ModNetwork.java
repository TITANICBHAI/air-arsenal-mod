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
 *  5 → PacketMissileSteer        (C→S)  Chunk 7 (extended with topAttack in Chunk 8)
 *  6 → PacketSetMortarAngle      (C→S)  Chunk 8
 *  7 → PacketSetHowitzerTarget   (C→S)  Chunk 8
 *  8 → PacketGuidanceStart       (S→C)  Chunk 8
 *  9 → PacketGuidanceLost        (S→C)  Chunk 8
 * 10 → PacketGuidanceEnd         (S→C)  Chunk 8
 * 11 → PacketFireMissileTube     (C→S)  Chunk 8
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

        // ── Chunk 8 ───────────────────────────────────────────────────────────
        CHANNEL.registerMessage(
            PacketSetMortarAngle.Handler.class,
            PacketSetMortarAngle.class, 6, Side.SERVER);

        CHANNEL.registerMessage(
            PacketSetHowitzerTarget.Handler.class,
            PacketSetHowitzerTarget.class, 7, Side.SERVER);

        CHANNEL.registerMessage(
            PacketGuidanceStart.Handler.class,
            PacketGuidanceStart.class, 8, Side.CLIENT);

        CHANNEL.registerMessage(
            PacketGuidanceLost.Handler.class,
            PacketGuidanceLost.class, 9, Side.CLIENT);

        CHANNEL.registerMessage(
            PacketGuidanceEnd.Handler.class,
            PacketGuidanceEnd.class, 10, Side.CLIENT);

        CHANNEL.registerMessage(
            PacketFireMissileTube.Handler.class,
            PacketFireMissileTube.class, 11, Side.SERVER);
    }
}
