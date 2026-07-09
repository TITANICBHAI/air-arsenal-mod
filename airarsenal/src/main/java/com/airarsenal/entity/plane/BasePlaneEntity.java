package com.airarsenal.entity.plane;

import com.airarsenal.AirArsenal;
import com.airarsenal.combat.weapon.IPlaneWeapon;
import com.airarsenal.entity.plane.component.PropellerComponent;
import com.airarsenal.entity.plane.component.PropellerState;
import com.airarsenal.entity.projectile.PropellerShardEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base for every flyable plane in Air Arsenal.
 *
 * Subclasses set {@code maxSpeed}, {@code maxPlaneHealth}, populate {@code weapons},
 * and implement {@link #getPlaneType()}.
 *
 * Controls are fed via {@link #processInput} (packets wired in Chunk 5).
 * {@link #applyFlightPhysics()} must be called once per tick from the subclass {@code onUpdate()}.
 */
public abstract class BasePlaneEntity extends Entity {

    // ── Flight state ──────────────────────────────────────────────────────────
    /** Current forward speed in blocks/second. */
    protected float speed = 0f;
    /** Maximum forward speed; set by subclass. */
    protected float maxSpeed = 10f;

    /** Nose-up / nose-down angle in degrees (negative = nose up). */
    protected float pitch = 0f;
    /** Heading in degrees (Minecraft convention: 0 = south, 90 = west). */
    protected float yaw = 0f;

    // ── Health ────────────────────────────────────────────────────────────────
    /** Plane structural HP, separate from the rider's personal health. */
    protected float planeHealth;
    /** Maximum structural HP; set by subclass. */
    protected float maxPlaneHealth = 40f;

    // ── Propeller ─────────────────────────────────────────────────────────────
    /** The propeller component — health, state, and derived penalties. */
    protected PropellerComponent propeller;

    // ── Weapons ───────────────────────────────────────────────────────────────
    /** Weapon slots. Size == number of hardpoints. Populated by the subclass. */
    protected List<IPlaneWeapon> weapons = new ArrayList<>();

    // ── Tactical mode (Chunk 5) ───────────────────────────────────────────────
    /** Toggled by key-bind in Chunk 5. */
    protected boolean isTacModeActive = false;

    // ── Physics constants ─────────────────────────────────────────────────────
    private static final float LIFT_THRESHOLD     = 10f;
    private static final float GRAVITY_DROP       = 0.05f;
    private static final float DESTROYED_SPEED_DECAY = 0.4f;

    // ─────────────────────────────────────────────────────────────────────────
    //  Constructor
    // ─────────────────────────────────────────────────────────────────────────

    public BasePlaneEntity(World world) {
        super(world);
        setSize(3.0f, 1.5f);
        // Propeller starts at max health (30 HP for Wood Biplane)
        this.propeller    = new PropellerComponent(30f);
        this.planeHealth  = this.maxPlaneHealth;
        this.yaw          = this.rotationYaw;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Abstract API
    // ─────────────────────────────────────────────────────────────────────────

    /** Short identifier used for NBT keys, registry, and HUD display. */
    public abstract String getPlaneType();

    // ─────────────────────────────────────────────────────────────────────────
    //  Per-tick update  (base logic; subclass calls super first)
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public void onUpdate() {
        super.onUpdate();

        // ── Contact hazard check (server side only) ──────────────────────────
        if (!world.isRemote && speed > 5f && !propeller.isDestroyed()) {
            checkPropellerContact();
        }

        // ── Particle effects (client side only) ──────────────────────────────
        if (world.isRemote) {
            spawnPropellerParticles();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Propeller contact hazard
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Scans a small AABB at the nose of the plane.
     * Any entity inside that zone gets hit by {@link #onPropellerContact}.
     */
    private void checkPropellerContact() {
        double yawRad = Math.toRadians(yaw);
        // Nose is ~1.5 blocks ahead of entity centre in the forward direction
        double noseX = posX - Math.sin(yawRad) * 1.5;
        double noseY = posY + 0.5;
        double noseZ = posZ + Math.cos(yawRad) * 1.5;

        AxisAlignedBB propBox = new AxisAlignedBB(
            noseX - 0.5, noseY - 0.5, noseZ - 0.5,
            noseX + 0.5, noseY + 0.5, noseZ + 0.5
        );

        List<Entity> nearby = world.getEntitiesWithinAABBExcludingEntity(this, propBox);
        for (Entity entity : nearby) {
            if (entity != getControllingPassenger()) {
                onPropellerContact(entity);
            }
        }
    }

    /**
     * Called when an entity enters the propeller's hitbox while the plane is moving.
     *
     * <ul>
     *   <li>Deals {@code 4 × (speed / maxSpeed)} damage to the toucher.</li>
     *   <li>Deals 2 HP structural damage to the propeller itself.</li>
     *   <li>If the propeller just died from this hit, ejects a shard.</li>
     * </ul>
     *
     * @param toucher The entity that entered the propeller zone.
     */
    public void onPropellerContact(Entity toucher) {
        float damage = 4f * (speed / maxSpeed);
        toucher.attackEntityFrom(
            net.minecraft.util.DamageSource.causeIndirectMagicDamage(this, this),
            damage
        );

        boolean justDestroyed = propeller.takeDamage(2f);
        if (justDestroyed) {
            ejectPropellerShard();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Propeller shard ejection
    // ─────────────────────────────────────────────────────────────────────────

    private void ejectPropellerShard() {
        if (!world.isRemote) {
            PropellerShardEntity shard = new PropellerShardEntity(world, this);
            world.spawnEntity(shard);
            AirArsenal.LOGGER.info("{} propeller destroyed — shard ejected", getPlaneType());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Particle dispatching (client side only)
    // ─────────────────────────────────────────────────────────────────────────

    private void spawnPropellerParticles() {
        // Only client-side; guard is in onUpdate()
        double yawRad = Math.toRadians(yaw);
        double noseX  = posX - Math.sin(yawRad) * 1.5;
        double noseY  = posY + 0.5;
        double noseZ  = posZ + Math.cos(yawRad) * 1.5;

        PropellerState state = propeller.getDamageState();
        switch (state) {
            case DAMAGED:
                AirArsenal.proxy.spawnPropellerSparks(world, noseX, noseY, noseZ);
                break;
            case HEAVY_DAMAGE:
                AirArsenal.proxy.spawnPropellerSparks(world, noseX, noseY, noseZ);
                AirArsenal.proxy.spawnPropellerSmoke(world, noseX, noseY, noseZ);
                break;
            case CRITICAL:
                AirArsenal.proxy.spawnPropellerSmoke(world, noseX, noseY, noseZ);
                AirArsenal.proxy.spawnPropellerFire(world, noseX, noseY, noseZ);
                break;
            default:
                // INTACT and DESTROYED: no extra particles here
                break;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Flight physics  (call once per tick from subclass onUpdate)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Applies propeller penalty, lift/gravity, and forward thrust each tick.
     *
     * <ul>
     *   <li>Effective speed = {@code speed × propeller.getSpeedMultiplier()}</li>
     *   <li>Propeller DESTROYED → always gravity, pitch-down 0.3°/tick, speed decays 0.4/tick</li>
     *   <li>speed ≥ 10 → lift (upward by {@code effectiveSpeed × 0.002})</li>
     *   <li>speed &lt; 10 → gravity drop ({@value #GRAVITY_DROP}/tick)</li>
     * </ul>
     */
    protected void applyFlightPhysics() {
        PropellerState propState = propeller.getDamageState();

        // Apply yaw drift from propeller damage
        yaw += propeller.getYawDrift();

        if (propState == PropellerState.DESTROYED) {
            // Thrust gone — decay speed, always fall
            speed = Math.max(0f, speed - DESTROYED_SPEED_DECAY);
            motionY = -GRAVITY_DROP;
            // Constant nose-down when destroyed
            pitch = Math.min(pitch + 0.3f, 30f);
        } else {
            float effectiveSpeed = speed * propeller.getSpeedMultiplier();

            if (effectiveSpeed >= LIFT_THRESHOLD) {
                motionY = effectiveSpeed * 0.002;
            } else {
                motionY = -GRAVITY_DROP;
            }
        }

        // Forward thrust (uses raw speed for distance, multiplier already in motionY logic)
        float thrustPerTick = (speed * propeller.getSpeedMultiplier()) / 20f;
        double yawRad = Math.toRadians(yaw);
        motionX = -Math.sin(yawRad) * thrustPerTick;
        motionZ =  Math.cos(yawRad) * thrustPerTick;

        this.rotationYaw   = yaw;
        this.rotationPitch = pitch;

        moveEntity(motionX, motionY, motionZ);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Input processing  (called by packet handler — Chunk 5)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * @param forward  W — throttle up
     * @param back     S — throttle down / brake
     * @param left     A — yaw left
     * @param right    D — yaw right
     * @param up       Space — pitch up (nose up)
     * @param down     Shift — pitch down (nose down)
     */
    public void processInput(boolean forward, boolean back,
                             boolean left,   boolean right,
                             boolean up,     boolean down) {
        if (forward) speed = Math.min(speed + 0.3f, maxSpeed);
        if (back)    speed = Math.max(speed - 0.5f, 0f);
        if (left)    yaw  -= 2.5f;
        if (right)   yaw  += 2.5f;
        if (up)      pitch = Math.max(pitch - 1.5f, -45f);
        if (down)    pitch = Math.min(pitch + 1.5f,  30f);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Mounting
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
        if (!world.isRemote && player.getRidingEntity() == null) {
            player.startRiding(this, true);
            AirArsenal.LOGGER.info("{} mounted {}", player.getName(), getPlaneType());
        }
        return true;
    }

    @Override
    public boolean canBePushed()       { return false; }
    @Override
    public boolean canBeCollidedWith() { return true;  }

    // ─────────────────────────────────────────────────────────────────────────
    //  NBT persistence
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        compound.setFloat("Speed",       speed);
        compound.setFloat("PlaneHealth", planeHealth);
        compound.setFloat("PlanePitch",  pitch);
        compound.setFloat("PlaneYaw",    yaw);
        // Propeller sub-compound
        NBTTagCompound propNBT = new NBTTagCompound();
        propeller.writeToNBT(propNBT);
        compound.setTag("Propeller", propNBT);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        speed       = compound.getFloat("Speed");
        planeHealth = compound.getFloat("PlaneHealth");
        pitch       = compound.getFloat("PlanePitch");
        yaw         = compound.getFloat("PlaneYaw");
        if (compound.hasKey("Propeller")) {
            propeller.readFromNBT(compound.getCompoundTag("Propeller"));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Required Entity override
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    protected void entityInit() {
        // DataWatcher entries added in Chunk 5 when synced client state is needed
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Getters
    // ─────────────────────────────────────────────────────────────────────────

    public float getSpeed()                  { return speed;          }
    public float getMaxSpeed()               { return maxSpeed;        }
    public float getPlaneHealth()            { return planeHealth;     }
    public float getMaxPlaneHealth()         { return maxPlaneHealth;  }
    public float getPlanePitch()             { return pitch;           }
    public float getPlaneYaw()               { return yaw;             }
    public boolean isTacModeActive()         { return isTacModeActive; }
    public List<IPlaneWeapon> getWeapons()   { return weapons;         }
    public PropellerComponent getPropeller() { return propeller;       }
}
