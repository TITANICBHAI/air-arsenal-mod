package com.airarsenal.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Server → Client, general-purpose screen-shake trigger. Stores the intensity
 * and duration in {@link com.airarsenal.client.ScreenShakeHandler}, which
 * applies a decaying camera offset via {@code EntityViewRenderEvent.CameraSetup}.
 *
 * Payload: {@code float intensity, int durationTicks}
 */
public class PacketScreenShake implements IMessage {

    private float intensity;
    private int durationTicks;

    public PacketScreenShake() {}
    public PacketScreenShake(float intensity, int durationTicks) {
        this.intensity = intensity;
        this.durationTicks = durationTicks;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeFloat(intensity);
        buf.writeInt(durationTicks);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        intensity = buf.readFloat();
        durationTicks = buf.readInt();
    }

    public static class Handler implements IMessageHandler<PacketScreenShake, IMessage> {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketScreenShake message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() ->
                com.airarsenal.client.ScreenShakeHandler.trigger(message.intensity, message.durationTicks));
            return null;
        }
    }
}
