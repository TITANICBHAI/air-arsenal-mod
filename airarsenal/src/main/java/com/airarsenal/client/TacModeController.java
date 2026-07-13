package com.airarsenal.client;

import com.airarsenal.entity.plane.BasePlaneEntity;
import com.airarsenal.network.ModNetwork;
import com.airarsenal.network.PacketMissileSteer;
import com.airarsenal.network.PacketTacModeToggle;
import com.airarsenal.network.PacketWeaponFire;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;

/**
 * Client-side controller: captures keyboard and mouse input while the player
 * is mounted in a {@link BasePlaneEntity} (normal flight + Tac Mode), and also
 * handles Predator Strike nose-cam steering.
 *
 * <h3>Modes</h3>
 * <ul>
 *   <li><b>Normal / Tac Mode</b>: F → toggle, scroll → cycle weapon, LMB → fire</li>
 *   <li><b>Predator cam</b>: mouse delta → {@link PacketMissileSteer} every 2 ticks;
 *       normal look input is suppressed during this mode.</li>
 * </ul>
 *
 * Registered as a Forge event listener via {@link com.airarsenal.ClientProxy#init}.
 *
 * <p>{@link #predatorCamActive} and {@link #steerTick} are {@code public static} so
 * {@link com.airarsenal.network.PacketPredatorCameraStart.Handler} and
 * {@link com.airarsenal.network.PacketPredatorCameraEnd.Handler} can set them
 * without holding a reference to this instance.</p>
 */
@SideOnly(Side.CLIENT)
public class TacModeController {

    // ── Shared state (written by packet handlers, read each tick) ─────────────
    /** Set by {@code PacketPredatorCameraStart.Handler} on the client thread. */
    public static volatile boolean predatorCamActive = false;

    /** Incremented each tick while predator cam active; used for 2-tick rate limit. */
    public static volatile int steerTick = 0;

    /**
     * Entity ID of the Predator missile when the camera-start packet arrived but the
     * entity wasn't loaded yet. -1 = no pending activation. Retried each tick for up
     * to {@link #predatorCamPendingTicks} ticks, then abandoned.
     */
    public static volatile int pendingPredatorEntityId = -1;

    /** Remaining retry ticks before abandoning a pending predator cam activation. */
    public static volatile int predatorCamPendingTicks = 0;

    // ── Per-instance state ────────────────────────────────────────────────────
    /** Client-tracked weapon selection index, synced to server via PacketWeaponFire. */
    private int selectedWeaponIndex = 0;

    /** Debounce: prevents firing every tick while LMB is held. */
    private boolean leftWasDown = false;

    // ─────────────────────────────────────────────────────────────────────────
    //  Client tick
    // ─────────────────────────────────────────────────────────────────────────

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;
        if (player == null) return;

        // ── Retry pending predator-cam activation (entity not loaded yet) ──────
        if (pendingPredatorEntityId >= 0 && predatorCamPendingTicks > 0) {
            net.minecraft.entity.Entity missile =
                mc.world != null ? mc.world.getEntityByID(pendingPredatorEntityId) : null;
            if (missile != null) {
                com.airarsenal.network.PacketPredatorCameraStart.Handler
                    .activatePredatorCam(mc, missile);
            } else {
                predatorCamPendingTicks--;
                if (predatorCamPendingTicks <= 0) {
                    // Give up — entity never arrived (missile likely already exploded)
                    pendingPredatorEntityId = -1;
                }
            }
        }

        // ── Predator cam mode ─────────────────────────────────────────────────
        if (predatorCamActive) {
            handlePredatorCamTick(mc);
            return; // skip normal flight input during nose-cam control
        }

        // ── Missile Truck / Static Battery guidance mode ─────────────────────
        if (activeGuidanceMissileId >= 0) {
            handleTruckGuidanceTick(mc);
            // Guidance steering doesn't take over the camera, so normal input
            // (including driving the truck, if mounted) continues below.
        }

        // ── Missile Truck driving input ───────────────────────────────────────
        if (player.getRidingEntity() instanceof com.airarsenal.entity.vehicle.MissileTruckEntity) {
            com.airarsenal.entity.vehicle.MissileTruckEntity truck =
                (com.airarsenal.entity.vehicle.MissileTruckEntity) player.getRidingEntity();
            truck.setDriveInput(
                mc.gameSettings.keyBindForward.isKeyDown(),
                mc.gameSettings.keyBindBack.isKeyDown(),
                mc.gameSettings.keyBindLeft.isKeyDown(),
                mc.gameSettings.keyBindRight.isKeyDown());

            // Space bar fires the next ready tube — reuses PacketWeaponFire's server
            // dispatch isn't appropriate here (no weapon list), so a dedicated fire
            // action piggybacks on Tac Mode's fire key for simplicity: left-click.
            boolean leftDown = Mouse.isButtonDown(0);
            if (leftDown && !leftWasDown) {
                ModNetwork.CHANNEL.sendToServer(new com.airarsenal.network.PacketFireMissileTube());
            }
            leftWasDown = leftDown;
            return;
        }

        // ── Normal flight / Tac Mode ──────────────────────────────────────────
        if (!(player.getRidingEntity() instanceof BasePlaneEntity)) {
            leftWasDown = false;
            return;
        }

        BasePlaneEntity plane = (BasePlaneEntity) player.getRidingEntity();

        // F key — toggle Tac Mode
        if (KeyBindings.KEY_TAC_MODE.isPressed()) {
            ModNetwork.CHANNEL.sendToServer(new PacketTacModeToggle());
        }

        if (!plane.isTacModeActive()) {
            leftWasDown = false;
            return;
        }

        // ── Scroll-wheel weapon cycling ───────────────────────────────────────
        int scroll = Mouse.getDWheel();
        if (scroll != 0 && !plane.getWeapons().isEmpty()) {
            int count = plane.getWeapons().size();
            selectedWeaponIndex = scroll > 0
                ? (selectedWeaponIndex + 1) % count
                : (selectedWeaponIndex - 1 + count) % count;
        }

        // ── Left-click fire (debounced — one packet per press) ────────────────
        boolean leftDown = Mouse.isButtonDown(0);
        if (leftDown && !leftWasDown) {
            Vec3d look = player.getLookVec();
            ModNetwork.CHANNEL.sendToServer(
                new PacketWeaponFire(look, selectedWeaponIndex));
        }
        leftWasDown = leftDown;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Predator cam steering
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Called every client tick while {@link #predatorCamActive} is true.
     * Reads raw mouse delta, converts to yaw/pitch deltas, and sends
     * {@link PacketMissileSteer} to the server every 2 ticks.
     *
     * Normal Minecraft look input is suppressed by resetting the player's
     * rotation back to its previous value after the engine processes it.
     */
    private void handlePredatorCamTick(Minecraft mc) {
        steerTick++;

        // Read raw mouse delta this tick (consumed from OS queue)
        int rawDX = Mouse.getDX();
        int rawDY = Mouse.getDY();

        // Sensitivity scaling — approximates Minecraft's own mouse sensitivity curve
        float sens = mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
        float yawDelta   =  rawDX * sens * 0.15f;
        float pitchDelta = -rawDY * sens * 0.15f;   // inverted: up = negative pitch

        // Clamp individual deltas to avoid overshooting on fast mouse moves
        yawDelta   = Math.max(-10f, Math.min(10f, yawDelta));
        pitchDelta = Math.max(-8f,  Math.min(8f,  pitchDelta));

        // Suppress the player's camera from rotating (we've consumed the delta)
        if (mc.player != null) {
            mc.player.rotationYaw   -= yawDelta;   // counter the movement Minecraft applies
            mc.player.rotationPitch -= pitchDelta;
        }

        // Send steer packet every 2 ticks (rate-limited)
        if (steerTick % 2 == 0 && (rawDX != 0 || rawDY != 0)) {
            ModNetwork.CHANNEL.sendToServer(new PacketMissileSteer(yawDelta, pitchDelta, false));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Missile Truck guidance steering (Chunk 8) — shares PacketMissileSteer with
    //  the Predator cam above, but has no camera swap: the gunner stays put and
    //  steers via mouse look while a HUD readout shows signal state.
    // ─────────────────────────────────────────────────────────────────────────

    /** Set by {@link com.airarsenal.network.PacketGuidanceStart.Handler}; -1 = inactive. */
    public static volatile int activeGuidanceMissileId = -1;

    /** Set by {@link com.airarsenal.network.PacketGuidanceLost.Handler}. */
    public static volatile boolean guidanceSignalLost = false;

    private int guidanceSteerTick = 0;

    /**
     * Called from {@link #onClientTick} when {@link #activeGuidanceMissileId} is set.
     * Reads mouse delta the same way as Predator cam, plus Shift to trigger top-attack.
     */
    private void handleTruckGuidanceTick(Minecraft mc) {
        guidanceSteerTick++;

        int rawDX = Mouse.getDX();
        int rawDY = Mouse.getDY();
        float sens = mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
        float yawDelta   =  rawDX * sens * 0.15f;
        float pitchDelta = -rawDY * sens * 0.15f;
        yawDelta   = Math.max(-10f, Math.min(10f, yawDelta));
        pitchDelta = Math.max(-8f,  Math.min(8f,  pitchDelta));

        boolean topAttackPressed = org.lwjgl.input.Keyboard.isKeyDown(
            org.lwjgl.input.Keyboard.KEY_LSHIFT);

        if (guidanceSteerTick % 2 == 0) {
            ModNetwork.CHANNEL.sendToServer(
                new PacketMissileSteer(yawDelta, pitchDelta, topAttackPressed));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Key input event (fallback for menus / focus loss)
    // ─────────────────────────────────────────────────────────────────────────

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (predatorCamActive) return; // key input suppressed during nose-cam

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;
        if (player == null) return;
        if (!(player.getRidingEntity() instanceof BasePlaneEntity)) return;

        if (KeyBindings.KEY_TAC_MODE.isPressed()) {
            ModNetwork.CHANNEL.sendToServer(new PacketTacModeToggle());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Getters
    // ─────────────────────────────────────────────────────────────────────────

    public int getSelectedWeaponIndex() { return selectedWeaponIndex; }
}
