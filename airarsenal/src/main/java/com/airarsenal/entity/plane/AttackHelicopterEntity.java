package com.airarsenal.entity.plane;

import com.airarsenal.combat.weapon.Minigun;
import com.airarsenal.combat.weapon.RocketPodLauncher;
import com.airarsenal.entity.plane.component.PropellerComponent;
import net.minecraft.world.World;

/**
 * Attack Helicopter — hovering rotorcraft (Chunk 9).
 * Not craftable — creative tab / loot only.
 *
 * Stats:
 * <ul>
 *   <li>Top speed:          40 blocks/second (can hover at 0)</li>
 *   <li>Structural health:  80 HP</li>
 *   <li>Main rotor health:  60 HP (still a spinning-blade hazard — see
 *       {@link #hasSpinningPropeller})</li>
 *   <li>Fuel consumption:   0.10/tick</li>
 *   <li>Hardpoints:         2 — {@link Minigun} + {@link RocketPodLauncher}</li>
 * </ul>
 *
 * <h3>Hover</h3>
 * Gravity is disabled whenever {@code speed == 0} and the engine is not
 * DESTROYED — see the override of {@link #applyFlightPhysics()}.
 *
 * <h3>Strafe</h3>
 * Lateral movement (key {@code Q}/{@code E}) is applied via {@link #applyStrafe}
 * — same wiring gap as the rest of {@link BasePlaneEntity#processInput}, which
 * isn't yet dispatched from a movement packet (pre-existing, not introduced by
 * this chunk); the hook is here for whichever future chunk finishes plane
 * movement networking.
 */
public class AttackHelicopterEntity extends BasePlaneEntity {

    private static final float STRAFE_SPEED = 0.25f;

    public AttackHelicopterEntity(World world) {
        super(world);
        this.maxSpeed        = 40f;
        this.maxPlaneHealth  = 80f;
        this.planeHealth     = this.maxPlaneHealth;
        this.propeller       = new PropellerComponent(60f); // main rotor
        this.fuelConsumption = 0.10f;

        this.weapons.add(new Minigun());
        this.weapons.add(new RocketPodLauncher());
    }

    @Override
    public String getPlaneType() { return "attack_helicopter"; }

    @Override
    public void onUpdate() {
        super.onUpdate(); // rotor contact check + fuel tick + particles
        applyFlightPhysics();
    }

    /**
     * Overrides the base gravity/lift model: when the helicopter is holding
     * position (speed 0) and the rotor is still functional, it hovers in place
     * instead of falling. Any nonzero speed falls back to normal plane physics.
     */
    @Override
    protected void applyFlightPhysics() {
        if (speed <= 0.01f && !propeller.isDestroyed()) {
            yaw += propeller.getYawDrift();
            motionX = 0;
            motionY = 0;
            motionZ = 0;
            this.rotationYaw   = yaw;
            this.rotationPitch = pitch;
            moveEntity(motionX, motionY, motionZ);
            return;
        }
        super.applyFlightPhysics();
    }

    /** Lateral strafe input — perpendicular to current heading. Server-side only. */
    public void applyStrafe(boolean left, boolean right) {
        if (world.isRemote) return;
        if (left == right) return; // none or both cancels out

        double yawRad = Math.toRadians(yaw);
        // Perpendicular to facing: rotate heading vector 90 degrees
        double strafeX = -Math.cos(yawRad) * STRAFE_SPEED;
        double strafeZ = -Math.sin(yawRad) * STRAFE_SPEED;
        float dir = left ? -1f : 1f;

        moveEntity(strafeX * dir, 0, strafeZ * dir);
    }
}
