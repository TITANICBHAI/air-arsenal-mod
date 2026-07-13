package com.airarsenal.item;

import com.airarsenal.config.AirArsenalConfig;
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

import javax.annotation.Nullable;
import java.util.List;

/**
 * Orbital Cannon target designator — right-click a block to paint a strike
 * target, right-click air to clear it. Read by {@link com.airarsenal.block.OrbitalCannonCoreBlock}
 * when the cannon's GUI is opened.
 *
 * <p>Not itself networked — the saved {@code targetPos} lives entirely in this
 * ItemStack's NBT, so it travels with the item, matching {@link ItemBrahMosTargeter}'s
 * pattern from Chunk 7.</p>
 */
public class ItemOrbitalDesignator extends Item {

    private static final String TAG_TARGET_POS = "targetPos";

    public ItemOrbitalDesignator() {
        setTranslationKey("airarsenal.orbital_designator");
        setRegistryName("airarsenal", "orbital_designator");
        setCreativeTab(AirArsenalTab.INSTANCE);
        setMaxStackSize(1);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);

        RayTraceResult trace = rayTraceFromPlayer(world, player);
        if (trace == null || trace.typeOfHit != RayTraceResult.Type.BLOCK) {
            // Right-clicked air — clear the saved target.
            NBTTagCompound tag = stack.getTagCompound();
            if (tag != null) tag.removeTag(TAG_TARGET_POS);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }

        BlockPos hitPos = trace.getBlockPos();
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        }
        tag.setLong(TAG_TARGET_POS, hitPos.toLong());
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Nullable
    private RayTraceResult rayTraceFromPlayer(World world, EntityPlayer player) {
        double reach = 32.0;
        net.minecraft.util.math.Vec3d start = player.getPositionEyes(1.0f);
        net.minecraft.util.math.Vec3d look = player.getLook(1.0f);
        net.minecraft.util.math.Vec3d end = start.add(look.x * reach, look.y * reach, look.z * reach);
        return world.rayTraceBlocks(start, end, false, false, true);
    }

    /** @return the saved target, or {@code null} if none is set. */
    @Nullable
    public static BlockPos getTarget(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null || !tag.hasKey(TAG_TARGET_POS)) return null;
        return BlockPos.fromLong(tag.getLong(TAG_TARGET_POS));
    }

    /** Scans the player's inventory for the first designator carrying a saved target. */
    @Nullable
    public static BlockPos findTargetInInventory(EntityPlayer player) {
        for (ItemStack stack : player.inventory.mainInventory) {
            if (stack.getItem() instanceof ItemOrbitalDesignator) {
                BlockPos target = getTarget(stack);
                if (target != null) return target;
            }
        }
        return null;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);
        BlockPos target = getTarget(stack);
        if (target == null) {
            tooltip.add(TextFormatting.GRAY + "NO TARGET SET");
            return;
        }
        tooltip.add(TextFormatting.AQUA + "TARGET: " + target.getX() + ", " + target.getY() + ", " + target.getZ());
        // Best-effort range check against the tooltip-viewing player (client-side world).
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getMinecraft();
        if (mc.player != null) {
            double dist = Math.sqrt(mc.player.getDistanceSq(target));
            if (dist > AirArsenalConfig.orbitalMaxRange) {
                tooltip.add(TextFormatting.RED + "OUT OF RANGE");
            }
        }
    }
}
