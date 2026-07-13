package com.airarsenal.client.gui;

import com.airarsenal.block.artillery.HowitzerTileEntity;
import com.airarsenal.network.ModNetwork;
import com.airarsenal.network.PacketSetHowitzerTarget;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Howitzer manual-targeting GUI — three text fields for X/Y/Z target coordinates,
 * for single-player operation (no spotter with a Laser Designator available).
 * "Set Target" sends {@link PacketSetHowitzerTarget}; the server enforces the
 * 20–150 block range and replies via chat message.
 */
@SideOnly(Side.CLIENT)
public class GuiHowitzer extends GuiScreen {

    private final HowitzerTileEntity te;
    private final BlockPos pos;

    private GuiTextField fieldX, fieldY, fieldZ;
    private static final int SET_TARGET_ID = 0;
    private static final int DONE_ID       = 1;

    public GuiHowitzer(HowitzerTileEntity te, BlockPos pos) {
        this.te = te;
        this.pos = pos;
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        Keyboard_enableRepeatEvents(true);

        int fieldW = 60;
        int startX = width / 2 - (fieldW * 3 + 20) / 2;
        int y = height / 2 - 30;

        BlockPos existing = te.getTargetPos() != null ? te.getTargetPos() : pos;

        fieldX = new GuiTextField(0, fontRenderer, startX, y, fieldW, 20);
        fieldY = new GuiTextField(1, fontRenderer, startX + fieldW + 10, y, fieldW, 20);
        fieldZ = new GuiTextField(2, fontRenderer, startX + (fieldW + 10) * 2, y, fieldW, 20);

        fieldX.setText(String.valueOf(existing.getX()));
        fieldY.setText(String.valueOf(existing.getY()));
        fieldZ.setText(String.valueOf(existing.getZ()));

        buttonList.add(new GuiButton(SET_TARGET_ID, width / 2 - 60, y + 30, 120, 20, "Set Target"));
        buttonList.add(new GuiButton(DONE_ID, width / 2 - 40, y + 55, 80, 20, "Close"));
    }

    private void Keyboard_enableRepeatEvents(boolean b) {
        try { org.lwjgl.input.Keyboard.enableRepeatEvents(b); } catch (Throwable ignored) {}
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == SET_TARGET_ID) {
            try {
                int x = Integer.parseInt(fieldX.getText().trim());
                int y = Integer.parseInt(fieldY.getText().trim());
                int z = Integer.parseInt(fieldZ.getText().trim());
                ModNetwork.CHANNEL.sendToServer(
                    new PacketSetHowitzerTarget(pos, new BlockPos(x, y, z)));
            } catch (NumberFormatException ignored) {
                // Invalid input — silently ignore; server never receives a malformed packet.
            }
        } else if (button.id == DONE_ID) {
            mc.player.closeScreen();
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws java.io.IOException {
        if (!fieldX.textboxKeyTyped(typedChar, keyCode)
                && !fieldY.textboxKeyTyped(typedChar, keyCode)
                && !fieldZ.textboxKeyTyped(typedChar, keyCode)) {
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws java.io.IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        fieldX.mouseClicked(mouseX, mouseY, mouseButton);
        fieldY.mouseClicked(mouseX, mouseY, mouseButton);
        fieldZ.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void updateScreen() {
        fieldX.updateCursorCounter();
        fieldY.updateCursorCounter();
        fieldZ.updateCursorCounter();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawCenteredString(fontRenderer, "Howitzer Target Coordinates", width / 2, height / 2 - 60, 0xFFFFFF);
        fieldX.drawTextBox();
        fieldY.drawTextBox();
        fieldZ.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() { return false; }
}
