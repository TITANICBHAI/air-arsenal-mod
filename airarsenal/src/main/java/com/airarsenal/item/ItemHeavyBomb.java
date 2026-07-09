package com.airarsenal.item;

import com.airarsenal.AirArsenal;
import com.airarsenal.creativetab.AirArsenalTab;
import com.airarsenal.entity.plane.BasePlaneEntity;
import com.airarsenal.entity.projectile.HeavyBombEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

/**
 * Heavy Bomb item — 14-block blast + 4-block deep crater.
 * Craftable: Iron Bomb surrounded by 4 Iron Blocks.
 */
public class ItemHeavyBomb extends Item {

    public ItemHeavyBomb() {
        setMaxStackSize(8);
        setCreativeTab(AirArsenalTab.INSTANCE);
        setTranslationKey("airarsenal.heavy_bomb");
        setRegistryName("airarsenal", "heavy_bomb");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);

        if (!(player.getRidingEntity() instanceof BasePlaneEntity)) {
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }

        if (!world.isRemote) {
            BasePlaneEntity plane = (BasePlaneEntity) player.getRidingEntity();
            HeavyBombEntity bomb = new HeavyBombEntity(world, plane);
            world.spawnEntity(bomb);
            AirArsenal.LOGGER.debug("Heavy Bomb dropped from {}", plane.getPlaneType());

            if (!player.capabilities.isCreativeMode) {
                stack.shrink(1);
            }
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}
