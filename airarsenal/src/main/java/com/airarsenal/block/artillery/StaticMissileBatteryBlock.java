package com.airarsenal.block.artillery;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

/**
 * Static Missile Battery block — single-tube guided missile emplacement.
 * Right-click (empty hand) fires immediately at the block's facing direction / player heading;
 * see {@link StaticMissileBatteryTileEntity#fire}.
 */
public class StaticMissileBatteryBlock extends BaseArtilleryBlock {

    public StaticMissileBatteryBlock() {
        super("static_missile_battery");
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new StaticMissileBatteryTileEntity();
    }

    @Override
    protected boolean onArtilleryActivated(World world, BlockPos pos, BaseArtilleryTileEntity teBase,
                                           EntityPlayer player, EnumHand hand) {
        if (!(teBase instanceof StaticMissileBatteryTileEntity)) return false;
        if (world.isRemote || !(player instanceof EntityPlayerMP)) return true;

        StaticMissileBatteryTileEntity te = (StaticMissileBatteryTileEntity) teBase;
        if (te.fire((EntityPlayerMP) player)) {
            player.sendMessage(new TextComponentString(TextFormatting.GRAY + "[Missile Battery] Launched."));
        } else {
            player.sendMessage(new TextComponentString(TextFormatting.RED + "[Missile Battery] Reloading."));
        }
        return true;
    }
}
