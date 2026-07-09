package com.airarsenal.item;

import com.airarsenal.AirArsenal;
import com.airarsenal.creativetab.AirArsenalTab;
import com.airarsenal.entity.plane.IronMonoplaneEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Spawn item for the {@link IronMonoplaneEntity}.
 * Right-click any solid surface to place the plane one block above it.
 */
public class ItemIronMonoplane extends Item {

    public ItemIronMonoplane() {
        setMaxStackSize(1);
        setCreativeTab(AirArsenalTab.INSTANCE);
        setTranslationKey("airarsenal.iron_monoplane");
        setRegistryName("airarsenal", "iron_monoplane");
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos,
                                      EnumHand hand, EnumFacing facing,
                                      float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return EnumActionResult.SUCCESS;
        }

        double spawnX = pos.getX() + 0.5;
        double spawnY = pos.getY() + 1.0;
        double spawnZ = pos.getZ() + 0.5;

        IronMonoplaneEntity plane = new IronMonoplaneEntity(world);
        plane.setPosition(spawnX, spawnY, spawnZ);
        plane.rotationYaw = player.rotationYaw;

        world.spawnEntity(plane);
        AirArsenal.LOGGER.info("Spawned IronMonoplaneEntity at ({}, {}, {})", spawnX, spawnY, spawnZ);

        ItemStack held = player.getHeldItem(hand);
        if (!player.capabilities.isCreativeMode) {
            held.shrink(1);
        }

        return EnumActionResult.SUCCESS;
    }
}
