package com.airarsenal.block;

import com.airarsenal.AirArsenal;
import com.airarsenal.config.AirArsenalConfig;
import com.airarsenal.entity.OrbitalWarningMarkerEntity;
import com.airarsenal.entity.projectile.OrbitalRodEntity;
import com.airarsenal.item.ItemSatelliteUplinkCard;
import com.airarsenal.network.ModNetwork;
import com.airarsenal.network.PacketOrbitalWarning;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Controller TileEntity for the Orbital Cannon multiblock. Drives the
 * IDLE → CHARGING → WARNING → FIRING → COOLDOWN state machine described in
 * the Chunk 11 spec, holds the single satellite-uplink-card slot, and stores
 * the player-designated target block position.
 */
public class OrbitalCannonTileEntity extends TileEntity implements ITickable, IInventory {

    public enum ChargeState { IDLE, CHARGING, WARNING, FIRING, COOLDOWN }

    private final NonNullList<ItemStack> inventory = NonNullList.withSize(1, ItemStack.EMPTY);

    private BlockPos targetPos;
    private ChargeState chargeState = ChargeState.IDLE;
    private int stateTicks = 0;
    private int cooldownTicks = 0;

    // ── Tick / state machine ────────────────────────────────────────────────

    @Override
    public void update() {
        if (world.isRemote) return;

        if ((chargeState == ChargeState.IDLE || chargeState == ChargeState.COOLDOWN)
                && world.getTotalWorldTime() % 40 == 0) {
            // Structure invalidity does not interrupt an in-progress strike — only
            // blocks new fire commands while IDLE, and does not affect COOLDOWN.
        }

        switch (chargeState) {
            case IDLE:
                // Waits for PacketOrbitalFire; nothing to tick.
                break;

            case CHARGING:
                stateTicks++;
                if (stateTicks >= AirArsenalConfig.orbitalChargingTicks) {
                    world.playSound(null, pos, com.airarsenal.registry.ModSounds.WEAPON_ORBITAL_CHARGE,
                        net.minecraft.util.SoundCategory.HOSTILE, 4.0f, 1.0f);
                    chargeState = ChargeState.WARNING;
                    stateTicks = 0;
                }
                break;

            case WARNING:
                if (stateTicks == 0 && targetPos != null) {
                    OrbitalWarningMarkerEntity marker = new OrbitalWarningMarkerEntity(world, targetPos);
                    world.spawnEntity(marker);
                    sendWarningToNearbyPlayers();
                }
                stateTicks++;
                if (stateTicks >= AirArsenalConfig.orbitalWarningTicks) {
                    chargeState = ChargeState.FIRING;
                    stateTicks = 0;
                }
                break;

            case FIRING:
                if (targetPos != null) {
                    OrbitalRodEntity rod = new OrbitalRodEntity(world,
                        targetPos.getX() + 0.5, 300.0, targetPos.getZ() + 0.5, targetPos.getY());
                    world.spawnEntity(rod);
                    world.playSound(null, pos, com.airarsenal.registry.ModSounds.WEAPON_ORBITAL_FIRE,
                        net.minecraft.util.SoundCategory.HOSTILE, 4.0f, 1.0f);
                }
                chargeState = ChargeState.COOLDOWN;
                cooldownTicks = AirArsenalConfig.orbitalCooldownTicks;
                stateTicks = 0;
                markDirty();
                break;

            case COOLDOWN:
                if (cooldownTicks > 0) cooldownTicks--;
                if (cooldownTicks <= 0) {
                    chargeState = ChargeState.IDLE;
                }
                break;
        }
    }

    private void sendWarningToNearbyPlayers() {
        if (!(world instanceof WorldServer) || targetPos == null) return;
        WorldServer server = (WorldServer) world;
        AxisAlignedBB area = new AxisAlignedBB(pos).grow(200);
        List<net.minecraft.entity.player.EntityPlayer> nearby =
            world.getEntitiesWithinAABB(net.minecraft.entity.player.EntityPlayer.class, area);
        for (net.minecraft.entity.player.EntityPlayer p : nearby) {
            if (p instanceof EntityPlayerMP) {
                ModNetwork.CHANNEL.sendTo(
                    new PacketOrbitalWarning(targetPos, AirArsenalConfig.orbitalWarningTicks),
                    (EntityPlayerMP) p);
            }
        }
    }

    // ── Fire command entry point (called from PacketOrbitalFire handler) ───

    /**
     * Attempts to begin a strike. Requires IDLE state, a valid 4-frame structure,
     * an uplink card in the slot, and a saved target. Consumes the card and
     * transitions to CHARGING on success.
     */
    public boolean tryFire() {
        if (chargeState != ChargeState.IDLE) return false;
        if (!validateStructure()) return false;
        if (targetPos == null) return false;
        ItemStack card = inventory.get(0);
        if (card.isEmpty() || !(card.getItem() instanceof ItemSatelliteUplinkCard)) return false;

        card.shrink(1);
        if (card.getCount() <= 0) inventory.set(0, ItemStack.EMPTY);

        chargeState = ChargeState.CHARGING;
        stateTicks = 0;
        markDirty();
        return true;
    }

    /**
     * Structure validation — the cannon only activates when all 4 cardinal
     * frame blocks (same Y level) are present. Called on-demand from
     * {@link #tryFire()} and available for the GUI to poll each render tick.
     */
    public boolean validateStructure() {
        BlockPos[] required = { pos.north(), pos.south(), pos.east(), pos.west() };
        for (BlockPos p : required) {
            if (!(world.getBlockState(p).getBlock() instanceof OrbitalCannonFrameBlock)) return false;
        }
        return true;
    }

    // ── Getters / target ─────────────────────────────────────────────────────

    @Nullable
    public BlockPos getTargetPos() { return targetPos; }

    public void setTargetPos(BlockPos targetPos) {
        this.targetPos = targetPos;
        markDirty();
    }

    public ChargeState getChargeState() { return chargeState; }
    public int getStateTicks() { return stateTicks; }
    public int getCooldownTicks() { return cooldownTicks; }

    // ── NBT persistence ───────────────────────────────────────────────────────

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (targetPos != null) compound.setLong("TargetPos", targetPos.toLong());
        compound.setInteger("ChargeState", chargeState.ordinal());
        compound.setInteger("StateTicks", stateTicks);
        compound.setInteger("CooldownTicks", cooldownTicks);
        ItemStackHelper.saveAllItems(compound, inventory);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        targetPos = compound.hasKey("TargetPos") ? BlockPos.fromLong(compound.getLong("TargetPos")) : null;
        chargeState = ChargeState.values()[
            Math.min(compound.getInteger("ChargeState"), ChargeState.values().length - 1)];
        stateTicks = compound.getInteger("StateTicks");
        cooldownTicks = compound.getInteger("CooldownTicks");
        ItemStackHelper.loadAllItems(compound, inventory);
    }

    // ── IInventory — single uplink-card slot ────────────────────────────────

    @Override public int getSizeInventory() { return inventory.size(); }

    @Override public boolean isEmpty() {
        for (ItemStack s : inventory) if (!s.isEmpty()) return false;
        return true;
    }

    @Override public ItemStack getStackInSlot(int index) { return inventory.get(index); }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack result = ItemStackHelper.getAndSplit(inventory, index, count);
        if (!result.isEmpty()) markDirty();
        return result;
    }

    @Override public ItemStack removeStackFromSlot(int index) {
        return ItemStackHelper.getAndRemove(inventory, index);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        inventory.set(index, stack);
        if (stack.getCount() > getInventoryStackLimit()) stack.setCount(getInventoryStackLimit());
        markDirty();
    }

    @Override public int getInventoryStackLimit() { return 16; }

    @Override public void markDirty() { super.markDirty(); }

    @Override
    public boolean isUsableByPlayer(net.minecraft.entity.player.EntityPlayer player) {
        return world.getTileEntity(pos) == this
            && player.getDistanceSq(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64.0;
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return stack.getItem() instanceof ItemSatelliteUplinkCard;
    }

    @Override public void openInventory(net.minecraft.entity.player.EntityPlayer player) {}
    @Override public void closeInventory(net.minecraft.entity.player.EntityPlayer player) {}
    @Override public int getField(int id) { return 0; }
    @Override public void setField(int id, int value) {}
    @Override public void clear() { inventory.clear(); }
    @Override public String getName() { return "container.airarsenal.orbital_cannon"; }
    @Override public boolean hasCustomName() { return false; }
    @Override public net.minecraft.util.text.ITextComponent getDisplayName() {
        return new net.minecraft.util.text.TextComponentString(getName());
    }
    @Override public int getFieldCount() { return 0; }
}
