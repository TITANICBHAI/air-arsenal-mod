package com.airarsenal.entity.vehicle;

import com.airarsenal.combat.AirArsenalDamageSource;
import com.airarsenal.combat.DamageCalculator;
import com.airarsenal.entity.projectile.TankShellEntity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Tank — patrolling NPC that stops to engage players within 30 blocks, firing
 * a {@link TankShellEntity} every 3 seconds (60 ticks). 250 HP, DR 80.
 *
 * <p>AP70 (guided missile) vs DR80 ≈ 87.5% damage ratio by design — tanks are meant
 * to survive multiple missile hits, not die to a single Hellfire/Predator strike.</p>
 *
 * <p>Drops gold plus a guaranteed {@link com.airarsenal.item.ItemBrahMosTargeter} on death.</p>
 */
public class TankEntity extends EntityCreature {

    private static final int ARMOR_DR = 80;
    private static final int DEFAULT_AP = 5;
    private static final double ENGAGE_RANGE = 30.0;
    private static final int FIRE_INTERVAL_TICKS = 60;

    private int fireCooldown = 0;

    public TankEntity(World world) {
        super(world);
        setSize(1.8f, 1.6f);
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(1, new EntityAIWanderAvoidWater(this, 0.4));
        tasks.addTask(2, new EntityAILookIdle(this));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getAttributeMap().registerAttribute(SharedMonsterAttributes.MAX_HEALTH);
        getAttributeMap().registerAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(250.0);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.15);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (world.isRemote) return;

        if (fireCooldown > 0) {
            fireCooldown--;
            return;
        }

        EntityPlayer target = world.getClosestPlayerToEntity(this, ENGAGE_RANGE);
        if (target != null && !target.isCreative() && !target.isSpectator()) {
            // Stop moving to fire (halt current pathing this tick).
            getNavigator().clearPath();
            faceEntity(target, 30f, 30f);
            fireAt(target);
            fireCooldown = FIRE_INTERVAL_TICKS;
        }
    }

    private void fireAt(EntityPlayer target) {
        TankShellEntity shell = new TankShellEntity(world, this);
        shell.setPosition(posX, posY + height * 0.6, posZ);

        Vec3d dir = new Vec3d(target.posX - posX, target.posY - posY, target.posZ - posZ).normalize();
        float speed = 1.8f;
        shell.motionX = dir.x * speed;
        shell.motionY = dir.y * speed + 0.1; // slight arc lift
        shell.motionZ = dir.z * speed;
        world.spawnEntity(shell);
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
        int goldCount = 1 + rand.nextInt(2 + looting);
        entityDropItem(new ItemStack(Items.GOLD_INGOT, goldCount), 0f);
        entityDropItem(new ItemStack(com.airarsenal.registry.ModItems.BRAHMOS_TARGETER), 0f);
    }

    @Override
    public boolean canBeLeashed(EntityPlayer player) { return false; }
}
