package com.airarsenal.network;

import com.airarsenal.entity.plane.PredatorDroneEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Client → Server (Chunk 9).
 * Sent when the pilot presses Shift while remote-piloting a
 * {@link PredatorDroneEntity}. Ends remote-pilot mode — the drone keeps flying
 * at its current heading/speed. No payload.
 */
public class PacketDroneExit implements IMessage {

    public PacketDroneExit() {}

    @Override public void toBytes(ByteBuf buf)   {}
    @Override public void fromBytes(ByteBuf buf) {}

    public static class Handler implements IMessageHandler<PacketDroneExit, IMessage> {

        @Override
        public IMessage onMessage(PacketDroneExit message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;

            player.getServerWorld().addScheduledTask(() -> {
                for (Entity e : player.world.loadedEntityList) {
                    if (e instanceof PredatorDroneEntity) {
                        PredatorDroneEntity drone = (PredatorDroneEntity) e;
                        if (drone.getControllingPlayer() == player) {
                            drone.stopRemotePilot();
                            return;
                        }
                    }
                }
            });

            return null;
        }
    }
}
