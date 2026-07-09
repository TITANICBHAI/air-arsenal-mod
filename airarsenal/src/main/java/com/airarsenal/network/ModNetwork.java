package com.airarsenal.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Registers all Air Arsenal network packets via Forge's {@link SimpleNetworkWrapper}.
 *
 * Call {@link #register()} once during {@code FMLPreInitializationEvent} (before init,
 * so packets are available when the world loads).
 *
 * Discriminator IDs must be unique per channel:
 *   1 → {@link PacketTacModeToggle}
 *   2 → {@link PacketWeaponFire}
 */
public final class ModNetwork {

    public static SimpleNetworkWrapper CHANNEL;
    private static final String CHANNEL_NAME = "airarsenal";

    private ModNetwork() {}

    public static void register() {
        CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(CHANNEL_NAME);

        // Client → Server packets
        CHANNEL.registerMessage(
            PacketTacModeToggle.Handler.class,
            PacketTacModeToggle.class,
            1,
            Side.SERVER
        );
        CHANNEL.registerMessage(
            PacketWeaponFire.Handler.class,
            PacketWeaponFire.class,
            2,
            Side.SERVER
        );
    }
}
