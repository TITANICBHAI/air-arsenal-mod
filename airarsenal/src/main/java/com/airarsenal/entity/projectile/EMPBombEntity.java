package com.airarsenal.entity.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

/**
 * EMP Bomb — electromagnetic pulse.
 *
 * Effects:
 * <ul>
 *   <li>No explosion, no block destruction (always).</li>
 *   <li>Applies Weakness III for 600 ticks (30 s) to all entities within 25 blocks.</li>
 *   <li>Invalidates (removes and replaces) all TileEntities within 25 blocks —
 *       simulating redstone disruption for the duration tracked in {@link EMPWorldData}.</li>
 * </ul>
 *
 * Not craftable — loot only.
 */
public class EMPBombEntity extends BaseBombEntity {

    private static final int EFFECT_RADIUS   = 25;
    private static final int EFFECT_DURATION = 600; // ticks

    public EMPBombEntity(World world) { super(world); }

    public EMPBombEntity(World world, Entity dropper) { super(world, dropper); }

    @Override
    protected void onImpact() {
        if (!world.isRemote) {
            applyEMPEffects();
        }
        setDead();
    }

    private void applyEMPEffects() {
        AxisAlignedBB aabb = new AxisAlignedBB(
            posX - EFFECT_RADIUS, posY - EFFECT_RADIUS, posZ - EFFECT_RADIUS,
            posX + EFFECT_RADIUS, posY + EFFECT_RADIUS, posZ + EFFECT_RADIUS
        );

        // ── Weakness to all living entities ───────────────────────────────────
        List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, aabb);
        for (EntityLivingBase entity : entities) {
            entity.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, EFFECT_DURATION, 2));
        }

        // ── Track all TileEntities for EMP disable ────────────────────────────
        EMPWorldData empData = EMPWorldData.get(world);
        BlockPos origin = new BlockPos(posX, posY, posZ);

        // Iterate a cube of blocks — gather all tile entities
        for (int dx = -EFFECT_RADIUS; dx <= EFFECT_RADIUS; dx++) {
            for (int dy = -EFFECT_RADIUS; dy <= EFFECT_RADIUS; dy++) {
                for (int dz = -EFFECT_RADIUS; dz <= EFFECT_RADIUS; dz++) {
                    if (dx * dx + dy * dy + dz * dz > EFFECT_RADIUS * EFFECT_RADIUS) continue;
                    BlockPos pos = origin.add(dx, dy, dz);
                    TileEntity te = world.getTileEntity(pos);
                    if (te != null) {
                        // Record position and expiry tick in world-saved data
                        long expiryTick = world.getTotalWorldTime() + EFFECT_DURATION;
                        empData.disableBlock(pos, expiryTick);
                    }
                }
            }
        }
        empData.markDirty();
    }
}
