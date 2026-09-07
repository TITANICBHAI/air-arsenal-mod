package com.airarsenal.client.renderer.entity;

import com.airarsenal.entity.projectile.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * 3D Renderer for shells, artillery projectiles, bullets, and propeller shards.
 */
public class ShellRenderer<T extends Entity> extends Render<T> {

    public ShellRenderer(RenderManager renderManager) {
        super(renderManager);
        this.shadowSize = 0.1f;
    }

    @Override
    public void doRender(T projectile, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y + 0.05, z);

        double hSpeed = Math.sqrt(projectile.motionX * projectile.motionX + projectile.motionZ * projectile.motionZ);
        float yaw = (float) (Math.atan2(projectile.motionX, projectile.motionZ) * (180.0 / Math.PI));
        float pitch = (float) (Math.atan2(projectile.motionY, Math.max(hSpeed, 0.001)) * (180.0 / Math.PI));

        GlStateManager.rotate(180.0f - yaw, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-pitch, 1.0f, 0.0f, 0.0f);

        GlStateManager.disableTexture2D();
        GlStateManager.enableRescaleNormal();

        if (projectile instanceof BulletEntity) {
            renderBullet();
        } else if (projectile instanceof AAShellEntity) {
            renderAAShell();
        } else if (projectile instanceof FlakShellEntity) {
            renderFlakShell();
        } else if (projectile instanceof MortarShellEntity) {
            renderMortarShell();
        } else if (projectile instanceof HowitzerShellEntity) {
            renderHowitzerShell();
        } else if (projectile instanceof TankShellEntity) {
            renderTankShell();
        } else if (projectile instanceof PropellerShardEntity) {
            renderPropellerShard(projectile.ticksExisted + partialTicks);
        } else {
            renderBullet();
        }

        GlStateManager.disableRescaleNormal();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();

        super.doRender(projectile, x, y, z, entityYaw, partialTicks);
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

    // ── Machine Gun Bullet ──────────────────────────────────────────────────
    private void renderBullet() {
        // High-speed copper/golden tracer slug
        drawBox(-0.03f, -0.03f, -0.25f, 0.03f, 0.03f, 0.25f, 0.95f, 0.75f, 0.2f);
    }

    // ── AA Tracer Round ─────────────────────────────────────────────────────
    private void renderAAShell() {
        // Glowing high-visibility anti-aircraft tracer
        drawBox(-0.05f, -0.05f, -0.4f, 0.05f, 0.05f, 0.4f, 1.0f, 0.35f, 0.1f);
    }

    // ── 88mm Flak Shell ─────────────────────────────────────────────────────
    private void renderFlakShell() {
        // Cylindrical steel shell body
        drawBox(-0.08f, -0.08f, -0.4f, 0.08f, 0.08f, 0.25f, 0.3f, 0.32f, 0.35f);
        // Ogive nose cone with mechanical time fuze
        drawBox(-0.06f, -0.06f, 0.25f, 0.06f, 0.06f, 0.5f, 0.85f, 0.75f, 0.25f);
    }

    // ── Mortar Shell ────────────────────────────────────────────────────────
    private void renderMortarShell() {
        // Teardrop aerodynamic body (olive drab)
        drawBox(-0.1f, -0.1f, -0.15f, 0.1f, 0.1f, 0.35f, 0.35f, 0.42f, 0.25f);
        // Pointed nose impact fuze
        drawBox(-0.04f, -0.04f, 0.35f, 0.04f, 0.04f, 0.48f, 0.75f, 0.75f, 0.75f);
        // Thin tail boom
        drawBox(-0.04f, -0.04f, -0.45f, 0.04f, 0.04f, -0.15f, 0.25f, 0.25f, 0.25f);
        // Tail stabilizing fins
        drawBox(-0.18f, -0.01f, -0.52f, 0.18f, 0.01f, -0.32f, 0.2f, 0.2f, 0.2f);
        drawBox(-0.01f, -0.18f, -0.52f, 0.01f, 0.18f, -0.32f, 0.2f, 0.2f, 0.2f);
    }

    // ── 155mm Howitzer Shell ────────────────────────────────────────────────
    private void renderHowitzerShell() {
        // Heavy artillery projectile
        drawBox(-0.12f, -0.12f, -0.55f, 0.12f, 0.12f, 0.25f, 0.35f, 0.38f, 0.42f);
        // Copper driving band
        drawBox(-0.13f, -0.13f, -0.45f, 0.13f, 0.13f, -0.35f, 0.85f, 0.55f, 0.2f);
        // Streamlined ogive nose & radar proximity / impact fuze
        drawBox(-0.08f, -0.08f, 0.25f, 0.08f, 0.08f, 0.65f, 0.25f, 0.25f, 0.25f);
    }

    // ── Tank APFSDS Shell ───────────────────────────────────────────────────
    private void renderTankShell() {
        // Kinetic penetrator dart
        drawBox(-0.04f, -0.04f, -0.6f, 0.04f, 0.04f, 0.6f, 0.22f, 0.22f, 0.25f);
        // Penetrator tip
        drawBox(-0.02f, -0.02f, 0.6f, 0.02f, 0.02f, 0.8f, 0.85f, 0.85f, 0.9f);
        // Rear stabilizing fins
        drawBox(-0.15f, -0.01f, -0.62f, 0.15f, 0.01f, -0.45f, 0.2f, 0.2f, 0.2f);
        drawBox(-0.01f, -0.15f, -0.62f, 0.01f, 0.15f, -0.45f, 0.2f, 0.2f, 0.2f);
    }

    // ── Tumbling Propeller Shard ────────────────────────────────────────────
    private void renderPropellerShard(float animTick) {
        GlStateManager.rotate(animTick * 35.0f, 1, 0, 1);
        // Jagged metal/wood blade fragment
        drawBox(-0.04f, -0.02f, -0.35f, 0.04f, 0.02f, 0.35f, 0.45f, 0.45f, 0.48f);
    }

    @Override
    protected ResourceLocation getEntityTexture(T entity) {
        return null;
    }
}
