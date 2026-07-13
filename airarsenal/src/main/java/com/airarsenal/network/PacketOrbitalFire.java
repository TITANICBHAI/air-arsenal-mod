package com.airarsenal.network;

import com.airarsenal.block.OrbitalCannonTileEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Client → Server. Sent by {@link com.airarsenal.client.gui.OrbitalCannonGui}'s FIRE
 * button. The server re-validates everything (state, structure, card, target) before
 * committing — the client-side greyed-out button is a UX hint only, not trusted.
 *
 * Payload: {@code BlockPos cannonPos}
 */
public class PacketOrbitalFire implements IMessage {

    private BlockPos cannonPos;

    public PacketOrbitalFire() {}
    public PacketOrbitalFire(BlockPos cannonPos) { this.cannonPos = cannonPos; }

    @Override
    public void toBytes(ByteBuf buf) { buf.writeLong(cannonPos.toLong()); }

    @Override
    public void fromBytes(ByteBuf buf) { cannonPos = BlockPos.fromLong(buf.readLong()); }

    public static class Handler implements IMessageHandler<PacketOrbitalFire, IMessage> {
        @Override
        public IMessage onMessage(PacketOrbitalFire message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> {
                if (player.getDistanceSq(message.cannonPos) > 64 * 64) return;
                TileEntity te = player.world.getTileEntity(message.cannonPos);
                if (!(te instanceof OrbitalCannonTileEntity)) return;
                boolean fired = ((OrbitalCannonTileEntity) te).tryFire();
                if (!fired) {
                    player.sendMessage(new TextComponentString(
                        TextFormatting.RED + "[Orbital Cannon] Cannot fire — check structure, target, and uplink card."));
                }
            });
            return null;
        }
    }
}
