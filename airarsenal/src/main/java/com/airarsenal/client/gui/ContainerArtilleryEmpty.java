package com.airarsenal.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

/**
 * A slot-less {@link Container} used purely so Forge's {@code EntityPlayer.openGui}
 * network handshake has something to open — the actual UI (sliders, text fields)
 * lives entirely in the paired {@link net.minecraft.client.gui.GuiScreen}, which reads
 * and writes the TileEntity's state directly via network packets rather than slots.
 *
 * <p>Not annotated {@code @SideOnly} — unlike the GUI screens, a {@link Container}
 * is instantiated on both sides by {@code NetworkRegistry}'s gui handler contract
 * (see {@code ModGuiHandler#getServerGuiElement}), so it must remain loadable on
 * a dedicated server.</p>
 */
public class ContainerArtilleryEmpty extends Container {

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }
}
