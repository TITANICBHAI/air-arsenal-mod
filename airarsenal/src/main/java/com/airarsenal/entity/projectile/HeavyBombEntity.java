package com.airarsenal.entity.projectile;

import com.airarsenal.config.AirArsenalConfig;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Heavy Bomb — large-yield bomb that also carves a crater.
 * Blast radius: 14 blocks. Crater: 4-block-deep sphere below impact point.
 */
public class HeavyBombEntity extends BaseBombEntity {

    private static final float EXPLOSION_RADIUS = 14f;
    private static final int   CRATER_DEPTH     = 4;

    public HeavyBombEntity(World world) { super(world); }

    public HeavyBombEntity(World world, Entity dropper) { super(world, dropper); }

    @Override
    protected void onImpact() {
        if (!world.isRemote) {
            world.createExplosion(
                droppedByEntity,
                posX, posY, posZ,
                EXPLOSION_RADIUS,
                AirArsenalConfig.allowBlockDestruction
            );
            if (AirArsenalConfig.allowBlockDestruction) {
                generateCrater((int) posX, (int) posY, (int) posZ, CRATER_DEPTH);
            }
        }
        setDead();
    }

    /**
     * Carves a sphere of {@code radius} below the impact point,
     * replacing solid terrain blocks with air.
     */
    private void generateCrater(int cx, int cy, int cz, int radius) {
        int r2 = radius * radius;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                for (int dy = -radius; dy <= 0; dy++) { // only downward hemisphere
                    if (dx * dx + dy * dy + dz * dz <= r2) {
                        BlockPos pos = new BlockPos(cx + dx, cy + dy, cz + dz);
                        Material mat = world.getBlockState(pos).getMaterial();
                        // Replace terrain blocks; leave ores, structures, etc. untouched
                        if (mat == Material.GROUND || mat == Material.ROCK
                                || mat == Material.GRASS || mat == Material.SAND) {
                            world.setBlockToAir(pos);
                        }
                    }
                }
            }
        }
    }
}
