package com.airarsenal.entity.projectile;

import com.airarsenal.config.AirArsenalConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

/**
 * Mortar shell — parabolic arc determined entirely by launch velocity (from firing angle).
 * Standard EntityThrowable gravity gives the arc; no guidance.
 *
 * Impact: 6-block explosion.
 */
public class MortarShellEntity extends EntityThrowable {

    private static final float BLAST_RADIUS = 6f;

    public MortarShellEntity(World world) {
        super(world);
        setSize(0.35f, 0.35f);
    }

    public MortarShellEntity(World world, Entity firer) {
        super(world, firer instanceof net.minecraft.entity.EntityLivingBase
            ? (net.minecraft.entity.EntityLivingBase) firer : null);
        setSize(0.35f, 0.35f);
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (!world.isRemote) {
            world.createExplosion(this, posX, posY, posZ,
                BLAST_RADIUS, AirArsenalConfig.allowBlockDestruction);
        }
        setDead();
    }

    @Override
    protected float getGravityVelocity() {
        return 0.05f; // matches the range formula's assumed gravity constant
    }
}
