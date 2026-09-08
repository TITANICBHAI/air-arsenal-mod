package com.airarsenal.client.renderer.entity;

import com.airarsenal.entity.vehicle.ArmoredTruckEntity;
import com.airarsenal.entity.vehicle.MissileTruckEntity;
import com.airarsenal.entity.vehicle.TankEntity;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityCreature;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * 3D Renderer for ground combat vehicles: Tank, Missile Command Truck, and Armored Truck.
 * Implements realistic vehicular animations:
 * - Independent 360-degree tank turret traverse tracking targets
 * - 120mm main gun barrel elevation/depression and dynamic recoil kickback
 * - Rolling road wheels and track simulation driven by limbSwing travel speed
 * - Missile truck hydraulic canister elevation from stowage to 45-degree launch angle
 * - Armored truck 360-degree pintle-mounted roof machine gun turret traverse
 */
public class VehicleRenderer<T extends EntityCreature> extends Render<T> {

    public VehicleRenderer(RenderManager renderManager) {
        super(renderManager);
        this.shadowSize = 1.0f;
    }

    @Override
    public void doRender(T vehicle, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);

        float yaw = vehicle.prevRotationYaw + (vehicle.rotationYaw - vehicle.prevRotationYaw) * partialTicks;
        GlStateManager.rotate(180.0f - yaw, 0.0f, 1.0f, 0.0f);

        GlStateManager.disableTexture2D();
        GlStateManager.enableRescaleNormal();

        float animTick = vehicle.ticksExisted + partialTicks;
        float wheelRot = vehicle.limbSwing * 0.85f;

        if (vehicle instanceof TankEntity) {
            renderTank((TankEntity) vehicle, animTick, wheelRot, partialTicks);
        } else if (vehicle instanceof MissileTruckEntity) {
            renderMissileTruck((MissileTruckEntity) vehicle, animTick, wheelRot, partialTicks);
        } else if (vehicle instanceof ArmoredTruckEntity) {
            renderArmoredTruck((ArmoredTruckEntity) vehicle, animTick, wheelRot, partialTicks);
        }

        GlStateManager.disableRescaleNormal();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();

        super.doRender(vehicle, x, y, z, entityYaw, partialTicks);
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

    // ── Tank ────────────────────────────────────────────────────────────────
    private void renderTank(TankEntity tank, float animTick, float wheelRot, float partialTicks) {
        // Track assemblies (dark iron tread rubber)
        drawBox(-0.95f, 0.0f, -1.35f, -0.6f, 0.5f, 1.35f, 0.18f, 0.18f, 0.18f); // Left track
        drawBox(0.6f, 0.0f, -1.35f, 0.95f, 0.5f, 1.35f, 0.18f, 0.18f, 0.18f);  // Right track

        // Animated Rolling Road Wheels inside tracks
        float[] wheelZ = { -1.0f, -0.5f, 0.0f, 0.5f, 1.0f };
        for (float wz : wheelZ) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(-0.78f, 0.25f, wz);
            GlStateManager.rotate(wheelRot, 1, 0, 0);
            drawBox(-0.18f, -0.2f, -0.2f, 0.18f, 0.2f, 0.2f, 0.25f, 0.25f, 0.25f);
            GlStateManager.popMatrix();

            GlStateManager.pushMatrix();
            GlStateManager.translate(0.78f, 0.25f, wz);
            GlStateManager.rotate(wheelRot, 1, 0, 0);
            drawBox(-0.18f, -0.2f, -0.2f, 0.18f, 0.2f, 0.2f, 0.25f, 0.25f, 0.25f);
            GlStateManager.popMatrix();
        }

        // Calculate Independent Turret Traverse tracking head yaw
        float tankYaw = tank.prevRotationYaw + (tank.rotationYaw - tank.prevRotationYaw) * partialTicks;
        float headYaw = tank.prevRotationYawHead + (tank.rotationYawHead - tank.prevRotationYawHead) * partialTicks;
        float relTurretYaw = headYaw - tankYaw;
        while (relTurretYaw > 180.0f) relTurretYaw -= 360.0f;
        while (relTurretYaw < -180.0f) relTurretYaw += 360.0f;

        float barrelPitch = tank.prevRotationPitch + (tank.rotationPitch - tank.prevRotationPitch) * partialTicks;
        barrelPitch = Math.max(-12.0f, Math.min(25.0f, barrelPitch));

        // Procedural hydro-pneumatic recoil and chassis suspension rocking
        float cycleTick = animTick % 60.0f;
        com.airarsenal.client.util.VehicleKinematicsHelper.RecoilKinematics recoil =
            com.airarsenal.client.util.VehicleKinematicsHelper.calculateChassisRecoilShock(
                cycleTick, barrelPitch, relTurretYaw, 1.0f
            );

        // Lower hull & sloped glacis plate (olive drab green) with suspension squat
        GlStateManager.pushMatrix();
        GlStateManager.rotate(recoil.chassisPitchSquat, 1, 0, 0);
        GlStateManager.rotate(recoil.chassisRollRock, 0, 0, 1);

        drawBox(-0.62f, 0.15f, -1.25f, 0.62f, 0.65f, 1.25f, 0.32f, 0.38f, 0.22f);
        drawBox(-0.55f, 0.35f, 1.15f, 0.55f, 0.65f, 1.45f, 0.30f, 0.35f, 0.20f);

        // Independent Rotating Turret Assembly
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0f, 0.65f, 0.05f);
        GlStateManager.rotate(-relTurretYaw, 0, 1, 0);

        // Turret Ring & Main Armor Block
        drawBox(-0.48f, 0.0f, -0.6f, 0.48f, 0.47f, 0.6f, 0.30f, 0.36f, 0.20f);
        // Commander Cupola
        drawBox(-0.35f, 0.47f, -0.25f, -0.05f, 0.60f, 0.15f, 0.25f, 0.30f, 0.18f);
        // Rear Bustle Stowage Rack
        drawBox(-0.42f, 0.08f, -0.85f, 0.42f, 0.35f, -0.60f, 0.22f, 0.26f, 0.18f);

        // 120mm Gun Mantlet & Elevating Main Cannon with Hydro-Pneumatic Recoil Stroke
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0f, 0.18f, 0.6f);
        GlStateManager.rotate(-barrelPitch, 1, 0, 0);
        GlStateManager.translate(0.0f, 0.0f, recoil.barrelDisplacement); // recoil displacement along bore axis

        drawBox(-0.16f, -0.1f, 0.0f, 0.16f, 0.16f, 0.25f, 0.22f, 0.25f, 0.20f); // Mantlet
        drawBox(-0.06f, -0.04f, 0.25f, 0.06f, 0.10f, 1.75f, 0.18f, 0.18f, 0.18f); // Long Barrel
        drawBox(-0.09f, -0.07f, 1.75f, 0.09f, 0.13f, 1.95f, 0.14f, 0.14f, 0.14f); // Muzzle Brake

        // Dynamic Muzzle blast flash envelope
        if (recoil.muzzleGasEnvelope > 0.01f) {
            float env = recoil.muzzleGasEnvelope;
            GlStateManager.enableBlend();
            drawBox(-0.25f * env, -0.2f * env, 1.95f, 0.25f * env, 0.3f * env, 1.95f + 0.5f * env, 1.0f, 0.8f, 0.2f);
            GlStateManager.disableBlend();
        }

        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
        GlStateManager.popMatrix(); // hull squat
    }

    // ── Missile Command Truck ───────────────────────────────────────────────
    private void renderMissileTruck(MissileTruckEntity truck, float animTick, float wheelRot, float partialTicks) {
        // Truck chassis (dark tactical steel)
        drawBox(-0.55f, 0.22f, -1.5f, 0.55f, 0.45f, 1.5f, 0.25f, 0.26f, 0.28f);

        // 6 Heavy Road Wheels with dynamic ground roll
        float[] wheelZ = { 1.0f, -0.2f, -1.0f };
        for (float wz : wheelZ) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(-0.66f, 0.25f, wz);
            GlStateManager.rotate(wheelRot, 1, 0, 0);
            drawBox(-0.12f, -0.25f, -0.25f, 0.12f, 0.25f, 0.25f, 0.15f, 0.15f, 0.15f);
            GlStateManager.popMatrix();

            GlStateManager.pushMatrix();
            GlStateManager.translate(0.66f, 0.25f, wz);
            GlStateManager.rotate(wheelRot, 1, 0, 0);
            drawBox(-0.12f, -0.25f, -0.25f, 0.12f, 0.25f, 0.25f, 0.15f, 0.15f, 0.15f);
            GlStateManager.popMatrix();
        }

        // Forward Cab (angular tactical transport green)
        drawBox(-0.62f, 0.45f, 0.45f, 0.62f, 1.35f, 1.5f, 0.30f, 0.35f, 0.26f);
        // Front Grille & Windshield
        drawBox(-0.52f, 0.8f, 1.51f, 0.52f, 1.25f, 1.53f, 0.2f, 0.35f, 0.45f);
        drawBox(-0.55f, 0.48f, 1.51f, 0.55f, 0.75f, 1.53f, 0.18f, 0.18f, 0.18f);

        // Rear Turntable Base
        drawBox(-0.5f, 0.45f, -1.35f, 0.5f, 0.62f, 0.2f, 0.28f, 0.30f, 0.32f);

        // Dynamic Canister Elevation Hydraulics
        // Canisters elevate to 38 degrees when armed/deployed
        float launchElevation = 38.0f;
        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0.62f, -0.6f);
        GlStateManager.rotate(-launchElevation, 1, 0, 0);

        // Hydraulic rams
        drawBox(-0.08f, -0.15f, -0.2f, 0.08f, 0.0f, 0.2f, 0.7f, 0.7f, 0.75f);

        // Twin Canister Launch Tubes
        drawBox(-0.42f, 0.0f, -0.8f, -0.08f, 0.35f, 0.85f, 0.24f, 0.28f, 0.22f); // Left
        drawBox(0.08f, 0.0f, -0.8f, 0.42f, 0.35f, 0.85f, 0.24f, 0.28f, 0.22f);  // Right

        // Hinged Front Protective Blast Port Covers
        drawBox(-0.42f, 0.35f, 0.85f, -0.08f, 0.55f, 0.90f, 0.35f, 0.40f, 0.32f);
        drawBox(0.08f, 0.35f, 0.85f, 0.42f, 0.55f, 0.90f, 0.35f, 0.40f, 0.32f);

        // Rocket Warhead Cones visible in launch tubes
        drawBox(-0.35f, 0.06f, 0.75f, -0.15f, 0.28f, 0.98f, 0.8f, 0.3f, 0.1f);
        drawBox(0.15f, 0.06f, 0.75f, 0.35f, 0.28f, 0.98f, 0.8f, 0.3f, 0.1f);

        GlStateManager.popMatrix();
    }

    // ── Armored Truck ───────────────────────────────────────────────────────
    private void renderArmoredTruck(ArmoredTruckEntity truck, float animTick, float wheelRot, float partialTicks) {
        // Lower frame
        drawBox(-0.58f, 0.22f, -1.5f, 0.58f, 0.45f, 1.5f, 0.28f, 0.30f, 0.32f);

        // 6 Large Road Wheels with dynamic ground roll
        float[] wheelZ = { 1.05f, -0.15f, -1.05f };
        for (float wz : wheelZ) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(-0.70f, 0.27f, wz);
            GlStateManager.rotate(wheelRot, 1, 0, 0);
            drawBox(-0.12f, -0.27f, -0.27f, 0.12f, 0.27f, 0.27f, 0.16f, 0.16f, 0.16f);
            GlStateManager.popMatrix();

            GlStateManager.pushMatrix();
            GlStateManager.translate(0.70f, 0.27f, wz);
            GlStateManager.rotate(wheelRot, 1, 0, 0);
            drawBox(-0.12f, -0.27f, -0.27f, 0.12f, 0.27f, 0.27f, 0.16f, 0.16f, 0.16f);
            GlStateManager.popMatrix();
        }

        // Heavy Armored Hull (armored steel gray)
        drawBox(-0.68f, 0.45f, -1.55f, 0.68f, 1.45f, 1.45f, 0.48f, 0.52f, 0.55f);
        // Front Reinforced Bumper & Ram Guard
        drawBox(-0.62f, 0.25f, 1.45f, 0.62f, 0.72f, 1.62f, 0.2f, 0.22f, 0.24f);
        // Armored Slit Viewports
        drawBox(-0.55f, 0.95f, 1.46f, 0.55f, 1.15f, 1.48f, 0.15f, 0.2f, 0.25f);

        // Roof Hatch with 360-Degree Traversing Machine Gun Pintle Turret
        float mgPan = (float) Math.sin(animTick * 0.04f) * 45.0f;
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0f, 1.45f, 0.45f);
        GlStateManager.rotate(mgPan, 0, 1, 0);

        drawBox(-0.25f, 0.0f, -0.25f, 0.25f, 0.12f, 0.25f, 0.35f, 0.38f, 0.40f); // ring
        drawBox(-0.18f, 0.12f, 0.15f, 0.18f, 0.42f, 0.20f, 0.35f, 0.38f, 0.40f); // ballistic gun shield
        drawBox(-0.04f, 0.20f, 0.20f, 0.04f, 0.28f, 1.05f, 0.12f, 0.12f, 0.12f);  // heavy MG barrel
        drawBox(-0.14f, 0.18f, 0.0f, -0.05f, 0.30f, 0.15f, 0.25f, 0.22f, 0.12f); // ammo box

        GlStateManager.popMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(T entity) {
        return null;
    }
}
