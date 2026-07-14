package com.airarsenal.client.gui;

import com.airarsenal.block.artillery.MortarTileEntity;
import com.airarsenal.network.ModNetwork;
import com.airarsenal.network.PacketSetMortarAngle;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Mortar elevation-angle GUI — a single slider from 45° (flattest, longest range)
 * to 85° (steepest, shortest range). Slider changes are sent live to the server
 * via {@link PacketSetMortarAngle}; "Done" simply closes the screen.
 *
 * <p>Implemented as a small self-contained drag slider rather than the vanilla
 * {@code GuiSlider} widget, whose constructor/accessor API is fragile across
 * MCP mapping versions.</p>
 */
@SideOnly(Side.CLIENT)
public class GuiMortar extends GuiScreen {

    private final MortarTileEntity te;
    private final BlockPos pos;

    private static final int DONE_ID = 1;

    private int sliderX, sliderY, sliderWidth, sliderHeight;
    private float angle;
    private boolean dragging = false;

    public GuiMortar(MortarTileEntity te, BlockPos pos) {
        this.te = te;
        this.pos = pos;
        this.angle = te.getElevationAngle();
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();

        sliderWidth  = 200;
        sliderHeight = 20;
        sliderX = width / 2 - sliderWidth / 2;
        sliderY = height / 2 - 20;

        buttonList.add(new GuiButton(DONE_ID, width / 2 - 50, height / 2 + 20, 100, 20, "Done"));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == DONE_ID) {
            mc.player.closeScreen();
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws java.io.IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton == 0 && isOverSlider(mouseX, mouseY)) {
            dragging = true;
            updateAngleFromMouse(mouseX);
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        if (dragging) updateAngleFromMouse(mouseX);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        dragging = false;
    }

    private boolean isOverSlider(int mouseX, int mouseY) {
        return mouseX >= sliderX && mouseX <= sliderX + sliderWidth
            && mouseY >= sliderY && mouseY <= sliderY + sliderHeight;
    }

    private void updateAngleFromMouse(int mouseX) {
        float frac = MathHelper.clamp((mouseX - sliderX) / (float) sliderWidth, 0f, 1f);
        angle = MortarTileEntity.MIN_ANGLE + frac * (MortarTileEntity.MAX_ANGLE - MortarTileEntity.MIN_ANGLE);
        ModNetwork.CHANNEL.sendToServer(new PacketSetMortarAngle(pos, angle));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawCenteredString(fontRenderer, "Mortar Elevation", width / 2, height / 2 - 50, 0xFFFFFF);

        // Slider track
        drawRect(sliderX, sliderY, sliderX + sliderWidth, sliderY + sliderHeight, 0xFF8B8B8B);
        drawRect(sliderX + 1, sliderY + 1, sliderX + sliderWidth - 1, sliderY + sliderHeight - 1, 0xFF000000);

        // Slider handle
        float frac = (angle - MortarTileEntity.MIN_ANGLE) / (MortarTileEntity.MAX_ANGLE - MortarTileEntity.MIN_ANGLE);
        int handleX = sliderX + (int) (frac * (sliderWidth - 8));
        drawRect(handleX, sliderY, handleX + 8, sliderY + sliderHeight, 0xFFFFFFFF);

        drawCenteredString(fontRenderer, "Angle: " + (int) angle + "°",
            width / 2, sliderY - 12, 0xFFFFFF);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() { return false; }
}
