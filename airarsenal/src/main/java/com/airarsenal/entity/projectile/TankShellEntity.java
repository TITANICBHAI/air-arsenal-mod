package com.airarsenal.entity.projectile;

import com.airarsenal.combat.AirArsenalDamageSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

/**
 * Tank main-gun shell — fired by {@link com.airarsenal.entity.vehicle.TankEntity} at players
 * within 30 blocks, every 3 seconds. High flat damage, bypasses armor entirely via
 * {@link AirArsenalDamageSource#SHELL_TANK}.
 */
public class TankShellEntity extends EntityThrowable {

    private static final float DAMAGE = 25f;
    private static final float BLAST_RADIUS = 3f;

    public TankShellEntity(World world) {
        super(world);
        setSize(0.35f, 0.35f);
    }

    public TankShellEntity(World world, Entity firer) {
        super(world, firer instanceof EntityLivingBase ? (EntityLivingBase) firer : null);
        setSize(0.35f, 0.35f);
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (!world.isRemote) {
            if (result.typeOfHit == RayTraceResult.Type.ENTITY && result.entityHit instanceof EntityLivingBase) {
                ((EntityLivingBase) result.entityHit).attackEntityFrom(AirArsenalDamageSource.SHELL_TANK, DAMAGE);
            }
            world.createExplosion(this, posX, posY, posZ, BLAST_RADIUS, false);
        }
        setDead();
    }

    @Override
    protected float getGravityVelocity() { return 0.03f; }
}
