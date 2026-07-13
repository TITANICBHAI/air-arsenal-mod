package com.airarsenal.network;

import com.airarsenal.entity.plane.AttackHelicopterEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Client → Server (Chunk 9).
 * Delivers Q/E lateral strafe input for an {@link AttackHelicopterEntity} the
 * sending player is riding. Sent every tick while either key is held (and once
 * more with both false to stop) by {@link com.airarsenal.client.TacModeController}.
 *
 * Payload: {@code boolean left, boolean right}
 */
public class PacketHelicopterStrafe implements IMessage {

    private boolean left;
    private boolean right;

    public PacketHelicopterStrafe() {}

    public PacketHelicopterStrafe(boolean left, boolean right) {
        this.left  = left;
        this.right = right;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(left);
        buf.writeBoolean(right);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        left  = buf.readBoolean();
        right = buf.readBoolean();
    }

    public static class Handler implements IMessageHandler<PacketHelicopterStrafe, IMessage> {

        @Override
        public IMessage onMessage(PacketHelicopterStrafe message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;

            player.getServerWorld().addScheduledTask(() -> {
                Entity riding = player.getRidingEntity();
                if (riding instanceof AttackHelicopterEntity) {
                    ((AttackHelicopterEntity) riding).applyStrafe(message.left, message.right);
                }
            });

            return null;
        }
    }
}
