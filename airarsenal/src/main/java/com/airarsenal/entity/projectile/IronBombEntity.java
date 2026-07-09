package com.airarsenal.entity.projectile;

import com.airarsenal.config.AirArsenalConfig;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

/**
 * Iron Bomb — standard dumb-drop bomb.
 * Blast radius: 8 blocks. Block destruction respects {@link AirArsenalConfig#allowBlockDestruction}.
 *
 * Also used as the sub-munition for {@link ClusterBombEntity} with a smaller radius.
 */
public class IronBombEntity extends BaseBombEntity {

    private float explosionRadius;

    // ── Construction ──────────────────────────────────────────────────────────

    public IronBombEntity(World world) {
        super(world);
        this.explosionRadius = 8f;
    }

    public IronBombEntity(World world, Entity dropper) {
        super(world, dropper);
        this.explosionRadius = 8f;
    }

    /** Used by ClusterBombEntity to spawn sub-munitions with a custom radius. */
    public IronBombEntity(World world, Entity dropper, float radius) {
        super(world, dropper);
        this.explosionRadius = radius;
    }

    // ── Impact ────────────────────────────────────────────────────────────────

    @Override
    protected void onImpact() {
        if (!world.isRemote) {
            world.createExplosion(
                droppedByEntity,
                posX, posY, posZ,
                explosionRadius,
                AirArsenalConfig.allowBlockDestruction
            );
        }
        setDead();
    }
}
