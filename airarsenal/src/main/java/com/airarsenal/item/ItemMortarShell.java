package com.airarsenal.item;

import com.airarsenal.creativetab.AirArsenalTab;
import net.minecraft.item.Item;

/**
 * Mortar Shell — held item consumed by {@link com.airarsenal.block.artillery.MortarBlock}
 * to load and fire a shot at the block's currently configured elevation angle.
 * Craftable via a simple shaped recipe (see {@link com.airarsenal.AirArsenal#registerRecipes}).
 */
public class ItemMortarShell extends Item {

    public ItemMortarShell() {
        setMaxStackSize(16);
        setCreativeTab(AirArsenalTab.INSTANCE);
        setTranslationKey("airarsenal.mortar_shell");
        setRegistryName("airarsenal", "mortar_shell");
    }
}
