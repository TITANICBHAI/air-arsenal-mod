package com.airarsenal.client.renderer.tileentity;

import com.airarsenal.block.OrbitalCannonTileEntity;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

/**
 * Renders a slowly rotating rod above the Orbital Cannon core block, with a
 * pulsing glow while CHARGING or WARNING.
 */
public class OrbitalCannonTESR extends TileEntitySpecialRenderer<OrbitalCannonTileEntity> {

    private float rotationAngle = 0f;

    @Override
    public void render(OrbitalCannonTileEntity te, double x, double y, double z,
                        float partialTicks, int destroyStage, float alpha) {
        boolean active = te.getChargeState() == OrbitalCannonTileEntity.ChargeState.CHARGING
            || te.getChargeState() == OrbitalCannonTileEntity.ChargeState.WARNING;

        if (active) {
            rotationAngle += 2f;
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, y + 1.5, z + 0.5);
        GlStateManager.rotate(rotationAngle, 0f, 1f, 0f);
        GlStateManager.disableTexture2D();

        float rodAlpha = 1.0f;
        if (active) {
            rodAlpha = 0.6f + 0.4f * MathHelper.sin((te.getWorld().getTotalWorldTime() + partialTicks) * 0.3f);
        }
        GlStateManager.enableBlend();
        GlStateManager.color(0.8f, 0.9f, 1.0f, rodAlpha);

        GL11.glBegin(GL11.GL_QUADS);
        float hw = 0.15f;
        float h = 1.5f;
        GL11.glVertex3f(-hw, 0, -hw);
        GL11.glVertex3f(hw, 0, -hw);
        GL11.glVertex3f(hw, h, -hw);
        GL11.glVertex3f(-hw, h, -hw);

        GL11.glVertex3f(-hw, 0, hw);
        GL11.glVertex3f(hw, 0, hw);
        GL11.glVertex3f(hw, h, hw);
        GL11.glVertex3f(-hw, h, hw);
        GL11.glEnd();

        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }
}
