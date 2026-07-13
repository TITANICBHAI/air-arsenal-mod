package com.airarsenal.network;

import com.airarsenal.entity.vehicle.MissileTruckEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Client → Server. Sent when the player driving/riding a {@link MissileTruckEntity}
 * left-clicks to fire whichever tube is ready. No payload — the server locates the
 * truck the sender is currently riding.
 */
public class PacketFireMissileTube implements IMessage {

    public PacketFireMissileTube() {}

    @Override public void toBytes(ByteBuf buf) {}
    @Override public void fromBytes(ByteBuf buf) {}

    public static class Handler implements IMessageHandler<PacketFireMissileTube, IMessage> {
        @Override
        public IMessage onMessage(PacketFireMissileTube message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> {
                Entity ridden = player.getRidingEntity();
                if (ridden instanceof MissileTruckEntity) {
                    ((MissileTruckEntity) ridden).fireTube(player);
                }
            });
            return null;
        }
    }
}
