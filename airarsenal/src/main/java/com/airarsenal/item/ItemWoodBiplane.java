package com.airarsenal.item;

import com.airarsenal.AirArsenal;
import com.airarsenal.creativetab.AirArsenalTab;
import com.airarsenal.entity.plane.WoodBiplaneEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Spawn item for the {@link WoodBiplaneEntity}.
 * Right-click any solid surface to place the biplane one block above it.
 */
public class ItemWoodBiplane extends Item {

    public ItemWoodBiplane() {
        setMaxStackSize(1);
        setCreativeTab(AirArsenalTab.INSTANCE);
        setTranslationKey("airarsenal.wood_biplane");
        setRegistryName("airarsenal", "wood_biplane");
    }

    /**
     * Right-click on a block face → spawn the Wood Biplane one block above the clicked position.
     */
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos,
                                      EnumHand hand, EnumFacing facing,
                                      float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            // Client side: let the server do the spawning
            return EnumActionResult.SUCCESS;
        }

        // Spawn slightly above the clicked block's top face
        double spawnX = pos.getX() + 0.5;
        double spawnY = pos.getY() + 1.0;
        double spawnZ = pos.getZ() + 0.5;

        WoodBiplaneEntity plane = new WoodBiplaneEntity(world);
        plane.setPosition(spawnX, spawnY, spawnZ);
        plane.rotationYaw = player.rotationYaw; // face the same direction as the placer

        world.spawnEntity(plane);
        AirArsenal.LOGGER.info("Spawned WoodBiplaneEntity at ({}, {}, {})", spawnX, spawnY, spawnZ);

        // Consume one item in survival mode
        ItemStack held = player.getHeldItem(hand);
        if (!player.capabilities.isCreativeMode) {
            held.shrink(1);
        }

        return EnumActionResult.SUCCESS;
    }
}
