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
 * 3D Renderer for ground vehicles: Tank, Missile Command Truck, and Armored Truck.
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

        if (vehicle instanceof TankEntity) {
            renderTank((TankEntity) vehicle, partialTicks);
        } else if (vehicle instanceof MissileTruckEntity) {
            renderMissileTruck();
        } else if (vehicle instanceof ArmoredTruckEntity) {
            renderArmoredTruck();
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
    private void renderTank(TankEntity tank, float partialTicks) {
        // Track assemblies (dark iron tread rubber)
        drawBox(-0.95f, 0.0f, -1.35f, -0.6f, 0.5f, 1.35f, 0.18f, 0.18f, 0.18f); // Left track
        drawBox(0.6f, 0.0f, -1.35f, 0.95f, 0.5f, 1.35f, 0.18f, 0.18f, 0.18f);  // Right track
        // Road wheels accents
        drawBox(-0.98f, 0.08f, -1.2f, -0.58f, 0.42f, 1.2f, 0.25f, 0.25f, 0.25f);
        drawBox(0.58f, 0.08f, -1.2f, 0.98f, 0.42f, 1.2f, 0.25f, 0.25f, 0.25f);

        // Lower hull & sloped glacis plate (olive drab green)
        drawBox(-0.62f, 0.15f, -1.25f, 0.62f, 0.65f, 1.25f, 0.32f, 0.38f, 0.22f);
        drawBox(-0.55f, 0.35f, 1.15f, 0.55f, 0.65f, 1.45f, 0.30f, 0.35f, 0.20f);

        // Turret Body (rotating relative to aim or facing forward)
        drawBox(-0.48f, 0.65f, -0.55f, 0.48f, 1.12f, 0.65f, 0.30f, 0.36f, 0.20f);
        // Commander Cupola
        drawBox(-0.35f, 1.12f, -0.2f, -0.05f, 1.25f, 0.2f, 0.25f, 0.30f, 0.18f);
        // Main 120mm Gun Mantlet & Long Barrel
        drawBox(-0.16f, 0.72f, 0.65f, 0.16f, 0.98f, 0.88f, 0.22f, 0.25f, 0.20f);
        drawBox(-0.06f, 0.78f, 0.88f, 0.06f, 0.92f, 2.35f, 0.18f, 0.18f, 0.18f); // Barrel
        // Muzzle Brake
        drawBox(-0.09f, 0.75f, 2.35f, 0.09f, 0.95f, 2.5f, 0.14f, 0.14f, 0.14f);
    }

    // ── Missile Command Truck ───────────────────────────────────────────────
    private void renderMissileTruck() {
        // Truck chassis (dark tactical steel)
        drawBox(-0.55f, 0.22f, -1.5f, 0.55f, 0.45f, 1.5f, 0.25f, 0.26f, 0.28f);

        // 6 Heavy Road Wheels
        float[] wheelZ = { 1.0f, -0.2f, -1.0f };
        for (float wz : wheelZ) {
            drawBox(-0.78f, 0.0f, wz - 0.25f, -0.55f, 0.5f, wz + 0.25f, 0.15f, 0.15f, 0.15f);
            drawBox(0.55f, 0.0f, wz - 0.25f, 0.78f, 0.5f, wz + 0.25f, 0.15f, 0.15f, 0.15f);
        }

        // Forward Cab (angular tactical transport green)
        drawBox(-0.62f, 0.45f, 0.45f, 0.62f, 1.35f, 1.5f, 0.30f, 0.35f, 0.26f);
        // Front Grille & Windshield
        drawBox(-0.52f, 0.8f, 1.51f, 0.52f, 1.25f, 1.53f, 0.2f, 0.35f, 0.45f);
        drawBox(-0.55f, 0.48f, 1.51f, 0.55f, 0.75f, 1.53f, 0.18f, 0.18f, 0.18f);

        // Rear Missile Turntable Base
        drawBox(-0.5f, 0.45f, -1.35f, 0.5f, 0.62f, 0.2f, 0.28f, 0.30f, 0.32f);

        // Elevated Twin Launch Canisters (angled ~25 degrees upward)
        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0.62f, -0.6f);
        GlStateManager.rotate(-25.0f, 1, 0, 0); // elevated launch angle
        // Left Canister Tube
        drawBox(-0.42f, 0.0f, -0.8f, -0.08f, 0.35f, 0.85f, 0.24f, 0.28f, 0.22f);
        // Right Canister Tube
        drawBox(0.08f, 0.0f, -0.8f, 0.42f, 0.35f, 0.85f, 0.24f, 0.28f, 0.22f);
        // Rocket tips visible inside launch tubes
        drawBox(-0.35f, 0.06f, 0.85f, -0.15f, 0.28f, 0.95f, 0.8f, 0.3f, 0.1f);
        drawBox(0.15f, 0.06f, 0.85f, 0.35f, 0.28f, 0.95f, 0.8f, 0.3f, 0.1f);
        GlStateManager.popMatrix();
    }

    // ── Armored Truck ───────────────────────────────────────────────────────
    private void renderArmoredTruck() {
        // Lower frame
        drawBox(-0.58f, 0.22f, -1.5f, 0.58f, 0.45f, 1.5f, 0.28f, 0.30f, 0.32f);

        // 6 Large Road Wheels
        float[] wheelZ = { 1.05f, -0.15f, -1.05f };
        for (float wz : wheelZ) {
            drawBox(-0.82f, 0.0f, wz - 0.28f, -0.58f, 0.55f, wz + 0.28f, 0.16f, 0.16f, 0.16f);
            drawBox(0.58f, 0.0f, wz - 0.28f, 0.82f, 0.55f, wz + 0.28f, 0.16f, 0.16f, 0.16f);
        }

        // Heavy Armored Hull (armored steel gray)
        drawBox(-0.68f, 0.45f, -1.55f, 0.68f, 1.45f, 1.45f, 0.48f, 0.52f, 0.55f);

        // Front Reinforced Bumper & Ram Guard
        drawBox(-0.62f, 0.25f, 1.45f, 0.62f, 0.72f, 1.62f, 0.2f, 0.22f, 0.24f);

        // Armored Slit Viewports
        drawBox(-0.55f, 0.95f, 1.46f, 0.55f, 1.15f, 1.48f, 0.15f, 0.2f, 0.25f);

        // Roof Hatch with Machine Gun Shield
        drawBox(-0.25f, 1.45f, 0.2f, 0.25f, 1.62f, 0.7f, 0.35f, 0.38f, 0.40f);
        drawBox(-0.18f, 1.62f, 0.6f, 0.18f, 1.88f, 0.65f, 0.35f, 0.38f, 0.40f); // Gun shield
        drawBox(-0.04f, 1.7f, 0.65f, 0.04f, 1.78f, 1.45f, 0.12f, 0.12f, 0.12f); // Machine gun barrel
    }

    @Override
    protected ResourceLocation getEntityTexture(T entity) {
        return null;
    }
}
