package com.airarsenal.block.artillery;

import com.airarsenal.entity.plane.BasePlaneEntity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ITickable;

import java.util.List;

/**
 * Shared base for every artillery / AA TileEntity.
 *
 * <p>Holds the common state every emplacement needs: locked target entity ID,
 * fire cooldown, ammo count (-1 = infinite / not applicable), and current
 * traversal yaw/pitch. All targeting and firing logic lives in
 * {@link #tickArtillery()}, called only server-side from {@link #update()}.</p>
 *
 * <p><b>Persistence:</b> all fields survive chunk unload/reload via
 * {@link #writeToNBT} / {@link #readFromNBT}.</p>
 */
public abstract class BaseArtilleryTileEntity extends TileEntity implements ITickable {

    /** Entity ID of the currently tracked target, or -1 if none. Not persisted (re-acquired). */
    protected int targetEntityId = -1;

    /** Ticks remaining before this emplacement may fire again. */
    protected int cooldownTicks = 0;

    /** Remaining ammo, or -1 for infinite / not ammo-limited. */
    protected int ammoCount = -1;

    /** Current traversal heading — degrees, Minecraft yaw convention. */
    protected float currentYaw = 0f;

    /** Current traversal elevation — degrees, positive = looking down (MC pitch convention). */
    protected float currentPitch = 0f;

    /** True = auto-target and auto-fire (AA/Flak). False = player must trigger fire manually. */
    protected boolean autoMode = true;

    // ── Tick dispatch ─────────────────────────────────────────────────────────

    @Override
    public void update() {
        if (world.isRemote) return; // NEVER run targeting logic on the client
        if (cooldownTicks > 0) cooldownTicks--;
        tickArtillery();
    }

    /** Server-side-only per-tick targeting/firing logic. Implemented by each emplacement type. */
    protected abstract void tickArtillery();

    // ── Manual/auto toggle (sneak + right-click) ───────────────────────────────

    public void toggleAutoMode(EntityPlayer player) {
        autoMode = !autoMode;
        if (!world.isRemote && player != null) {
            player.sendMessage(new TextComponentString(
                TextFormatting.YELLOW + "Mode: " + (autoMode ? "AUTO" : "MANUAL")));
        }
        markDirty();
    }

    public boolean isAutoMode() { return autoMode; }

    // ── Targeting helpers (shared by AA + Flak) ────────────────────────────────

    /** Finds the nearest {@link BasePlaneEntity} within {@code range} blocks of this TE. */
    protected BasePlaneEntity findNearestPlane(double range) {
        AxisAlignedBB box = new AxisAlignedBB(pos).grow(range);
        List<BasePlaneEntity> planes = world.getEntitiesWithinAABB(BasePlaneEntity.class, box);
        BasePlaneEntity best = null;
        double bestDistSq = Double.MAX_VALUE;
        for (BasePlaneEntity plane : planes) {
            double dx = plane.posX - (pos.getX() + 0.5);
            double dy = plane.posY - (pos.getY() + 1.0);
            double dz = plane.posZ - (pos.getZ() + 0.5);
            double distSq = dx * dx + dy * dy + dz * dz;
            if (distSq <= range * range && distSq < bestDistSq) {
                bestDistSq = distSq;
                best = plane;
            }
        }
        return best;
    }

    /** Moves {@code current} toward {@code desired} by at most {@code maxDelta} degrees, shortest path. */
    protected static float turnToward(float current, float desired, float maxDelta) {
        float diff = MathHelper.wrapDegrees(desired - current);
        float clamped = MathHelper.clamp(diff, -maxDelta, maxDelta);
        return current + clamped;
    }

    /** Shortest-path angular difference between two headings, in degrees (-180..180). */
    protected static float angleDiff(float a, float b) {
        return MathHelper.wrapDegrees(b - a);
    }

    // ── NBT persistence ───────────────────────────────────────────────────────

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("CooldownTicks", cooldownTicks);
        compound.setInteger("AmmoCount",     ammoCount);
        compound.setFloat("CurrentYaw",      currentYaw);
        compound.setFloat("CurrentPitch",    currentPitch);
        compound.setBoolean("AutoMode",      autoMode);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        cooldownTicks = compound.getInteger("CooldownTicks");
        ammoCount     = compound.hasKey("AmmoCount") ? compound.getInteger("AmmoCount") : -1;
        currentYaw    = compound.getFloat("CurrentYaw");
        currentPitch  = compound.getFloat("CurrentPitch");
        autoMode      = !compound.hasKey("AutoMode") || compound.getBoolean("AutoMode");
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public int getAmmoCount()      { return ammoCount;     }
    public int getCooldownTicks()  { return cooldownTicks; }
    public float getCurrentYaw()   { return currentYaw;    }
    public float getCurrentPitch() { return currentPitch;  }
}
