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
 * Server → Client.
 * Restores the player's render view to themselves and removes the drone-cam shader.
 * Sent on missile impact or 160-tick timeout.
 *
 * No payload.
 */
public class PacketPredatorCameraEnd implements IMessage {

    public PacketPredatorCameraEnd() {}

    @Override public void toBytes(ByteBuf buf)   {}
    @Override public void fromBytes(ByteBuf buf) {}

    // ── Client handler ────────────────────────────────────────────────────────

    @SideOnly(Side.CLIENT)
    public static class Handler implements IMessageHandler<PacketPredatorCameraEnd, IMessage> {

        @Override
        public IMessage onMessage(PacketPredatorCameraEnd message, MessageContext ctx) {
            Minecraft mc = Minecraft.getMinecraft();
            mc.addScheduledTask(() -> handleClient(mc));
            return null;
        }

        @SideOnly(Side.CLIENT)
        private static void handleClient(Minecraft mc) {
            if (mc.player == null) return;

            // Restore player-perspective view
            mc.setRenderViewEntity(mc.player);

            // Remove post-process shader
            mc.entityRenderer.stopUseShader();

            // Tell the controller to stop routing mouse to steer packets
            TacModeController.predatorCamActive = false;
        }
    }
}
