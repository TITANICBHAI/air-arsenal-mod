package com.airarsenal.client.gui;

import com.airarsenal.block.artillery.MortarTileEntity;
import com.airarsenal.network.ModNetwork;
import com.airarsenal.network.PacketSetMortarAngle;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlider;
import net.minecraft.util.math.BlockPos;

/**
 * Mortar elevation-angle GUI — a single slider from 45° (flattest, longest range)
 * to 85° (steepest, shortest range). Slider changes are sent live to the server
 * via {@link PacketSetMortarAngle}; "Done" simply closes the screen.
 */
public class GuiMortar extends GuiScreen {

    private final MortarTileEntity te;
    private final BlockPos pos;
    private GuiSlider angleSlider;

    private static final int SLIDER_ID = 0;
    private static final int DONE_ID   = 1;

    public GuiMortar(MortarTileEntity te, BlockPos pos) {
        this.te = te;
        this.pos = pos;
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();

        angleSlider = new GuiSlider(SLIDER_ID, width / 2 - 100, height / 2 - 20, 200, 20,
            "Angle: ", "°",
            MortarTileEntity.MIN_ANGLE, MortarTileEntity.MAX_ANGLE,
            te.getElevationAngle(), false, true);
        buttonList.add(angleSlider);

        buttonList.add(new GuiButton(DONE_ID, width / 2 - 50, height / 2 + 20, 100, 20, "Done"));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == DONE_ID) {
            mc.player.closeScreen();
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        sendAngleUpdate();
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        sendAngleUpdate();
    }

    private void sendAngleUpdate() {
        float angle = (float) angleSlider.getValue();
        ModNetwork.CHANNEL.sendToServer(new PacketSetMortarAngle(pos, angle));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawCenteredString(fontRenderer, "Mortar Elevation", width / 2, height / 2 - 50, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() { return false; }
}
