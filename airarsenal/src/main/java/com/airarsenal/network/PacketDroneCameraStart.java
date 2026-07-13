package com.airarsenal.network;

import com.airarsenal.client.TacModeController;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Server → Client (Chunk 9).
 * Switches the client's render view to a {@link com.airarsenal.entity.plane.PredatorDroneEntity}
 * without mounting — used for the Predator Drone's remote-pilot mode. Unlike
 * {@link PacketPredatorCameraStart}, no post-process shader is applied (the pilot
 * gets a plain drone's-eye view, not the grainy Predator Strike look).
 *
 * Payload: {@code int droneEntityId}
 */
public class PacketDroneCameraStart implements IMessage {

    private int droneEntityId;

    public PacketDroneCameraStart() {}

    public PacketDroneCameraStart(int droneEntityId) {
        this.droneEntityId = droneEntityId;
    }

    @Override
    public void toBytes(ByteBuf buf) { buf.writeInt(droneEntityId); }

    @Override
    public void fromBytes(ByteBuf buf) { droneEntityId = buf.readInt(); }

    @SideOnly(Side.CLIENT)
    public static class Handler implements IMessageHandler<PacketDroneCameraStart, IMessage> {

        @Override
        public IMessage onMessage(PacketDroneCameraStart message, MessageContext ctx) {
            Minecraft mc = Minecraft.getMinecraft();
            mc.addScheduledTask(() -> {
                if (mc.world == null) return;
                Entity drone = mc.world.getEntityByID(message.droneEntityId);
                if (drone == null) return;
                mc.renderViewEntity = drone;
                TacModeController.droneCamActive = true;
            });
            return null;
        }
    }
}
