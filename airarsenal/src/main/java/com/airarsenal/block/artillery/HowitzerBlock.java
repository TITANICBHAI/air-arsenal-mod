package com.airarsenal.block.artillery;

import com.airarsenal.item.ItemLaserDesignator;
import com.airarsenal.registry.ModGuiHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class HowitzerBlock extends BaseArtilleryBlock {

    public HowitzerBlock() {
        super("howitzer");
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new HowitzerTileEntity();
    }

    @Override
    protected boolean onArtilleryActivated(World world, BlockPos pos, BaseArtilleryTileEntity teBase,
                                           EntityPlayer player, EnumHand hand) {
        if (!(teBase instanceof HowitzerTileEntity)) return false;
        HowitzerTileEntity te = (HowitzerTileEntity) teBase;

        // Sneak + right-click (handled here since we need to check the empty-hand case
        // ourselves — the base class intercepts sneak+right-click for mode toggling,
        // but Howitzer has no auto/manual concept, so we repurpose sneak here for the GUI).
        if (player.isSneaking()) {
            if (!world.isRemote) {
                player.openGui(com.airarsenal.AirArsenal.instance,
                    ModGuiHandler.GUI_HOWITZER, world, pos.getX(), pos.getY(), pos.getZ());
            }
            return true;
        }

        if (world.isRemote) return true;

        @Nullable BlockPos laserMark = ItemLaserDesignator.getTarget(player);
        if (laserMark != null && !laserMark.equals(te.getTargetPos())) {
            boolean accepted = te.setTargetPos(laserMark);
            player.sendMessage(new TextComponentString(accepted
                ? TextFormatting.GOLD + "[Howitzer] Target synced from laser mark."
                : TextFormatting.RED + "[Howitzer] Laser mark out of range (20-150 blocks)."));
            return true;
        }

        if (te.getTargetPos() != null) {
            te.fire(world);
            player.sendMessage(new TextComponentString(TextFormatting.GRAY + "[Howitzer] Fired."));
        } else {
            player.sendMessage(new TextComponentString(TextFormatting.RED
                + "[Howitzer] No target — sneak+right-click to enter coordinates, or mark one with a Laser Designator."));
        }
        return true;
    }
}
