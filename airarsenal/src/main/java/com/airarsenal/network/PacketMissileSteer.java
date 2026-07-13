package com.airarsenal.network;

import com.airarsenal.entity.projectile.PredatorMissileEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Client → Server.
 * Delivers mouse-derived yaw and pitch deltas to steer the {@link PredatorMissileEntity}.
 * Rate-limited: sent every 2 ticks by {@link com.airarsenal.client.TacModeController}.
 *
 * Payload: {@code float yaw, float pitch}
 *
 * <p>NOTE: Do NOT add a {@code topAttack} boolean here yet —
 * that extension comes in Chunk 8 (Missile Command Truck).</p>
 */
public class PacketMissileSteer implements IMessage {

    private float yaw;
    private float pitch;

    public PacketMissileSteer() {}

    public PacketMissileSteer(float yaw, float pitch) {
        this.yaw   = yaw;
        this.pitch = pitch;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeFloat(yaw);
        buf.writeFloat(pitch);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        yaw   = buf.readFloat();
        pitch = buf.readFloat();
    }

    // ── Server handler ────────────────────────────────────────────────────────

    public static class Handler implements IMessageHandler<PacketMissileSteer, IMessage> {

        @Override
        public IMessage onMessage(PacketMissileSteer message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;

            player.getServerWorld().addScheduledTask(() -> {
                // Find the PredatorMissileEntity that lists this player as controller
                for (Entity e : player.world.loadedEntityList) {
                    if (e instanceof PredatorMissileEntity) {
                        PredatorMissileEntity missile = (PredatorMissileEntity) e;
                        if (missile.controllerPlayer == player) {
                            missile.yawInput   += message.yaw;
                            missile.pitchInput += message.pitch;
                            break;
                        }
                    }
                }
            });

            return null;
        }
    }
}
