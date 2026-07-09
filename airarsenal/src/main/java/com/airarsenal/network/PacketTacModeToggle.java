package com.airarsenal.network;

import com.airarsenal.entity.plane.BasePlaneEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Client → Server.
 * Toggles {@code isTacModeActive} on the {@link BasePlaneEntity} the sending
 * player is currently riding. No payload — just the packet arriving is enough.
 *
 * Server-side, only activates if the plane has at least one weapon hardpoint.
 */
public class PacketTacModeToggle implements IMessage {

    // ── IMessage ──────────────────────────────────────────────────────────────

    public PacketTacModeToggle() {} // required by Forge

    @Override
    public void fromBytes(ByteBuf buf) {} // no payload

    @Override
    public void toBytes(ByteBuf buf) {}   // no payload

    // ── Server-side handler ───────────────────────────────────────────────────

    public static class Handler implements IMessageHandler<PacketTacModeToggle, IMessage> {

        @Override
        public IMessage onMessage(PacketTacModeToggle message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;

            // Schedule on the main server thread (network thread safety)
            player.getServerWorld().addScheduledTask(() -> {
                Entity riding = player.getRidingEntity();
                if (riding instanceof BasePlaneEntity) {
                    BasePlaneEntity plane = (BasePlaneEntity) riding;
                    // Only allow Tac Mode on armed planes
                    if (!plane.getWeapons().isEmpty()) {
                        plane.toggleTacMode();
                    }
                }
            });

            return null; // no reply
        }
    }
}
