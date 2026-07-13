package com.airarsenal.client.gui;

import com.airarsenal.item.ItemMANPADS;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

/**
 * Draws the MANPADS lock-on ring while the player holds right-click with the launcher raised.
 * Ring shrinks from full size to a tight circle as the 40-tick lock completes, then flashes green.
 */
@SideOnly(Side.CLIENT)
public class ManpadsHUD {

    private static final float RING_START = 26f;
    private static final float RING_LOCKED = 10f;

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;
        if (player == null || !player.isHandActive()) return;

        ItemStack activeStack = player.getActiveItemStack();
        if (!(activeStack.getItem() instanceof ItemMANPADS)) return;

        int maxDuration = activeStack.getItem().getMaxItemUseDuration(activeStack);
        int ticksHeld   = maxDuration - player.getItemInUseCount();
        boolean locked  = ticksHeld >= ItemMANPADS.LOCK_ON_TICKS;

        float progress = Math.min(1f, ticksHeld / (float) ItemMANPADS.LOCK_ON_TICKS);
        float radius = RING_START - (RING_START - RING_LOCKED) * progress;

        net.minecraft.client.gui.ScaledResolution sr =
            new net.minecraft.client.gui.ScaledResolution(mc);
        int cx = sr.getScaledWidth() / 2;
        int cy = sr.getScaledHeight() / 2;

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(
            GlStateManager.SourceFactor.SRC_ALPHA,
            GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
            GlStateManager.SourceFactor.ONE,
            GlStateManager.DestFactor.ZERO);

        if (locked) {
            GlStateManager.color(0f, 1f, 0f, 0.9f);
        } else {
            GlStateManager.color(1f, 0.6f, 0f, 0.8f);
        }

        GL11.glLineWidth(2f);
        GL11.glBegin(GL11.GL_LINE_LOOP);
        int segments = 32;
        // Partial arc while locking, full ring once locked
        int drawSegments = locked ? segments : (int) (segments * progress) + 1;
        for (int i = 0; i <= drawSegments; i++) {
            double a = (2.0 * Math.PI / segments) * i;
            GL11.glVertex2d(cx + Math.cos(a) * radius, cy + Math.sin(a) * radius);
        }
        GL11.glEnd();

        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();

        String label = locked ? "LOCKED" : "LOCKING...";
        int labelW = mc.fontRenderer.getStringWidth(label);
        mc.fontRenderer.drawStringWithShadow(label, cx - labelW / 2f, cy + radius + 6,
            locked ? 0x00FF00 : 0xFFAA00);
    }
}
