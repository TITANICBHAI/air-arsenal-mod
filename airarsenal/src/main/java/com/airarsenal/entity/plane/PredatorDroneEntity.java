package com.airarsenal.entity.plane;

import com.airarsenal.combat.weapon.HellfireLauncher;
import com.airarsenal.entity.plane.component.JetEngineComponent;
import com.airarsenal.item.ItemDroneController;
import com.airarsenal.network.ModNetwork;
import com.airarsenal.network.PacketDroneCameraEnd;
import com.airarsenal.network.PacketDroneCameraStart;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

/**
 * Predator Drone — remote-piloted reconnaissance/strike UAV (Chunk 9).
 * Not craftable — creative tab / loot only.
 *
 * Stats:
 * <ul>
 *   <li>Top speed:          55 blocks/second</li>
 *   <li>Structural health:  60 HP</li>
 *   <li>Jet engine health:  25 HP</li>
 *   <li>Fuel consumption:   0.05/tick</li>
 *   <li>Hardpoints:         2× {@link HellfireLauncher}</li>
 * </ul>
 *
 * <h3>Remote pilot mode</h3>
 * <p>Started by {@link com.airarsenal.item.ItemDroneController}: the pilot never
 * physically mounts the drone — their view switches to the drone's camera via
 * {@link PacketDroneCameraStart} while they stay on the ground. Movement input
 * arrives via {@link com.airarsenal.network.PacketDroneSteer}. Pressing Shift
 * exits remote pilot mode ({@link #stopRemotePilot()}): the pilot's view is
 * restored, but the drone keeps flying at its last heading/speed server-side
 * until it hits something or runs out of fuel.</p>
 */
public class PredatorDroneEntity extends BaseJetPlaneEntity {

    /** Transient — not saved to NBT. Null when nobody is remote-piloting this drone. */
    private EntityPlayerMP controllingPlayer;

    public PredatorDroneEntity(World world) {
        super(world);
        this.maxSpeed        = 55f;
        this.maxPlaneHealth  = 60f;
        this.planeHealth     = this.maxPlaneHealth;
        this.propeller       = new JetEngineComponent(25f);
        this.fuelConsumption = 0.05f;

        this.weapons.add(new HellfireLauncher());
        this.weapons.add(new HellfireLauncher());
    }

    @Override
    public String getPlaneType() { return "predator_drone"; }

    @Override
    public void onUpdate() {
        super.onUpdate(); // fuel tick + particles
        applyFlightPhysics();

        if (!world.isRemote && controllingPlayer != null && !controllingPlayer.isEntityAlive()) {
            // Pilot logged off / died — drone continues flying autonomously
            controllingPlayer = null;
        }
    }

    // ── Remote pilot control ──────────────────────────────────────────────────

    /**
     * Applies WASD/mouse input from {@link com.airarsenal.network.PacketDroneSteer}.
     * Same shape as {@link BasePlaneEntity#processInput}, adapted for the
     * yaw/pitch-delta style used by camera-driven steering.
     */
    public void applyRemoteSteer(float yawDelta, float pitchDelta,
                                 boolean throttleUp, boolean throttleDown) {
        yaw   += yawDelta;
        pitch  = MathHelper.clamp(pitch + pitchDelta, -45f, 30f);
        if (throttleUp)   speed = Math.min(speed + 0.3f, maxSpeed);
        if (throttleDown) speed = Math.max(speed - 0.5f, 0f);
    }

    /** Server-side: begins remote-pilot mode for {@code player}. Called by ItemDroneController. */
    public void startRemotePilot(EntityPlayerMP player) {
        this.controllingPlayer = player;
        ModNetwork.CHANNEL.sendTo(new PacketDroneCameraStart(getEntityId()), player);
    }

    /** Server-side: ends remote-pilot mode. The drone keeps flying at its current heading. */
    public void stopRemotePilot() {
        if (controllingPlayer != null) {
            ModNetwork.CHANNEL.sendTo(new PacketDroneCameraEnd(), controllingPlayer);
        }
        controllingPlayer = null;
    }

    public EntityPlayerMP getControllingPlayer() { return controllingPlayer; }

    @Override
    protected EntityLivingBase getRemotePilot() { return controllingPlayer; }

    // ── Interaction ───────────────────────────────────────────────────────────

    /**
     * Right-clicking a drone with {@link ItemDroneController} starts remote-pilot
     * mode instead of the default mount behavior; any other interaction falls
     * back to {@link BasePlaneEntity#processInitialInteract} (physical mounting).
     */
    @Override
    public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (stack.getItem() instanceof ItemDroneController) {
            if (!world.isRemote && player instanceof EntityPlayerMP) {
                startRemotePilot((EntityPlayerMP) player);
            }
            return true;
        }
        return super.processInitialInteract(player, hand);
    }
}
