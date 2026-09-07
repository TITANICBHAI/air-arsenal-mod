package com.airarsenal.client.renderer.entity;

import com.airarsenal.entity.projectile.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * 3D Renderer for falling aerial bombs in Air Arsenal.
 * Calculates dynamic aerodynamic pitch and yaw along the fall trajectory.
 */
public class BombRenderer<T extends BaseBombEntity> extends Render<T> {

    public BombRenderer(RenderManager renderManager) {
        super(renderManager);
        this.shadowSize = 0.4f;
    }

    @Override
    public void doRender(T bomb, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y + 0.2, z);

        double hSpeed = Math.sqrt(bomb.motionX * bomb.motionX + bomb.motionZ * bomb.motionZ);
        float yaw = (float) (Math.atan2(bomb.motionX, bomb.motionZ) * (180.0 / Math.PI));
        float pitch = (float) (Math.atan2(bomb.motionY, Math.max(hSpeed, 0.001)) * (180.0 / Math.PI));

        GlStateManager.rotate(180.0f - yaw, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-pitch, 1.0f, 0.0f, 0.0f);

        GlStateManager.disableTexture2D();
        GlStateManager.enableRescaleNormal();

        if (bomb instanceof HeavyBombEntity) {
            renderHeavyBomb();
        } else if (bomb instanceof NapalmBombEntity) {
            renderNapalmBomb();
        } else if (bomb instanceof EMPBombEntity) {
            renderEMPBomb(bomb.ticksExisted + partialTicks);
        } else if (bomb instanceof DepthChargeEntity) {
            renderDepthCharge();
        } else if (bomb instanceof ClusterBombEntity) {
            renderClusterBomb();
        } else if (bomb instanceof SmokeBombEntity) {
            renderSmokeBomb();
        } else if (bomb instanceof FuelAirBombEntity) {
            renderFuelAirBomb();
        } else {
            renderIronBomb();
        }

        GlStateManager.disableRescaleNormal();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();

        super.doRender(bomb, x, y, z, entityYaw, partialTicks);
    }

    private static void drawBox(float minX, float minY, float minZ, float maxX, float maxY, float maxZ,
                                float r, float g, float b) {
        GlStateManager.color(r, g, b, 1.0f);
        GL11.glBegin(GL11.GL_QUADS);
        // North
        GL11.glNormal3f(0, 0, -1);
        GL11.glVertex3f(minX, minY, minZ);
        GL11.glVertex3f(minX, maxY, minZ);
        GL11.glVertex3f(maxX, maxY, minZ);
        GL11.glVertex3f(maxX, minY, minZ);
        // South
        GL11.glNormal3f(0, 0, 1);
        GL11.glVertex3f(maxX, minY, maxZ);
        GL11.glVertex3f(maxX, maxY, maxZ);
        GL11.glVertex3f(minX, maxY, maxZ);
        GL11.glVertex3f(minX, minY, maxZ);
        // West
        GL11.glNormal3f(-1, 0, 0);
        GL11.glVertex3f(minX, minY, maxZ);
        GL11.glVertex3f(minX, maxY, maxZ);
        GL11.glVertex3f(minX, maxY, minZ);
        GL11.glVertex3f(minX, minY, minZ);
        // East
        GL11.glNormal3f(1, 0, 0);
        GL11.glVertex3f(maxX, minY, minZ);
        GL11.glVertex3f(maxX, maxY, minZ);
        GL11.glVertex3f(maxX, maxY, maxZ);
        GL11.glVertex3f(maxX, minY, maxZ);
        // Top
        GL11.glNormal3f(0, 1, 0);
        GL11.glVertex3f(minX, maxY, minZ);
        GL11.glVertex3f(minX, maxY, maxZ);
        GL11.glVertex3f(maxX, maxY, maxZ);
        GL11.glVertex3f(maxX, maxY, minZ);
        // Bottom
        GL11.glNormal3f(0, -1, 0);
        GL11.glVertex3f(minX, minY, maxZ);
        GL11.glVertex3f(minX, minY, minZ);
        GL11.glVertex3f(maxX, minY, minZ);
        GL11.glVertex3f(maxX, minY, maxZ);
        GL11.glEnd();
    }

    // ── Iron Bomb ───────────────────────────────────────────────────────────
    private void renderIronBomb() {
        // Bomb body (dark cast iron)
        drawBox(-0.16f, -0.16f, -0.6f, 0.16f, 0.16f, 0.35f, 0.25f, 0.26f, 0.28f);
        // Nose cone & fuze
        drawBox(-0.12f, -0.12f, 0.35f, 0.12f, 0.12f, 0.55f, 0.85f, 0.70f, 0.15f); // yellow nose band
        drawBox(-0.04f, -0.04f, 0.55f, 0.04f, 0.04f, 0.65f, 0.7f, 0.7f, 0.7f);   // brass striker fuze
        // 4 Cruciform Tail Fins
        drawBox(-0.35f, -0.01f, -0.85f, 0.35f, 0.01f, -0.45f, 0.2f, 0.2f, 0.2f);
        drawBox(-0.01f, -0.35f, -0.85f, 0.01f, 0.35f, -0.45f, 0.2f, 0.2f, 0.2f);
    }

    // ── Heavy Bomb ──────────────────────────────────────────────────────────
    private void renderHeavyBomb() {
        // Massive cylindrical body (dark military olive)
        drawBox(-0.28f, -0.28f, -0.9f, 0.28f, 0.28f, 0.55f, 0.32f, 0.36f, 0.26f);
        // Heavy rounded nose
        drawBox(-0.22f, -0.22f, 0.55f, 0.22f, 0.22f, 0.85f, 0.25f, 0.28f, 0.20f);
        // Impact detonator
        drawBox(-0.05f, -0.05f, 0.85f, 0.05f, 0.05f, 0.98f, 0.8f, 0.8f, 0.8f);
        // Tail Fins and Shroud Ring
        drawBox(-0.45f, -0.02f, -1.25f, 0.45f, 0.02f, -0.7f, 0.25f, 0.25f, 0.25f);
        drawBox(-0.02f, -0.45f, -1.25f, 0.02f, 0.45f, -0.7f, 0.25f, 0.25f, 0.25f);
        // Circular Tail Shroud Ring
        drawBox(-0.32f, -0.32f, -1.28f, 0.32f, 0.32f, -1.15f, 0.22f, 0.22f, 0.22f);
    }

    // ── Napalm Bomb ─────────────────────────────────────────────────────────
    private void renderNapalmBomb() {
        // Incendiary Tank (bright warning orange)
        drawBox(-0.2f, -0.2f, -0.75f, 0.2f, 0.2f, 0.45f, 0.92f, 0.38f, 0.12f);
        // Nose igniter cap (crimson red)
        drawBox(-0.14f, -0.14f, 0.45f, 0.14f, 0.14f, 0.68f, 0.85f, 0.15f, 0.15f);
        // Rear fins
        drawBox(-0.38f, -0.01f, -0.98f, 0.38f, 0.01f, -0.6f, 0.2f, 0.2f, 0.2f);
        drawBox(-0.01f, -0.38f, -0.98f, 0.01f, 0.38f, -0.6f, 0.2f, 0.2f, 0.2f);
    }

    // ── EMP Bomb ────────────────────────────────────────────────────────────
    private void renderEMPBomb(float animTick) {
        // Tech casing (matte dark titanium)
        drawBox(-0.18f, -0.18f, -0.7f, 0.18f, 0.18f, 0.4f, 0.22f, 0.24f, 0.28f);
        // Electromagnetic pulsing coils (electric cyan)
        float glow = 0.7f + (float) Math.sin(animTick * 0.5f) * 0.3f;
        drawBox(-0.21f, -0.21f, -0.3f, 0.21f, 0.21f, -0.1f, 0.1f * glow, 0.7f * glow, 0.95f * glow);
        drawBox(-0.21f, -0.21f, 0.1f, 0.21f, 0.21f, 0.3f, 0.1f * glow, 0.7f * glow, 0.95f * glow);
        // Nose emitter antenna
        drawBox(-0.05f, -0.05f, 0.4f, 0.05f, 0.05f, 0.75f, 0.7f, 0.75f, 0.8f);
        // Tail stabilizers
        drawBox(-0.32f, -0.01f, -0.92f, 0.32f, 0.01f, -0.55f, 0.25f, 0.28f, 0.32f);
        drawBox(-0.01f, -0.32f, -0.92f, 0.01f, 0.32f, -0.55f, 0.25f, 0.28f, 0.32f);
    }

    // ── Depth Charge ────────────────────────────────────────────────────────
    private void renderDepthCharge() {
        // Cylindrical barrel drum (heavy naval steel)
        drawBox(-0.25f, -0.25f, -0.6f, 0.25f, 0.25f, 0.6f, 0.22f, 0.25f, 0.28f);
        // Pressure sensor dials
        drawBox(-0.12f, 0.25f, -0.2f, 0.12f, 0.32f, 0.2f, 0.75f, 0.75f, 0.2f);
        // Hydrostatic tail shroud ring
        drawBox(-0.3f, -0.3f, -0.85f, 0.3f, 0.3f, -0.6f, 0.18f, 0.18f, 0.18f);
    }

    // ── Cluster Bomb ────────────────────────────────────────────────────────
    private void renderClusterBomb() {
        // Green dispenser body
        drawBox(-0.2f, -0.2f, -0.8f, 0.2f, 0.2f, 0.5f, 0.25f, 0.38f, 0.22f);
        // Clamshell release seams (dark grooves)
        drawBox(-0.21f, -0.21f, -0.1f, 0.21f, 0.21f, 0.0f, 0.15f, 0.15f, 0.15f);
        // Nose cone
        drawBox(-0.15f, -0.15f, 0.5f, 0.15f, 0.15f, 0.72f, 0.22f, 0.22f, 0.22f);
        // Tail assembly
        drawBox(-0.35f, -0.01f, -1.05f, 0.35f, 0.01f, -0.65f, 0.2f, 0.2f, 0.2f);
        drawBox(-0.01f, -0.35f, -1.05f, 0.01f, 0.35f, -0.65f, 0.2f, 0.2f, 0.2f);
    }

    // ── Smoke Bomb ──────────────────────────────────────────────────────────
    private void renderSmokeBomb() {
        // Canister (matte grey with green band)
        drawBox(-0.15f, -0.15f, -0.55f, 0.15f, 0.15f, 0.4f, 0.75f, 0.75f, 0.75f);
        drawBox(-0.16f, -0.16f, -0.1f, 0.16f, 0.16f, 0.1f, 0.2f, 0.6f, 0.25f);
        // Vent ports
        drawBox(-0.08f, -0.08f, 0.4f, 0.08f, 0.08f, 0.55f, 0.3f, 0.3f, 0.3f);
        // Tail fins
        drawBox(-0.28f, -0.01f, -0.75f, 0.28f, 0.01f, -0.45f, 0.2f, 0.2f, 0.2f);
        drawBox(-0.01f, -0.28f, -0.75f, 0.01f, 0.28f, -0.45f, 0.2f, 0.2f, 0.2f);
    }

    // ── Fuel-Air Bomb ───────────────────────────────────────────────────────
    private void renderFuelAirBomb() {
        // Heavy thermobaric tank (deep charcoal with warning hazard stripes)
        drawBox(-0.26f, -0.26f, -0.85f, 0.26f, 0.26f, 0.55f, 0.25f, 0.25f, 0.25f);
        drawBox(-0.27f, -0.27f, -0.2f, 0.27f, 0.27f, 0.0f, 0.85f, 0.2f, 0.15f); // Red hazard band
        // Central dispersion burster & delay igniter
        drawBox(-0.18f, -0.18f, 0.55f, 0.18f, 0.18f, 0.82f, 0.7f, 0.6f, 0.1f);
        // Box tail stabilizing fin assembly
        drawBox(-0.42f, -0.02f, -1.15f, 0.42f, 0.02f, -0.7f, 0.2f, 0.2f, 0.2f);
        drawBox(-0.02f, -0.42f, -1.15f, 0.02f, 0.42f, -0.7f, 0.2f, 0.2f, 0.2f);
    }

    @Override
    protected ResourceLocation getEntityTexture(T entity) {
        return null;
    }
}
