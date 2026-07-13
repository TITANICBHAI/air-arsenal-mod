package com.airarsenal.entity.projectile;

import com.airarsenal.AirArsenal;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

/**
 * BrahMos cruise missile — fire-and-forget, flies to a player-set {@link BlockPos} waypoint.
 *
 * <ul>
 *   <li>Speed: 4.4 blocks/tick (≈ 220 b/s)</li>
 *   <li>Range: unlimited — flies until it reaches the waypoint or hits a block</li>
 *   <li>Warhead: 14-block blast radius; always breaks blocks regardless of config</li>
 *   <li>Guidance: pure proportional navigation — straight line to target BlockPos</li>
 * </ul>
 *
 * Waypoint is set via {@link com.airarsenal.item.ItemBrahMosTargeter} before boarding.
 */
public class BrahMosEntity extends Entity {

    private static final float SPEED            = 4.4f;
    private static final float BLAST_RADIUS     = 14f;
    private static final float ARRIVAL_DISTANCE = 2f;

    /** Destination waypoint set by the player before launch. */
    private BlockPos targetPos;

    /** Plane or player that launched this missile. Transient. */
    private Entity shooter;

    // ── Construction ──────────────────────────────────────────────────────────

    public BrahMosEntity(World world) {
        super(world);
        setSize(0.8f, 0.5f);
    }

    public BrahMosEntity(World world, Entity shooter, BlockPos target) {
        this(world);
        this.shooter   = shooter;
        this.targetPos = target;
        setPosition(shooter.posX, shooter.posY, shooter.posZ);
        // Point initial velocity toward target
        Vec3d dir = directionToTarget();
        motionX = dir.x * SPEED;
        motionY = dir.y * SPEED;
        motionZ = dir.z * SPEED;
    }

    // ── Per-tick update ───────────────────────────────────────────────────────

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (targetPos == null) { setDead(); return; }

        // ── Steer toward target ───────────────────────────────────────────────
        if (!world.isRemote) {
            Vec3d dir = directionToTarget();
            motionX = dir.x * SPEED;
            motionY = dir.y * SPEED;
            motionZ = dir.z * SPEED;

            // ── Arrival check ─────────────────────────────────────────────────
            double distToTarget = new Vec3d(
                posX - (targetPos.getX() + 0.5),
                posY - (targetPos.getY() + 0.5),
                posZ - (targetPos.getZ() + 0.5)
            ).lengthVector();

            if (distToTarget <= ARRIVAL_DISTANCE) {
                AirArsenal.LOGGER.debug("BrahMos arrived at waypoint {}", targetPos);
                triggerExplosion();
                return;
            }

            // ── Entity collision ──────────────────────────────────────────────
            AxisAlignedBB sweptBox = getEntityBoundingBox()
                .expand(motionX, motionY, motionZ).grow(0.5);
            List<Entity> hits = world.getEntitiesInAABBexcluding(this, sweptBox,
                e -> e != shooter && e.canBeCollidedWith() && e.isEntityAlive());
            if (!hits.isEmpty()) { triggerExplosion(); return; }
        }

        // ── Move ──────────────────────────────────────────────────────────────
        Vec3d start = new Vec3d(posX, posY, posZ);
        moveEntity(motionX, motionY, motionZ);
        Vec3d end   = new Vec3d(posX, posY, posZ);

        // ── Block collision ───────────────────────────────────────────────────
        if (!world.isRemote) {
            RayTraceResult hit = world.rayTraceBlocks(start, end, false, true, false);
            if (hit != null && hit.typeOfHit == RayTraceResult.Type.BLOCK) {
                triggerExplosion();
            }
        }
    }

    // ── Warhead ───────────────────────────────────────────────────────────────

    private void triggerExplosion() {
        if (!world.isRemote) {
            // BrahMos always breaks blocks — it's a cruise missile, not a precision round
            world.createExplosion(shooter, posX, posY, posZ, BLAST_RADIUS, true);
        }
        setDead();
    }

    // ── Navigation ───────────────────────────────────────────────────────────

    private Vec3d directionToTarget() {
        Vec3d toTarget = new Vec3d(
            targetPos.getX() + 0.5 - posX,
            targetPos.getY() + 0.5 - posY,
            targetPos.getZ() + 0.5 - posZ
        );
        double len = toTarget.lengthVector();
        return len < 0.001 ? Vec3d.ZERO : toTarget.scale(1.0 / len);
    }

    // ── Entity framework ──────────────────────────────────────────────────────

    @Override protected void entityInit() {}

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        if (targetPos != null) {
            compound.setInteger("TargetX", targetPos.getX());
            compound.setInteger("TargetY", targetPos.getY());
            compound.setInteger("TargetZ", targetPos.getZ());
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        if (compound.hasKey("TargetX")) {
            targetPos = new BlockPos(
                compound.getInteger("TargetX"),
                compound.getInteger("TargetY"),
                compound.getInteger("TargetZ")
            );
        }
    }

    @Override public boolean canBePushed()       { return false; }
    @Override public boolean canBeCollidedWith() { return true;  }

    public void setShooter(Entity shooter) { this.shooter = shooter; }
}
