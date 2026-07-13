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
 * Server → Client, sent to all players within 300 blocks of an
 * {@link com.airarsenal.entity.projectile.OrbitalRodEntity} impact.
 * Triggers a screen flash and the impact sound; the accompanying screen
 * shake is sent separately via {@link PacketScreenShake}.
 *
 * Payload: {@code BlockPos targetPos}
 */
public class PacketOrbitalImpact implements IMessage {

    private BlockPos targetPos;

    public PacketOrbitalImpact() {}
    public PacketOrbitalImpact(BlockPos targetPos) { this.targetPos = targetPos; }

    @Override
    public void toBytes(ByteBuf buf) { buf.writeLong(targetPos.toLong()); }

    @Override
    public void fromBytes(ByteBuf buf) { targetPos = BlockPos.fromLong(buf.readLong()); }

    public static class Handler implements IMessageHandler<PacketOrbitalImpact, IMessage> {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketOrbitalImpact message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                com.airarsenal.client.ScreenShakeHandler.triggerFlash();
                Minecraft mc = Minecraft.getMinecraft();
                if (mc.player != null) {
                    mc.player.playSound(ModSounds.WEAPON_ORBITAL_IMPACT, 1.0f, 1.0f);
                }
            });
            return null;
        }
    }
}
