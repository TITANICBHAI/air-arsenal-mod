package com.airarsenal.client.gui;

import com.airarsenal.block.OrbitalCannonTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Server/client-shared container for the Orbital Cannon GUI. Holds the single
 * uplink-card slot and syncs {@code cooldownTicks} / {@code chargeState} to
 * the client via the classic 1.12 "progress bar" (window-property) channel,
 * consistent with how vanilla furnaces sync burn time.
 */
public class OrbitalCannonContainer extends Container {

    private final OrbitalCannonTileEntity te;

    private int lastStateTicks = -1;
    private int lastCooldownTicks = -1;
    private int lastChargeState = -1;

    /** Client-side mirrors, populated via {@link #updateProgressBar}. */
    public int clientStateTicks;
    public int clientCooldownTicks;
    public int clientChargeState;

    public OrbitalCannonContainer(OrbitalCannonTileEntity te) {
        this.te = te;
        addSlotToContainer(new Slot((IInventory) te, 0, 80, 35) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return te.isItemValidForSlot(0, stack);
            }
        });
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return te.isUsableByPlayer(playerIn);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        int stateTicks = te.getStateTicks();
        int cooldownTicks = te.getCooldownTicks();
        int chargeStateOrdinal = te.getChargeState().ordinal();

        if (stateTicks != lastStateTicks) {
            for (Object listener : listeners) {
                ((net.minecraft.inventory.IContainerListener) listener).sendWindowProperty(this, 0, stateTicks);
            }
            lastStateTicks = stateTicks;
        }
        if (cooldownTicks != lastCooldownTicks) {
            for (Object listener : listeners) {
                ((net.minecraft.inventory.IContainerListener) listener).sendWindowProperty(this, 1, cooldownTicks);
            }
            lastCooldownTicks = cooldownTicks;
        }
        if (chargeStateOrdinal != lastChargeState) {
            for (Object listener : listeners) {
                ((net.minecraft.inventory.IContainerListener) listener).sendWindowProperty(this, 2, chargeStateOrdinal);
            }
            lastChargeState = chargeStateOrdinal;
        }
    }

    @Override
    public void updateProgressBar(int id, int data) {
        switch (id) {
            case 0: clientStateTicks = data; break;
            case 1: clientCooldownTicks = data; break;
            case 2: clientChargeState = data; break;
            default: break;
        }
    }

    public OrbitalCannonTileEntity getTileEntity() { return te; }
}
