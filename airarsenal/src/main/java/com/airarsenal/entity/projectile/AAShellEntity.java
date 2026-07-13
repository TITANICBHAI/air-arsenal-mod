package com.airarsenal.entity.projectile;

import com.airarsenal.entity.plane.BasePlaneEntity;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

/**
 * AA Cannon shell — fast, flat-trajectory anti-air round.
 *
 * <ul>
 *   <li>Speed: 2 blocks/tick (set via {@link #setThrowableHeading} at spawn).</li>
 *   <li>Damage: flat 8 HP to {@link BasePlaneEntity#damagePlane}, bypasses AP/DR entirely.</li>
 *   <li>Lifetime: 200 ticks (~10s) before self-despawn if it never connects.</li>
 * </ul>
 */
public class AAShellEntity extends EntityThrowable {

    private static final int MAX_LIFETIME = 200;
    private static final float DAMAGE = 8f;

    private int ticksAlive = 0;

    public AAShellEntity(World world) {
        super(world);
        setSize(0.3f, 0.3f);
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (result.typeOfHit == RayTraceResult.Type.ENTITY
                && result.entityHit instanceof BasePlaneEntity) {
            if (!world.isRemote) {
                ((BasePlaneEntity) result.entityHit).damagePlane(DAMAGE);
            }
            setDead();
        } else if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
            if (world.isRemote && result.hitVec != null) {
                world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL,
                    result.hitVec.x, result.hitVec.y, result.hitVec.z, 0, 0.02, 0);
            }
            setDead();
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!world.isRemote) {
            ticksAlive++;
            if (ticksAlive >= MAX_LIFETIME) setDead();
        }
    }

    @Override
    protected float getGravityVelocity() {
        return 0.01f; // near-flat trajectory over its short lifetime
    }
}
