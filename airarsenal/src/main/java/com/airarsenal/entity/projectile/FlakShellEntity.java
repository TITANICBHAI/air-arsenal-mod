package com.airarsenal.entity.projectile;

import com.airarsenal.config.AirArsenalConfig;
import com.airarsenal.entity.plane.BasePlaneEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

/**
 * Flak Battery shell — proximity-fused anti-air round.
 *
 * <p>Unlike {@link AAShellEntity}, this shell detonates even on a near-miss:
 * a 5-block proximity fuse triggers relative to the tracked target's
 * <i>current</i> position, so a plane juking at the last second still eats
 * the blast if it stays within range.</p>
 *
 * <ul>
 *   <li>Speed: 3 blocks/tick straight-line (no guidance after launch — dumb-fired flak).</li>
 *   <li>Proximity fuse: detonates within 5 blocks of {@link #trackedTarget}'s position.</li>
 *   <li>Blast: 5-block radius, deals 15 HP to any {@link BasePlaneEntity} caught inside.</li>
 *   <li>Lifetime: 160 ticks — detonates harmlessly if it never gets close.</li>
 * </ul>
 */
public class FlakShellEntity extends Entity {

    private static final float SPEED           = 3f;
    private static final float PROXIMITY_FUSE  = 5f;
    private static final float BLAST_RADIUS    = 5f;
    private static final float BLAST_DAMAGE    = 15f;
    private static final int   MAX_LIFETIME    = 160;

    /** The plane this shell was aimed at when fired. May move — proximity fuse tracks it live. */
    public Entity trackedTarget;

    private int lifetimeTicks = 0;

    public FlakShellEntity(World world) {
        super(world);
        setSize(0.4f, 0.4f);
    }

    /**
     * Spawns a flak shell at an explicit muzzle position, aimed along {@code launchDir}.
     *
     * @param world      World to spawn in.
     * @param muzzleX/Y/Z Emplacement muzzle position.
     * @param target     Plane to track for the proximity fuse (may out-maneuver the shell).
     * @param launchDir  Unit direction vector to fire along.
     */
    public FlakShellEntity(World world, double muzzleX, double muzzleY, double muzzleZ,
                           Entity target, Vec3d launchDir) {
        this(world);
        this.trackedTarget = target;
        setPosition(muzzleX, muzzleY, muzzleZ);
        motionX = launchDir.x * SPEED;
        motionY = launchDir.y * SPEED;
        motionZ = launchDir.z * SPEED;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        lifetimeTicks++;

        if (!world.isRemote) {
            // ── Proximity fuse ─────────────────────────────────────────────────
            if (trackedTarget != null && trackedTarget.isEntityAlive()) {
                double dist = getDistance(trackedTarget);
                if (dist <= PROXIMITY_FUSE) {
                    detonate();
                    return;
                }
            }

            if (lifetimeTicks >= MAX_LIFETIME) {
                detonate();
                return;
            }
        }

        move(net.minecraft.entity.MoverType.SELF, motionX, motionY, motionZ);
    }

    private void detonate() {
        if (!world.isRemote) {
            AxisAlignedBB blastBox = new AxisAlignedBB(
                posX - BLAST_RADIUS, posY - BLAST_RADIUS, posZ - BLAST_RADIUS,
                posX + BLAST_RADIUS, posY + BLAST_RADIUS, posZ + BLAST_RADIUS
            );
            List<BasePlaneEntity> planes = world.getEntitiesWithinAABB(BasePlaneEntity.class, blastBox);
            for (BasePlaneEntity plane : planes) {
                if (getDistance(plane) <= BLAST_RADIUS) {
                    plane.damagePlane(BLAST_DAMAGE);
                }
            }
            // Small visual/ground effect only — flak bursts don't break terrain
            if (AirArsenalConfig.allowBlockDestruction) {
                world.createExplosion(null, posX, posY, posZ, 0f, false);
            }
        } else {
            for (int i = 0; i < 12; i++) {
                world.spawnParticle(EnumParticleTypes.SMOKE_LARGE,
                    posX, posY, posZ,
                    (world.rand.nextDouble() - 0.5) * 0.3,
                    world.rand.nextDouble() * 0.2,
                    (world.rand.nextDouble() - 0.5) * 0.3);
            }
        }
        setDead();
    }

    @Override protected void entityInit() {}

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        compound.setInteger("LifetimeTicks", lifetimeTicks);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        lifetimeTicks = compound.getInteger("LifetimeTicks");
    }

    @Override public boolean canBePushed()       { return false; }
    @Override public boolean canBeCollidedWith() { return false; }
}
