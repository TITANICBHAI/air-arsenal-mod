package com.airarsenal.network;

import com.airarsenal.block.artillery.HowitzerTileEntity;
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
 * Client → Server. Sent by {@link com.airarsenal.client.gui.GuiHowitzer} when the
 * player confirms manually-entered target coordinates (single-player operation mode).
 *
 * Payload: {@code BlockPos guiPos, int targetX, int targetY, int targetZ}
 */
public class PacketSetHowitzerTarget implements IMessage {

    private BlockPos guiPos;
    private int targetX, targetY, targetZ;

    public PacketSetHowitzerTarget() {}

    public PacketSetHowitzerTarget(BlockPos guiPos, BlockPos target) {
        this.guiPos = guiPos;
        this.targetX = target.getX();
        this.targetY = target.getY();
        this.targetZ = target.getZ();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(guiPos.toLong());
        buf.writeInt(targetX);
        buf.writeInt(targetY);
        buf.writeInt(targetZ);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        guiPos = BlockPos.fromLong(buf.readLong());
        targetX = buf.readInt();
        targetY = buf.readInt();
        targetZ = buf.readInt();
    }

    public static class Handler implements IMessageHandler<PacketSetHowitzerTarget, IMessage> {
        @Override
        public IMessage onMessage(PacketSetHowitzerTarget message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> {
                TileEntity te = player.world.getTileEntity(message.guiPos);
                if (te instanceof HowitzerTileEntity
                        && player.getDistanceSq(message.guiPos) <= 64 * 64) {
                    BlockPos target = new BlockPos(message.targetX, message.targetY, message.targetZ);
                    boolean ok = ((HowitzerTileEntity) te).setTargetPos(target);
                    player.sendMessage(new TextComponentString(ok
                        ? TextFormatting.GOLD + "[Howitzer] Target set."
                        : TextFormatting.RED + "[Howitzer] Out of range (20-150 blocks)."));
                }
            });
            return null;
        }
    }
}
