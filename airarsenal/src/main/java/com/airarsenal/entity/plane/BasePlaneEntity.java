package com.airarsenal.entity.plane;

import com.airarsenal.AirArsenal;
import com.airarsenal.combat.weapon.IPlaneWeapon;
import com.airarsenal.entity.plane.component.FuelSystem;
import com.airarsenal.entity.plane.component.IEngineComponent;
import com.airarsenal.entity.plane.component.PropellerComponent;
import com.airarsenal.entity.plane.component.PropellerState;
import com.airarsenal.entity.projectile.PropellerShardEntity;
import com.airarsenal.registry.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
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
 * Controls are driven by network packets (Chunk 5):
 * - {@link com.airarsenal.network.PacketTacModeToggle} toggles Tac Mode
 * - {@link com.airarsenal.network.PacketWeaponFire} fires the selected weapon
 */
public abstract class BasePlaneEntity extends Entity {

    // ── Flight state ──────────────────────────────────────────────────────────
    protected float speed    = 0f;
    protected float maxSpeed = 10f;
    protected float pitch    = 0f;
    protected float yaw      = 0f;

    // ── Health ────────────────────────────────────────────────────────────────
    protected float planeHealth    = 40f;
    protected float maxPlaneHealth = 40f;

    // ── Propeller / jet engine ────────────────────────────────────────────────
    protected IEngineComponent propeller;

    /**
     * {@code false} for jet-driven planes (Chunk 9) — they have no exposed
     * spinning blade, so {@link #checkPropellerContact()} is skipped entirely.
     */
    protected boolean hasSpinningPropeller = true;

    // ── Fuel (Chunk 9) ────────────────────────────────────────────────────────
    /** Every plane carries a tank; {@link #fuelConsumption} of 0 means it never depletes. */
    protected FuelSystem fuelSystem = new FuelSystem();

    /** Fuel units consumed per tick while airborne. Set by subclass constructor; 0 = unlimited. */
    protected float fuelConsumption = 0f;

    // ── Weapons ───────────────────────────────────────────────────────────────
    protected List<IPlaneWeapon> weapons = new ArrayList<>();

    /**
     * Index into {@link #weapons} of the currently active hardpoint.
     * Updated server-side from {@link com.airarsenal.network.PacketWeaponFire}.
     */
    protected int selectedWeaponIndex = 0;

    // ── Tactical mode ─────────────────────────────────────────────────────────
    /** Toggled by {@link com.airarsenal.network.PacketTacModeToggle}. */
    protected boolean isTacModeActive = false;

    // ── Physics constants ─────────────────────────────────────────────────────
    private static final float LIFT_THRESHOLD        = 10f;
    private static final float GRAVITY_DROP          = 0.05f;
    private static final float DESTROYED_SPEED_DECAY = 0.4f;

    // ─────────────────────────────────────────────────────────────────────────
    //  Constructor
    // ─────────────────────────────────────────────────────────────────────────

    public BasePlaneEntity(World world) {
        super(world);
        setSize(3.0f, 1.5f);
        this.propeller   = new PropellerComponent(30f);
        this.planeHealth = this.maxPlaneHealth;
        this.yaw         = this.rotationYaw;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Abstract API
    // ─────────────────────────────────────────────────────────────────────────

    public abstract String getPlaneType();

    // ─────────────────────────────────────────────────────────────────────────
    //  Per-tick update
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (!world.isRemote && hasSpinningPropeller && speed > 5f && !propeller.isDestroyed()) {
            checkPropellerContact();
        }

        if (!world.isRemote) {
            tickFuel();
        }

        if (world.isRemote) {
            spawnPropellerParticles();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Fuel system (Chunk 9)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Consumes fuel each tick while the engine is otherwise healthy. When the tank
     * runs dry the engine is forced to DESTROYED (same behavior as a propeller/jet
     * engine destroyed by combat damage) by draining the engine component's
     * remaining health in one call.
     */
    private void tickFuel() {
        if (fuelConsumption <= 0f) return;      // this plane doesn't use fuel
        if (propeller.isDestroyed()) return;    // already dead — nothing to drain

        boolean stillHasFuel = fuelSystem.consumeFuel(fuelConsumption);
        if (!stillHasFuel) {
            boolean justDestroyed = propeller.takeDamage(propeller.getCurrentHealth());
            if (justDestroyed) onEngineDestroyed();
        }
    }

    /**
     * Called once, server-side, the instant the engine component transitions to
     * DESTROYED — whether from combat damage or fuel exhaustion. Default behavior
     * (prop planes) ejects a propeller shard; jet-engined planes override this to
     * spawn fire and apply Wither to the pilot instead.
     */
    protected void onEngineDestroyed() {
        ejectPropellerShard();
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Propeller contact hazard
    // ─────────────────────────────────────────────────────────────────────────

    private void checkPropellerContact() {
        double yawRad = Math.toRadians(yaw);
        double noseX  = posX - Math.sin(yawRad) * 1.5;
        double noseY  = posY + 0.5;
        double noseZ  = posZ + Math.cos(yawRad) * 1.5;

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

    public void onPropellerContact(Entity toucher) {
        float damage = 4f * (speed / maxSpeed);
        toucher.attackEntityFrom(
            net.minecraft.util.DamageSource.causeIndirectMagicDamage(this, this),
            damage
        );
        boolean justDestroyed = propeller.takeDamage(2f);
        world.playSound(null, posX, posY, posZ,
            ModSounds.PLANE_PROPELLER_DAMAGE, SoundCategory.NEUTRAL, 1.0f, 1.0f);
        if (justDestroyed) onEngineDestroyed();
    }

    protected void ejectPropellerShard() {
        if (!world.isRemote) {
            world.playSound(null, posX, posY, posZ,
                ModSounds.PLANE_PROPELLER_DESTROYED, SoundCategory.NEUTRAL, 1.0f, 1.0f);
            PropellerShardEntity shard = new PropellerShardEntity(world, this);
            world.spawnEntity(shard);
            AirArsenal.LOGGER.info("{} propeller destroyed — shard ejected", getPlaneType());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Sound dispatching (Chunk 10)
    // ─────────────────────────────────────────────────────────────────────────

    private int engineSoundTicks = 0;

    /**
     * Plays a plane's looping engine sound periodically (client side only) while
     * it is moving. Called by each concrete plane's {@code onUpdate()} after
     * flight physics has updated {@link #speed} for the current tick.
     */
    protected void playEngineLoopSound(SoundEvent engineSound) {
        if (!world.isRemote) return;
        if (speed <= 0.5f) {
            engineSoundTicks = 0;
            return;
        }
        engineSoundTicks++;
        if (engineSoundTicks >= 20) {
            engineSoundTicks = 0;
            world.playSound(posX, posY, posZ,
                engineSound, SoundCategory.NEUTRAL, 1.0f, 1.0f, false);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Particle dispatching (client side only)
    // ─────────────────────────────────────────────────────────────────────────

    private void spawnPropellerParticles() {
        double yawRad = Math.toRadians(yaw);
        double noseX  = posX - Math.sin(yawRad) * 1.5;
        double noseY  = posY + 0.5;
        double noseZ  = posZ + Math.cos(yawRad) * 1.5;

        switch (propeller.getDamageState()) {
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
                break;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Flight physics
    // ─────────────────────────────────────────────────────────────────────────

    protected void applyFlightPhysics() {
        PropellerState propState = propeller.getDamageState();
        yaw += propeller.getYawDrift();

        if (propState == PropellerState.DESTROYED) {
            speed  = Math.max(0f, speed - DESTROYED_SPEED_DECAY);
            motionY = -GRAVITY_DROP;
            pitch  = Math.min(pitch + 0.3f, 30f);
        } else {
            float effectiveSpeed = speed * propeller.getSpeedMultiplier();
            motionY = (effectiveSpeed >= LIFT_THRESHOLD)
                ? effectiveSpeed * 0.002
                : -GRAVITY_DROP;
        }

        float thrustPerTick = (speed * propeller.getSpeedMultiplier()) / 20f;
        double yawRad = Math.toRadians(yaw);
        motionX = -Math.sin(yawRad) * thrustPerTick;
        motionZ =  Math.cos(yawRad) * thrustPerTick;

        this.rotationYaw   = yaw;
        this.rotationPitch = pitch;

        move(net.minecraft.entity.MoverType.SELF, motionX, motionY, motionZ);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Input processing (called by packet handler)
    // ─────────────────────────────────────────────────────────────────────────

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
    //  Weapon selection
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Returns the weapon at {@link #selectedWeaponIndex}, or {@code null} if
     * the plane has no weapons or the index is out of range.
     */
    public IPlaneWeapon getSelectedWeapon() {
        if (weapons.isEmpty() || selectedWeaponIndex >= weapons.size()) return null;
        return weapons.get(selectedWeaponIndex);
    }

    public void setSelectedWeaponIndex(int index) {
        if (!weapons.isEmpty()) {
            this.selectedWeaponIndex = Math.max(0, Math.min(index, weapons.size() - 1));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Tac Mode control
    // ─────────────────────────────────────────────────────────────────────────

    /** Flip Tac Mode state. Only call on server. */
    public void toggleTacMode() {
        this.isTacModeActive = !this.isTacModeActive;
        AirArsenal.LOGGER.debug("{} Tac Mode → {}", getPlaneType(), isTacModeActive);
    }

    public void setTacModeActive(boolean active) {
        this.isTacModeActive = active;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Direct airframe damage (Chunk 8 — AA/Flak ground fire)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Applies direct damage to the airframe, bypassing the AP/DR bullet formula.
     * Used by ground-based anti-air (AA Cannon, Flak Battery). Server-side only.
     *
     * @param amount HP to remove from {@link #planeHealth}.
     */
    public void damagePlane(float amount) {
        if (world.isRemote) return;
        planeHealth = Math.max(0f, planeHealth - amount);
        if (planeHealth <= 0f) {
            onPlaneDestroyed();
        }
    }

    /** Ejects any rider and removes the plane. Called when {@link #planeHealth} reaches 0. */
    protected void onPlaneDestroyed() {
        for (Entity passenger : getPassengers()) {
            passenger.dismountRidingEntity();
        }
        AirArsenal.LOGGER.info("{} destroyed by ground fire", getPlaneType());
        setDead();
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

    @Override public boolean canBePushed()       { return false; }
    @Override public boolean canBeCollidedWith() { return true;  }

    // ─────────────────────────────────────────────────────────────────────────
    //  NBT persistence
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        compound.setFloat("Speed",               speed);
        compound.setFloat("PlaneHealth",         planeHealth);
        compound.setFloat("PlanePitch",          pitch);
        compound.setFloat("PlaneYaw",            yaw);
        compound.setInteger("SelectedWeapon",    selectedWeaponIndex);
        compound.setBoolean("TacModeActive",     isTacModeActive);
        NBTTagCompound propNBT = new NBTTagCompound();
        propeller.writeToNBT(propNBT);
        compound.setTag("Propeller", propNBT);
        NBTTagCompound fuelNBT = new NBTTagCompound();
        fuelSystem.writeToNBT(fuelNBT);
        compound.setTag("FuelSystem", fuelNBT);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        speed               = compound.getFloat("Speed");
        planeHealth         = compound.getFloat("PlaneHealth");
        pitch               = compound.getFloat("PlanePitch");
        yaw                 = compound.getFloat("PlaneYaw");
        selectedWeaponIndex = compound.getInteger("SelectedWeapon");
        isTacModeActive     = compound.getBoolean("TacModeActive");
        if (compound.hasKey("Propeller")) {
            propeller.readFromNBT(compound.getCompoundTag("Propeller"));
        }
        if (compound.hasKey("FuelSystem")) {
            fuelSystem.readFromNBT(compound.getCompoundTag("FuelSystem"));
        }
    }

    @Override
    protected void entityInit() {
        // DataWatcher entries added when synced client state is needed (future chunks)
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Getters
    // ─────────────────────────────────────────────────────────────────────────

    public float getSpeed()                  { return speed;                }
    public float getMaxSpeed()               { return maxSpeed;             }
    public float getPlaneHealth()            { return planeHealth;          }
    public float getMaxPlaneHealth()         { return maxPlaneHealth;       }
    public float getPlanePitch()             { return pitch;                }
    public float getPlaneYaw()               { return yaw;                  }
    public boolean isTacModeActive()         { return isTacModeActive;      }
    public List<IPlaneWeapon> getWeapons()   { return weapons;              }
    public IEngineComponent getPropeller()   { return propeller;            }
    public int getSelectedWeaponIndex()      { return selectedWeaponIndex;  }
    public FuelSystem getFuelSystem()        { return fuelSystem;           }
}
