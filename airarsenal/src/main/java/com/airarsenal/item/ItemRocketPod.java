package com.airarsenal.item;

import com.airarsenal.creativetab.AirArsenalTab;
import net.minecraft.item.Item;

/**
 * Rocket Pod — a pack of 8 rockets, consumed whole to reload
 * {@link com.airarsenal.block.artillery.MLRSTileEntity}. Stack size 1 (it represents a
 * single pod, not a stack of loose rockets).
 */
public class ItemRocketPod extends Item {

    public static final int ROCKETS_PER_POD = 8;

    public ItemRocketPod() {
        setMaxStackSize(4);
        setCreativeTab(AirArsenalTab.INSTANCE);
        setTranslationKey("airarsenal.rocket_pod");
        setRegistryName("airarsenal", "rocket_pod");
    }
}
