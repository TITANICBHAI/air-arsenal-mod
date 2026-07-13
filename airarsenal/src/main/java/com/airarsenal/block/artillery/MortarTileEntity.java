package com.airarsenal.block.artillery;

import com.airarsenal.entity.projectile.MortarShellEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Mortar — indirect-fire ground weapon with a player-adjustable elevation angle.
 *
 * <p>Range formula (per spec): {@code R = v0² × sin(2θ) / g}, with {@code v0 = 1.5},
 * {@code g = 0.05} (Minecraft's per-tick gravity constant used by
 * {@link MortarShellEntity#getGravityVelocity}). At 45° this gives ≈45 blocks; the
 * practical range across 45°–85° is roughly 10–46 blocks (peaking near 45°, shortening
 * as the angle steepens toward vertical).</p>
 *
 * <p>Angle is set via {@link com.airarsenal.client.gui.GuiMortar} (opened on empty-hand
 * right-click) which sends {@link com.airarsenal.network.PacketSetMortarAngle}. Firing
 * happens separately: right-click the block while holding {@link com.airarsenal.item.ItemMortarShell}.</p>
 */
public class MortarTileEntity extends BaseArtilleryTileEntity {

    public static final float MIN_ANGLE = 45f;
    public static final float MAX_ANGLE = 85f;
    private static final float MUZZLE_VELOCITY = 1.5f;

    /** Elevation angle in degrees, 45 (flattest / longest range) to 85 (steepest / shortest range). */
    private float elevationAngle = MIN_ANGLE;

    @Override
    protected void tickArtillery() {
        // Mortar is entirely player-triggered — no autonomous targeting logic.
    }

    /** Sets the firing angle, clamped to [{@link #MIN_ANGLE}, {@link #MAX_ANGLE}]. */
    public void setElevationAngle(float degrees) {
        this.elevationAngle = net.minecraft.util.math.MathHelper.clamp(degrees, MIN_ANGLE, MAX_ANGLE);
        markDirty();
    }

    public float getElevationAngle() { return elevationAngle; }

    /**
     * Fires a shell toward the direction the placing block currently faces, at the
     * configured elevation. Called from {@link MortarBlock#onArtilleryActivated}.
     */
    public void fire(World world, EntityPlayer player) {
        if (world.isRemote) return;

        net.minecraft.util.EnumFacing facing =
            world.getBlockState(pos).getValue(net.minecraft.block.BlockHorizontal.FACING);

        double thetaRad = Math.toRadians(elevationAngle);
        double horizSpeed = MUZZLE_VELOCITY * Math.cos(thetaRad);
        double vertSpeed   = MUZZLE_VELOCITY * Math.sin(thetaRad);

        Vec3d horizDir = new Vec3d(facing.getFrontOffsetX(), 0, facing.getFrontOffsetZ());

        MortarShellEntity shell = new MortarShellEntity(world, player);
        shell.setPosition(pos.getX() + 0.5, pos.getY() + 1.3, pos.getZ() + 0.5);
        shell.motionX = horizDir.x * horizSpeed;
        shell.motionY = vertSpeed;
        shell.motionZ = horizDir.z * horizSpeed;
        world.spawnEntity(shell);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setFloat("ElevationAngle", elevationAngle);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        elevationAngle = compound.hasKey("ElevationAngle")
            ? compound.getFloat("ElevationAngle") : MIN_ANGLE;
    }
}
