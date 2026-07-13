package com.airarsenal.entity.projectile;

import com.airarsenal.combat.AirArsenalDamageSource;
import com.airarsenal.config.AirArsenalConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

/**
 * MANPADS missile — shoulder-fired anti-air, same lerp guidance as {@link HellfireEntity}
 * but slower and shorter-ranged.
 *
 * <ul>
 *   <li>Speed: 2 blocks/tick.</li>
 *   <li>Guidance: lerp factor 0.15 toward {@link #targetEntity}, same as Hellfire.</li>
 *   <li>Max range: 100 blocks from {@link #launchPos} — self-destructs beyond that.</li>
 *   <li>Warhead: 6-block explosion, 20 HP true damage via {@link AirArsenalDamageSource#MISSILE_GUIDED}.</li>
 * </ul>
 */
public class ManpadsEntity extends Entity {

    private static final float SPEED        = 2f;
    private static final float LERP_FACTOR  = 0.15f;
    private static final float MAX_RANGE    = 100f;
    private static final float BLAST_RADIUS = 6f;
    private static final float WARHEAD_DMG  = 20f;

    public Entity targetEntity;
    private Vec3d launchPos;
    private Entity shooter;

    public ManpadsEntity(World world) {
        super(world);
        setSize(0.4f, 0.4f);
    }

    public ManpadsEntity(World world, Entity shooter, Entity target, Vec3d initialDir) {
        this(world);
        this.shooter      = shooter;
        this.targetEntity = target;
        setPosition(shooter.posX, shooter.posY + shooter.getEyeHeight(), shooter.posZ);
        this.launchPos = new Vec3d(posX, posY, posZ);
        motionX = initialDir.x * SPEED;
        motionY = initialDir.y * SPEED;
        motionZ = initialDir.z * SPEED;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (!world.isRemote) {
            if (launchPos == null) launchPos = new Vec3d(posX, posY, posZ);

            // ── Max range check ────────────────────────────────────────────────
            if (new Vec3d(posX, posY, posZ).distanceTo(launchPos) > MAX_RANGE) {
                setDead();
                return;
            }

            // ── Guidance ───────────────────────────────────────────────────────
            if (targetEntity != null && targetEntity.isEntityAlive()) {
                Vec3d toTarget = new Vec3d(
                    targetEntity.posX - posX,
                    targetEntity.posY + targetEntity.height * 0.5 - posY,
                    targetEntity.posZ - posZ
                ).normalize().scale(SPEED);

                motionX = lerp(motionX, toTarget.x, LERP_FACTOR);
                motionY = lerp(motionY, toTarget.y, LERP_FACTOR);
                motionZ = lerp(motionZ, toTarget.z, LERP_FACTOR);
            }

            // ── Entity collision ───────────────────────────────────────────────
            AxisAlignedBB sweptBox = getEntityBoundingBox()
                .expand(motionX, motionY, motionZ).grow(0.4);
            List<Entity> hits = world.getEntitiesInAABBexcluding(this, sweptBox,
                e -> e != shooter && e.canBeCollidedWith() && e.isEntityAlive());
            if (!hits.isEmpty()) { detonate(); return; }
        }

        Vec3d start = new Vec3d(posX, posY, posZ);
        moveEntity(motionX, motionY, motionZ);
        Vec3d end = new Vec3d(posX, posY, posZ);

        if (!world.isRemote) {
            RayTraceResult hit = world.rayTraceBlocks(start, end, false, true, false);
            if (hit != null && hit.typeOfHit == RayTraceResult.Type.BLOCK) {
                detonate();
            }
        }
    }

    private void detonate() {
        if (!world.isRemote) {
            world.createExplosion(shooter, posX, posY, posZ,
                BLAST_RADIUS, AirArsenalConfig.allowBlockDestruction);

            AxisAlignedBB aabb = new AxisAlignedBB(
                posX - BLAST_RADIUS, posY - BLAST_RADIUS, posZ - BLAST_RADIUS,
                posX + BLAST_RADIUS, posY + BLAST_RADIUS, posZ + BLAST_RADIUS
            );
            List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, aabb);
            for (EntityLivingBase e : entities) {
                e.attackEntityFrom(AirArsenalDamageSource.MISSILE_GUIDED, WARHEAD_DMG);
            }
        }
        setDead();
    }

    private static float lerp(double current, double target, float t) {
        return (float) (current + (target - current) * t);
    }

    @Override protected void entityInit() {}

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        if (launchPos != null) {
            compound.setDouble("LaunchX", launchPos.x);
            compound.setDouble("LaunchY", launchPos.y);
            compound.setDouble("LaunchZ", launchPos.z);
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        if (compound.hasKey("LaunchX")) {
            launchPos = new Vec3d(
                compound.getDouble("LaunchX"),
                compound.getDouble("LaunchY"),
                compound.getDouble("LaunchZ"));
        }
    }

    @Override public boolean canBePushed()       { return false; }
    @Override public boolean canBeCollidedWith() { return true;  }
}
