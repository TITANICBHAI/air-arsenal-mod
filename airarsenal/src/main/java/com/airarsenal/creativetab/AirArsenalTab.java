package com.airarsenal.creativetab;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class AirArsenalTab extends CreativeTabs {

    public static final AirArsenalTab INSTANCE = new AirArsenalTab();

    private AirArsenalTab() {
        super("airarsenal");
    }

    @Override
    public ItemStack createIcon() {
        // Placeholder icon — will be replaced with a plane spawn egg or item later
        return new ItemStack(Items.IRON_INGOT);
    }

    @Override
    public String getTranslationKey() {
        return "Air Arsenal";
    }
}
