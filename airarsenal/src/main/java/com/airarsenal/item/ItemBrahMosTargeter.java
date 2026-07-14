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
 * BrahMos Targeter — sets a ground waypoint for the BrahMos cruise missile.
 *
 * <ul>
 *   <li><b>Right-click on a block</b>: saves the block's {@link BlockPos} to the player's
 *       persistent NBT under the key {@code "brahmos_target"}.</li>
 *   <li><b>Right-click in air</b>: clears the saved target.</li>
 *   <li><b>Tooltip</b>: displays saved target coordinates (client-side).</li>
 * </ul>
 *
 * Not craftable — creative tab / loot only.
 */
public class ItemBrahMosTargeter extends Item {

    /** NBT key on the player's persistent data where the target BlockPos is stored. */
    public static final String NBT_TARGET_KEY = "brahmos_target";

    public ItemBrahMosTargeter() {
        setMaxStackSize(1);
        setCreativeTab(AirArsenalTab.INSTANCE);
        setTranslationKey("airarsenal.brahmos_targeter");
        setRegistryName("airarsenal", "brahmos_targeter");
    }

    // ── Right-click ───────────────────────────────────────────────────────────

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);

        // Ray-trace from player eyes to find a block within reach
        RayTraceResult rtr = player.rayTrace(100.0, 1.0f);

        if (rtr != null && rtr.typeOfHit == RayTraceResult.Type.BLOCK) {
            BlockPos pos = rtr.getBlockPos();
            saveTarget(player, pos);
            if (!world.isRemote) {
                player.sendMessage(new net.minecraft.util.text.TextComponentString(
                    TextFormatting.GOLD + "[BrahMos] Target set: "
                    + pos.getX() + ", " + pos.getY() + ", " + pos.getZ()
                ));
            }
        } else {
            // Looking into the sky / no block in range — clear target
            clearTarget(player);
            if (!world.isRemote) {
                player.sendMessage(new net.minecraft.util.text.TextComponentString(
                    TextFormatting.YELLOW + "[BrahMos] Target cleared."
                ));
            }
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    // ── Tooltip ───────────────────────────────────────────────────────────────

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world,
                               List<String> tooltip, ITooltipFlag flag) {
        tooltip.add(TextFormatting.GRAY + "Right-click block: set BrahMos waypoint");
        tooltip.add(TextFormatting.GRAY + "Right-click air:   clear waypoint");
        tooltip.add(TextFormatting.DARK_AQUA + "Max range: unlimited");

        // Show live target coordinates read from the holding player's persistent data
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getMinecraft();
        if (mc.player != null) {
            BlockPos target = getTarget(mc.player);
            if (target != null) {
                tooltip.add(TextFormatting.GOLD + "Target: "
                    + TextFormatting.WHITE
                    + target.getX() + ", " + target.getY() + ", " + target.getZ());
            } else {
                tooltip.add(TextFormatting.DARK_GRAY + "No target set");
            }
        }
    }

    // ── NBT helpers (static — called by launch code) ──────────────────────────

    /**
     * Saves a {@link BlockPos} waypoint to the player's Forge persistent tag.
     * The persistent tag survives dimension changes and reconnects.
     */
    public static void saveTarget(EntityPlayer player, BlockPos pos) {
        NBTTagCompound data = player.getEntityData();
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("X", pos.getX());
        tag.setInteger("Y", pos.getY());
        tag.setInteger("Z", pos.getZ());
        data.setTag(NBT_TARGET_KEY, tag);
    }

    /**
     * Retrieves the stored waypoint, or {@code null} if none is set.
     */
    @Nullable
    public static BlockPos getTarget(EntityPlayer player) {
        NBTTagCompound data = player.getEntityData();
        if (!data.hasKey(NBT_TARGET_KEY)) return null;
        NBTTagCompound tag = data.getCompoundTag(NBT_TARGET_KEY);
        return new BlockPos(
            tag.getInteger("X"),
            tag.getInteger("Y"),
            tag.getInteger("Z")
        );
    }

    /** Removes the stored waypoint. */
    public static void clearTarget(EntityPlayer player) {
        player.getEntityData().removeTag(NBT_TARGET_KEY);
    }
}
