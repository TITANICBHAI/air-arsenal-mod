package com.airarsenal.client.renderer.entity;

import com.airarsenal.entity.plane.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * 3D Renderer for all BasePlaneEntity aircraft in Air Arsenal.
 * Implements realistic aeronautical flight physics and animations:
 * - Bank / roll into turns based on yaw turn rate
 * - Throttle-responsive propeller RPM with dynamic semi-transparent motion blur discs
 * - Dynamic hinged aileron, elevator, and rudder deflections
 * - Attack Helicopter cyclic rotor disc tilt, tail anti-torque rotor, and aiming chin minigun
 * - Fighter Jet variable afterburner plumes with supersonic Mach shock diamonds and wingtip vapor
 * - Stealth Bomber operable bomb bay doors with loaded internal munition carousel
 * - Predator Drone dual-axis electro-optical surveillance gimbal active target panning
 * - Rolling landing gear wheels and compression suspension
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

        // Dynamic aeronautical banking (roll) into coordinated turns
        float yawDelta = plane.rotationYaw - plane.prevRotationYaw;
        while (yawDelta > 180.0f) yawDelta -= 360.0f;
        while (yawDelta < -180.0f) yawDelta += 360.0f;
        float bankRoll = Math.max(-45.0f, Math.min(45.0f, yawDelta * 4.2f));

        GlStateManager.rotate(180.0f - yaw, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-pitch, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate(bankRoll, 0.0f, 0.0f, 1.0f);

        GlStateManager.disableTexture2D();
        GlStateManager.enableRescaleNormal();

        String type = plane.getPlaneType();
        float animTick = plane.ticksExisted + partialTicks;
        float planeSpeed = plane.getSpeed();
        float wheelRot = (animTick * (planeSpeed > 0.05f ? planeSpeed * 10.0f : 0.0f)) * 0.4f;

        if ("wood_biplane".equals(type)) {
            renderWoodBiplane(plane, animTick, planeSpeed, bankRoll, pitch, yawDelta, wheelRot);
        } else if ("iron_monoplane".equals(type)) {
            renderIronMonoplane(plane, animTick, planeSpeed, bankRoll, pitch, yawDelta, wheelRot);
        } else if ("fighter_jet".equals(type)) {
            renderFighterJet(plane, animTick, planeSpeed, bankRoll, pitch, yawDelta);
        } else if ("stealth_bomber".equals(type)) {
            renderStealthBomber(plane, animTick, bankRoll, pitch);
        } else if ("predator_drone".equals(type)) {
            renderPredatorDrone(plane, animTick, planeSpeed, pitch, yawDelta);
        } else if ("attack_helicopter".equals(type)) {
            renderAttackHelicopter(plane, animTick, planeSpeed, bankRoll, pitch);
        } else {
            renderIronMonoplane(plane, animTick, planeSpeed, bankRoll, pitch, yawDelta, wheelRot);
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

    private static void drawDisk(float radius, float r, float g, float b, float alpha) {
        GlStateManager.enableBlend();
        GlStateManager.disableCull();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(r, g, b, alpha);
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        GL11.glVertex3f(0.0f, 0.0f, 0.0f);
        int segments = 16;
        for (int i = 0; i <= segments; i++) {
            double angle = i * (2.0 * Math.PI / segments);
            GL11.glVertex3f((float) (Math.cos(angle) * radius), (float) (Math.sin(angle) * radius), 0.0f);
        }
        GL11.glEnd();
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
    }

    // ── Wood Biplane ────────────────────────────────────────────────────────
    private void renderWoodBiplane(T plane, float animTick, float speed, float bankRoll,
                                   float pitch, float yawDelta, float wheelRot) {
        // Fuselage (warm wood brown)
        drawBox(-0.35f, -0.2f, -1.2f, 0.35f, 0.45f, 1.0f, 0.48f, 0.32f, 0.18f);
        // Engine cowl (dark iron)
        drawBox(-0.32f, -0.15f, 1.0f, 0.32f, 0.4f, 1.25f, 0.25f, 0.25f, 0.25f);
        // Open cockpit (dark cavity)
        drawBox(-0.25f, 0.45f, -0.2f, 0.25f, 0.48f, 0.3f, 0.15f, 0.15f, 0.15f);

        // Lower Wing (linen canvas cream)
        drawBox(-2.2f, -0.15f, -0.1f, 2.2f, -0.05f, 0.7f, 0.82f, 0.78f, 0.65f);
        // Upper Wing Center
        drawBox(-1.2f, 0.75f, -0.1f, 1.2f, 0.85f, 0.7f, 0.82f, 0.78f, 0.65f);

        // Dynamic Left Aileron (deflects up/down with roll)
        float aileronAngle = bankRoll * 0.55f;
        GlStateManager.pushMatrix();
        GlStateManager.translate(-1.7f, 0.8f, -0.1f);
        GlStateManager.rotate(-aileronAngle, 1, 0, 0);
        drawBox(-0.5f, -0.05f, 0.0f, 0.5f, 0.05f, 0.8f, 0.80f, 0.76f, 0.62f);
        GlStateManager.popMatrix();

        // Dynamic Right Aileron (deflects opposite for aerodynamic torque)
        GlStateManager.pushMatrix();
        GlStateManager.translate(1.7f, 0.8f, -0.1f);
        GlStateManager.rotate(aileronAngle, 1, 0, 0);
        drawBox(-0.5f, -0.05f, 0.0f, 0.5f, 0.05f, 0.8f, 0.80f, 0.76f, 0.62f);
        GlStateManager.popMatrix();

        // Wing Struts (left and right)
        drawBox(-1.6f, -0.05f, 0.1f, -1.55f, 0.75f, 0.5f, 0.35f, 0.22f, 0.12f);
        drawBox(1.55f, -0.05f, 0.1f, 1.6f, 0.75f, 0.5f, 0.35f, 0.22f, 0.12f);

        // Tail vertical fin
        drawBox(-0.04f, 0.45f, -1.45f, 0.04f, 1.05f, -1.0f, 0.48f, 0.32f, 0.18f);

        // Dynamic Rudder (yaw deflection)
        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0.75f, -1.45f);
        GlStateManager.rotate(-yawDelta * 2.2f, 0, 1, 0);
        drawBox(-0.03f, -0.28f, -0.35f, 0.03f, 0.28f, 0.0f, 0.42f, 0.28f, 0.15f);
        GlStateManager.popMatrix();

        // Dynamic Tail Elevator (pitch trim)
        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0.38f, -1.25f);
        GlStateManager.rotate(-pitch * 0.45f, 1, 0, 0);
        drawBox(-0.85f, -0.03f, -0.4f, 0.85f, 0.03f, 0.0f, 0.80f, 0.76f, 0.62f);
        GlStateManager.popMatrix();

        // Landing Gear Struts & Rolling Wheels
        drawBox(-0.55f, -0.55f, 0.3f, -0.45f, -0.2f, 0.5f, 0.25f, 0.25f, 0.25f);
        drawBox(0.45f, -0.55f, 0.3f, 0.55f, -0.2f, 0.5f, 0.25f, 0.25f, 0.25f);
        GlStateManager.pushMatrix();
        GlStateManager.translate(-0.5f, -0.55f, 0.4f);
        GlStateManager.rotate(wheelRot, 1, 0, 0);
        drawBox(-0.08f, -0.16f, -0.16f, 0.08f, 0.16f, 0.16f, 0.12f, 0.12f, 0.12f);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.5f, -0.55f, 0.4f);
        GlStateManager.rotate(wheelRot, 1, 0, 0);
        drawBox(-0.08f, -0.16f, -0.16f, 0.08f, 0.16f, 0.16f, 0.12f, 0.12f, 0.12f);
        GlStateManager.popMatrix();

        // Animated Propeller with RPM Scaling & Translucent Blur Disc
        float propRpm = Math.max(15.0f, speed * 25.0f);
        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0.12f, 1.27f);
        GlStateManager.rotate(animTick * propRpm, 0, 0, 1);
        drawBox(-0.1f, -0.1f, 0.0f, 0.1f, 0.1f, 0.08f, 0.7f, 0.7f, 0.7f); // hub
        drawBox(-0.85f, -0.05f, 0.03f, 0.85f, 0.05f, 0.05f, 0.35f, 0.20f, 0.10f); // blades
        if (speed > 1.2f) {
            drawDisk(0.85f, 0.85f, 0.85f, 0.85f, Math.min(0.35f, speed * 0.06f));
        }
        GlStateManager.popMatrix();
    }

    // ── Iron Monoplane ──────────────────────────────────────────────────────
    private void renderIronMonoplane(T plane, float animTick, float speed, float bankRoll,
                                     float pitch, float yawDelta, float wheelRot) {
        // Fuselage (metallic iron gray)
        drawBox(-0.4f, -0.2f, -1.4f, 0.4f, 0.5f, 1.1f, 0.65f, 0.68f, 0.70f);
        // Nose engine cowling (darker brushed steel)
        drawBox(-0.35f, -0.15f, 1.1f, 0.35f, 0.45f, 1.35f, 0.38f, 0.40f, 0.42f);
        // Glass windshield
        drawBox(-0.28f, 0.5f, 0.1f, 0.28f, 0.72f, 0.45f, 0.35f, 0.55f, 0.75f);

        // Monoplane Main Wings
        drawBox(-1.4f, 0.1f, -0.2f, 1.4f, 0.22f, 0.65f, 0.62f, 0.65f, 0.68f);

        // Dynamic Flaperons / Ailerons
        float aileronAngle = bankRoll * 0.6f;
        GlStateManager.pushMatrix();
        GlStateManager.translate(-1.95f, 0.16f, -0.2f);
        GlStateManager.rotate(-aileronAngle, 1, 0, 0);
        drawBox(-0.55f, -0.05f, 0.0f, 0.55f, 0.05f, 0.85f, 0.58f, 0.60f, 0.64f);
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.translate(1.95f, 0.16f, -0.2f);
        GlStateManager.rotate(aileronAngle, 1, 0, 0);
        drawBox(-0.55f, -0.05f, 0.0f, 0.55f, 0.05f, 0.85f, 0.58f, 0.60f, 0.64f);
        GlStateManager.popMatrix();

        // Nose Mounted LMG Gun Barrel
        drawBox(-0.06f, 0.48f, 0.6f, 0.06f, 0.58f, 1.55f, 0.15f, 0.15f, 0.15f);

        // Tail vertical fin and dynamic rudder
        drawBox(-0.05f, 0.5f, -1.65f, 0.05f, 1.1f, -1.15f, 0.65f, 0.68f, 0.70f);
        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0.8f, -1.65f);
        GlStateManager.rotate(-yawDelta * 2.0f, 0, 1, 0);
        drawBox(-0.04f, -0.3f, -0.35f, 0.04f, 0.3f, 0.0f, 0.55f, 0.58f, 0.60f);
        GlStateManager.popMatrix();

        // Tail dynamic elevator
        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0.38f, -1.45f);
        GlStateManager.rotate(-pitch * 0.45f, 1, 0, 0);
        drawBox(-0.95f, -0.03f, -0.35f, 0.95f, 0.03f, 0.0f, 0.58f, 0.60f, 0.64f);
        GlStateManager.popMatrix();

        // Rolling Wheels
        GlStateManager.pushMatrix();
        GlStateManager.translate(-0.6f, -0.45f, 0.3f);
        GlStateManager.rotate(wheelRot, 1, 0, 0);
        drawBox(-0.09f, -0.16f, -0.16f, 0.09f, 0.16f, 0.16f, 0.12f, 0.12f, 0.12f);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.6f, -0.45f, 0.3f);
        GlStateManager.rotate(wheelRot, 1, 0, 0);
        drawBox(-0.09f, -0.16f, -0.16f, 0.09f, 0.16f, 0.16f, 0.12f, 0.12f, 0.12f);
        GlStateManager.popMatrix();

        // Animated High-Speed Propeller
        float propRpm = Math.max(20.0f, speed * 28.0f);
        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0.15f, 1.38f);
        GlStateManager.rotate(animTick * propRpm, 0, 0, 1);
        drawBox(-0.08f, -0.08f, 0f, 0.08f, 0.08f, 0.1f, 0.8f, 0.8f, 0.8f);
        drawBox(-0.9f, -0.04f, 0.03f, 0.9f, 0.04f, 0.05f, 0.2f, 0.2f, 0.2f);
        if (speed > 1.2f) {
            drawDisk(0.9f, 0.9f, 0.9f, 0.9f, Math.min(0.40f, speed * 0.07f));
        }
        GlStateManager.popMatrix();
    }

    // ── Fighter Jet ─────────────────────────────────────────────────────────
    private void renderFighterJet(T plane, float animTick, float speed, float bankRoll,
                                  float pitch, float yawDelta) {
        // Needle nose radome
        drawBox(-0.18f, 0.0f, 1.4f, 0.18f, 0.28f, 2.2f, 0.28f, 0.30f, 0.33f);
        // Cockpit canopy (tinted reflective blue/black)
        drawBox(-0.25f, 0.28f, 0.4f, 0.25f, 0.6f, 1.3f, 0.2f, 0.45f, 0.65f);
        // Fuselage body (military air superiority gray)
        drawBox(-0.55f, -0.15f, -1.5f, 0.55f, 0.35f, 1.4f, 0.35f, 0.38f, 0.42f);

        // Delta wings (swept back)
        drawBox(-2.4f, 0.0f, -1.3f, 2.4f, 0.1f, 0.1f, 0.33f, 0.36f, 0.40f);

        // Twin Tailerons (All-Moving Elevators with differential roll & pitch trim)
        float leftTaileron = (-pitch * 0.5f) - (bankRoll * 0.4f);
        float rightTaileron = (-pitch * 0.5f) + (bankRoll * 0.4f);
        GlStateManager.pushMatrix();
        GlStateManager.translate(-0.85f, 0.05f, -1.4f);
        GlStateManager.rotate(leftTaileron, 1, 0, 0);
        drawBox(-0.65f, -0.04f, -0.55f, 0.0f, 0.04f, 0.15f, 0.28f, 0.32f, 0.35f);
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.85f, 0.05f, -1.4f);
        GlStateManager.rotate(rightTaileron, 1, 0, 0);
        drawBox(0.0f, -0.04f, -0.55f, 0.65f, 0.04f, 0.15f, 0.28f, 0.32f, 0.35f);
        GlStateManager.popMatrix();

        // Twin Canted Vertical Fins and Rudders
        drawBox(-0.48f, 0.35f, -1.6f, -0.4f, 1.25f, -0.9f, 0.33f, 0.36f, 0.40f);
        drawBox(0.4f, 0.35f, -1.6f, 0.48f, 1.25f, -0.9f, 0.33f, 0.36f, 0.40f);

        // Underwing missile rails
        drawBox(-1.6f, -0.08f, -0.8f, -1.52f, 0.0f, 0.0f, 0.2f, 0.2f, 0.2f);
        drawBox(1.52f, -0.08f, -0.8f, 1.6f, 0.0f, 0.0f, 0.2f, 0.2f, 0.2f);

        // Twin Afterburner Nozzles
        drawBox(-0.45f, -0.08f, -1.75f, -0.12f, 0.25f, -1.5f, 0.2f, 0.2f, 0.2f);
        drawBox(0.12f, -0.08f, -1.75f, 0.45f, 0.25f, -1.5f, 0.2f, 0.2f, 0.2f);

        // Dynamic Supersonic Flame Plumes with Mach Diamonds
        float baseFlame = 0.25f + (speed * 0.12f) + (float) Math.sin(animTick * 1.5f) * 0.08f;
        // Outer glowing fire envelope
        drawBox(-0.42f, -0.05f, -1.75f - baseFlame, -0.15f, 0.22f, -1.75f, 1.0f, 0.55f, 0.08f);
        drawBox(0.15f, -0.05f, -1.75f - baseFlame, 0.42f, 0.22f, -1.75f, 1.0f, 0.55f, 0.08f);
        // Supersonic Blue/White Core Mach Diamond
        float coreFlame = baseFlame * 0.65f;
        drawBox(-0.35f, 0.0f, -1.75f - coreFlame, -0.22f, 0.17f, -1.75f, 0.35f, 0.85f, 1.0f);
        drawBox(0.22f, 0.0f, -1.75f - coreFlame, 0.35f, 0.17f, -1.75f, 0.35f, 0.85f, 1.0f);

        // High-G Wingtip Vortex Condensation Ribbons
        if (Math.abs(bankRoll) > 12.0f || speed > 4.5f) {
            GlStateManager.enableBlend();
            drawBox(-2.45f, 0.02f, -2.4f, -2.35f, 0.08f, -1.3f, 0.95f, 0.98f, 1.0f);
            drawBox(2.35f, 0.02f, -2.4f, 2.45f, 0.08f, -1.3f, 0.95f, 0.98f, 1.0f);
            GlStateManager.disableBlend();
        }
    }

    // ── Stealth Bomber ──────────────────────────────────────────────────────
    private void renderStealthBomber(T plane, float animTick, float bankRoll, float pitch) {
        // Faceted center fuselage (deep matte stealth black)
        drawBox(-0.75f, -0.12f, -0.7f, 0.75f, 0.32f, 1.5f, 0.12f, 0.13f, 0.15f);
        // Cockpit narrow stealth slits
        drawBox(-0.35f, 0.32f, 0.6f, 0.35f, 0.42f, 1.1f, 0.22f, 0.24f, 0.28f);

        // Huge angular flying wing panels
        drawBox(-3.5f, -0.08f, -1.6f, -0.75f, 0.12f, 0.3f, 0.14f, 0.15f, 0.17f);
        drawBox(0.75f, -0.08f, -1.6f, 3.5f, 0.12f, 0.3f, 0.14f, 0.15f, 0.17f);

        // Trailing-Edge Elevons / Split Drag Rudders
        float elevonDeflection = -pitch * 0.35f;
        GlStateManager.pushMatrix();
        GlStateManager.translate(-2.4f, 0.02f, -1.4f);
        GlStateManager.rotate(elevonDeflection - (bankRoll * 0.3f), 1, 0, 0);
        drawBox(-0.8f, -0.03f, -0.25f, 0.8f, 0.03f, 0.0f, 0.11f, 0.11f, 0.12f);
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.translate(2.4f, 0.02f, -1.4f);
        GlStateManager.rotate(elevonDeflection + (bankRoll * 0.3f), 1, 0, 0);
        drawBox(-0.8f, -0.03f, -0.25f, 0.8f, 0.03f, 0.0f, 0.11f, 0.11f, 0.12f);
        GlStateManager.popMatrix();

        // Operable Underbelly Bomb Bay Doors
        boolean bayOpen = plane.isTacModeActive();
        float bayAngle = bayOpen ? 65.0f : 0.0f;

        // Left Bay Door
        GlStateManager.pushMatrix();
        GlStateManager.translate(-0.45f, -0.12f, 0.2f);
        GlStateManager.rotate(-bayAngle, 0, 0, 1);
        drawBox(-0.02f, -0.02f, -0.7f, 0.42f, 0.02f, 0.7f, 0.15f, 0.16f, 0.18f);
        GlStateManager.popMatrix();

        // Right Bay Door
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.45f, -0.12f, 0.2f);
        GlStateManager.rotate(bayAngle, 0, 0, 1);
        drawBox(-0.42f, -0.02f, -0.7f, 0.02f, 0.02f, 0.7f, 0.15f, 0.16f, 0.18f);
        GlStateManager.popMatrix();

        // Exposed Rotary Bomb Bay Launcher with Loaded Heavy Bombs when open
        if (bayOpen) {
            drawBox(-0.1f, -0.08f, -0.5f, 0.1f, 0.15f, 0.5f, 0.28f, 0.30f, 0.32f); // central rotary spindle
            drawBox(-0.25f, -0.05f, -0.4f, -0.12f, 0.08f, 0.4f, 0.30f, 0.35f, 0.22f); // left bomb
            drawBox(0.12f, -0.05f, -0.4f, 0.25f, 0.08f, 0.4f, 0.30f, 0.35f, 0.22f); // right bomb
        }
    }

    // ── Predator Drone ──────────────────────────────────────────────────────
    private void renderPredatorDrone(T plane, float animTick, float speed, float pitch, float yawDelta) {
        // Slender UAV body (tactical light grey)
        drawBox(-0.25f, -0.15f, -1.2f, 0.25f, 0.35f, 1.2f, 0.70f, 0.72f, 0.74f);
        // Forward satellite communication bulb
        drawBox(-0.28f, 0.2f, 0.4f, 0.28f, 0.52f, 1.1f, 0.72f, 0.74f, 0.76f);

        // Long High-Aspect Glider Wings
        drawBox(-3.0f, 0.1f, -0.1f, 3.0f, 0.18f, 0.4f, 0.68f, 0.70f, 0.72f);

        // Inverted V-tail stabilizers with animated ruddervators
        float ruddervator = (-pitch * 0.4f) + (yawDelta * 1.5f);
        GlStateManager.pushMatrix();
        GlStateManager.translate(-0.4f, -0.1f, -1.3f);
        GlStateManager.rotate(ruddervator, 1, 0, 0);
        drawBox(-0.05f, -0.35f, -0.25f, 0.05f, 0.35f, 0.15f, 0.65f, 0.68f, 0.70f);
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.4f, -0.1f, -1.3f);
        GlStateManager.rotate(ruddervator, 1, 0, 0);
        drawBox(-0.05f, -0.35f, -0.25f, 0.05f, 0.35f, 0.15f, 0.65f, 0.68f, 0.70f);
        GlStateManager.popMatrix();

        // Active 2-Axis Electro-Optical Surveillance Gimbal (pans and tilts scanning terrain)
        float panAngle = (float) Math.sin(animTick * 0.05f) * 35.0f;
        float tiltAngle = 20.0f + (float) Math.sin(animTick * 0.08f) * 15.0f;
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0f, -0.22f, 0.95f);
        GlStateManager.rotate(panAngle, 0, 1, 0);
        GlStateManager.rotate(tiltAngle, 1, 0, 0);
        drawBox(-0.14f, -0.14f, -0.14f, 0.14f, 0.14f, 0.14f, 0.22f, 0.24f, 0.28f); // ball turret
        drawBox(-0.06f, -0.06f, 0.12f, 0.06f, 0.06f, 0.16f, 0.08f, 0.55f, 0.85f); // optical lens aperture
        GlStateManager.popMatrix();

        // Rear Pusher Propeller
        float propRpm = Math.max(18.0f, speed * 22.0f);
        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0.1f, -1.25f);
        GlStateManager.rotate(animTick * propRpm, 0, 0, 1);
        drawBox(-0.06f, -0.06f, -0.06f, 0.06f, 0.06f, 0.0f, 0.3f, 0.3f, 0.3f);
        drawBox(-0.55f, -0.03f, -0.04f, 0.55f, 0.03f, -0.02f, 0.15f, 0.15f, 0.15f);
        if (speed > 1.0f) {
            drawDisk(0.55f, 0.8f, 0.8f, 0.8f, 0.3f);
        }
        GlStateManager.popMatrix();
    }

    // ── Attack Helicopter ───────────────────────────────────────────────────
    private void renderAttackHelicopter(T plane, float animTick, float speed, float bankRoll, float pitch) {
        // Military Gunship Fuselage (camo olive drab)
        drawBox(-0.38f, -0.2f, -1.0f, 0.38f, 0.55f, 0.9f, 0.28f, 0.36f, 0.22f);
        // Stepped Tandem Cockpit
        drawBox(-0.3f, 0.2f, 0.5f, 0.3f, 0.75f, 1.45f, 0.2f, 0.45f, 0.65f);
        // Tail Boom
        drawBox(-0.15f, 0.1f, -2.4f, 0.15f, 0.42f, -1.0f, 0.28f, 0.36f, 0.22f);
        // Tail Vertical Fin
        drawBox(-0.05f, 0.2f, -2.65f, 0.05f, 1.0f, -2.25f, 0.28f, 0.36f, 0.22f);

        // Chin Turret (3-barrel Minigun with active tracking and barrel spin)
        float gunPan = (float) Math.sin(animTick * 0.07f) * 20.0f;
        float gunTilt = 12.0f + (float) Math.sin(animTick * 0.05f) * 10.0f;
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0f, -0.28f, 1.35f);
        GlStateManager.rotate(gunPan, 0, 1, 0);
        GlStateManager.rotate(gunTilt, 1, 0, 0);
        drawBox(-0.10f, -0.08f, -0.15f, 0.10f, 0.08f, 0.15f, 0.2f, 0.2f, 0.2f); // housing
        // 3-barrel cluster spinning
        GlStateManager.pushMatrix();
        GlStateManager.rotate(animTick * 60.0f, 0, 0, 1);
        drawBox(-0.04f, -0.04f, 0.15f, 0.04f, 0.04f, 0.75f, 0.1f, 0.1f, 0.1f);
        GlStateManager.popMatrix();
        GlStateManager.popMatrix();

        // Stub Weapon Wings and Rocket Pods
        drawBox(-1.3f, 0.08f, -0.2f, 1.3f, 0.18f, 0.3f, 0.25f, 0.32f, 0.20f);
        drawBox(-1.15f, -0.1f, -0.35f, -0.85f, 0.15f, 0.45f, 0.2f, 0.2f, 0.2f);
        drawBox(0.85f, -0.1f, -0.35f, 1.15f, 0.15f, 0.45f, 0.2f, 0.2f, 0.2f);

        // Landing Skids
        drawBox(-0.55f, -0.42f, -0.8f, -0.45f, -0.35f, 1.0f, 0.18f, 0.18f, 0.18f);
        drawBox(0.45f, -0.42f, -0.8f, 0.55f, -0.35f, 1.0f, 0.18f, 0.18f, 0.18f);

        // Spinning Main 4-Blade Rotor with Cyclic Feathering (disc tilts forward when moving)
        float cyclicTiltForward = Math.min(18.0f, speed * 2.5f + 4.0f);
        float cyclicTiltRoll = bankRoll * 0.45f;
        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0.72f, 0.0f);
        GlStateManager.rotate(-cyclicTiltForward, 1, 0, 0);
        GlStateManager.rotate(cyclicTiltRoll, 0, 0, 1);

        // Rotor Mast and Hub
        drawBox(-0.12f, 0f, -0.12f, 0.12f, 0.15f, 0.12f, 0.2f, 0.2f, 0.2f);

        // High RPM 4-blade rotor spin
        GlStateManager.pushMatrix();
        GlStateManager.rotate(animTick * 65.0f, 0, 1, 0);
        drawBox(-2.8f, 0.08f, -0.08f, 2.8f, 0.12f, 0.08f, 0.12f, 0.12f, 0.12f); // blade 1-2
        drawBox(-0.08f, 0.08f, -2.8f, 0.08f, 0.12f, 2.8f, 0.12f, 0.12f, 0.12f); // blade 3-4

        // Semi-transparent rotor disk blur
        GlStateManager.pushMatrix();
        GlStateManager.rotate(90.0f, 1, 0, 0);
        drawDisk(2.8f, 0.2f, 0.25f, 0.2f, 0.28f);
        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
        GlStateManager.popMatrix();

        // Spinning Tail Anti-Torque Rotor
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.12f, 0.75f, -2.45f);
        GlStateManager.rotate(animTick * 75.0f, 1, 0, 0);
        drawBox(-0.05f, -0.6f, -0.04f, 0.02f, 0.6f, 0.04f, 0.15f, 0.15f, 0.15f);
        GlStateManager.popMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(T entity) {
        return null;
    }
}
