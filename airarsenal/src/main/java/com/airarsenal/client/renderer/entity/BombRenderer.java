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
 * Implements realistic in-flight ballistics:
 * - High-speed spinning nose & tail arming vanes driven by oncoming airstream
 * - Initial bomb-bay release aerodynamic pitch wobble dampening into streamlined fall
 * - Pulsing electromagnetic induction fields for EMP ordinance
 * - Flashing hydrostatic depth sensor strobe indicators
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

        // Initial release wobble dampening out over first 15 ticks
        float animTick = bomb.ticksExisted + partialTicks;
        float wobble = (float) (Math.sin(animTick * 1.4) * Math.exp(-animTick * 0.12) * 8.0);

        GlStateManager.rotate(180.0f - yaw, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-pitch + wobble, 1.0f, 0.0f, 0.0f);

        GlStateManager.disableTexture2D();
        GlStateManager.enableRescaleNormal();

        if (bomb instanceof HeavyBombEntity) {
            renderHeavyBomb(animTick);
        } else if (bomb instanceof NapalmBombEntity) {
            renderNapalmBomb(animTick);
        } else if (bomb instanceof EMPBombEntity) {
            renderEMPBomb(animTick);
        } else if (bomb instanceof DepthChargeEntity) {
            renderDepthCharge(animTick);
        } else if (bomb instanceof ClusterBombEntity) {
            renderClusterBomb(animTick);
        } else if (bomb instanceof SmokeBombEntity) {
            renderSmokeBomb(animTick);
        } else if (bomb instanceof FuelAirBombEntity) {
            renderFuelAirBomb(animTick);
        } else {
            renderIronBomb(animTick);
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
    private void renderIronBomb(float animTick) {
        // Cylindrical main body (olive drab)
        drawBox(-0.2f, -0.2f, -0.6f, 0.2f, 0.2f, 0.35f, 0.35f, 0.40f, 0.25f);
        // Rounded ogive nose
        drawBox(-0.15f, -0.15f, 0.35f, 0.15f, 0.15f, 0.55f, 0.30f, 0.35f, 0.22f);

        // High-speed spinning nose arming vane
        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0, 0.55f);
        GlStateManager.rotate(animTick * 65.0f, 0, 0, 1);
        drawBox(-0.02f, -0.02f, 0.0f, 0.02f, 0.02f, 0.12f, 0.85f, 0.75f, 0.25f); // brass spindle
        drawBox(-0.12f, -0.01f, 0.08f, 0.12f, 0.01f, 0.10f, 0.9f, 0.9f, 0.9f);  // arming impeller
        GlStateManager.popMatrix();

        // 4 Cruciform Tail Fins
        drawBox(-0.35f, -0.01f, -0.85f, 0.35f, 0.01f, -0.45f, 0.2f, 0.2f, 0.2f);
        drawBox(-0.01f, -0.35f, -0.85f, 0.01f, 0.35f, -0.45f, 0.2f, 0.2f, 0.2f);
    }

    // ── Heavy Bomb ──────────────────────────────────────────────────────────
    private void renderHeavyBomb(float animTick) {
        // Massive cylindrical body (dark military olive)
        drawBox(-0.28f, -0.28f, -0.9f, 0.28f, 0.28f, 0.55f, 0.32f, 0.36f, 0.26f);
        // Heavy rounded nose
        drawBox(-0.22f, -0.22f, 0.55f, 0.22f, 0.22f, 0.85f, 0.25f, 0.28f, 0.20f);

        // Heavy arming impeller spinner
        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0, 0.85f);
        GlStateManager.rotate(animTick * 55.0f, 0, 0, 1);
        drawBox(-0.03f, -0.03f, 0.0f, 0.03f, 0.03f, 0.15f, 0.8f, 0.8f, 0.8f);
        drawBox(-0.16f, -0.015f, 0.1f, 0.16f, 0.015f, 0.12f, 0.85f, 0.75f, 0.2f);
        GlStateManager.popMatrix();

        // Tail Fins and Shroud Ring
        drawBox(-0.45f, -0.02f, -1.25f, 0.45f, 0.02f, -0.7f, 0.25f, 0.25f, 0.25f);
        drawBox(-0.02f, -0.45f, -1.25f, 0.02f, 0.45f, -0.7f, 0.25f, 0.25f, 0.25f);
        // Circular Tail Shroud Ring
        drawBox(-0.32f, -0.32f, -1.28f, 0.32f, 0.32f, -1.15f, 0.22f, 0.22f, 0.22f);
    }

    // ── Napalm Bomb ─────────────────────────────────────────────────────────
    private void renderNapalmBomb(float animTick) {
        // Incendiary Tank (bright warning orange)
        drawBox(-0.2f, -0.2f, -0.75f, 0.2f, 0.2f, 0.45f, 0.92f, 0.38f, 0.12f);
        // Nose igniter cap (crimson red)
        drawBox(-0.14f, -0.14f, 0.45f, 0.14f, 0.14f, 0.68f, 0.85f, 0.15f, 0.15f);

        // Nose impact detonator spinner
        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0, 0.68f);
        GlStateManager.rotate(animTick * 70.0f, 0, 0, 1);
        drawBox(-0.09f, -0.01f, 0.0f, 0.09f, 0.01f, 0.04f, 0.95f, 0.95f, 0.95f);
        GlStateManager.popMatrix();

        // Rear fins
        drawBox(-0.38f, -0.01f, -0.98f, 0.38f, 0.01f, -0.6f, 0.2f, 0.2f, 0.2f);
        drawBox(-0.01f, -0.38f, -0.98f, 0.01f, 0.38f, -0.6f, 0.2f, 0.2f, 0.2f);
    }

    // ── EMP Bomb ────────────────────────────────────────────────────────────
    private void renderEMPBomb(float animTick) {
        // Tech casing (matte dark titanium)
        drawBox(-0.18f, -0.18f, -0.7f, 0.18f, 0.18f, 0.4f, 0.22f, 0.24f, 0.28f);

        // Pulsating electromagnetic induction coils
        float glow = 0.65f + (float) Math.sin(animTick * 0.45f) * 0.35f;
        drawBox(-0.21f, -0.21f, -0.3f, 0.21f, 0.21f, -0.1f, 0.05f * glow, 0.75f * glow, 1.0f * glow);
        drawBox(-0.21f, -0.21f, 0.1f, 0.21f, 0.21f, 0.3f, 0.05f * glow, 0.75f * glow, 1.0f * glow);

        // Nose emitter probe
        drawBox(-0.05f, -0.05f, 0.4f, 0.05f, 0.05f, 0.75f, 0.7f, 0.75f, 0.8f);

        // Tail stabilizers
        drawBox(-0.32f, -0.01f, -0.92f, 0.32f, 0.01f, -0.55f, 0.25f, 0.28f, 0.32f);
        drawBox(-0.01f, -0.32f, -0.92f, 0.01f, 0.32f, -0.55f, 0.25f, 0.28f, 0.32f);
    }

    // ── Depth Charge ────────────────────────────────────────────────────────
    private void renderDepthCharge(float animTick) {
        // Cylindrical barrel drum (heavy naval steel)
        drawBox(-0.25f, -0.25f, -0.6f, 0.25f, 0.25f, 0.6f, 0.22f, 0.25f, 0.28f);

        // Hydrostatic armed strobe beacon (flashes red/green)
        boolean strobe = (animTick % 10.0f) < 5.0f;
        float sr = strobe ? 1.0f : 0.2f;
        float sg = strobe ? 0.2f : 0.0f;
        drawBox(-0.08f, 0.25f, -0.1f, 0.08f, 0.36f, 0.1f, sr, sg, 0.1f);

        // Hydrostatic tail shroud ring
        drawBox(-0.3f, -0.3f, -0.85f, 0.3f, 0.3f, -0.6f, 0.18f, 0.18f, 0.18f);
    }

    // ── Cluster Bomb ────────────────────────────────────────────────────────
    private void renderClusterBomb(float animTick) {
        // Green dispenser body
        drawBox(-0.2f, -0.2f, -0.8f, 0.2f, 0.2f, 0.5f, 0.25f, 0.38f, 0.22f);
        // Clamshell release seams
        drawBox(-0.21f, -0.21f, -0.1f, 0.21f, 0.21f, 0.0f, 0.15f, 0.15f, 0.15f);
        // Nose cone
        drawBox(-0.15f, -0.15f, 0.5f, 0.15f, 0.15f, 0.72f, 0.22f, 0.22f, 0.22f);

        // Nose radar proximity fuze spinner
        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0, 0.72f);
        GlStateManager.rotate(animTick * 60.0f, 0, 0, 1);
        drawBox(-0.08f, -0.01f, 0.0f, 0.08f, 0.01f, 0.04f, 0.9f, 0.9f, 0.9f);
        GlStateManager.popMatrix();

        // Tail assembly
        drawBox(-0.35f, -0.01f, -1.05f, 0.35f, 0.01f, -0.65f, 0.2f, 0.2f, 0.2f);
        drawBox(-0.01f, -0.35f, -1.05f, 0.01f, 0.35f, -0.65f, 0.2f, 0.2f, 0.2f);
    }

    // ── Smoke Bomb ──────────────────────────────────────────────────────────
    private void renderSmokeBomb(float animTick) {
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
    private void renderFuelAirBomb(float animTick) {
        // Heavy thermobaric tank (deep charcoal with warning hazard stripes)
        drawBox(-0.26f, -0.26f, -0.85f, 0.26f, 0.26f, 0.55f, 0.25f, 0.25f, 0.25f);
        drawBox(-0.27f, -0.27f, -0.2f, 0.27f, 0.27f, 0.0f, 0.85f, 0.2f, 0.15f); // Red hazard band

        // Dispersion burster nose spinner
        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0, 0.82f);
        GlStateManager.rotate(animTick * 60.0f, 0, 0, 1);
        drawBox(-0.12f, -0.015f, 0.0f, 0.12f, 0.015f, 0.05f, 0.95f, 0.85f, 0.2f);
        GlStateManager.popMatrix();

        // Box tail stabilizing fin assembly
        drawBox(-0.42f, -0.02f, -1.15f, 0.42f, 0.02f, -0.7f, 0.2f, 0.2f, 0.2f);
        drawBox(-0.02f, -0.42f, -1.15f, 0.02f, 0.42f, -0.7f, 0.2f, 0.2f, 0.2f);
    }

    @Override
    protected ResourceLocation getEntityTexture(T entity) {
        return null;
    }
}
