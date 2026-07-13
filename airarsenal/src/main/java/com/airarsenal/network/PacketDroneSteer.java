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
 * Delivers WASD + mouse-derived movement/steering input for a
 * {@link PredatorDroneEntity} being flown in remote-pilot mode — same shape
 * as {@link PacketMissileSteer}, but also carries throttle input since the
 * drone (unlike a missile) can slow down and hover.
 *
 * Payload: {@code float yaw, float pitch, boolean throttleUp, boolean throttleDown}
 * Sent every 2 ticks by {@link com.airarsenal.client.TacModeController}.
 */
public class PacketDroneSteer implements IMessage {

    private float yaw;
    private float pitch;
    private boolean throttleUp;
    private boolean throttleDown;

    public PacketDroneSteer() {}

    public PacketDroneSteer(float yaw, float pitch, boolean throttleUp, boolean throttleDown) {
        this.yaw          = yaw;
        this.pitch        = pitch;
        this.throttleUp   = throttleUp;
        this.throttleDown = throttleDown;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeFloat(yaw);
        buf.writeFloat(pitch);
        buf.writeBoolean(throttleUp);
        buf.writeBoolean(throttleDown);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        yaw          = buf.readFloat();
        pitch        = buf.readFloat();
        throttleUp   = buf.readBoolean();
        throttleDown = buf.readBoolean();
    }

    // ── Server-side handler ───────────────────────────────────────────────────

    public static class Handler implements IMessageHandler<PacketDroneSteer, IMessage> {

        @Override
        public IMessage onMessage(PacketDroneSteer message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;

            player.getServerWorld().addScheduledTask(() -> {
                for (Entity e : player.world.loadedEntityList) {
                    if (e instanceof PredatorDroneEntity) {
                        PredatorDroneEntity drone = (PredatorDroneEntity) e;
                        if (drone.getControllingPlayer() == player) {
                            drone.applyRemoteSteer(message.yaw, message.pitch,
                                message.throttleUp, message.throttleDown);
                            return;
                        }
                    }
                }
            });

            return null;
        }
    }
}
