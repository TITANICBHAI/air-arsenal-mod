package com.airarsenal.client;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

/**
 * All Air Arsenal keybindings.
 * Registered in {@link com.airarsenal.ClientProxy#preInit}.
 *
 * Scroll-wheel weapon cycling is handled in {@link TacModeController}
 * via {@code MouseEvent} — it cannot be expressed as a standard KeyBinding.
 */
public final class KeyBindings {

    public static final String CATEGORY = "Air Arsenal";

    /** Toggle Tac Mode overlay (default: F). */
    public static KeyBinding KEY_TAC_MODE;

    /** Cycle to next weapon hardpoint (handled via scroll-up in TacModeController). */
    public static KeyBinding KEY_NEXT_WEAPON;

    /** Cycle to previous weapon hardpoint (handled via scroll-down in TacModeController). */
    public static KeyBinding KEY_PREV_WEAPON;

    /**
     * Shared "special action" key (Chunk 9): activates the Fighter Jet's afterburner
     * or toggles the Stealth Bomber's stealth mode, depending on the plane ridden.
     * Routed via {@code PacketSpecialAction}.
     */
    public static KeyBinding KEY_SPECIAL_ACTION;

    /** Attack Helicopter strafe left (Chunk 9). */
    public static KeyBinding KEY_STRAFE_LEFT;

    /** Attack Helicopter strafe right (Chunk 9). */
    public static KeyBinding KEY_STRAFE_RIGHT;

    private KeyBindings() {}

    public static void register() {
        KEY_TAC_MODE = new KeyBinding("key.airarsenal.tac_mode",  Keyboard.KEY_F,    CATEGORY);
        KEY_NEXT_WEAPON = new KeyBinding("key.airarsenal.next_weapon", Keyboard.KEY_NONE, CATEGORY);
        KEY_PREV_WEAPON = new KeyBinding("key.airarsenal.prev_weapon", Keyboard.KEY_NONE, CATEGORY);
        KEY_SPECIAL_ACTION = new KeyBinding("key.airarsenal.special_action", Keyboard.KEY_G, CATEGORY);
        KEY_STRAFE_LEFT  = new KeyBinding("key.airarsenal.strafe_left",  Keyboard.KEY_Q, CATEGORY);
        KEY_STRAFE_RIGHT = new KeyBinding("key.airarsenal.strafe_right", Keyboard.KEY_E, CATEGORY);

        ClientRegistry.registerKeyBinding(KEY_TAC_MODE);
        ClientRegistry.registerKeyBinding(KEY_NEXT_WEAPON);
        ClientRegistry.registerKeyBinding(KEY_PREV_WEAPON);
        ClientRegistry.registerKeyBinding(KEY_SPECIAL_ACTION);
        ClientRegistry.registerKeyBinding(KEY_STRAFE_LEFT);
        ClientRegistry.registerKeyBinding(KEY_STRAFE_RIGHT);
    }
}
