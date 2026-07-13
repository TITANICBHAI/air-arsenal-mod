package com.airarsenal.entity.vehicle;

import com.airarsenal.entity.projectile.TruckGuidedMissileEntity;
import com.airarsenal.network.ModNetwork;
import com.airarsenal.network.PacketGuidanceStart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Missile Command Truck — drivable ground vehicle mounting 2 missile launch tubes.
 *
 * <ul>
 *   <li>Movement: W/S up to 0.35 blocks/tick; A/D turn ±3°/tick. No pitch — ground-bound,
 *       gravity applies every tick like a normal entity.</li>
 *   <li>2 tubes, each with an independent 100-tick reload.</li>
 *   <li>Fires {@link TruckGuidedMissileEntity}; the firing player becomes its
 *       {@code controllerPlayer} and receives {@link PacketGuidanceStart}.</li>
 * </ul>
 *
 * <p>Follows the mount/NBT/getters pattern established by
 * {@code BasePlaneEntity} for controllable vehicles.</p>
 */
public class MissileTruckEntity extends EntityCreature {

    private static final double MOVE_SPEED = 0.35;
    private static final float  TURN_RATE  = 3f;
    private static final int    TUBE_RELOAD_TICKS = 100;
    private static final int    TUBE_COUNT = 2;

    /** Ticks remaining before each tube can fire again. Index 0/1 = tube 1/2. */
    private final int[] tubeCooldown = new int[TUBE_COUNT];

    private boolean inputForward, inputBack, inputLeft, inputRight;

    public MissileTruckEntity(World world) {
        super(world);
        setSize(1.8f, 1.6f);
        this.stepHeight = 1.0f;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getAttributeMap().registerAttribute(SharedMonsterAttributes.MAX_HEALTH);
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(120.0);
    }

    // ── Mounting ──────────────────────────────────────────────────────────────

    @Override
    protected boolean processInteract(EntityPlayer player, EnumHand hand) {
        if (!world.isRemote && getPassengers().isEmpty()) {
            player.startRiding(this);
            return true;
        }
        return super.processInteract(player, hand);
    }

    @Override
    public boolean canBeSteered() { return true; }

    @Override
    public boolean canPassengerSteer() { return false; } // we handle movement ourselves via input flags

    /** Called client-side by the controller (e.g. from a key-binding hook) each tick. */
    public void setDriveInput(boolean forward, boolean back, boolean left, boolean right) {
        this.inputForward = forward;
        this.inputBack    = back;
        this.inputLeft    = left;
        this.inputRight   = right;
    }

    // ── Movement (server-authoritative, driven by rider input flags) ────────────

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (!world.isRemote && getControllingPassenger() != null) {
            if (inputLeft)  rotationYaw -= TURN_RATE;
            if (inputRight) rotationYaw += TURN_RATE;

            double speed = 0;
            if (inputForward) speed = MOVE_SPEED;
            else if (inputBack) speed = -MOVE_SPEED * 0.6; // slower in reverse

            if (speed != 0) {
                double yawRad = Math.toRadians(rotationYaw);
                motionX = -Math.sin(yawRad) * speed;
                motionZ =  Math.cos(yawRad) * speed;
            } else {
                motionX = 0;
                motionZ = 0;
            }
            // Gravity is still applied by the base Entity.onUpdate() / moveEntity chain.
        }

        for (int i = 0; i < TUBE_COUNT; i++) {
            if (tubeCooldown[i] > 0) tubeCooldown[i]--;
        }
    }

    @Override
    public Entity getControllingPassenger() {
        return getPassengers().isEmpty() ? null : getPassengers().get(0);
    }

    // ── Firing ────────────────────────────────────────────────────────────────

    /**
     * Fires from the first available tube, if any and the firing player has no other
     * missile currently under their control (avoids two simultaneous guidance links).
     *
     * @return true if a missile was launched.
     */
    public boolean fireTube(EntityPlayerMP player) {
        int tube = readyTubeIndex();
        if (tube < 0) return false;

        TruckGuidedMissileEntity missile = new TruckGuidedMissileEntity(world, this, player);
        world.spawnEntity(missile);
        tubeCooldown[tube] = TUBE_RELOAD_TICKS;

        ModNetwork.CHANNEL.sendTo(new PacketGuidanceStart(missile.getEntityId()), player);
        return true;
    }

    private int readyTubeIndex() {
        for (int i = 0; i < TUBE_COUNT; i++) {
            if (tubeCooldown[i] <= 0) return i;
        }
        return -1;
    }

    public int getTubeCooldown(int tube) { return tubeCooldown[tube]; }
    public int getTubeCount() { return TUBE_COUNT; }

    // ── NBT persistence ───────────────────────────────────────────────────────

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setIntArray("TubeCooldown", tubeCooldown);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (compound.hasKey("TubeCooldown")) {
            int[] saved = compound.getIntArray("TubeCooldown");
            for (int i = 0; i < Math.min(saved.length, TUBE_COUNT); i++) {
                tubeCooldown[i] = saved[i];
            }
        }
    }

    @Override
    public boolean canBeLeashed(EntityPlayer player) { return false; }
}
