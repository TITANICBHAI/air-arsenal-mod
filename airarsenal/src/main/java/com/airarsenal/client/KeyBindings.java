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

    private KeyBindings() {}

    public static void register() {
        KEY_TAC_MODE = new KeyBinding("key.airarsenal.tac_mode",  Keyboard.KEY_F,    CATEGORY);
        KEY_NEXT_WEAPON = new KeyBinding("key.airarsenal.next_weapon", Keyboard.KEY_NONE, CATEGORY);
        KEY_PREV_WEAPON = new KeyBinding("key.airarsenal.prev_weapon", Keyboard.KEY_NONE, CATEGORY);

        ClientRegistry.registerKeyBinding(KEY_TAC_MODE);
        ClientRegistry.registerKeyBinding(KEY_NEXT_WEAPON);
        ClientRegistry.registerKeyBinding(KEY_PREV_WEAPON);
    }
}
