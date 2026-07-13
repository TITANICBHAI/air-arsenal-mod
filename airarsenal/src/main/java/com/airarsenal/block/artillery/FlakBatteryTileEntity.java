package com.airarsenal.block.artillery;

import com.airarsenal.entity.plane.BasePlaneEntity;
import com.airarsenal.entity.projectile.FlakShellEntity;
import net.minecraft.util.math.Vec3d;

/**
 * Flak Battery — long-range (120-block) anti-air emplacement with proximity-fused shells.
 *
 * <ul>
 *   <li>Turn rate: 2°/tick — slower traversal than the AA Cannon, but longer reach.</li>
 *   <li>Fires every 20 ticks (1 shot / 2 sec) once aimed within 30°.</li>
 *   <li>{@link FlakShellEntity} detonates within 5 blocks of the target even on a miss.</li>
 * </ul>
 */
public class FlakBatteryTileEntity extends BaseArtilleryTileEntity {

    private static final double RANGE            = 120.0;
    private static final float  TURN_RATE         = 2f;
    private static final int    FIRE_INTERVAL     = 20; // ticks — 1 shot / 2 sec
    private static final float  FIRE_CONE_DEGREES = 30f;

    @Override
    protected void tickArtillery() {
        BasePlaneEntity target = findNearestPlane(RANGE);
        if (target == null) {
            targetEntityId = -1;
            return;
        }
        targetEntityId = target.getEntityId();

        double dx = target.posX - (pos.getX() + 0.5);
        double dy = (target.posY + target.height * 0.5) - (pos.getY() + 1.0);
        double dz = target.posZ - (pos.getZ() + 0.5);
        double horizDist = Math.sqrt(dx * dx + dz * dz);

        float desiredYaw   = (float) Math.toDegrees(Math.atan2(-dx, dz));
        float desiredPitch = (float) Math.toDegrees(Math.atan2(-dy, horizDist));

        currentYaw   = turnToward(currentYaw,   desiredYaw,   TURN_RATE);
        currentPitch = turnToward(currentPitch, desiredPitch, TURN_RATE);

        boolean aligned = Math.abs(angleDiff(currentYaw, desiredYaw))     <= FIRE_CONE_DEGREES
                        && Math.abs(angleDiff(currentPitch, desiredPitch)) <= FIRE_CONE_DEGREES;

        if (autoMode && aligned && cooldownTicks <= 0) {
            fireAt(target);
            cooldownTicks = FIRE_INTERVAL;
        }

        markDirty();
    }

    private void fireAt(BasePlaneEntity target) {
        double yawRad   = Math.toRadians(currentYaw);
        double pitchRad = Math.toRadians(currentPitch);
        double cosP = Math.cos(pitchRad);
        Vec3d dir = new Vec3d(
            -Math.sin(yawRad) * cosP,
            -Math.sin(pitchRad),
             Math.cos(yawRad) * cosP
        ).normalize();

        FlakShellEntity shell = new FlakShellEntity(world,
            pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5,
            target, dir);
        world.spawnEntity(shell);
    }
}
