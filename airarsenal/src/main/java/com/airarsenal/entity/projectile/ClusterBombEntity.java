package com.airarsenal.entity.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

/**
 * Cluster Bomb — scatters 12 mini Iron Bombs on impact.
 *
 * Each sub-munition has:
 * <ul>
 *   <li>Blast radius: 2 blocks</li>
 *   <li>Random horizontal velocity: ±0.8</li>
 *   <li>Downward velocity: 0.3 blocks/tick</li>
 * </ul>
 *
 * Not craftable — loot only.
 */
public class ClusterBombEntity extends BaseBombEntity {

    private static final int   SUB_BOMB_COUNT  = 12;
    private static final float SUB_BOMB_RADIUS = 2f;
    private static final float SPREAD_VELOCITY = 0.8f;
    private static final float DROP_VELOCITY   = 0.3f;

    public ClusterBombEntity(World world) { super(world); }

    public ClusterBombEntity(World world, Entity dropper) { super(world, dropper); }

    @Override
    protected void onImpact() {
        if (!world.isRemote) {
            for (int i = 0; i < SUB_BOMB_COUNT; i++) {
                IronBombEntity sub = new IronBombEntity(world, droppedByEntity, SUB_BOMB_RADIUS);

                // Scatter position slightly around impact point
                double spawnX = posX + (world.rand.nextDouble() - 0.5) * 2.0;
                double spawnY = posY + 0.5;
                double spawnZ = posZ + (world.rand.nextDouble() - 0.5) * 2.0;
                sub.setPosition(spawnX, spawnY, spawnZ);

                // Random horizontal spread, fixed downward velocity
                sub.motionX = (world.rand.nextDouble() - 0.5) * 2.0 * SPREAD_VELOCITY;
                sub.motionY = -DROP_VELOCITY;
                sub.motionZ = (world.rand.nextDouble() - 0.5) * 2.0 * SPREAD_VELOCITY;

                // Short fuse so the sub-bombs don't immediately detonate on spawn
                sub.fuseTime = 3;

                world.spawnEntity(sub);
            }
        }
        setDead();
    }
}
