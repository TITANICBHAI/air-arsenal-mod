package com.airarsenal.entity.projectile;

import com.airarsenal.AirArsenal;
import com.airarsenal.config.AirArsenalConfig;
import com.airarsenal.network.ModNetwork;
import com.airarsenal.network.PacketPredatorCameraEnd;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

/**
 * Predator Strike missile — player steers for up to 8 seconds (160 ticks) via nose-cam.
 *
 * <p>Control flow:</p>
 * <ol>
 *   <li>Server spawns this entity, sends {@link PacketPredatorCameraStart} to client.</li>
 *   <li>Client sends {@link com.airarsenal.network.PacketMissileSteer} every 2 ticks.</li>
 *   <li>Server applies yaw/pitch inputs here each tick.</li>
 *   <li>On impact or 160-tick timeout: triggers 8-block explosion and sends
 *       {@link PacketPredatorCameraEnd} to restore player view.</li>
 * </ol>
 *
 * Speed: 3.2 blocks/tick. {@code controllerPlayer} is transient — if the player disconnects,
 * the missile self-destructs on the next update tick.
 */
public class PredatorMissileEntity extends Entity {

    private static final float SPEED       = 3.2f;
    private static final int   MAX_TICKS   = 160;
    private static final float BLAST_RADIUS = 8f;
    private static final float MAX_PITCH   = 85f;

    /** Set server-side by {@link PacketMissileSteer} handler. */
    public float yawInput   = 0f;
    public float pitchInput = 0f;

    /** Controlling player. Transient — not saved to NBT. */
    public EntityPlayerMP controllerPlayer;

    private int lifetimeTicks = 0;

    // ── Construction ──────────────────────────────────────────────────────────

    public PredatorMissileEntity(World world) {
        super(world);
        setSize(0.5f, 0.5f);
    }

    public PredatorMissileEntity(World world, Entity launcher, EntityPlayerMP controller) {
        this(world);
        this.controllerPlayer = controller;
        setPosition(launcher.posX, launcher.posY, launcher.posZ);
        this.rotationYaw   = launcher.rotationYaw;
        this.rotationPitch = launcher.rotationPitch;
        // Initial velocity forward at speed
        Vec3d dir = forwardVector();
        motionX = dir.x * SPEED;
        motionY = dir.y * SPEED;
        motionZ = dir.z * SPEED;
    }

    // ── Per-tick update ───────────────────────────────────────────────────────

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (!world.isRemote) {
            // ── Self-destruct if controller disconnected ───────────────────────
            if (controllerPlayer == null || !controllerPlayer.isEntityAlive()) {
                detonate();
                return;
            }

            // ── Apply steering inputs ─────────────────────────────────────────
            rotationYaw   += yawInput;
            rotationPitch  = MathHelper.clamp(rotationPitch + pitchInput, -MAX_PITCH, MAX_PITCH);
            // Reset inputs to zero each tick; new inputs arrive via packet
            yawInput   = 0f;
            pitchInput = 0f;

            // ── Update velocity from new heading ──────────────────────────────
            Vec3d dir = forwardVector();
            motionX = dir.x * SPEED;
            motionY = dir.y * SPEED;
            motionZ = dir.z * SPEED;

            // ── Lifetime check ────────────────────────────────────────────────
            lifetimeTicks++;
            if (lifetimeTicks >= MAX_TICKS) {
                detonate();
                return;
            }

            // ── Entity collision scan ─────────────────────────────────────────
            AxisAlignedBB sweptBox = getEntityBoundingBox()
                .expand(motionX, motionY, motionZ).grow(0.5);
            List<Entity> hits = world.getEntitiesInAABBexcluding(this, sweptBox,
                e -> e != controllerPlayer && e.canBeCollidedWith() && e.isEntityAlive());
            if (!hits.isEmpty()) { detonate(); return; }
        }

        // ── Move ──────────────────────────────────────────────────────────────
        Vec3d start = new Vec3d(posX, posY, posZ);
        move(net.minecraft.entity.MoverType.SELF, motionX, motionY, motionZ);
        Vec3d end = new Vec3d(posX, posY, posZ);

        // ── Trail particles (Chunk 10 polish, client-only) ──────────────────────
        if (world.isRemote) {
            AirArsenal.proxy.spawnMissileTrail(world, posX, posY, posZ);
        }

        // ── Block collision ───────────────────────────────────────────────────
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
            world.createExplosion(controllerPlayer,
                posX, posY, posZ, BLAST_RADIUS,
                AirArsenalConfig.allowBlockDestruction);

            // Snap camera back to player immediately
            if (controllerPlayer != null && controllerPlayer.isEntityAlive()) {
                ModNetwork.CHANNEL.sendTo(new PacketPredatorCameraEnd(), controllerPlayer);
            }
        }
        setDead();
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    /** Computes the unit forward vector from this entity's yaw and pitch. */
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
        compound.setFloat("RotYaw",          rotationYaw);
        compound.setFloat("RotPitch",        rotationPitch);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        lifetimeTicks = compound.getInteger("LifetimeTicks");
        rotationYaw   = compound.getFloat("RotYaw");
        rotationPitch = compound.getFloat("RotPitch");
        // controllerPlayer is transient — missile will self-destruct next tick if null
    }

    @Override public boolean canBePushed()       { return false; }
    @Override public boolean canBeCollidedWith() { return true;  }
}
