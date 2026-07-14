package com.airarsenal.block.artillery;

import com.airarsenal.item.ItemMortarShell;
import com.airarsenal.registry.ModGuiHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Mortar block — right-click with {@link ItemMortarShell} to load + fire at the
 * currently configured elevation angle; right-click empty-handed to open the
 * angle-adjustment GUI ({@link com.airarsenal.client.gui.GuiMortar}).
 */
public class MortarBlock extends BaseArtilleryBlock {

    public MortarBlock() {
        super("mortar");
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new MortarTileEntity();
    }

    @Override
    protected boolean onArtilleryActivated(World world, BlockPos pos, BaseArtilleryTileEntity teBase,
                                           EntityPlayer player, EnumHand hand) {
        if (!(teBase instanceof MortarTileEntity)) return false;
        MortarTileEntity te = (MortarTileEntity) teBase;

        ItemStack held = player.getHeldItem(hand);
        if (held.getItem() instanceof ItemMortarShell) {
            if (!world.isRemote) {
                te.fire(world, player);
                if (!player.capabilities.isCreativeMode) held.shrink(1);
            }
            return true;
        }

        if (held.isEmpty() && !world.isRemote) {
            player.openGui(com.airarsenal.AirArsenal.instance,
                ModGuiHandler.GUI_MORTAR, world, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }
}
