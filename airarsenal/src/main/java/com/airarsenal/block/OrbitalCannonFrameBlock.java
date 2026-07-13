package com.airarsenal.block;

import com.airarsenal.creativetab.AirArsenalTab;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;

/**
 * Purely structural block — one of the four cardinal-adjacent blocks required
 * around an {@link OrbitalCannonCoreBlock} for it to validate and fire.
 * No TileEntity, no interaction logic.
 */
public class OrbitalCannonFrameBlock extends Block {

    public OrbitalCannonFrameBlock() {
        super(Material.IRON, MapColor.IRON_BLOCK_COLOR);
        setHardness(10.0f);
        setResistance(20.0f);
        setCreativeTab(AirArsenalTab.INSTANCE);
        setTranslationKey("airarsenal.orbital_cannon_frame");
        setRegistryName("airarsenal", "orbital_cannon_frame");
    }

    @Override
    public MapColor getMapColor(IBlockState state, net.minecraft.world.IBlockAccess worldIn, net.minecraft.util.math.BlockPos pos) {
        return MapColor.IRON_BLOCK_COLOR;
    }
}
