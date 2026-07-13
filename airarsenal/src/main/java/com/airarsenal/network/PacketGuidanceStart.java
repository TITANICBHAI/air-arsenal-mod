package com.airarsenal.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Server → Client. Tells the controlling player their
 * {@link com.airarsenal.entity.projectile.TruckGuidedMissileEntity} guidance link is
 * active — switches to a HUD "SIGNAL" readout (no camera swap, unlike Predator Strike;
 * the gunner stays at the truck/battery and steers via a simple directional overlay).
 *
 * Payload: {@code int missileEntityId}
 */
public class PacketGuidanceStart implements IMessage {

    private int missileEntityId;

    public PacketGuidanceStart() {}
    public PacketGuidanceStart(int missileEntityId) { this.missileEntityId = missileEntityId; }

    @Override
    public void toBytes(ByteBuf buf) { buf.writeInt(missileEntityId); }

    @Override
    public void fromBytes(ByteBuf buf) { missileEntityId = buf.readInt(); }

    public static class Handler implements IMessageHandler<PacketGuidanceStart, IMessage> {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketGuidanceStart message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() ->
                com.airarsenal.client.TacModeController.activeGuidanceMissileId = message.missileEntityId);
            return null;
        }
    }
}
