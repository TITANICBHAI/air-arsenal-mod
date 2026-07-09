package com.airarsenal.item;

import com.airarsenal.AirArsenal;
import com.airarsenal.creativetab.AirArsenalTab;
import com.airarsenal.entity.plane.BasePlaneEntity;
import com.airarsenal.entity.projectile.IronBombEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

/**
 * Iron Bomb item — right-click while mounted in any plane to drop.
 * Craftable via shaped recipe (see {@link com.airarsenal.AirArsenal#registerRecipes}).
 */
public class ItemIronBomb extends Item {

    public ItemIronBomb() {
        setMaxStackSize(16);
        setCreativeTab(AirArsenalTab.INSTANCE);
        setTranslationKey("airarsenal.iron_bomb");
        setRegistryName("airarsenal", "iron_bomb");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);

        if (!(player.getRidingEntity() instanceof BasePlaneEntity)) {
            // Must be mounted to drop a bomb
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }

        if (!world.isRemote) {
            BasePlaneEntity plane = (BasePlaneEntity) player.getRidingEntity();
            IronBombEntity bomb = new IronBombEntity(world, plane);
            world.spawnEntity(bomb);
            AirArsenal.LOGGER.debug("Iron Bomb dropped from {}", plane.getPlaneType());

            if (!player.capabilities.isCreativeMode) {
                stack.shrink(1);
            }
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}
