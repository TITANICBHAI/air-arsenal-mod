package com.airarsenal.client.gui;

import com.airarsenal.client.TacModeController;
import com.airarsenal.combat.weapon.IPlaneWeapon;
import com.airarsenal.entity.plane.BasePlaneEntity;
import com.airarsenal.entity.plane.component.PropellerState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.List;

/**
 * Tac Mode heads-up display.
 *
 * <p>Renders only when the local player is riding a {@link BasePlaneEntity}
 * with {@code isTacModeActive == true}. Registered in
 * {@link com.airarsenal.ClientProxy#init} via
 * {@code MinecraftForge.EVENT_BUS.register(new TacModeHUD(...))}.</p>
 *
 * <h3>Elements drawn:</h3>
 * <ol>
 *   <li>Targeting reticle — white circle (r=12) + 4 extending lines, centred on screen</li>
 *   <li>Weapon name + ammo — bottom-centre</li>
 *   <li>Lock-on indicator — reticle turns red, radius animates 12→6 over 30 ticks</li>
 *   <li>Speed readout — top-left</li>
 *   <li>Altitude readout — below speed</li>
 *   <li>Airframe HP bar — top-right</li>
 *   <li>Propeller HP bar — below airframe, colour-coded by state</li>
 * </ol>
 */
@SideOnly(Side.CLIENT)
public class TacModeHUD {

    private static final int BAR_W = 80;
    private static final int BAR_H = 6;
    private static final int LOCK_ON_RANGE  = 40;
    private static final int LOCK_ON_TICKS  = 30;
    private static final float RETICLE_FULL = 12f;
    private static final float RETICLE_LOCK = 6f;

    /** Reference to the controller so we can read the client-side weapon index. */
    private final TacModeController controller;

    /** Ticks since lock-on target was first acquired (0 = no lock). */
    private int lockOnTick = 0;
    private boolean wasLocked = false;

    public TacModeHUD(TacModeController controller) {
        this.controller = controller;
    }

    // ── Main render event ─────────────────────────────────────────────────────

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;
        if (player == null) return;

        Entity riding = player.getRidingEntity();
        if (!(riding instanceof BasePlaneEntity)) return;

        BasePlaneEntity plane = (BasePlaneEntity) riding;
        if (!plane.isTacModeActive()) return;

        ScaledResolution sr = new ScaledResolution(mc);
        int sw = sr.getScaledWidth();
        int sh = sr.getScaledHeight();
        int cx = sw / 2;
        int cy = sh / 2;

        // Determine lock-on state
        Entity lockTarget = findLockOnTarget(player, plane);
        boolean locked = lockTarget != null;

        if (locked && !wasLocked) {
            lockOnTick = 0;
        }
        if (locked) {
            lockOnTick = Math.min(lockOnTick + 1, LOCK_ON_TICKS);
        } else {
            lockOnTick = 0;
        }
        wasLocked = locked;

        float reticleRadius = locked
            ? RETICLE_FULL - (RETICLE_FULL - RETICLE_LOCK) * (lockOnTick / (float) LOCK_ON_TICKS)
            : RETICLE_FULL;

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(
            GlStateManager.SourceFactor.SRC_ALPHA,
            GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
            GlStateManager.SourceFactor.ONE,
            GlStateManager.DestFactor.ZERO);

        if (locked) {
            GlStateManager.color(1f, 0f, 0f, 0.9f); // red when locked
        } else {
            GlStateManager.color(1f, 1f, 1f, 0.85f); // white normal
        }

        GL11.glLineWidth(1.5f);
        drawReticle(cx, cy, reticleRadius);

        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();

        // ── Text elements ─────────────────────────────────────────────────────
        drawTextElements(mc, plane, sw, sh, locked);
        drawBars(mc, plane, sw);
    }

    /** Hide the default crosshair while Tac Mode is active. */
    @SubscribeEvent
    public void onRenderOverlayPre(RenderGameOverlayEvent.Pre event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.CROSSHAIRS) return;
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;
        if (player == null) return;
        Entity riding = player.getRidingEntity();
        if (!(riding instanceof BasePlaneEntity)) return;
        BasePlaneEntity plane = (BasePlaneEntity) riding;
        if (plane.isTacModeActive()) {
            event.setCanceled(true);
        }
    }

    // ── Reticle drawing ───────────────────────────────────────────────────────

    private void drawReticle(int cx, int cy, float radius) {
        int segments = 48;
        // Circle
        GL11.glBegin(GL11.GL_LINE_LOOP);
        for (int i = 0; i < segments; i++) {
            double a = (2.0 * Math.PI / segments) * i;
            GL11.glVertex2d(cx + Math.cos(a) * radius, cy + Math.sin(a) * radius);
        }
        GL11.glEnd();

        // 4 extending lines (gap = radius, length = radius * 1.5)
        float gap = radius + 2f;
        float len = radius * 1.5f;
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex2f(cx,            cy - gap);      // top
        GL11.glVertex2f(cx,            cy - gap - len);
        GL11.glVertex2f(cx,            cy + gap);      // bottom
        GL11.glVertex2f(cx,            cy + gap + len);
        GL11.glVertex2f(cx - gap,      cy);            // left
        GL11.glVertex2f(cx - gap - len,cy);
        GL11.glVertex2f(cx + gap,      cy);            // right
        GL11.glVertex2f(cx + gap + len,cy);
        GL11.glEnd();
    }

    // ── Text HUD elements ─────────────────────────────────────────────────────

    private void drawTextElements(Minecraft mc, BasePlaneEntity plane,
                                  int sw, int sh, boolean locked) {
        // Speed readout — top-left
        String spdText = String.format("SPD: %.1f b/s", plane.getSpeed());
        mc.fontRenderer.drawStringWithShadow(spdText, 4, 4, 0xFFFFFF);

        // Altitude readout — below speed
        String altText = String.format("ALT: %dm", (int) plane.posY);
        mc.fontRenderer.drawStringWithShadow(altText, 4, 14, 0xFFFFFF);

        // Weapon name + ammo — bottom-centre
        List<IPlaneWeapon> weapons = plane.getWeapons();
        if (!weapons.isEmpty()) {
            int idx = Math.min(controller.getSelectedWeaponIndex(), weapons.size() - 1);
            IPlaneWeapon w = weapons.get(idx);
            int ammo = w.getAmmoCount();
            String ammoStr = (ammo == -1) ? "∞" : String.valueOf(ammo);
            String wepText = w.getDisplayName() + " [" + ammoStr + "]";
            int textW = mc.fontRenderer.getStringWidth(wepText);
            mc.fontRenderer.drawStringWithShadow(wepText, (sw - textW) / 2, sh - 20,
                locked ? 0xFF4444 : 0xFFFFFF);
        }

        // Lock-on status
        if (locked) {
            String lockText = "LOCKED";
            int textW = mc.fontRenderer.getStringWidth(lockText);
            mc.fontRenderer.drawStringWithShadow(lockText, (sw - textW) / 2, sh / 2 + 20, 0xFF4444);
        }
    }

    // ── HP bars ───────────────────────────────────────────────────────────────

    private void drawBars(Minecraft mc, BasePlaneEntity plane, int sw) {
        int barX = sw - BAR_W - 4;
        int barY = 4;

        // Airframe HP bar
        mc.fontRenderer.drawStringWithShadow("AIRFRAME", barX, barY, 0xAAAAAA);
        barY += 9;
        float hpFrac = plane.getPlaneHealth() / plane.getMaxPlaneHealth();
        drawBar(barX, barY, hpFrac, 0x00CC00); // green
        barY += BAR_H + 6;

        // Propeller / engine HP bar
        mc.fontRenderer.drawStringWithShadow("PROPELLER", barX, barY, 0xAAAAAA);
        barY += 9;
        float propFrac = plane.getPropeller().getCurrentHealth()
                       / plane.getPropeller().getMaxHealth();
        int propColor = propellerColor(plane.getPropeller().getDamageState());
        drawBar(barX, barY, Math.max(0f, propFrac), propColor);
        barY += BAR_H + 6;

        // Fuel bar (Chunk 9) — read directly off the entity, same simplification
        // as the propeller bar above: no dedicated sync packet, since the plane
        // entity itself is already visible to the client that's riding it.
        float fuelFrac = plane.getFuelSystem().getFuelPercent(); // 0.0–1.0
        int fuelPct = Math.round(fuelFrac * 100f);
        mc.fontRenderer.drawStringWithShadow("FUEL: " + fuelPct + "%", barX, barY, 0xAAAAAA);
        barY += 9;
        drawBar(barX, barY, Math.max(0f, fuelFrac), fuelColor(fuelFrac));
    }

    private static int fuelColor(float frac) {
        if (frac > 0.5f) return 0x00CC00; // green
        if (frac > 0.2f) return 0xFFFF00; // yellow
        return 0xFF2200;                  // red
    }

    /**
     * Draws a horizontal bar: dark background + filled foreground.
     *
     * @param x      Left edge.
     * @param y      Top edge.
     * @param frac   Fill fraction 0.0–1.0.
     * @param color  RRGGBB (no alpha) foreground colour.
     */
    private void drawBar(int x, int y, float frac, int color) {
        // Background
        Gui.drawRect(x, y, x + BAR_W, y + BAR_H, 0xFF222222);
        // Foreground
        int filled = (int) (BAR_W * frac);
        if (filled > 0) {
            Gui.drawRect(x, y, x + filled, y + BAR_H, 0xFF000000 | color);
        }
    }

    private static int propellerColor(PropellerState state) {
        switch (state) {
            case INTACT:       return 0x00CC00; // green
            case DAMAGED:      return 0xFFFF00; // yellow
            case HEAVY_DAMAGE: return 0xFF8800; // orange
            case CRITICAL:     return 0xFF2200; // red
            case DESTROYED:    return 0x444444; // dark grey
            default:           return 0x00CC00;
        }
    }

    // ── Lock-on target detection ──────────────────────────────────────────────

    /**
     * Finds the nearest entity within {@value #LOCK_ON_RANGE} blocks inside a
     * 30° forward cone of the player's view. Returns {@code null} if none found.
     */
    private Entity findLockOnTarget(EntityPlayer player, BasePlaneEntity plane) {
        Vec3d look   = player.getLookVec();
        double cosThreshold = Math.cos(Math.toRadians(30.0));

        List<Entity> nearby = player.world.getEntitiesInAABBexcluding(
            player,
            player.getEntityBoundingBox().grow(LOCK_ON_RANGE),
            e -> e != plane && e.isEntityAlive()
        );

        Entity best = null;
        double bestDist = Double.MAX_VALUE;

        for (Entity e : nearby) {
            Vec3d toEntity = new Vec3d(
                e.posX - player.posX,
                e.posY + e.height * 0.5 - player.posY,
                e.posZ - player.posZ
            ).normalize();

            double dot = look.dotProduct(toEntity);
            if (dot >= cosThreshold) {
                double dist = player.getDistanceSq(e);
                if (dist < bestDist) {
                    bestDist = dist;
                    best = e;
                }
            }
        }

        return best;
    }
}
