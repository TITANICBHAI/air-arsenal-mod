package com.airarsenal.item;

import com.airarsenal.creativetab.AirArsenalTab;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Drone Controller — right-click a {@link com.airarsenal.entity.plane.PredatorDroneEntity}
 * to start remote-pilot mode (Chunk 9). Not craftable — creative tab / loot only.
 *
 * <p>The actual interaction is handled in
 * {@link com.airarsenal.entity.plane.PredatorDroneEntity#processInitialInteract}
 * (entity right-click dispatch checks the held item before falling back to the
 * default mount behavior), not here — this class only defines the item itself.</p>
 */
public class ItemDroneController extends Item {

    public ItemDroneController() {
        setMaxStackSize(1);
        setCreativeTab(AirArsenalTab.INSTANCE);
        setTranslationKey("airarsenal.drone_controller");
        setRegistryName("airarsenal", "drone_controller");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world,
                               List<String> tooltip, ITooltipFlag flag) {
        tooltip.add(TextFormatting.GRAY + "Right-click a Predator Drone to remote-pilot it");
        tooltip.add(TextFormatting.DARK_GRAY + "Press Shift to exit — the drone keeps flying");
    }
}
