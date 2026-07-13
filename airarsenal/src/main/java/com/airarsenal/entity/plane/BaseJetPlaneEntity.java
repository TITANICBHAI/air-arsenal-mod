package com.airarsenal.entity.plane;

import com.airarsenal.AirArsenal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

/**
 * Shared base for the two jet-engined planes introduced in Chunk 9
 * ({@link FighterJetEntity}, {@link PredatorDroneEntity}).
 *
 * <p>Jets have no exposed spinning blade ({@link #hasSpinningPropeller} = false)
 * and a different destruction consequence than prop planes: instead of ejecting
 * a {@link com.airarsenal.entity.projectile.PropellerShardEntity}, the engine
 * fire consequence is fire + {@link MobEffects#WITHER} on the pilot.</p>
 */
public abstract class BaseJetPlaneEntity extends BasePlaneEntity {

    protected BaseJetPlaneEntity(World world) {
        super(world);
        this.hasSpinningPropeller = false;
    }

    @Override
    protected void onEngineDestroyed() {
        if (world.isRemote) return;

        AirArsenal.proxy.spawnJetEngineFire(world, posX, posY, posZ);

        EntityLivingBase pilot = resolvePilot();
        if (pilot != null) {
            pilot.addPotionEffect(new PotionEffect(MobEffects.WITHER, 100, 1));
        }
        AirArsenal.LOGGER.info("{} jet engine destroyed", getPlaneType());
    }

    /**
     * Finds who should take the Wither penalty: the physical rider if mounted,
     * otherwise {@link #getRemotePilot()} for planes that can be destroyed while
     * flown remotely (e.g. {@link PredatorDroneEntity}).
     */
    private EntityLivingBase resolvePilot() {
        Entity passenger = getControllingPassenger();
        if (passenger instanceof EntityLivingBase) return (EntityLivingBase) passenger;
        return getRemotePilot();
    }

    /** Override for planes that can be destroyed while remote-piloted (no physical rider). */
    protected EntityLivingBase getRemotePilot() { return null; }
}
