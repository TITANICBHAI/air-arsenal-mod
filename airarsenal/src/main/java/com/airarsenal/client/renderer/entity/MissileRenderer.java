package com.airarsenal.client.renderer.entity;

import com.airarsenal.entity.projectile.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * 3D Renderer for guided missiles and rockets in flight.
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

        GlStateManager.disableTexture2D();
        GlStateManager.enableRescaleNormal();

        if (missile instanceof BrahMosEntity) {
            renderBrahMos();
        } else if (missile instanceof HellfireEntity) {
            renderHellfire();
        } else if (missile instanceof ManpadsEntity) {
            renderManpads();
        } else if (missile instanceof MLRSRocketEntity) {
            renderMLRSRocket();
        } else if (missile instanceof TruckGuidedMissileEntity) {
            renderTruckMissile();
        } else {
            renderGenericMissile();
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

    // ── BrahMos Supersonic Cruise Missile ───────────────────────────────────
    private void renderBrahMos() {
        // Main missile fuselage (tactical white/grey)
        drawBox(-0.16f, -0.16f, -1.5f, 0.16f, 0.16f, 1.2f, 0.90f, 0.90f, 0.92f);
        // Supersonic center intake cone spike
        drawBox(-0.06f, -0.06f, 1.2f, 0.06f, 0.06f, 1.7f, 0.2f, 0.2f, 0.2f);
        // Mid-body delta cruise wings
        drawBox(-0.65f, -0.02f, -0.2f, 0.65f, 0.02f, 0.3f, 0.85f, 0.85f, 0.88f);
        // Tail control cruciform fins
        drawBox(-0.45f, -0.02f, -1.45f, 0.45f, 0.02f, -1.15f, 0.8f, 0.2f, 0.1f); // horizontal (red tip)
        drawBox(-0.02f, -0.45f, -1.45f, 0.02f, 0.45f, -1.15f, 0.8f, 0.2f, 0.1f); // vertical
        // Rocket exhaust nozzle
        drawBox(-0.12f, -0.12f, -1.65f, 0.12f, 0.12f, -1.5f, 0.15f, 0.15f, 0.15f);
        // Glowing exhaust flame
        drawBox(-0.08f, -0.08f, -1.95f, 0.08f, 0.08f, -1.65f, 1.0f, 0.5f, 0.0f);
    }

    // ── Hellfire Missile ────────────────────────────────────────────────────
    private void renderHellfire() {
        // Cylindrical fuselage (matte olive drab / black)
        drawBox(-0.09f, -0.09f, -0.9f, 0.09f, 0.09f, 0.6f, 0.20f, 0.22f, 0.18f);
        // Seeker dome (semi-reflective glass sensor)
        drawBox(-0.07f, -0.07f, 0.6f, 0.07f, 0.07f, 0.78f, 0.95f, 0.75f, 0.15f);
        // Forward guidance canards
        drawBox(-0.25f, -0.01f, 0.35f, 0.25f, 0.01f, 0.5f, 0.25f, 0.25f, 0.25f);
        drawBox(-0.01f, -0.25f, 0.35f, 0.01f, 0.25f, 0.5f, 0.25f, 0.25f, 0.25f);
        // Rear stabilizing fins
        drawBox(-0.28f, -0.01f, -0.85f, 0.28f, 0.01f, -0.6f, 0.25f, 0.25f, 0.25f);
        drawBox(-0.01f, -0.28f, -0.85f, 0.01f, 0.28f, -0.6f, 0.25f, 0.25f, 0.25f);
        // Exhaust flame
        drawBox(-0.05f, -0.05f, -1.15f, 0.05f, 0.05f, -0.9f, 1.0f, 0.6f, 0.1f);
    }

    // ── MANPADS Missile ─────────────────────────────────────────────────────
    private void renderManpads() {
        // Compact missile body (metallic grey)
        drawBox(-0.06f, -0.06f, -0.7f, 0.06f, 0.06f, 0.5f, 0.7f, 0.72f, 0.75f);
        // IR Seeker Head (dark sapphire dome)
        drawBox(-0.05f, -0.05f, 0.5f, 0.05f, 0.05f, 0.65f, 0.2f, 0.3f, 0.6f);
        // Small forward control canards
        drawBox(-0.16f, -0.01f, 0.32f, 0.16f, 0.01f, 0.42f, 0.3f, 0.3f, 0.3f);
        drawBox(-0.01f, -0.16f, 0.32f, 0.01f, 0.16f, 0.42f, 0.3f, 0.3f, 0.3f);
        // Rear folding fins
        drawBox(-0.2f, -0.01f, -0.68f, 0.2f, 0.01f, -0.52f, 0.3f, 0.3f, 0.3f);
        drawBox(-0.01f, -0.2f, -0.68f, 0.01f, 0.2f, -0.52f, 0.3f, 0.3f, 0.3f);
        // Exhaust flame
        drawBox(-0.04f, -0.04f, -0.9f, 0.04f, 0.04f, -0.7f, 1.0f, 0.5f, 0.0f);
    }

    // ── MLRS Rocket ─────────────────────────────────────────────────────────
    private void renderMLRSRocket() {
        // Artillery rocket body (military olive)
        drawBox(-0.08f, -0.08f, -1.0f, 0.08f, 0.08f, 0.6f, 0.35f, 0.42f, 0.28f);
        // Conical Warhead (dark steel with fuze)
        drawBox(-0.06f, -0.06f, 0.6f, 0.06f, 0.06f, 0.85f, 0.22f, 0.22f, 0.22f);
        // Rear curved fins
        drawBox(-0.25f, -0.01f, -0.98f, 0.25f, 0.01f, -0.75f, 0.25f, 0.25f, 0.25f);
        drawBox(-0.01f, -0.25f, -0.98f, 0.01f, 0.25f, -0.75f, 0.25f, 0.25f, 0.25f);
        // Rocket trail flame
        drawBox(-0.06f, -0.06f, -1.3f, 0.06f, 0.06f, -1.0f, 1.0f, 0.7f, 0.1f);
    }

    // ── Truck Guided Missile ────────────────────────────────────────────────
    private void renderTruckMissile() {
        // Heavy missile body (light tactical grey)
        drawBox(-0.12f, -0.12f, -1.2f, 0.12f, 0.12f, 0.8f, 0.82f, 0.84f, 0.86f);
        // Pointed nosecone
        drawBox(-0.08f, -0.08f, 0.8f, 0.08f, 0.08f, 1.15f, 0.25f, 0.25f, 0.28f);
        // Cruciform flight surfaces
        drawBox(-0.45f, -0.01f, -0.2f, 0.45f, 0.01f, 0.2f, 0.75f, 0.77f, 0.8f);
        drawBox(-0.01f, -0.45f, -0.2f, 0.01f, 0.45f, 0.2f, 0.75f, 0.77f, 0.8f);
        // Rear tail steering fins
        drawBox(-0.35f, -0.01f, -1.15f, 0.35f, 0.01f, -0.9f, 0.3f, 0.3f, 0.3f);
        drawBox(-0.01f, -0.35f, -1.15f, 0.01f, 0.35f, -0.9f, 0.3f, 0.3f, 0.3f);
        // Exhaust flame
        drawBox(-0.08f, -0.08f, -1.55f, 0.08f, 0.08f, -1.2f, 1.0f, 0.6f, 0.1f);
    }

    private void renderGenericMissile() {
        renderHellfire();
    }

    @Override
    protected ResourceLocation getEntityTexture(T entity) {
        return null;
    }
}
