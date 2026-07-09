package com.airarsenal.item;

import com.airarsenal.AirArsenal;
import com.airarsenal.creativetab.AirArsenalTab;
import com.airarsenal.entity.plane.BasePlaneEntity;
import com.airarsenal.entity.projectile.NapalmBombEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

/**
 * Napalm Canister item — incendiary bomb with wide fire spread.
 * Craftable: Blaze Powder ring around a Lava Bucket, Iron Ingot base.
 */
public class ItemNapalmCanister extends Item {

    public ItemNapalmCanister() {
        setMaxStackSize(8);
        setCreativeTab(AirArsenalTab.INSTANCE);
        setTranslationKey("airarsenal.napalm_canister");
        setRegistryName("airarsenal", "napalm_canister");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);

        if (!(player.getRidingEntity() instanceof BasePlaneEntity)) {
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }

        if (!world.isRemote) {
            BasePlaneEntity plane = (BasePlaneEntity) player.getRidingEntity();
            NapalmBombEntity bomb = new NapalmBombEntity(world, plane);
            world.spawnEntity(bomb);
            AirArsenal.LOGGER.debug("Napalm Canister dropped from {}", plane.getPlaneType());

            if (!player.capabilities.isCreativeMode) {
                stack.shrink(1);
            }
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}
