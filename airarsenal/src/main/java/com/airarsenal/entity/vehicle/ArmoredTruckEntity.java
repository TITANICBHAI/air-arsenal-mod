package com.airarsenal.entity.vehicle;

import com.airarsenal.combat.AirArsenalDamageSource;
import com.airarsenal.combat.DamageCalculator;
import com.airarsenal.item.ItemHeavyRound;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

/**
 * Armored Truck — passive NPC vehicle that patrols/wanders but never attacks.
 * 150 HP, DR 50 (mitigated via {@link DamageCalculator#calculateDamage}, same
 * AP-vs-DR formula used for players). Drops iron plus a chance of
 * {@link ItemHeavyRound} on death.
 */
public class ArmoredTruckEntity extends EntityCreature {

    private static final int ARMOR_DR = 50;
    /** AP assumed for damage sources that don't carry their own rating (e.g. vanilla melee). */
    private static final int DEFAULT_AP = 5;

    public ArmoredTruckEntity(World world) {
        super(world);
        setSize(1.6f, 1.5f);
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(1, new EntityAIWanderAvoidWater(this, 0.6));
        tasks.addTask(2, new EntityAILookIdle(this));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getAttributeMap().registerAttribute(SharedMonsterAttributes.MAX_HEALTH);
        getAttributeMap().registerAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(150.0);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (world.isRemote) return false;
        int ap = (source instanceof AirArsenalDamageSource)
            ? ((AirArsenalDamageSource) source).armorPenetration : DEFAULT_AP;
        float mitigated = DamageCalculator.calculateDamage(amount, ap, ARMOR_DR);
        return super.attackEntityFrom(source, mitigated);
    }

    @Override
    protected void dropFewItems(boolean playerKill, int looting) {
        int ironCount = 1 + rand.nextInt(2 + looting);
        entityDropItem(new ItemStack(Items.IRON_INGOT, ironCount), 0f);

        if (rand.nextFloat() < 0.35f + looting * 0.05f) {
            entityDropItem(new ItemStack(com.airarsenal.registry.ModItems.HEAVY_ROUND), 0f);
        }
    }

    @Override
    public boolean canBeLeashed(net.minecraft.entity.player.EntityPlayer player) { return false; }
}
