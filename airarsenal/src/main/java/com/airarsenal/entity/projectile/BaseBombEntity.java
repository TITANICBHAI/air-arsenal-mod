package com.airarsenal.entity.projectile;

import com.airarsenal.config.AirArsenalConfig;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

/**
 * Abstract base for every bomb entity in Air Arsenal.
 *
 * <h3>Physics (per tick):</h3>
 * <ul>
 *   <li>Gravity:       {@code motionY -= 0.03}</li>
 *   <li>Air resistance: {@code motionX *= 0.99}, {@code motionZ *= 0.99}</li>
 *   <li>Block collision: ray-trace from previous to current position → {@link #onImpact()}</li>
 *   <li>Entity collision: AABB scan along flight path → {@link #onEntityImpact(Entity)}</li>
 * </ul>
 *
 * Subclasses implement {@link #onImpact()} for their specific explosion / effect.
 */
public abstract class BaseBombEntity extends Entity {

    /**
     * Ticks before the bomb arms.  0 = armed immediately on drop.
     * During the fuse period, entity and block collisions are ignored.
     */
    protected int fuseTime = 0;

    /** The entity (plane/player) that dropped this bomb — used for friendly-fire checks. */
    protected Entity droppedByEntity;

    private int ticksExisted = 0;

    // ── Construction ──────────────────────────────────────────────────────────

    public BaseBombEntity(World world) {
        super(world);
        setSize(0.5f, 0.5f);
    }

    public BaseBombEntity(World world, Entity dropper) {
        this(world);
        this.droppedByEntity = dropper;
        setPosition(dropper.posX, dropper.posY, dropper.posZ);
        // Inherit the plane's horizontal velocity so the bomb arcs naturally
        this.motionX = dropper.motionX;
        this.motionY = 0;
        this.motionZ = dropper.motionZ;
    }

    // ── Abstract API ──────────────────────────────────────────────────────────

    /** Called when the bomb hits a block. Subclasses trigger their explosion here. */
    protected abstract void onImpact();

    /**
     * Called when the bomb's path intersects a living entity.
     * Default: calls {@link #onImpact()} at the bomb's current position.
     * Override for bombs that should pass through entities.
     */
    protected void onEntityImpact(Entity target) {
        if (!AirArsenalConfig.allowFriendlyFire && target == droppedByEntity) return;
        onImpact();
    }

    // ── Per-tick physics ──────────────────────────────────────────────────────

    @Override
    public void onUpdate() {
        super.onUpdate();
        ticksExisted++;

        // ── Apply physics ─────────────────────────────────────────────────────
        motionY -= 0.03;         // gravity
        motionX *= 0.99;         // air resistance
        motionZ *= 0.99;

        // ── Entity collision scan (before moving, check the swept path) ───────
        if (!world.isRemote && ticksExisted > fuseTime) {
            AxisAlignedBB sweptBox = getEntityBoundingBox()
                .expand(motionX, motionY, motionZ)
                .grow(0.5);

            List<Entity> candidates = world.getEntitiesInAABBexcluding(this, sweptBox,
                e -> e != droppedByEntity && e.canBeCollidedWith() && e.isEntityAlive());

            if (!candidates.isEmpty()) {
                // Hit the nearest entity along the path
                Entity nearest = candidates.get(0);
                double bestDist = Double.MAX_VALUE;
                for (Entity e : candidates) {
                    double d = getDistanceSq(e);
                    if (d < bestDist) { bestDist = d; nearest = e; }
                }
                onEntityImpact(nearest);
                return;
            }
        }

        // ── Move ──────────────────────────────────────────────────────────────
        Vec3d startVec = new Vec3d(posX, posY, posZ);
        move(net.minecraft.entity.MoverType.SELF, motionX, motionY, motionZ);
        Vec3d endVec = new Vec3d(posX, posY, posZ);

        // ── Block collision check (ray from old → new position) ───────────────
        if (!world.isRemote && ticksExisted > fuseTime) {
            RayTraceResult hit = world.rayTraceBlocks(startVec, endVec, false, true, false);
            if (hit != null && hit.typeOfHit == RayTraceResult.Type.BLOCK) {
                onImpact();
            }
        }
    }

    // ── Entity framework ──────────────────────────────────────────────────────

    @Override
    protected void entityInit() {}

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        compound.setInteger("FuseTime", fuseTime);
        compound.setInteger("TicksExisted", ticksExisted);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        fuseTime    = compound.getInteger("FuseTime");
        ticksExisted = compound.getInteger("TicksExisted");
    }

    @Override
    public boolean canBeCollidedWith() { return true; }
    @Override
    public boolean canBePushed()       { return false; }

    // ── Getters ───────────────────────────────────────────────────────────────
    public Entity getDroppedByEntity() { return droppedByEntity; }
}
