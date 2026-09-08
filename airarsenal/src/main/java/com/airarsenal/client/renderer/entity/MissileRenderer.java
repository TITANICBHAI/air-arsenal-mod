package com.airarsenal.client.renderer.entity;

import com.airarsenal.entity.projectile.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * 3D Renderer for guided missiles, cruise missiles, and rockets in flight.
 * Implements authentic ballistics and aerodynamic flight animations:
 * - Longitudinal axis spin-stabilization (high-speed roll along Z axis)
 * - Dynamic rocket motor exhaust flame flickering with inner white-hot core
 * - BrahMos Mach 3 supersonic ramjet nose shock-cone compression and thermal friction glow
 * - Deploying guidance grid fins unfolding outwards after launch canister separation
 */
public class MissileRenderer<T extends Entity> extends Render<T> {

    public MissileRenderer(RenderManager renderManager) {
        super(renderManager);
        this.shadowSize = 0.3f;
    }

    @Override
    public void doRender(T missile, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y + 0.1, z);

        float yaw = missile.prevRotationYaw + (missile.rotationYaw - missile.prevRotationYaw) * partialTicks;
        float pitch = missile.prevRotationPitch + (missile.rotationPitch - missile.prevRotationPitch) * partialTicks;

        GlStateManager.rotate(180.0f - yaw, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-pitch, 1.0f, 0.0f, 0.0f);

        float animTick = missile.ticksExisted + partialTicks;

        // Fin deployment progress (0.0 to 1.0 over first 6 ticks of flight)
        float deployProgress = Math.min(1.0f, (missile.ticksExisted + partialTicks) / 6.0f);

        GlStateManager.disableTexture2D();
        GlStateManager.enableRescaleNormal();

        if (missile instanceof BrahMosEntity) {
            renderBrahMos(animTick);
        } else if (missile instanceof HellfireEntity) {
            renderHellfire(animTick, deployProgress);
        } else if (missile instanceof ManpadsEntity) {
            renderManpads(animTick, deployProgress);
        } else if (missile instanceof MLRSRocketEntity) {
            renderMLRSRocket(animTick);
        } else if (missile instanceof TruckGuidedMissileEntity) {
            renderTruckMissile(animTick, deployProgress);
        } else {
            renderHellfire(animTick, deployProgress);
        }

        GlStateManager.disableRescaleNormal();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();

        super.doRender(missile, x, y, z, entityYaw, partialTicks);
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

    // ── BrahMos Cruise Missile (Mach 3 Supersonic Ramjet) ───────────────────
    private void renderBrahMos(float animTick) {
        // Slender cylindrical fuselage (titanium white/grey)
        drawBox(-0.16f, -0.16f, -1.5f, 0.16f, 0.16f, 1.1f, 0.85f, 0.87f, 0.88f);

        // Supersonic Ramjet Nose Air Intake & Central Shock Cone
        drawBox(-0.15f, -0.15f, 1.1f, 0.15f, 0.15f, 1.4f, 0.25f, 0.26f, 0.28f);
        // Shock cone glowing slightly from aerodynamic kinetic heating
        drawBox(-0.07f, -0.07f, 1.4f, 0.07f, 0.07f, 1.75f, 0.95f, 0.45f, 0.15f);

        // Cruciform Mid-Body Delta Wings
        drawBox(-0.65f, -0.01f, -0.2f, 0.65f, 0.01f, 0.35f, 0.80f, 0.82f, 0.84f);
        drawBox(-0.01f, -0.65f, -0.2f, 0.01f, 0.65f, 0.35f, 0.80f, 0.82f, 0.84f);

        // Tail Control Fins
        drawBox(-0.45f, -0.01f, -1.45f, 0.45f, 0.01f, -1.15f, 0.3f, 0.3f, 0.3f);
        drawBox(-0.01f, -0.45f, -1.45f, 0.01f, 0.45f, -1.15f, 0.3f, 0.3f, 0.3f);

        // Rocket exhaust nozzle
        drawBox(-0.12f, -0.12f, -1.65f, 0.12f, 0.12f, -1.5f, 0.15f, 0.15f, 0.15f);

        // Dynamic High-Mach Ramjet Exhaust Plume (pulsating flame + white-hot core)
        float flameLength = 0.55f + (float) Math.sin(animTick * 1.8f) * 0.15f;
        drawBox(-0.10f, -0.10f, -1.65f - flameLength, 0.10f, 0.10f, -1.65f, 1.0f, 0.55f, 0.05f);
        drawBox(-0.05f, -0.05f, -1.65f - (flameLength * 0.7f), 0.05f, 0.05f, -1.65f, 1.0f, 0.95f, 0.75f);
    }

    // ── Hellfire Missile ────────────────────────────────────────────────────
    private void renderHellfire(float animTick, float deployProgress) {
        // Cylindrical fuselage (matte olive drab / black)
        drawBox(-0.09f, -0.09f, -0.9f, 0.09f, 0.09f, 0.6f, 0.20f, 0.22f, 0.18f);
        // Laser Seeker Dome
        drawBox(-0.07f, -0.07f, 0.6f, 0.07f, 0.07f, 0.78f, 0.95f, 0.75f, 0.15f);

        // Deploying forward guidance canards
        float canardSpan = 0.25f * deployProgress;
        drawBox(-canardSpan, -0.01f, 0.35f, canardSpan, 0.01f, 0.5f, 0.25f, 0.25f, 0.25f);
        drawBox(-0.01f, -canardSpan, 0.35f, 0.01f, canardSpan, 0.5f, 0.25f, 0.25f, 0.25f);

        // Deploying rear stabilizing fins
        float finSpan = 0.28f * deployProgress;
        drawBox(-finSpan, -0.01f, -0.85f, finSpan, 0.01f, -0.6f, 0.25f, 0.25f, 0.25f);
        drawBox(-0.01f, -finSpan, -0.85f, 0.01f, finSpan, -0.6f, 0.25f, 0.25f, 0.25f);

        // Dynamic rocket motor exhaust
        float flame = 0.32f + (float) Math.sin(animTick * 2.0f) * 0.08f;
        drawBox(-0.06f, -0.06f, -0.9f - flame, 0.06f, 0.06f, -0.9f, 1.0f, 0.6f, 0.1f);
        drawBox(-0.03f, -0.03f, -0.9f - (flame * 0.6f), 0.03f, 0.03f, -0.9f, 1.0f, 0.95f, 0.5f);
    }

    // ── MANPADS Missile (High-RPM Spin Stabilized) ───────────────────────────
    private void renderManpads(float animTick, float deployProgress) {
        // High-speed axial roll spin for stabilization (720 deg/s)
        GlStateManager.pushMatrix();
        GlStateManager.rotate(animTick * 36.0f, 0, 0, 1);

        // Compact missile body (metallic grey)
        drawBox(-0.06f, -0.06f, -0.7f, 0.06f, 0.06f, 0.5f, 0.7f, 0.72f, 0.75f);
        // Infrared Seeker Head (deep sapphire glass)
        drawBox(-0.05f, -0.05f, 0.5f, 0.05f, 0.05f, 0.65f, 0.2f, 0.3f, 0.6f);

        // Forward canards
        float canardSpan = 0.16f * deployProgress;
        drawBox(-canardSpan, -0.01f, 0.32f, canardSpan, 0.01f, 0.42f, 0.3f, 0.3f, 0.3f);
        drawBox(-0.01f, -canardSpan, 0.32f, 0.01f, canardSpan, 0.42f, 0.3f, 0.3f, 0.3f);

        // Rear wrap-around folding spring fins
        float finSpan = 0.20f * deployProgress;
        drawBox(-finSpan, -0.01f, -0.68f, finSpan, 0.01f, -0.52f, 0.3f, 0.3f, 0.3f);
        drawBox(-0.01f, -finSpan, -0.68f, 0.01f, finSpan, -0.52f, 0.3f, 0.3f, 0.3f);

        // Solid Rocket Booster Flame
        float flame = 0.28f + (float) Math.sin(animTick * 2.2f) * 0.07f;
        drawBox(-0.04f, -0.04f, -0.7f - flame, 0.04f, 0.04f, -0.7f, 1.0f, 0.55f, 0.05f);
        drawBox(-0.02f, -0.02f, -0.7f - (flame * 0.6f), 0.02f, 0.02f, -0.7f, 1.0f, 0.95f, 0.8f);

        GlStateManager.popMatrix();
    }

    // ── MLRS Artillery Rocket (High-Speed Spin Stabilized) ───────────────────
    private void renderMLRSRocket(float animTick) {
        // High-speed axial roll (ballistic spin stabilization)
        GlStateManager.pushMatrix();
        GlStateManager.rotate(animTick * 45.0f, 0, 0, 1);

        // Artillery rocket body (military olive)
        drawBox(-0.08f, -0.08f, -1.0f, 0.08f, 0.08f, 0.6f, 0.35f, 0.42f, 0.28f);
        // Conical Warhead (dark steel with fuze tip)
        drawBox(-0.06f, -0.06f, 0.6f, 0.06f, 0.06f, 0.85f, 0.22f, 0.22f, 0.22f);
        drawBox(-0.02f, -0.02f, 0.85f, 0.02f, 0.02f, 0.92f, 0.7f, 0.7f, 0.7f);

        // 4 Helical Curved Tail Fins
        drawBox(-0.25f, -0.01f, -0.98f, 0.25f, 0.01f, -0.75f, 0.25f, 0.25f, 0.25f);
        drawBox(-0.01f, -0.25f, -0.98f, 0.01f, 0.25f, -0.75f, 0.25f, 0.25f, 0.25f);

        // Roaring solid propellant trail plume
        float flame = 0.45f + (float) Math.sin(animTick * 2.5f) * 0.12f;
        drawBox(-0.07f, -0.07f, -1.0f - flame, 0.07f, 0.07f, -1.0f, 1.0f, 0.65f, 0.1f);
        drawBox(-0.035f, -0.035f, -1.0f - (flame * 0.65f), 0.035f, 0.035f, -1.0f, 1.0f, 0.95f, 0.8f);

        GlStateManager.popMatrix();
    }

    // ── Truck Guided Cruise Missile ─────────────────────────────────────────
    private void renderTruckMissile(float animTick, float deployProgress) {
        // Heavy missile body (light tactical grey)
        drawBox(-0.12f, -0.12f, -1.2f, 0.12f, 0.12f, 0.8f, 0.82f, 0.84f, 0.86f);
        // Pointed radar nosecone
        drawBox(-0.08f, -0.08f, 0.8f, 0.08f, 0.08f, 1.15f, 0.25f, 0.25f, 0.28f);

        // Deploying main cruciform cruise flight surfaces
        float wingSpan = 0.45f * deployProgress;
        drawBox(-wingSpan, -0.01f, -0.2f, wingSpan, 0.01f, 0.2f, 0.75f, 0.77f, 0.8f);
        drawBox(-0.01f, -wingSpan, -0.2f, 0.01f, wingSpan, 0.2f, 0.75f, 0.77f, 0.8f);

        // Rear tail steering fins
        float tailSpan = 0.35f * deployProgress;
        drawBox(-tailSpan, -0.01f, -1.15f, tailSpan, 0.01f, -0.9f, 0.3f, 0.3f, 0.3f);
        drawBox(-0.01f, -tailSpan, -1.15f, 0.01f, tailSpan, -0.9f, 0.3f, 0.3f, 0.3f);

        // Rocket booster thrust flame
        float flame = 0.40f + (float) Math.sin(animTick * 1.8f) * 0.10f;
        drawBox(-0.09f, -0.09f, -1.2f - flame, 0.09f, 0.09f, -1.2f, 1.0f, 0.6f, 0.1f);
        drawBox(-0.04f, -0.04f, -1.2f - (flame * 0.6f), 0.04f, 0.04f, -1.2f, 1.0f, 0.95f, 0.7f);
    }

    @Override
    protected ResourceLocation getEntityTexture(T entity) {
        return null;
    }
}
