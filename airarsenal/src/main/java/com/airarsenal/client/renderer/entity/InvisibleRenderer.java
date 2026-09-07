package com.airarsenal.client.renderer.entity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

/**
 * Invisible renderer for marker entities like OrbitalWarningMarkerEntity.
 */
public class InvisibleRenderer<T extends Entity> extends Render<T> {

    public InvisibleRenderer(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
        // No rendering needed; warning markers use particle effects
    }

    @Override
    protected ResourceLocation getEntityTexture(T entity) {
        return null;
    }
}
