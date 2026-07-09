package com.airarsenal.entity.projectile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * World-level persistent data tracking which blocks have been EMP-disabled
 * and when they expire. Saved to the world's {@code data/} folder as
 * {@code airarsenal_emp.dat}.
 *
 * <p>Call {@link #tickAndCleanup(World)} once per world tick to expire
 * entries whose duration has elapsed.</p>
 */
public class EMPWorldData extends WorldSavedData {

    private static final String DATA_NAME = "airarsenal_emp";

    /** Maps block position → world-tick at which the EMP effect expires. */
    private final Map<BlockPos, Long> disabledBlocks = new HashMap<>();

    public EMPWorldData() {
        super(DATA_NAME);
    }

    public EMPWorldData(String name) {
        super(name);
    }

    // ── API ───────────────────────────────────────────────────────────────────

    /** Mark a block as EMP-disabled until {@code expiryTick}. */
    public void disableBlock(BlockPos pos, long expiryTick) {
        disabledBlocks.put(pos, expiryTick);
        markDirty();
    }

    /** Returns true if the given block is currently EMP-disabled. */
    public boolean isDisabled(BlockPos pos) {
        return disabledBlocks.containsKey(pos);
    }

    /**
     * Removes expired entries. Call from a server tick event.
     *
     * @param world The server world whose time is used for comparison.
     */
    public void tickAndCleanup(World world) {
        long now = world.getTotalWorldTime();
        boolean changed = false;
        Iterator<Map.Entry<BlockPos, Long>> it = disabledBlocks.entrySet().iterator();
        while (it.hasNext()) {
            if (it.next().getValue() <= now) {
                it.remove();
                changed = true;
            }
        }
        if (changed) markDirty();
    }

    // ── WorldSavedData ────────────────────────────────────────────────────────

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        disabledBlocks.clear();
        NBTTagList list = compound.getTagList("DisabledBlocks", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound entry = list.getCompoundTagAt(i);
            BlockPos pos = new BlockPos(
                entry.getInteger("X"),
                entry.getInteger("Y"),
                entry.getInteger("Z")
            );
            long expiry = entry.getLong("Expiry");
            disabledBlocks.put(pos, expiry);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        for (Map.Entry<BlockPos, Long> entry : disabledBlocks.entrySet()) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("X",      entry.getKey().getX());
            tag.setInteger("Y",      entry.getKey().getY());
            tag.setInteger("Z",      entry.getKey().getZ());
            tag.setLong("Expiry",    entry.getValue());
            list.appendTag(tag);
        }
        compound.setTag("DisabledBlocks", list);
        return compound;
    }

    // ── Static accessor ───────────────────────────────────────────────────────

    public static EMPWorldData get(World world) {
        EMPWorldData data = (EMPWorldData) world.getMapStorage().getOrLoadData(
            EMPWorldData.class, DATA_NAME);
        if (data == null) {
            data = new EMPWorldData();
            world.getMapStorage().setData(DATA_NAME, data);
        }
        return data;
    }
}
