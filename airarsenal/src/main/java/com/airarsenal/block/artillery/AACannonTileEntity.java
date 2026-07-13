package com.airarsenal.block.artillery;

import com.airarsenal.entity.plane.BasePlaneEntity;
import com.airarsenal.entity.projectile.AAShellEntity;
import net.minecraft.util.math.Vec3d;

/**
 * AA Cannon — 80-block vertical range anti-air emplacement.
 *
 * <ul>
 *   <li>Turn rate: 3°/tick (both yaw and pitch) — a fast plane can outrun its traversal.</li>
 *   <li>Fires when aimed within 30° of the target, every 10 ticks (2 shots/sec).</li>
 *   <li>Each {@link AAShellEntity} deals a flat 8 HP to the target's airframe.</li>
 * </ul>
 */
public class AACannonTileEntity extends BaseArtilleryTileEntity {

    private static final double RANGE            = 80.0;
    private static final float  TURN_RATE         = 3f;
    private static final int    FIRE_INTERVAL     = 10; // ticks — 2 shots/sec
    private static final float  FIRE_CONE_DEGREES = 30f;

    @Override
    protected void tickArtillery() {
        BasePlaneEntity target = findNearestPlane(RANGE);
        if (target == null) {
            targetEntityId = -1;
            return;
        }
        targetEntityId = target.getEntityId();

        // ── Compute desired heading toward target ───────────────────────────────
        double dx = target.posX - (pos.getX() + 0.5);
        double dy = (target.posY + target.height * 0.5) - (pos.getY() + 1.0);
        double dz = target.posZ - (pos.getZ() + 0.5);
        double horizDist = Math.sqrt(dx * dx + dz * dz);

        float desiredYaw   = (float) Math.toDegrees(Math.atan2(-dx, dz));
        float desiredPitch = (float) Math.toDegrees(Math.atan2(-dy, horizDist));

        // ── Traverse toward it at a fixed turn rate — NOT an instant snap ────────
        currentYaw   = turnToward(currentYaw,   desiredYaw,   TURN_RATE);
        currentPitch = turnToward(currentPitch, desiredPitch, TURN_RATE);

        // ── Fire only when the barrel is close enough to the target ─────────────
        boolean aligned = Math.abs(angleDiff(currentYaw, desiredYaw))   <= FIRE_CONE_DEGREES
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

        AAShellEntity shell = new AAShellEntity(world);
        shell.setPosition(pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5);
        shell.shootingEntity = null;
        shell.setThrowableHeading(dir.x, dir.y, dir.z, 2.0f, 0f); // 2 blocks/tick, no inaccuracy
        world.spawnEntity(shell);
    }
}
