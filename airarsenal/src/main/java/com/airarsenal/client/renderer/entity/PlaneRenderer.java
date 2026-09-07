package com.airarsenal.client.renderer.entity;

import com.airarsenal.entity.plane.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * 3D Renderer for all BasePlaneEntity aircraft in Air Arsenal.
 * Renders distinct airframe geometry, color palettes, and animated rotors/propellers.
 */
public class PlaneRenderer<T extends BasePlaneEntity> extends Render<T> {

    public PlaneRenderer(RenderManager renderManager) {
        super(renderManager);
        this.shadowSize = 1.2f;
    }

    @Override
    public void doRender(T plane, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y + 0.35, z);

        // Yaw and Pitch rotation from entity
        float yaw = plane.prevRotationYaw + (plane.rotationYaw - plane.prevRotationYaw) * partialTicks;
        float pitch = plane.prevRotationPitch + (plane.rotationPitch - plane.prevRotationPitch) * partialTicks;
        GlStateManager.rotate(180.0f - yaw, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-pitch, 1.0f, 0.0f, 0.0f);

        GlStateManager.disableTexture2D();
        GlStateManager.enableRescaleNormal();

        String type = plane.getPlaneType();
        float animTick = plane.ticksExisted + partialTicks;

        if ("wood_biplane".equals(type)) {
            renderWoodBiplane(animTick);
        } else if ("iron_monoplane".equals(type)) {
            renderIronMonoplane(animTick);
        } else if ("fighter_jet".equals(type)) {
            renderFighterJet(animTick);
        } else if ("stealth_bomber".equals(type)) {
            renderStealthBomber();
        } else if ("predator_drone".equals(type)) {
            renderPredatorDrone(animTick);
        } else if ("attack_helicopter".equals(type)) {
            renderAttackHelicopter(animTick);
        } else {
            renderGenericPlane(animTick);
        }

        GlStateManager.disableRescaleNormal();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();

        super.doRender(plane, x, y, z, entityYaw, partialTicks);
    }

    // ── Helper Box Drawer ───────────────────────────────────────────────────
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

    // ── Wood Biplane ────────────────────────────────────────────────────────
    private void renderWoodBiplane(float animTick) {
        // Fuselage (warm wood brown)
        drawBox(-0.35f, -0.2f, -1.2f, 0.35f, 0.45f, 1.0f, 0.48f, 0.32f, 0.18f);
        // Engine cowl (dark iron)
        drawBox(-0.32f, -0.15f, 1.0f, 0.32f, 0.4f, 1.25f, 0.25f, 0.25f, 0.25f);
        // Open cockpit (dark cavity)
        drawBox(-0.25f, 0.45f, -0.2f, 0.25f, 0.48f, 0.3f, 0.15f, 0.15f, 0.15f);

        // Lower Wing (linen canvas cream)
        drawBox(-2.2f, -0.15f, -0.1f, 2.2f, -0.05f, 0.7f, 0.82f, 0.78f, 0.65f);
        // Upper Wing
        drawBox(-2.2f, 0.75f, -0.1f, 2.2f, 0.85f, 0.7f, 0.82f, 0.78f, 0.65f);

        // Wing Struts (left and right)
        drawBox(-1.6f, -0.05f, 0.1f, -1.55f, 0.75f, 0.5f, 0.35f, 0.22f, 0.12f);
        drawBox(1.55f, -0.05f, 0.1f, 1.6f, 0.75f, 0.5f, 0.35f, 0.22f, 0.12f);

        // Tail vertical stabilizer & rudder
        drawBox(-0.04f, 0.45f, -1.45f, 0.04f, 1.05f, -1.0f, 0.48f, 0.32f, 0.18f);
        // Tail horizontal elevators
        drawBox(-0.85f, 0.35f, -1.45f, 0.85f, 0.42f, -1.05f, 0.82f, 0.78f, 0.65f);

        // Animated Propeller
        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0.12f, 1.27f);
        GlStateManager.rotate(animTick * 45.0f, 0, 0, 1);
        drawBox(-0.1f, -0.1f, 0.0f, 0.1f, 0.1f, 0.08f, 0.7f, 0.7f, 0.7f); // hub
        drawBox(-0.85f, -0.05f, 0.03f, 0.85f, 0.05f, 0.05f, 0.35f, 0.20f, 0.10f); // blades
        GlStateManager.popMatrix();
    }

    // ── Iron Monoplane ──────────────────────────────────────────────────────
    private void renderIronMonoplane(float animTick) {
        // Fuselage (metallic iron gray)
        drawBox(-0.4f, -0.2f, -1.4f, 0.4f, 0.5f, 1.1f, 0.65f, 0.68f, 0.70f);
        // Nose engine cowling (darker brushed steel)
        drawBox(-0.35f, -0.15f, 1.1f, 0.35f, 0.45f, 1.35f, 0.38f, 0.40f, 0.42f);
        // Glass windshield
        drawBox(-0.28f, 0.5f, 0.1f, 0.28f, 0.72f, 0.45f, 0.35f, 0.55f, 0.75f);

        // Monoplane Main Wing (streamlined aluminum)
        drawBox(-2.5f, 0.1f, -0.2f, 2.5f, 0.22f, 0.65f, 0.62f, 0.65f, 0.68f);

        // Nose Mounted LMG Gun Barrel
        drawBox(-0.06f, 0.48f, 0.6f, 0.06f, 0.58f, 1.55f, 0.15f, 0.15f, 0.15f);

        // Tail
        drawBox(-0.05f, 0.5f, -1.65f, 0.05f, 1.1f, -1.15f, 0.65f, 0.68f, 0.70f);
        drawBox(-0.95f, 0.35f, -1.65f, 0.95f, 0.42f, -1.25f, 0.62f, 0.65f, 0.68f);

        // Animated Propeller
        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0.15f, 1.38f);
        GlStateManager.rotate(animTick * 50.0f, 0, 0, 1);
        drawBox(-0.08f, -0.08f, 0f, 0.08f, 0.08f, 0.1f, 0.8f, 0.8f, 0.8f);
        drawBox(-0.9f, -0.04f, 0.03f, 0.9f, 0.04f, 0.05f, 0.2f, 0.2f, 0.2f);
        GlStateManager.popMatrix();
    }

    // ── Fighter Jet ─────────────────────────────────────────────────────────
    private void renderFighterJet(float animTick) {
        // Needle nose radome
        drawBox(-0.18f, 0.0f, 1.4f, 0.18f, 0.28f, 2.2f, 0.28f, 0.30f, 0.33f);
        // Cockpit canopy (tinted reflective blue/black)
        drawBox(-0.25f, 0.28f, 0.4f, 0.25f, 0.6f, 1.3f, 0.2f, 0.45f, 0.65f);
        // Fuselage body (military air superiority gray)
        drawBox(-0.55f, -0.15f, -1.5f, 0.55f, 0.35f, 1.4f, 0.35f, 0.38f, 0.42f);

        // Delta wings (swept back)
        drawBox(-2.4f, 0.0f, -1.3f, 2.4f, 0.1f, 0.1f, 0.33f, 0.36f, 0.40f);

        // Twin Canted Vertical Fins
        drawBox(-0.48f, 0.35f, -1.6f, -0.4f, 1.25f, -0.9f, 0.33f, 0.36f, 0.40f);
        drawBox(0.4f, 0.35f, -1.6f, 0.48f, 1.25f, -0.9f, 0.33f, 0.36f, 0.40f);

        // Underwing missile rails
        drawBox(-1.6f, -0.08f, -0.8f, -1.52f, 0.0f, 0.0f, 0.2f, 0.2f, 0.2f);
        drawBox(1.52f, -0.08f, -0.8f, 1.6f, 0.0f, 0.0f, 0.2f, 0.2f, 0.2f);

        // Twin Afterburner Exhausts (glowing interior)
        drawBox(-0.45f, -0.08f, -1.75f, -0.12f, 0.25f, -1.5f, 0.2f, 0.2f, 0.2f);
        drawBox(0.12f, -0.08f, -1.75f, 0.45f, 0.25f, -1.5f, 0.2f, 0.2f, 0.2f);
        // Jet flame plume
        float flameSize = 0.15f + (float) Math.sin(animTick * 0.8f) * 0.05f;
        drawBox(-0.4f, -0.03f, -1.75f - flameSize, -0.17f, 0.2f, -1.75f, 1.0f, 0.6f, 0.1f);
        drawBox(0.17f, -0.03f, -1.75f - flameSize, 0.4f, 0.2f, -1.75f, 1.0f, 0.6f, 0.1f);
    }

    // ── Stealth Bomber ──────────────────────────────────────────────────────
    private void renderStealthBomber() {
        // Faceted center fuselage (deep matte stealth black)
        drawBox(-0.75f, -0.12f, -0.7f, 0.75f, 0.32f, 1.5f, 0.12f, 0.13f, 0.15f);
        // Cockpit narrow stealth slits
        drawBox(-0.35f, 0.32f, 0.6f, 0.35f, 0.42f, 1.1f, 0.22f, 0.24f, 0.28f);

        // Huge angular flying wing panels
        drawBox(-3.5f, -0.08f, -1.6f, -0.75f, 0.12f, 0.3f, 0.14f, 0.15f, 0.17f);
        drawBox(0.75f, -0.08f, -1.6f, 3.5f, 0.12f, 0.3f, 0.14f, 0.15f, 0.17f);

        // Serrated rear trailing edge and engine intake bays
        drawBox(-0.55f, 0.12f, -1.2f, -0.2f, 0.28f, -0.4f, 0.08f, 0.08f, 0.09f);
        drawBox(0.2f, 0.12f, -1.2f, 0.55f, 0.28f, -0.4f, 0.08f, 0.08f, 0.09f);
    }

    // ── Predator Drone ──────────────────────────────────────────────────────
    private void renderPredatorDrone(float animTick) {
        // Slender UAV body (tactical light grey)
        drawBox(-0.25f, -0.15f, -1.2f, 0.25f, 0.35f, 1.2f, 0.70f, 0.72f, 0.74f);
        // Forward satellite communication bulb
        drawBox(-0.28f, 0.2f, 0.4f, 0.28f, 0.52f, 1.1f, 0.72f, 0.74f, 0.76f);
        // Electro-optical camera ball turret underneath
        drawBox(-0.16f, -0.32f, 0.8f, 0.16f, -0.15f, 1.12f, 0.2f, 0.2f, 0.25f);

        // Long High-Aspect Glider Wings
        drawBox(-3.0f, 0.1f, -0.1f, 3.0f, 0.18f, 0.4f, 0.68f, 0.70f, 0.72f);

        // Inverted V-tail stabilizers
        drawBox(-0.45f, -0.45f, -1.45f, -0.35f, 0.25f, -1.15f, 0.68f, 0.70f, 0.72f);
        drawBox(0.35f, -0.45f, -1.45f, 0.45f, 0.25f, -1.15f, 0.68f, 0.70f, 0.72f);

        // Rear Pusher Propeller
        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0.1f, -1.25f);
        GlStateManager.rotate(animTick * 40.0f, 0, 0, 1);
        drawBox(-0.06f, -0.06f, -0.06f, 0.06f, 0.06f, 0.0f, 0.3f, 0.3f, 0.3f);
        drawBox(-0.55f, -0.03f, -0.04f, 0.55f, 0.03f, -0.02f, 0.15f, 0.15f, 0.15f);
        GlStateManager.popMatrix();
    }

    // ── Attack Helicopter ───────────────────────────────────────────────────
    private void renderAttackHelicopter(float animTick) {
        // Military Gunship Fuselage (camo olive drab)
        drawBox(-0.38f, -0.2f, -1.0f, 0.38f, 0.55f, 0.9f, 0.28f, 0.36f, 0.22f);
        // Stepped Tandem Cockpit
        drawBox(-0.3f, 0.2f, 0.5f, 0.3f, 0.75f, 1.45f, 0.2f, 0.45f, 0.65f);
        // Tail Boom
        drawBox(-0.15f, 0.1f, -2.4f, 0.15f, 0.42f, -1.0f, 0.28f, 0.36f, 0.22f);
        // Tail Vertical Fin
        drawBox(-0.05f, 0.2f, -2.65f, 0.05f, 1.0f, -2.25f, 0.28f, 0.36f, 0.22f);

        // Chin Turret (3-barrel Minigun)
        drawBox(-0.12f, -0.35f, 1.2f, 0.12f, -0.15f, 1.55f, 0.2f, 0.2f, 0.2f);
        drawBox(-0.04f, -0.3f, 1.55f, 0.04f, -0.22f, 2.1f, 0.1f, 0.1f, 0.1f);

        // Stub Weapon Wings
        drawBox(-1.3f, 0.08f, -0.2f, 1.3f, 0.18f, 0.3f, 0.25f, 0.32f, 0.20f);
        // Rocket Pods
        drawBox(-1.15f, -0.1f, -0.35f, -0.85f, 0.15f, 0.45f, 0.2f, 0.2f, 0.2f);
        drawBox(0.85f, -0.1f, -0.35f, 1.15f, 0.15f, 0.45f, 0.2f, 0.2f, 0.2f);

        // Landing Skids
        drawBox(-0.55f, -0.42f, -0.8f, -0.45f, -0.35f, 1.0f, 0.18f, 0.18f, 0.18f);
        drawBox(0.45f, -0.42f, -0.8f, 0.55f, -0.35f, 1.0f, 0.18f, 0.18f, 0.18f);

        // Spinning Main 4-Blade Rotor
        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0.72f, 0.0f);
        GlStateManager.rotate(animTick * 60.0f, 0, 1, 0);
        drawBox(-0.12f, 0f, -0.12f, 0.12f, 0.15f, 0.12f, 0.2f, 0.2f, 0.2f); // rotor hub
        drawBox(-2.8f, 0.08f, -0.08f, 2.8f, 0.12f, 0.08f, 0.12f, 0.12f, 0.12f); // blade 1-2
        drawBox(-0.08f, 0.08f, -2.8f, 0.08f, 0.12f, 2.8f, 0.12f, 0.12f, 0.12f); // blade 3-4
        GlStateManager.popMatrix();

        // Spinning Tail Rotor
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.12f, 0.75f, -2.45f);
        GlStateManager.rotate(animTick * 70.0f, 1, 0, 0);
        drawBox(-0.05f, -0.6f, -0.04f, 0.02f, 0.6f, 0.04f, 0.15f, 0.15f, 0.15f);
        GlStateManager.popMatrix();
    }

    private void renderGenericPlane(float animTick) {
        renderIronMonoplane(animTick);
    }

    @Override
    protected ResourceLocation getEntityTexture(T entity) {
        return null;
    }
}
