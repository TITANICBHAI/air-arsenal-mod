package com.airarsenal.block.artillery;

import com.airarsenal.creativetab.AirArsenalTab;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Shared base for every placeable artillery / AA emplacement block.
 *
 * <ul>
 *   <li>Extends {@link BlockHorizontal} — placed facing the player (N/S/E/W).</li>
 *   <li>Always has a paired TileEntity (see {@link #createNewTileEntity}).</li>
 *   <li>Sneak + right-click (empty hand) → toggle auto/manual mode.</li>
 *   <li>Normal right-click is dispatched to {@link #onArtilleryActivated} for
 *       subclasses to implement item-based interactions (load ammo, fire, etc).</li>
 * </ul>
 */
public abstract class BaseArtilleryBlock extends BlockHorizontal {

    protected BaseArtilleryBlock(String registryName) {
        super(Material.IRON);
        setDefaultState(getBlockState().getBaseState().withProperty(FACING, EnumFacing.NORTH));
        setHardness(5.0f);
        setResistance(10.0f);
        setCreativeTab(AirArsenalTab.INSTANCE);
        setTranslationKey("airarsenal." + registryName);
        setRegistryName("airarsenal", registryName);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) { return true; }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing,
                                            float hitX, float hitY, float hitZ,
                                            int meta, EntityLivingBase placer) {
        return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    // ── Interaction dispatch ─────────────────────────────────────────────────

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state,
                                    EntityPlayer player, EnumHand hand,
                                    EnumFacing side, float hitX, float hitY, float hitZ) {
        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof BaseArtilleryTileEntity)) return false;
        BaseArtilleryTileEntity artilleryTE = (BaseArtilleryTileEntity) te;

        if (player.isSneaking() && player.getHeldItem(hand).isEmpty()) {
            if (!world.isRemote) artilleryTE.toggleAutoMode(player);
            return true;
        }

        return onArtilleryActivated(world, pos, artilleryTE, player, hand);
    }

    /**
     * Subclass hook for normal (non-sneak) right-click interactions —
     * loading ammo, opening a GUI, or triggering a manual fire.
     *
     * @return {@code true} if the interaction was handled.
     */
    protected abstract boolean onArtilleryActivated(World world, BlockPos pos,
        BaseArtilleryTileEntity te, EntityPlayer player, EnumHand hand);

    // ── BlockHorizontal boilerplate ───────────────────────────────────────────

    @Override
    protected net.minecraft.block.state.BlockStateContainer createBlockState() {
        return new net.minecraft.block.state.BlockStateContainer(this, FACING);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FACING, EnumFacing.byIndex(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex();
    }
}
