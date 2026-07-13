package com.airarsenal.network;

import com.airarsenal.entity.projectile.PredatorMissileEntity;
import com.airarsenal.entity.projectile.TruckGuidedMissileEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Client → Server.
 * Delivers mouse-derived yaw and pitch deltas to steer either a {@link PredatorMissileEntity}
 * (Chunk 7) or a {@link TruckGuidedMissileEntity} (Chunk 8 — shared packet, dispatched by
 * scanning for whichever missile type lists the sender as controller).
 * Rate-limited: sent every 2 ticks by {@link com.airarsenal.client.TacModeController}.
 *
 * Payload: {@code float yaw, float pitch, boolean topAttack}
 *
 * <p>{@code topAttack} (added Chunk 8) toggles top-attack mode on
 * {@link TruckGuidedMissileEntity} only — the Predator Strike handler below reads and
 * discards it so the shared buffer never desyncs between the two missile types.</p>
 */
public class PacketMissileSteer implements IMessage {

    private float yaw;
    private float pitch;
    private boolean topAttack;

    public PacketMissileSteer() {}

    public PacketMissileSteer(float yaw, float pitch, boolean topAttack) {
        this.yaw       = yaw;
        this.pitch     = pitch;
        this.topAttack = topAttack;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeFloat(yaw);
        buf.writeFloat(pitch);
        buf.writeBoolean(topAttack);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        yaw       = buf.readFloat();
        pitch     = buf.readFloat();
        topAttack = buf.readBoolean();
    }

    // ── Server handler ────────────────────────────────────────────────────────

    public static class Handler implements IMessageHandler<PacketMissileSteer, IMessage> {

        @Override
        public IMessage onMessage(PacketMissileSteer message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;

            player.getServerWorld().addScheduledTask(() -> {
                // Find whichever guided missile lists this player as controller.
                // Only one of the two branches below will ever match for a given player.
                for (Entity e : player.world.loadedEntityList) {
                    if (e instanceof PredatorMissileEntity) {
                        PredatorMissileEntity missile = (PredatorMissileEntity) e;
                        if (missile.controllerPlayer == player) {
                            // topAttack is intentionally ignored here — Predator Strike has no such mode.
                            missile.yawInput   += message.yaw;
                            missile.pitchInput += message.pitch;
                            return;
                        }
                    } else if (e instanceof TruckGuidedMissileEntity) {
                        TruckGuidedMissileEntity missile = (TruckGuidedMissileEntity) e;
                        if (missile.controllerPlayer == player) {
                            missile.yawInput   += message.yaw;
                            missile.pitchInput += message.pitch;
                            // One-way trigger, not a toggle: a single Shift press starts the
                            // climb-then-dive maneuver; repeated true values while already
                            // active or diving are no-ops (see activateTopAttack's guard).
                            if (message.topAttack) missile.activateTopAttack();
                            return;
                        }
                    }
                }
            });

            return null;
        }
    }
}
