package com.airarsenal.entity.projectile;

import com.airarsenal.config.AirArsenalConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

/**
 * A single MLRS rocket — one of a burst of 8 fired by {@link com.airarsenal.block.artillery.MLRSTileEntity}.
 * Straight-line dumb-fire with a small random spread applied at launch (up to ±8 blocks
 * at typical engagement range, baked into the launch direction by the caller).
 *
 * Blast: 5-block radius.
 */
public class MLRSRocketEntity extends EntityThrowable {

    private static final float BLAST_RADIUS = 5f;

    public MLRSRocketEntity(World world) {
        super(world);
        setSize(0.3f, 0.3f);
    }

    public MLRSRocketEntity(World world, Entity firer) {
        super(world, firer instanceof net.minecraft.entity.EntityLivingBase
            ? (net.minecraft.entity.EntityLivingBase) firer : null);
        setSize(0.3f, 0.3f);
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
        return 0.02f; // fast, mostly flat rocket trajectory
    }
}
