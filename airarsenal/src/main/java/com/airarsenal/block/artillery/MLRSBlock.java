package com.airarsenal.block.artillery;

import com.airarsenal.item.ItemRocketPod;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

/**
 * MLRS block — right-click with {@link ItemRocketPod} to load 8 rockets;
 * right-click empty-handed once loaded to fire the full burst.
 */
public class MLRSBlock extends BaseArtilleryBlock {

    public MLRSBlock() {
        super("mlrs");
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new MLRSTileEntity();
    }

    @Override
    protected boolean onArtilleryActivated(World world, BlockPos pos, BaseArtilleryTileEntity teBase,
                                           EntityPlayer player, EnumHand hand) {
        if (!(teBase instanceof MLRSTileEntity)) return false;
        MLRSTileEntity te = (MLRSTileEntity) teBase;
        ItemStack held = player.getHeldItem(hand);

        if (held.getItem() instanceof ItemRocketPod) {
            if (world.isRemote) return true;
            if (te.canLoad()) {
                te.load();
                if (!player.capabilities.isCreativeMode) held.shrink(1);
                player.sendMessage(new TextComponentString(TextFormatting.GRAY + "[MLRS] Pod loaded — 8 rockets."));
            } else {
                player.sendMessage(new TextComponentString(TextFormatting.RED + "[MLRS] Cannot reload yet."));
            }
            return true;
        }

        if (held.isEmpty()) {
            if (world.isRemote) return true;
            if (te.canFireBurst()) {
                te.startBurst();
                player.sendMessage(new TextComponentString(TextFormatting.GRAY + "[MLRS] Firing burst!"));
            } else {
                player.sendMessage(new TextComponentString(TextFormatting.RED + "[MLRS] Not loaded."));
            }
            return true;
        }

        return false;
    }
}
