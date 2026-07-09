package com.airarsenal.entity.projectile;

import com.airarsenal.config.AirArsenalConfig;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Depth Charge — anti-submarine weapon optimised for underwater detonation.
 *
 * <ul>
 *   <li>In water: blast radius 12.5 blocks (5 × 2.5 amplification)</li>
 *   <li>In air:   blast radius 5 blocks (base)</li>
 * </ul>
 *
 * Not craftable — loot only.
 */
public class DepthChargeEntity extends BaseBombEntity {

    private static final float BASE_RADIUS      = 5f;
    private static final float WATER_MULTIPLIER = 2.5f;

    public DepthChargeEntity(World world) { super(world); }

    public DepthChargeEntity(World world, Entity dropper) { super(world, dropper); }

    @Override
    protected void onImpact() {
        if (!world.isRemote) {
            BlockPos impactPos = new BlockPos(posX, posY, posZ);
            Material mat = world.getBlockState(impactPos).getMaterial();

            float radius = (mat == Material.WATER || mat == Material.LAVA)
                ? BASE_RADIUS * WATER_MULTIPLIER  // 12.5 blocks underwater
                : BASE_RADIUS;                     // 5 blocks in air

            world.createExplosion(
                droppedByEntity,
                posX, posY, posZ,
                radius,
                AirArsenalConfig.allowBlockDestruction
            );
        }
        setDead();
    }
}
