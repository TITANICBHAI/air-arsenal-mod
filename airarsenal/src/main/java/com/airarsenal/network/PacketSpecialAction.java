package com.airarsenal.network;

import com.airarsenal.entity.plane.BasePlaneEntity;
import com.airarsenal.entity.plane.FighterJetEntity;
import com.airarsenal.entity.plane.StealthBomberEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Client → Server (Chunk 9).
 * Sent when the pilot presses the shared "special action" key ({@code G}).
 * Its effect depends on which plane the sender is riding:
 * <ul>
 *   <li>{@link FighterJetEntity} → activates the afterburner.</li>
 *   <li>{@link StealthBomberEntity} → toggles stealth mode.</li>
 *   <li>Any other plane → no-op.</li>
 * </ul>
 * No payload — just the packet arriving is enough.
 */
public class PacketSpecialAction implements IMessage {

    public PacketSpecialAction() {}

    @Override public void toBytes(ByteBuf buf)   {}
    @Override public void fromBytes(ByteBuf buf) {}

    public static class Handler implements IMessageHandler<PacketSpecialAction, IMessage> {

        @Override
        public IMessage onMessage(PacketSpecialAction message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;

            player.getServerWorld().addScheduledTask(() -> {
                Entity riding = player.getRidingEntity();
                if (!(riding instanceof BasePlaneEntity)) return;

                if (riding instanceof FighterJetEntity) {
                    ((FighterJetEntity) riding).tryActivateAfterburner();
                } else if (riding instanceof StealthBomberEntity) {
                    ((StealthBomberEntity) riding).toggleStealth();
                }
            });

            return null;
        }
    }
}
