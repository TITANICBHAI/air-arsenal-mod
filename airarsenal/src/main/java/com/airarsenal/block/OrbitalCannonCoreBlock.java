package com.airarsenal.block;

import com.airarsenal.creativetab.AirArsenalTab;
import com.airarsenal.item.ItemOrbitalDesignator;
import com.airarsenal.registry.ModGuiHandler;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * The Orbital Cannon's controller block. Deliberately extends {@link Block}
 * (not {@code BlockContainer}, which is deprecated in modern 1.12.2 Forge and
 * causes TESR rendering glitches) and opts into a TileEntity via
 * {@link #hasTileEntity(IBlockState)} / {@link #createTileEntity}.
 */
public class OrbitalCannonCoreBlock extends Block {

    public OrbitalCannonCoreBlock() {
        super(Material.IRON);
        setHardness(15.0f);
        setResistance(30.0f);
        setCreativeTab(AirArsenalTab.INSTANCE);
        setTranslationKey("airarsenal.orbital_cannon_core");
        setRegistryName("airarsenal", "orbital_cannon_core");
    }

    @Override
    public boolean hasTileEntity(IBlockState state) { return true; }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new OrbitalCannonTileEntity();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        // Keep the normal cube model — the rod/glow effect is added on top via OrbitalCannonTESR.
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                                    EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) return true;

        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof OrbitalCannonTileEntity)) return false;

        BlockPos designatorTarget = ItemOrbitalDesignator.findTargetInInventory(player);
        if (designatorTarget != null) {
            ((OrbitalCannonTileEntity) te).setTargetPos(designatorTarget);
        }

        player.openGui(com.airarsenal.AirArsenal.instance, ModGuiHandler.GUI_ORBITAL_CANNON,
            world, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof OrbitalCannonTileEntity) {
            ItemStack card = ((OrbitalCannonTileEntity) te).getStackInSlot(0);
            if (!card.isEmpty()) {
                net.minecraft.entity.item.EntityItem drop = new net.minecraft.entity.item.EntityItem(
                    world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, card.copy());
                world.spawnEntity(drop);
            }
        }
        super.breakBlock(world, pos, state);
    }
}
