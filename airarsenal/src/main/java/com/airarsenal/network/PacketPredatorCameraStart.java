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
 * Server → Client.
 * Switches the client's render view to the {@link com.airarsenal.entity.projectile.PredatorMissileEntity}
 * and applies the grainy drone-cam post-process shader.
 *
 * Payload: {@code int missileEntityId}
 */
public class PacketPredatorCameraStart implements IMessage {

    private int missileEntityId;

    public PacketPredatorCameraStart() {}

    public PacketPredatorCameraStart(int missileEntityId) {
        this.missileEntityId = missileEntityId;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(missileEntityId);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        missileEntityId = buf.readInt();
    }

    // ── Client handler ────────────────────────────────────────────────────────

    @SideOnly(Side.CLIENT)
    public static class Handler implements IMessageHandler<PacketPredatorCameraStart, IMessage> {

        @Override
        public IMessage onMessage(PacketPredatorCameraStart message, MessageContext ctx) {
            Minecraft mc = Minecraft.getMinecraft();
            // Schedule on the client main thread — never touch GL/render state from network thread
            mc.addScheduledTask(() -> handleClient(mc, message.missileEntityId));
            return null;
        }

        @SideOnly(Side.CLIENT)
        private static void handleClient(Minecraft mc, int entityId) {
            if (mc.world == null) return;

            Entity missile = mc.world.getEntityByID(entityId);
            if (missile == null) {
                // Entity not yet loaded on client (chunk load lag) — retry next tick
                TacModeController.pendingPredatorEntityId = entityId;
                TacModeController.predatorCamPendingTicks = 10; // retry for up to 10 ticks
                return;
            }

            activatePredatorCam(mc, missile);
        }

        @SideOnly(Side.CLIENT)
        public static void activatePredatorCam(Minecraft mc, Entity missile) {
            // Switch render view to the missile nose
            mc.setRenderViewEntity(missile);

            // Apply drone-cam post-process shader (grayscale + slight blur)
            try {
                mc.entityRenderer.loadShader(
                    new net.minecraft.util.ResourceLocation("airarsenal",
                        "shaders/post/predator_cam.json")
                );
            } catch (Exception e) {
                // Shader load failure should not crash the game — degrade gracefully
            }

            TacModeController.pendingPredatorEntityId = -1;
            TacModeController.predatorCamPendingTicks = 0;
            TacModeController.predatorCamActive       = true;
            TacModeController.steerTick               = 0;
        }
    }
}
