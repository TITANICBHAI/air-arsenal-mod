package com.airarsenal.block.artillery;

import com.airarsenal.entity.projectile.HowitzerShellEntity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Howitzer — long-range (20–150 block) indirect-fire gun.
 *
 * <p>Two ways to set a target:</p>
 * <ol>
 *   <li><b>Two-player:</b> a spotter marks ground with {@link com.airarsenal.item.ItemLaserDesignator};
 *       a gunner right-clicks the Howitzer to adopt that mark as the target.</li>
 *   <li><b>Single-player:</b> sneak+right-click opens {@link com.airarsenal.client.gui.GuiHowitzer}
 *       to type coordinates directly, sent via {@link com.airarsenal.network.PacketSetHowitzerTarget}.</li>
 * </ol>
 *
 * <p>Once a target is set, a plain right-click (empty hand, not sneaking, no laser mark to
 * adopt) fires {@link HowitzerShellEntity} at it. No ammo item is required — treated as
 * always-loaded heavy ordnance.</p>
 */
public class HowitzerTileEntity extends BaseArtilleryTileEntity {

    public static final double MIN_RANGE = 20.0;
    public static final double MAX_RANGE = 150.0;

    private BlockPos targetPos;

    @Override
    protected void tickArtillery() {
        // Entirely player-triggered — no autonomous targeting.
    }

    @Nullable
    public BlockPos getTargetPos() { return targetPos; }

    /**
     * Attempts to set the target position, enforcing the 20–150 block range.
     * @return true if accepted, false if out of range.
     */
    public boolean setTargetPos(BlockPos target) {
        double dist = Math.sqrt(pos.distanceSq(target));
        if (dist < MIN_RANGE || dist > MAX_RANGE) return false;
        this.targetPos = target;
        markDirty();
        return true;
    }

    /** Fires at the currently stored target. No-op if none is set. Called server-side only. */
    public void fire(World world) {
        if (world.isRemote || targetPos == null) return;
        HowitzerShellEntity shell = new HowitzerShellEntity(world, null,
            pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, targetPos);
        world.spawnEntity(shell);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (targetPos != null) {
            compound.setInteger("TargetX", targetPos.getX());
            compound.setInteger("TargetY", targetPos.getY());
            compound.setInteger("TargetZ", targetPos.getZ());
        }
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("TargetX")) {
            targetPos = new BlockPos(
                compound.getInteger("TargetX"),
                compound.getInteger("TargetY"),
                compound.getInteger("TargetZ"));
        }
    }
}
