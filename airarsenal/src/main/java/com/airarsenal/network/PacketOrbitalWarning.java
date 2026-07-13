package com.airarsenal.network;

import com.airarsenal.registry.ModSounds;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Server → Client, sent to all players within 200 blocks when the Orbital
 * Cannon enters its WARNING state. Client stores the target so
 * {@code OrbitalCannonClientEvents} (a {@code RenderWorldLastEvent} listener,
 * see {@link com.airarsenal.client.ScreenShakeHandler} sibling registration in
 * {@code ClientProxy}) can optionally draw a marker, and starts the warning
 * sound loop.
 *
 * Payload: {@code BlockPos targetPos, int ticksToImpact}
 */
public class PacketOrbitalWarning implements IMessage {

    private BlockPos targetPos;
    private int ticksToImpact;

    public PacketOrbitalWarning() {}
    public PacketOrbitalWarning(BlockPos targetPos, int ticksToImpact) {
        this.targetPos = targetPos;
        this.ticksToImpact = ticksToImpact;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(targetPos.toLong());
        buf.writeInt(ticksToImpact);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        targetPos = BlockPos.fromLong(buf.readLong());
        ticksToImpact = buf.readInt();
    }

    public static BlockPos warningTargetPos;

    public static class Handler implements IMessageHandler<PacketOrbitalWarning, IMessage> {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketOrbitalWarning message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                warningTargetPos = message.targetPos;
                Minecraft mc = Minecraft.getMinecraft();
                if (mc.player != null) {
                    mc.player.playSound(ModSounds.WEAPON_ORBITAL_WARNING, 1.0f, 1.0f);
                }
            });
            return null;
        }
    }
}
