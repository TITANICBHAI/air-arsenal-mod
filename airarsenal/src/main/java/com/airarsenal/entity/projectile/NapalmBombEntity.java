package com.airarsenal.entity.projectile;

import com.airarsenal.config.AirArsenalConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

/**
 * Napalm Canister — small blast plus wide incendiary fire spread.
 *
 * <ul>
 *   <li>Explosion radius: 6 blocks</li>
 *   <li>Fire spread: flammable blocks within 20-block horizontal radius</li>
 *   <li>Entity effect: Wither II for 200 ticks (10 s) within 6-block radius</li>
 * </ul>
 */
public class NapalmBombEntity extends BaseBombEntity {

    private static final float EXPLOSION_RADIUS   = 6f;
    private static final int   FIRE_SPREAD_RADIUS = 20;
    private static final int   EFFECT_RADIUS      = 6;

    public NapalmBombEntity(World world) { super(world); }

    public NapalmBombEntity(World world, Entity dropper) { super(world, dropper); }

    @Override
    protected void onImpact() {
        if (!world.isRemote) {
            world.createExplosion(
                droppedByEntity,
                posX, posY, posZ,
                EXPLOSION_RADIUS,
                AirArsenalConfig.allowBlockDestruction
            );
            spreadFire();
            applyWitherEffect();
        }
        setDead();
    }

    /** Places fire on top of flammable surfaces within the spread radius. */
    private void spreadFire() {
        BlockPos origin = new BlockPos(posX, posY, posZ);
        for (int dx = -FIRE_SPREAD_RADIUS; dx <= FIRE_SPREAD_RADIUS; dx++) {
            for (int dz = -FIRE_SPREAD_RADIUS; dz <= FIRE_SPREAD_RADIUS; dz++) {
                if (dx * dx + dz * dz > FIRE_SPREAD_RADIUS * FIRE_SPREAD_RADIUS) continue;

                BlockPos surface = origin.add(dx, 0, dz);
                // Search up to 3 blocks above the surface to find a flammable face
                for (int dy = -1; dy <= 2; dy++) {
                    BlockPos check = surface.add(0, dy, 0);
                    Block block = world.getBlockState(check).getBlock();
                    BlockPos above = check.up();

                    if (block.isFlammable(world, check, net.minecraft.util.EnumFacing.UP)
                            && world.isAirBlock(above)) {
                        world.setBlockState(above, Blocks.FIRE.getDefaultState());
                        break;
                    }
                }
            }
        }
    }

    /** Applies Wither II (200 ticks) to all living entities within 6 blocks. */
    private void applyWitherEffect() {
        AxisAlignedBBHelper aabb = new AxisAlignedBBHelper(posX, posY, posZ, EFFECT_RADIUS);
        List<EntityLivingBase> entities = world.getEntitiesWithinAABB(
            EntityLivingBase.class, aabb.get());

        for (EntityLivingBase entity : entities) {
            if (!AirArsenalConfig.allowFriendlyFire && entity == droppedByEntity) continue;
            entity.addPotionEffect(new PotionEffect(MobEffects.WITHER, 200, 1));
        }
    }

    /** Inline AABB builder to avoid import clutter. */
    private static class AxisAlignedBBHelper {
        private final net.minecraft.util.math.AxisAlignedBB aabb;
        AxisAlignedBBHelper(double x, double y, double z, int r) {
            aabb = new net.minecraft.util.math.AxisAlignedBB(x - r, y - r, z - r,
                                                              x + r, y + r, z + r);
        }
        net.minecraft.util.math.AxisAlignedBB get() { return aabb; }
    }
}
