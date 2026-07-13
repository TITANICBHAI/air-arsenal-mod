package com.airarsenal.network;

import com.airarsenal.block.artillery.MortarTileEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Client → Server. Sent by {@link com.airarsenal.client.gui.GuiMortar} whenever its
 * slider changes, to update the target {@link MortarTileEntity}'s elevation angle live.
 *
 * Payload: {@code BlockPos pos, float angleDegrees}
 */
public class PacketSetMortarAngle implements IMessage {

    private BlockPos pos;
    private float angle;

    public PacketSetMortarAngle() {}

    public PacketSetMortarAngle(BlockPos pos, float angle) {
        this.pos = pos;
        this.angle = angle;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
        buf.writeFloat(angle);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = BlockPos.fromLong(buf.readLong());
        angle = buf.readFloat();
    }

    public static class Handler implements IMessageHandler<PacketSetMortarAngle, IMessage> {
        @Override
        public IMessage onMessage(PacketSetMortarAngle message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> {
                TileEntity te = player.world.getTileEntity(message.pos);
                if (te instanceof MortarTileEntity
                        && player.getDistanceSq(message.pos) <= 64 * 64) {
                    ((MortarTileEntity) te).setElevationAngle(message.angle);
                }
            });
            return null;
        }
    }
}
