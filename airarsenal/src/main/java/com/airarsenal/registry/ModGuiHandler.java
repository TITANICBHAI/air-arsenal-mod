package com.airarsenal.registry;

import com.airarsenal.block.artillery.HowitzerTileEntity;
import com.airarsenal.block.artillery.MortarTileEntity;
import com.airarsenal.client.gui.ContainerArtilleryEmpty;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

/**
 * Central GUI dispatcher for Air Arsenal's data-only block GUIs (angle sliders,
 * coordinate entry — no inventory slots). Registered once in {@code CommonProxy.preInit}
 * via {@code NetworkRegistry.INSTANCE.registerGuiHandler(AirArsenal.instance, new ModGuiHandler())}.
 */
public class ModGuiHandler implements IGuiHandler {

    public static final int GUI_MORTAR   = 0;
    public static final int GUI_HOWITZER = 1;

    @Override
    @Nullable
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
        switch (id) {
            case GUI_MORTAR:
                if (te instanceof MortarTileEntity) return new ContainerArtilleryEmpty();
                return null;
            case GUI_HOWITZER:
                if (te instanceof HowitzerTileEntity) return new ContainerArtilleryEmpty();
                return null;
            default:
                return null;
        }
    }

    @Override
    @Nullable
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
        switch (id) {
            case GUI_MORTAR:
                if (te instanceof MortarTileEntity) {
                    return new com.airarsenal.client.gui.GuiMortar((MortarTileEntity) te, new BlockPos(x, y, z));
                }
                return null;
            case GUI_HOWITZER:
                if (te instanceof HowitzerTileEntity) {
                    return new com.airarsenal.client.gui.GuiHowitzer((HowitzerTileEntity) te, new BlockPos(x, y, z));
                }
                return null;
            default:
                return null;
        }
    }
}
