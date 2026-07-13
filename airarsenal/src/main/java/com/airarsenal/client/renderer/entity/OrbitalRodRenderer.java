package com.airarsenal.client.renderer.entity;

import com.airarsenal.entity.projectile.OrbitalRodEntity;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * Renders the falling kinetic rod as a tall thin emissive white box.
 */
public class OrbitalRodRenderer extends Render<OrbitalRodEntity> {

    private static final ResourceLocation NO_TEXTURE = null;

    public OrbitalRodRenderer(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(OrbitalRodEntity entity, double x, double y, double z,
                          float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.color(1f, 1f, 1f, 1f);

        float hw = 0.1f;
        float h = 2.0f;
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex3f(-hw, 0, -hw);
        GL11.glVertex3f(hw, 0, -hw);
        GL11.glVertex3f(hw, h, -hw);
        GL11.glVertex3f(-hw, h, -hw);

        GL11.glVertex3f(-hw, 0, hw);
        GL11.glVertex3f(hw, 0, hw);
        GL11.glVertex3f(hw, h, hw);
        GL11.glVertex3f(-hw, h, hw);

        GL11.glVertex3f(-hw, 0, -hw);
        GL11.glVertex3f(-hw, 0, hw);
        GL11.glVertex3f(-hw, h, hw);
        GL11.glVertex3f(-hw, h, -hw);

        GL11.glVertex3f(hw, 0, -hw);
        GL11.glVertex3f(hw, 0, hw);
        GL11.glVertex3f(hw, h, hw);
        GL11.glVertex3f(hw, h, -hw);
        GL11.glEnd();

        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(OrbitalRodEntity entity) {
        return NO_TEXTURE;
    }
}
