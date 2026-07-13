package com.airarsenal.item;

import com.airarsenal.creativetab.AirArsenalTab;
import net.minecraft.item.Item;

/**
 * Heavy Round — crafting/trade material dropped by {@link com.airarsenal.entity.vehicle.ArmoredTruckEntity}
 * (chance-based). Not itself a projectile or weapon in this chunk.
 */
public class ItemHeavyRound extends Item {

    public ItemHeavyRound() {
        setMaxStackSize(64);
        setCreativeTab(AirArsenalTab.INSTANCE);
        setTranslationKey("airarsenal.heavy_round");
        setRegistryName("airarsenal", "heavy_round");
    }
}
