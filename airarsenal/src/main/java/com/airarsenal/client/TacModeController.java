package com.airarsenal.client;

import com.airarsenal.entity.plane.BasePlaneEntity;
import com.airarsenal.network.ModNetwork;
import com.airarsenal.network.PacketTacModeToggle;
import com.airarsenal.network.PacketWeaponFire;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;

/**
 * Client-side controller: captures keyboard and mouse input while the player
 * is mounted in a {@link BasePlaneEntity} with Tac Mode active.
 *
 * <p>Registered as a Forge event listener in {@link com.airarsenal.ClientProxy#init}.</p>
 *
 * <h3>Responsibilities:</h3>
 * <ul>
 *   <li>F key → send {@link PacketTacModeToggle}</li>
 *   <li>Left click while Tac Mode active → send {@link PacketWeaponFire}</li>
 *   <li>Scroll wheel while Tac Mode active → cycle weapon index client-side</li>
 * </ul>
 */
@SideOnly(Side.CLIENT)
public class TacModeController {

    /** Client-tracked weapon selection (synced to server via PacketWeaponFire). */
    private int selectedWeaponIndex = 0;

    /** Debounce flag — prevents holding left-click from sending fire packets every tick. */
    private boolean leftWasDown = false;

    // ── Keybinding polling ────────────────────────────────────────────────────

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;
        if (player == null) return;

        // Guard: only act when riding a plane
        if (!(player.getRidingEntity() instanceof BasePlaneEntity)) {
            leftWasDown = false;
            return;
        }

        BasePlaneEntity plane = (BasePlaneEntity) player.getRidingEntity();

        // ── Tac Mode toggle (F key) ───────────────────────────────────────────
        if (KeyBindings.KEY_TAC_MODE.isPressed()) {
            ModNetwork.CHANNEL.sendToServer(new PacketTacModeToggle());
        }

        if (!plane.isTacModeActive()) return;

        // ── Scroll-wheel weapon cycling ───────────────────────────────────────
        int scroll = Mouse.getDWheel();
        if (scroll != 0 && !plane.getWeapons().isEmpty()) {
            int count = plane.getWeapons().size();
            if (scroll > 0) {
                selectedWeaponIndex = (selectedWeaponIndex + 1) % count;
            } else {
                selectedWeaponIndex = (selectedWeaponIndex - 1 + count) % count;
            }
        }

        // ── Left-click fire (debounced — one packet per press, not per tick) ──
        boolean leftDown = Mouse.isButtonDown(0);
        if (leftDown && !leftWasDown) {
            Vec3dCompat dir = getLookVec(player);
            ModNetwork.CHANNEL.sendToServer(new PacketWeaponFire(
                new net.minecraft.util.math.Vec3d(dir.x, dir.y, dir.z),
                selectedWeaponIndex
            ));
        }
        leftWasDown = leftDown;
    }

    // ── Key input event (catches key presses even when MC ignores them) ───────

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;
        if (player == null) return;
        if (!(player.getRidingEntity() instanceof BasePlaneEntity)) return;

        if (KeyBindings.KEY_TAC_MODE.isPressed()) {
            ModNetwork.CHANNEL.sendToServer(new PacketTacModeToggle());
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    public int getSelectedWeaponIndex() {
        return selectedWeaponIndex;
    }

    /** Wraps getLookVec so it can be inlined without a separate import. */
    private static class Vec3dCompat {
        final double x, y, z;
        Vec3dCompat(double x, double y, double z) { this.x = x; this.y = y; this.z = z; }
    }

    private static Vec3dCompat getLookVec(EntityPlayer player) {
        net.minecraft.util.math.Vec3d v = player.getLookVec();
        return new Vec3dCompat(v.x, v.y, v.z);
    }
}
