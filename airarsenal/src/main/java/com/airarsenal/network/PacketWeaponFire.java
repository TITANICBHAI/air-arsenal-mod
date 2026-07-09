package com.airarsenal.network;

import com.airarsenal.combat.weapon.IPlaneWeapon;
import com.airarsenal.entity.plane.BasePlaneEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Client → Server.
 * Tells the server to fire the plane's currently selected weapon in the direction
 * of the client player's look vector. Also carries the weapon index so the server
 * can sync selection with the client's scroll-wheel choice.
 *
 * Payload:
 * <ul>
 *   <li>3 × double — normalised look vector (x, y, z)</li>
 *   <li>1 × int    — selected weapon index</li>
 * </ul>
 */
public class PacketWeaponFire implements IMessage {

    private double dirX, dirY, dirZ;
    private int weaponIndex;

    // ── IMessage ──────────────────────────────────────────────────────────────

    public PacketWeaponFire() {} // required by Forge

    /**
     * @param direction   Normalised look vector from the client.
     * @param weaponIndex Client's currently selected hardpoint index.
     */
    public PacketWeaponFire(Vec3d direction, int weaponIndex) {
        this.dirX = direction.x;
        this.dirY = direction.y;
        this.dirZ = direction.z;
        this.weaponIndex = weaponIndex;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeDouble(dirX);
        buf.writeDouble(dirY);
        buf.writeDouble(dirZ);
        buf.writeInt(weaponIndex);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        dirX = buf.readDouble();
        dirY = buf.readDouble();
        dirZ = buf.readDouble();
        weaponIndex = buf.readInt();
    }

    // ── Server-side handler ───────────────────────────────────────────────────

    public static class Handler implements IMessageHandler<PacketWeaponFire, IMessage> {

        @Override
        public IMessage onMessage(PacketWeaponFire message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;

            player.getServerWorld().addScheduledTask(() -> {
                Entity riding = player.getRidingEntity();
                if (!(riding instanceof BasePlaneEntity)) return;

                BasePlaneEntity plane = (BasePlaneEntity) riding;

                // Sync weapon selection from client scroll choice
                plane.setSelectedWeaponIndex(message.weaponIndex);

                IPlaneWeapon weapon = plane.getSelectedWeapon();
                if (weapon == null) return;

                Vec3d direction = new Vec3d(message.dirX, message.dirY, message.dirZ);
                weapon.tryFire(plane.world, plane, direction);
            });

            return null;
        }
    }
}
