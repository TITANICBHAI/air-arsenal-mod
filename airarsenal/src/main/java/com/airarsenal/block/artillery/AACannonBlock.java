package com.airarsenal.block.artillery;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * AA Cannon block — auto-targets planes within 80 blocks (see {@link AACannonTileEntity}).
 * No item-based loading; sneak+right-click (handled by the base class) toggles auto/manual.
 */
public class AACannonBlock extends BaseArtilleryBlock {

    public AACannonBlock() {
        super("aa_cannon");
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new AACannonTileEntity();
    }

    @Override
    protected boolean onArtilleryActivated(World world, BlockPos pos, BaseArtilleryTileEntity te,
                                           EntityPlayer player, EnumHand hand) {
        // No manual-fire trigger for AA Cannon in this chunk — auto-mode does all the work.
        // Manual mode simply stops it from firing until switched back to auto.
        return false;
    }
}
