package com.airarsenal.item;

import com.airarsenal.creativetab.AirArsenalTab;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Single-use authorisation card consumed by {@link com.airarsenal.block.OrbitalCannonTileEntity}
 * when a strike is fired. Purely a crafting-gated ammo item — carries no NBT state.
 */
public class ItemSatelliteUplinkCard extends Item {

    public ItemSatelliteUplinkCard() {
        setTranslationKey("airarsenal.satellite_uplink_card");
        setRegistryName("airarsenal", "satellite_uplink_card");
        setCreativeTab(AirArsenalTab.INSTANCE);
        setMaxStackSize(16);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);
        tooltip.add(TextFormatting.GRAY + "Authorises one orbital strike. Handle with care.");
    }
}
