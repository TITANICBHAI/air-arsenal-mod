package com.airarsenal.client.gui;

import com.airarsenal.block.OrbitalCannonTileEntity;
import com.airarsenal.item.ItemOrbitalDesignator;
import com.airarsenal.network.ModNetwork;
import com.airarsenal.network.PacketOrbitalFire;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Orbital Cannon control panel — target readout, uplink-card slot, cooldown
 * bar, state label, and a FIRE button that is greyed out unless every
 * precondition is met (the server re-validates regardless, see
 * {@link PacketOrbitalFire}).
 */
@SideOnly(Side.CLIENT)
public class OrbitalCannonGui extends GuiContainer {

    private final OrbitalCannonTileEntity te;
    private final BlockPos pos;
    private static final int FIRE_BUTTON_ID = 0;

    public OrbitalCannonGui(OrbitalCannonContainer container, BlockPos pos) {
        super(container);
        this.te = container.getTileEntity();
        this.pos = pos;
        xSize = 176;
        ySize = 166;
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        buttonList.add(new GuiButton(FIRE_BUTTON_ID, guiLeft + 128, guiTop + 130, 40, 20, "FIRE"));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == FIRE_BUTTON_ID && isFireReady()) {
            ModNetwork.CHANNEL.sendToServer(new PacketOrbitalFire(pos));
        }
    }

    private boolean isFireReady() {
        OrbitalCannonContainer container = (OrbitalCannonContainer) inventorySlots;
        boolean idle = container.clientChargeState == OrbitalCannonTileEntity.ChargeState.IDLE.ordinal();
        boolean structureOk = te.validateStructure();
        boolean hasCard = !te.getStackInSlot(0).isEmpty();
        boolean hasTarget = te.getTargetPos() != null;
        return idle && structureOk && hasCard && hasTarget;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        drawDefaultBackground();
        drawRect(guiLeft, guiTop, guiLeft + xSize, guiTop + ySize, 0xEE101010);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        drawCenteredString(fontRenderer, "ORBITAL CANNON", xSize / 2, 6, 0xFFFFFF);

        // ── Left panel: target coordinates ─────────────────────────────────
        drawString(fontRenderer, "TARGET", 8, 22, 0xAAAAAA);
        BlockPos target = te.getTargetPos();
        if (target != null) {
            drawString(fontRenderer, "X: " + target.getX(), 8, 34, 0xFFFFFF);
            drawString(fontRenderer, "Y: " + target.getY(), 8, 46, 0xFFFFFF);
            drawString(fontRenderer, "Z: " + target.getZ(), 8, 58, 0xFFFFFF);
        } else {
            drawString(fontRenderer, "NO TARGET SET", 8, 34, 0xFF5555);
        }

        // ── Centre: card slot label ─────────────────────────────────────────
        drawString(fontRenderer, "UPLINK CARD", 62, 24, 0xAAAAAA);

        // ── Right panel: cooldown bar + state label ─────────────────────────
        OrbitalCannonContainer container = (OrbitalCannonContainer) inventorySlots;
        int chargeStateOrdinal = container.clientChargeState;
        OrbitalCannonTileEntity.ChargeState state =
            OrbitalCannonTileEntity.ChargeState.values()[
                Math.min(chargeStateOrdinal, OrbitalCannonTileEntity.ChargeState.values().length - 1)];

        String stateLabel;
        float progress;
        if (!te.validateStructure()) {
            stateLabel = "NO STRUCTURE";
            progress = 0f;
        } else {
            switch (state) {
                case CHARGING:
                    stateLabel = "CHARGING";
                    progress = com.airarsenal.config.AirArsenalConfig.orbitalChargingTicks > 0
                        ? (float) container.clientStateTicks / com.airarsenal.config.AirArsenalConfig.orbitalChargingTicks
                        : 0f;
                    break;
                case WARNING:
                    stateLabel = "WARNING";
                    progress = com.airarsenal.config.AirArsenalConfig.orbitalWarningTicks > 0
                        ? (float) container.clientStateTicks / com.airarsenal.config.AirArsenalConfig.orbitalWarningTicks
                        : 0f;
                    break;
                case FIRING:
                    stateLabel = "FIRING";
                    progress = 1f;
                    break;
                case COOLDOWN:
                    stateLabel = "COOLDOWN";
                    progress = com.airarsenal.config.AirArsenalConfig.orbitalCooldownTicks > 0
                        ? 1f - ((float) container.clientCooldownTicks / com.airarsenal.config.AirArsenalConfig.orbitalCooldownTicks)
                        : 1f;
                    break;
                default:
                    stateLabel = "READY";
                    progress = 1f;
                    break;
            }
        }

        drawString(fontRenderer, stateLabel, 110, 22, 0xFFFFFF);
        drawCooldownBar(110, 34, 58, 8, progress);
    }

    private void drawCooldownBar(int x, int y, int w, int h, float progress) {
        progress = Math.max(0f, Math.min(1f, progress));
        drawRect(x, y, x + w, y + h, 0xFF303030);
        int filled = (int) (w * progress);
        int red = (int) (255 * (1f - progress));
        int green = (int) (255 * progress);
        int color = 0xFF000000 | (red << 16) | (green << 8);
        if (filled > 0) {
            drawRect(x, y, x + filled, y + h, color);
        }
    }

    @Override
    public boolean doesGuiPauseGame() { return false; }
}
