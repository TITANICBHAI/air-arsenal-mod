package com.airarsenal.network;

import com.airarsenal.client.TacModeController;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Server → Client (Chunk 9).
 * Restores the pilot's own view after exiting Predator Drone remote-pilot mode
 * (Shift key) — the drone itself keeps flying at its last heading server-side.
 *
 * No payload.
 */
public class PacketDroneCameraEnd implements IMessage {

    public PacketDroneCameraEnd() {}

    @Override public void toBytes(ByteBuf buf)   {}
    @Override public void fromBytes(ByteBuf buf) {}

    @SideOnly(Side.CLIENT)
    public static class Handler implements IMessageHandler<PacketDroneCameraEnd, IMessage> {

        @Override
        public IMessage onMessage(PacketDroneCameraEnd message, MessageContext ctx) {
            Minecraft mc = Minecraft.getMinecraft();
            mc.addScheduledTask(() -> {
                if (mc.player == null) return;
                mc.setRenderViewEntity(mc.player);
                TacModeController.droneCamActive = false;
            });
            return null;
        }
    }
}
