package com.airarsenal.block.artillery;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Flak Battery block — long-range auto-targeting AA with proximity-fused shells
 * (see {@link FlakBatteryTileEntity}).
 */
public class FlakBatteryBlock extends BaseArtilleryBlock {

    public FlakBatteryBlock() {
        super("flak_battery");
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new FlakBatteryTileEntity();
    }

    @Override
    protected boolean onArtilleryActivated(World world, BlockPos pos, BaseArtilleryTileEntity te,
                                           EntityPlayer player, EnumHand hand) {
        return false;
    }
}
