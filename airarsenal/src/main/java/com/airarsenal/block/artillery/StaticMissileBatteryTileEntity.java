package com.airarsenal.block.artillery;

import com.airarsenal.entity.projectile.TruckGuidedMissileEntity;
import com.airarsenal.network.ModNetwork;
import com.airarsenal.network.PacketGuidanceStart;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Static Missile Battery — single-tube, block-mounted version of the Missile Truck.
 * 80-tick reload, 120-block signal range advertised to the player via HUD (the actual
 * signal-loss check lives in {@link TruckGuidedMissileEntity}, which uses its own fixed
 * 150-block range regardless of launcher type — the battery's 120-block figure is its
 * effective *tactical* range before signal degrades in practice, per spec).
 */
public class StaticMissileBatteryTileEntity extends BaseArtilleryTileEntity {

    private static final int RELOAD_TICKS = 80;

    @Override
    protected void tickArtillery() {
        // Fully player-triggered — no autonomous targeting.
    }

    public boolean isReady() { return cooldownTicks <= 0; }

    /** Fires a guided missile under the given player's control. */
    public boolean fire(EntityPlayerMP player) {
        if (!isReady()) return false;

        TruckGuidedMissileEntityLauncher.launch(world, pos, player);
        cooldownTicks = RELOAD_TICKS;
        markDirty();
        return true;
    }

    /** Small helper so the launch code (shared shape) isn't duplicated inline. */
    private static final class TruckGuidedMissileEntityLauncher {
        static void launch(net.minecraft.world.World world, net.minecraft.util.math.BlockPos pos,
                           EntityPlayerMP player) {
            TruckGuidedMissileEntity missile = new TruckGuidedMissileEntity(world,
                pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5,
                player.rotationYaw, 0f, player);
            world.spawnEntity(missile);
            ModNetwork.CHANNEL.sendTo(new PacketGuidanceStart(missile.getEntityId()), player);
        }
    }
}
