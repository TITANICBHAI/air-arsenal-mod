package com.airarsenal.item;

import com.airarsenal.creativetab.AirArsenalTab;
import com.airarsenal.entity.plane.BasePlaneEntity;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Jet Fuel — bucket-like refueling item introduced by the Chunk 9 fuel system.
 * Right-click while mounted in any {@link BasePlaneEntity} to add 25 fuel,
 * capped at {@link com.airarsenal.entity.plane.component.FuelSystem#MAX_FUEL}.
 *
 * <p>Craftable from a Blaze Rod, a Lava Bucket, and a Coal Block (see
 * {@link com.airarsenal.AirArsenal#registerRecipes()}).</p>
 *
 * <p>One item = one refuel; consumed on use (not returned as an empty bucket —
 * a deliberate simplification since jet fuel isn't a vanilla fluid).</p>
 */
public class ItemJetFuel extends Item {

    public static final float REFUEL_AMOUNT = 25f;

    public ItemJetFuel() {
        setMaxStackSize(16);
        setCreativeTab(AirArsenalTab.INSTANCE);
        setTranslationKey("airarsenal.jet_fuel");
        setRegistryName("airarsenal", "jet_fuel");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);

        Entity riding = player.getRidingEntity();
        if (!(riding instanceof BasePlaneEntity)) {
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }

        if (!world.isRemote) {
            BasePlaneEntity plane = (BasePlaneEntity) riding;
            plane.getFuelSystem().refuel(REFUEL_AMOUNT);
            if (!player.isCreative()) {
                stack.shrink(1);
            }
            player.sendMessage(new net.minecraft.util.text.TextComponentString(
                TextFormatting.AQUA + "[Jet Fuel] +" + (int) REFUEL_AMOUNT + " fuel"));
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world,
                               List<String> tooltip, ITooltipFlag flag) {
        tooltip.add(TextFormatting.GRAY + "Right-click while mounted in a plane to refuel");
        tooltip.add(TextFormatting.DARK_AQUA + "+" + (int) REFUEL_AMOUNT + " fuel per use");
    }
}
