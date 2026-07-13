package com.airarsenal.entity.projectile;

import com.airarsenal.combat.AirArsenalDamageSource;
import com.airarsenal.config.AirArsenalConfig;
import com.airarsenal.network.ModNetwork;
import com.airarsenal.network.PacketGuidanceEnd;
import com.airarsenal.network.PacketGuidanceLost;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

/**
 * Player-guided missile fired by the {@link com.airarsenal.entity.vehicle.MissileTruckEntity}
 * or the Static Missile Battery.
 *
 * <ul>
 *   <li>Speed: 2.5 blocks/tick. AP 70 (bypasses standard armor via
 *       {@link AirArsenalDamageSource#MISSILE_GUIDED}). Blast: 6 blocks. Lifetime: 160 ticks.</li>
 *   <li><b>Signal range:</b> beyond 150 blocks from {@link #launchPos}, steering input is
 *       ignored and the client is told via {@link PacketGuidanceLost} (sent once on the
 *       transition, not every tick).</li>
 *   <li><b>Top-attack mode:</b> triggered once by Shift ({@link #activateTopAttack()}).
 *       Climbs at +2°/tick pitch for 40 ticks, then switches to a steep dive to strike
 *       the target from above.</li>
 * </ul>
 */
public class TruckGuidedMissileEntity extends Entity {

    private static final float SPEED        = 2.5f;
    private static final int   AP_RATING    = 70;
    private static final float BLAST_RADIUS = 6f;
    private static final int   MAX_TICKS    = 160;
    private static final float SIGNAL_RANGE = 150f;
    private static final float MAX_PITCH    = 85f;

    private static final int   CLIMB_TICKS  = 40;
    private static final float CLIMB_RATE   = 2f;  // degrees/tick nose-up during climb
    private static final float DIVE_PITCH   = 70f; // target dive pitch (degrees, nose-down)

    /** Set server-side by {@link com.airarsenal.network.PacketMissileSteer} handler. */
    public float yawInput   = 0f;
    public float pitchInput = 0f;

    /** Controlling player. Transient — not saved to NBT. */
    public EntityPlayerMP controllerPlayer;

    private Vec3d launchPos;
    private int lifetimeTicks = 0;
    private boolean signalLost = false;

    /** Top-attack maneuver state: 0 = inactive, 1 = climbing, 2 = diving. */
    private int topAttackPhase = 0;
    private int topAttackTicks = 0;

    // ── Construction ──────────────────────────────────────────────────────────

    public TruckGuidedMissileEntity(World world) {
        super(world);
        setSize(0.45f, 0.45f);
    }

    public TruckGuidedMissileEntity(World world, Entity launcher, EntityPlayerMP controller) {
        this(world, launcher.posX, launcher.posY + 0.5, launcher.posZ,
            launcher.rotationYaw, launcher.rotationPitch, controller);
    }

    /**
     * Spawns at an explicit position/heading rather than copying an entity's transform.
     * Used by the block-mounted Static Missile Battery, which has no launcher entity.
     */
    public TruckGuidedMissileEntity(World world, double x, double y, double z,
                                    float yaw, float pitch, EntityPlayerMP controller) {
        this(world);
        this.controllerPlayer = controller;
        setPosition(x, y, z);
        this.launchPos = new Vec3d(posX, posY, posZ);
        this.rotationYaw   = yaw;
        this.rotationPitch = pitch;
        Vec3d dir = forwardVector();
        motionX = dir.x * SPEED;
        motionY = dir.y * SPEED;
        motionZ = dir.z * SPEED;
    }

    /** Starts the climb-then-dive maneuver. Idempotent once already active. */
    public void activateTopAttack() {
        if (topAttackPhase == 0) {
            topAttackPhase = 1;
            topAttackTicks = 0;
        }
    }

    // ── Per-tick update ───────────────────────────────────────────────────────

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (!world.isRemote) {
            if (launchPos == null) launchPos = new Vec3d(posX, posY, posZ);

            if (controllerPlayer == null || !controllerPlayer.isEntityAlive()) {
                detonate();
                return;
            }

            // ── Signal range check ─────────────────────────────────────────────
            double distFromLaunch = new Vec3d(posX, posY, posZ).distanceTo(launchPos);
            boolean inRange = distFromLaunch <= SIGNAL_RANGE;
            if (!inRange && !signalLost) {
                signalLost = true;
                ModNetwork.CHANNEL.sendTo(new PacketGuidanceLost(getEntityId()), controllerPlayer);
            } else if (inRange && signalLost) {
                signalLost = false;
            }

            // ── Apply steering (ignored while signal lost or during top-attack maneuver) ──
            if (inRange && topAttackPhase == 0) {
                rotationYaw   += yawInput;
                rotationPitch  = MathHelper.clamp(rotationPitch + pitchInput, -MAX_PITCH, MAX_PITCH);
            }
            yawInput   = 0f;
            pitchInput = 0f;

            // ── Top-attack maneuver override ───────────────────────────────────
            if (topAttackPhase == 1) {
                rotationPitch = MathHelper.clamp(rotationPitch - CLIMB_RATE, -MAX_PITCH, MAX_PITCH);
                topAttackTicks++;
                if (topAttackTicks >= CLIMB_TICKS) {
                    topAttackPhase = 2;
                }
            } else if (topAttackPhase == 2) {
                rotationPitch = MathHelper.clamp(rotationPitch + CLIMB_RATE * 1.5f, -MAX_PITCH, DIVE_PITCH);
            }

            Vec3d dir = forwardVector();
            motionX = dir.x * SPEED;
            motionY = dir.y * SPEED;
            motionZ = dir.z * SPEED;

            lifetimeTicks++;
            if (lifetimeTicks >= MAX_TICKS) { detonate(); return; }

            AxisAlignedBB sweptBox = getEntityBoundingBox()
                .expand(motionX, motionY, motionZ).grow(0.45);
            List<Entity> hits = world.getEntitiesInAABBexcluding(this, sweptBox,
                e -> e != controllerPlayer && e.canBeCollidedWith() && e.isEntityAlive());
            if (!hits.isEmpty()) {
                // Direct-hit entities are also inside the blast radius computed in detonate(),
                // so no separate damage call is needed here — just trigger the warhead.
                detonate();
                return;
            }
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

    // ── Detonation ────────────────────────────────────────────────────────────

    public void detonate() {
        if (!world.isRemote) {
            world.createExplosion(controllerPlayer, posX, posY, posZ,
                BLAST_RADIUS, AirArsenalConfig.allowBlockDestruction);

            AxisAlignedBB blastBox = new AxisAlignedBB(
                posX - BLAST_RADIUS, posY - BLAST_RADIUS, posZ - BLAST_RADIUS,
                posX + BLAST_RADIUS, posY + BLAST_RADIUS, posZ + BLAST_RADIUS);
            List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, blastBox);
            for (EntityLivingBase e : entities) {
                e.attackEntityFrom(AirArsenalDamageSource.MISSILE_GUIDED, AP_RATING * 0.7f);
            }

            if (controllerPlayer != null && controllerPlayer.isEntityAlive()) {
                ModNetwork.CHANNEL.sendTo(new PacketGuidanceEnd(), controllerPlayer);
            }
        }
        setDead();
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    private Vec3d forwardVector() {
        float yawRad   = (float) Math.toRadians(rotationYaw);
        float pitchRad = (float) Math.toRadians(rotationPitch);
        double cosP = Math.cos(pitchRad);
        return new Vec3d(
            -Math.sin(yawRad) * cosP,
            -Math.sin(pitchRad),
             Math.cos(yawRad) * cosP
        ).normalize();
    }

    // ── Entity framework ──────────────────────────────────────────────────────

    @Override protected void entityInit() {}

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        compound.setInteger("LifetimeTicks", lifetimeTicks);
        compound.setFloat("RotYaw",   rotationYaw);
        compound.setFloat("RotPitch", rotationPitch);
        compound.setInteger("TopAttackPhase", topAttackPhase);
        compound.setInteger("TopAttackTicks", topAttackTicks);
        if (launchPos != null) {
            compound.setDouble("LaunchX", launchPos.x);
            compound.setDouble("LaunchY", launchPos.y);
            compound.setDouble("LaunchZ", launchPos.z);
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        lifetimeTicks   = compound.getInteger("LifetimeTicks");
        rotationYaw     = compound.getFloat("RotYaw");
        rotationPitch   = compound.getFloat("RotPitch");
        topAttackPhase  = compound.getInteger("TopAttackPhase");
        topAttackTicks  = compound.getInteger("TopAttackTicks");
        if (compound.hasKey("LaunchX")) {
            launchPos = new Vec3d(
                compound.getDouble("LaunchX"),
                compound.getDouble("LaunchY"),
                compound.getDouble("LaunchZ"));
        }
        // controllerPlayer is transient — missile self-destructs next tick if null
    }

    @Override public boolean canBePushed()       { return false; }
    @Override public boolean canBeCollidedWith() { return true;  }
}
