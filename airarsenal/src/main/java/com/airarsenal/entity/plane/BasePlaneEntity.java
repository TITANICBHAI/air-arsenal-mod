package com.airarsenal.entity.plane;

import com.airarsenal.AirArsenal;
import com.airarsenal.combat.weapon.IPlaneWeapon;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base for every flyable plane in Air Arsenal.
 *
 * Subclasses set {@code maxSpeed}, {@code maxPlaneHealth}, populate {@code weapons},
 * and implement {@link #getPlaneType()}.
 *
 * Controls are fed in via {@link #processInput} (packets wired in Chunk 5).
 * {@link #applyFlightPhysics()} must be called once per tick from the subclass.
 */
public abstract class BasePlaneEntity extends Entity {

    // ── Flight state ─────────────────────────────────────────────────────────
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

    // ── Weapons ───────────────────────────────────────────────────────────────
    /**
     * Weapon slots.  Size == number of hardpoints.
     * Populated by the subclass constructor.  Empty = unarmed.
     */
    protected List<IPlaneWeapon> weapons = new ArrayList<>();

    // ── Tactical mode (Chunk 5) ───────────────────────────────────────────────
    /** Toggled by key-bind in Chunk 5. */
    protected boolean isTacModeActive = false;

    // ── Lift threshold ────────────────────────────────────────────────────────
    private static final float LIFT_THRESHOLD = 10f;
    private static final float GRAVITY_DROP   = 0.05f;

    // ─────────────────────────────────────────────────────────────────────────
    //  Constructor
    // ─────────────────────────────────────────────────────────────────────────

    public BasePlaneEntity(World world) {
        super(world);
        // Size: 3 wide, 1.5 tall — matches the rough silhouette of a biplane
        setSize(3.0f, 1.5f);
        this.planeHealth = this.maxPlaneHealth;
        this.yaw = this.rotationYaw;
        this.noClip = false;
        this.isImmuneToFire = false;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Abstract API
    // ─────────────────────────────────────────────────────────────────────────

    /** Short identifier used for NBT keys, registry, and HUD display. */
    public abstract String getPlaneType();

    // ─────────────────────────────────────────────────────────────────────────
    //  Flight physics  (call once per tick from subclass onUpdate)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Applies lift/gravity and forward thrust each tick.
     *
     * <ul>
     *   <li>speed ≥ {@value #LIFT_THRESHOLD} b/s → lift upward by {@code speed * 0.002} per tick</li>
     *   <li>speed &lt; {@value #LIFT_THRESHOLD} b/s → fall by {@value #GRAVITY_DROP} per tick</li>
     *   <li>Forward motion is in the direction of {@link #yaw}.</li>
     * </ul>
     */
    protected void applyFlightPhysics() {
        // Convert speed (blocks/sec) to blocks/tick
        float speedPerTick = speed / 20f;

        // Vertical component
        if (speed >= LIFT_THRESHOLD) {
            motionY = speed * 0.002;
        } else {
            motionY = -GRAVITY_DROP;
        }

        // Horizontal thrust along yaw heading
        double yawRad = Math.toRadians(yaw);
        motionX = -Math.sin(yawRad) * speedPerTick;
        motionZ =  Math.cos(yawRad) * speedPerTick;

        // Sync rotation so the entity faces the right way
        this.rotationYaw = yaw;
        this.rotationPitch = pitch;

        moveEntity(motionX, motionY, motionZ);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Input processing  (called by packet handler — Chunk 5)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Translate raw boolean key states into changes in speed, pitch, and yaw.
     *
     * @param forward  W key — throttle up
     * @param back     S key — throttle down / brake
     * @param left     A key — yaw left
     * @param right    D key — yaw right
     * @param up       Space — pitch up (nose up)
     * @param down     Shift — pitch down (nose down)
     */
    public void processInput(boolean forward, boolean back,
                             boolean left,   boolean right,
                             boolean up,     boolean down) {
        if (forward) {
            speed = Math.min(speed + 0.3f, maxSpeed);
        }
        if (back) {
            speed = Math.max(speed - 0.5f, 0f);
        }
        if (left) {
            yaw -= 2.5f;
        }
        if (right) {
            yaw += 2.5f;
        }
        if (up) {
            pitch = Math.max(pitch - 1.5f, -45f);
        }
        if (down) {
            pitch = Math.min(pitch + 1.5f, 30f);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Mounting
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
        if (!world.isRemote) {
            // Only mount if the player is not already riding something
            if (player.getRidingEntity() == null) {
                player.startRiding(this, true);
                AirArsenal.LOGGER.info("{} mounted {}", player.getName(), getPlaneType());
            }
        }
        return true; // consume interaction on both sides
    }

    /** Planes cannot be pushed by other entities. */
    @Override
    public boolean canBePushed() {
        return false;
    }

    /** Must be true for right-click interaction to fire. */
    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  NBT persistence
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        compound.setFloat("Speed",       speed);
        compound.setFloat("PlaneHealth", planeHealth);
        compound.setFloat("PlanePitch",  pitch);
        compound.setFloat("PlaneYaw",    yaw);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        speed       = compound.getFloat("Speed");
        planeHealth = compound.getFloat("PlaneHealth");
        pitch       = compound.getFloat("PlanePitch");
        yaw         = compound.getFloat("PlaneYaw");
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Required Entity override
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    protected void entityInit() {
        // DataWatcher entries go here when synced client state is needed (Chunk 5+)
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Getters (used by HUD, packets, and debug commands)
    // ─────────────────────────────────────────────────────────────────────────

    public float getSpeed()           { return speed;          }
    public float getMaxSpeed()        { return maxSpeed;        }
    public float getPlaneHealth()     { return planeHealth;     }
    public float getMaxPlaneHealth()  { return maxPlaneHealth;  }
    public float getPlanePitch()      { return pitch;           }
    public float getPlaneYaw()        { return yaw;             }
    public boolean isTacModeActive()  { return isTacModeActive; }
    public List<IPlaneWeapon> getWeapons() { return weapons;   }
}
