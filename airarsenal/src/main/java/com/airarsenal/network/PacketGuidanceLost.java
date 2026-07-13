package com.airarsenal.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Server → Client. Sent when a {@link com.airarsenal.entity.projectile.TruckGuidedMissileEntity}
 * flies beyond its 150-block signal range — steering inputs stop being applied and the HUD
 * shows "SIGNAL LOST" until either the missile returns into range or detonates.
 *
 * Payload: {@code int missileEntityId}
 */
public class PacketGuidanceLost implements IMessage {

    private int missileEntityId;

    public PacketGuidanceLost() {}
    public PacketGuidanceLost(int missileEntityId) { this.missileEntityId = missileEntityId; }

    @Override
    public void toBytes(ByteBuf buf) { buf.writeInt(missileEntityId); }

    @Override
    public void fromBytes(ByteBuf buf) { missileEntityId = buf.readInt(); }

    public static class Handler implements IMessageHandler<PacketGuidanceLost, IMessage> {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketGuidanceLost message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() ->
                com.airarsenal.client.TacModeController.guidanceSignalLost = true);
            return null;
        }
    }
}
