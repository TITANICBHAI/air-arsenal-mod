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
 * detonates or is otherwise removed — clears the guidance HUD state.
 */
public class PacketGuidanceEnd implements IMessage {

    public PacketGuidanceEnd() {}

    @Override public void toBytes(ByteBuf buf) {}
    @Override public void fromBytes(ByteBuf buf) {}

    public static class Handler implements IMessageHandler<PacketGuidanceEnd, IMessage> {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketGuidanceEnd message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                com.airarsenal.client.TacModeController.activeGuidanceMissileId = -1;
                com.airarsenal.client.TacModeController.guidanceSignalLost = false;
            });
            return null;
        }
    }
}
