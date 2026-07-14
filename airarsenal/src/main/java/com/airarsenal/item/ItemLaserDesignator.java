package com.airarsenal.item;

import com.airarsenal.creativetab.AirArsenalTab;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Laser Designator — marks a ground {@link BlockPos} for the Howitzer's two-player
 * "spotter + gunner" workflow. Same storage pattern as {@link ItemBrahMosTargeter},
 * kept on a separate NBT key so both targeters can be used independently.
 */
public class ItemLaserDesignator extends Item {

    public static final String NBT_TARGET_KEY = "laser_target";

    public ItemLaserDesignator() {
        setMaxStackSize(1);
        setCreativeTab(AirArsenalTab.INSTANCE);
        setTranslationKey("airarsenal.laser_designator");
        setRegistryName("airarsenal", "laser_designator");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        RayTraceResult rtr = player.rayTrace(150.0, 1.0f);

        if (rtr != null && rtr.typeOfHit == RayTraceResult.Type.BLOCK) {
            BlockPos pos = rtr.getBlockPos();
            saveTarget(player, pos);
            if (!world.isRemote) {
                player.sendMessage(new net.minecraft.util.text.TextComponentString(
                    TextFormatting.GOLD + "[Laser Designator] Marked: "
                    + pos.getX() + ", " + pos.getY() + ", " + pos.getZ()));
            }
        } else {
            clearTarget(player);
            if (!world.isRemote) {
                player.sendMessage(new net.minecraft.util.text.TextComponentString(
                    TextFormatting.YELLOW + "[Laser Designator] Mark cleared."));
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world,
                               List<String> tooltip, ITooltipFlag flag) {
        tooltip.add(TextFormatting.GRAY + "Right-click block: mark Howitzer target");
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getMinecraft();
        if (mc.player != null) {
            BlockPos target = getTarget(mc.player);
            if (target != null) {
                tooltip.add(TextFormatting.GOLD + "Marked: " + TextFormatting.WHITE
                    + target.getX() + ", " + target.getY() + ", " + target.getZ());
            } else {
                tooltip.add(TextFormatting.DARK_GRAY + "No mark set");
            }
        }
    }

    public static void saveTarget(EntityPlayer player, BlockPos pos) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("X", pos.getX());
        tag.setInteger("Y", pos.getY());
        tag.setInteger("Z", pos.getZ());
        player.getEntityData().setTag(NBT_TARGET_KEY, tag);
    }

    @Nullable
    public static BlockPos getTarget(EntityPlayer player) {
        NBTTagCompound data = player.getEntityData();
        if (!data.hasKey(NBT_TARGET_KEY)) return null;
        NBTTagCompound tag = data.getCompoundTag(NBT_TARGET_KEY);
        return new BlockPos(tag.getInteger("X"), tag.getInteger("Y"), tag.getInteger("Z"));
    }

    public static void clearTarget(EntityPlayer player) {
        player.getEntityData().removeTag(NBT_TARGET_KEY);
    }
}
