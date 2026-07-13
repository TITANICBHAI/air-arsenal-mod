package com.airarsenal.entity.plane;

import com.airarsenal.AirArsenal;
import com.airarsenal.combat.weapon.BrahMosLauncher;
import com.airarsenal.combat.weapon.HeavyRoundGun;
import com.airarsenal.combat.weapon.HellfireLauncher;
import com.airarsenal.entity.plane.component.JetEngineComponent;
import net.minecraft.world.World;

/**
 * Fighter Jet — the fastest and most heavily armed plane in Air Arsenal (Chunk 9).
 * Not craftable — creative tab / loot only.
 *
 * Stats:
 * <ul>
 *   <li>Top speed:          180 blocks/second (220 with afterburner)</li>
 *   <li>Structural health:  100 HP</li>
 *   <li>Jet engine health:  60 HP (no propeller — see {@link JetEngineComponent})</li>
 *   <li>Fuel consumption:   0.15/tick (3× while afterburner is active)</li>
 *   <li>Hardpoints:         4 — 2× {@link HeavyRoundGun}, 1× {@link HellfireLauncher},
 *       1× {@link BrahMosLauncher}</li>
 * </ul>
 *
 * Afterburner (key {@code G}, routed via {@code PacketSpecialAction}): boosts to
 * 220 b/s for 100 ticks, then a 30-tick cooldown. Sonic boom particle ring fires
 * every 20 ticks while speed exceeds 160 b/s.
 */
public class FighterJetEntity extends BaseJetPlaneEntity {

    private static final float AFTERBURNER_TOP_SPEED   = 220f;
    private static final int   AFTERBURNER_DURATION    = 100; // ticks
    private static final int   AFTERBURNER_COOLDOWN    = 30;  // ticks
    private static final float SONIC_BOOM_THRESHOLD    = 160f;
    private static final int   SONIC_BOOM_INTERVAL     = 20;  // ticks

    private boolean afterburnerActive     = false;
    private int     afterburnerTicksLeft  = 0;
    private int     afterburnerCooldownTicks = 0;
    private int     sonicBoomTicks        = 0;

    public FighterJetEntity(World world) {
        super(world);
        this.maxSpeed       = 180f;
        this.maxPlaneHealth = 100f;
        this.planeHealth    = this.maxPlaneHealth;
        this.propeller      = new JetEngineComponent(60f);
        this.fuelConsumption = 0.15f;

        this.weapons.add(new HeavyRoundGun());
        this.weapons.add(new HeavyRoundGun());
        this.weapons.add(new HellfireLauncher());
        this.weapons.add(new BrahMosLauncher());
    }

    @Override
    public String getPlaneType() { return "fighter_jet"; }

    @Override
    public void onUpdate() {
        super.onUpdate(); // fuel tick + particles
        tickAfterburner();
        applyFlightPhysics();
        if (!world.isRemote) tickSonicBoom();
    }

    // ── Afterburner ───────────────────────────────────────────────────────────

    private void tickAfterburner() {
        if (afterburnerCooldownTicks > 0) afterburnerCooldownTicks--;

        if (afterburnerActive) {
            speed = Math.min(speed + 1.2f, AFTERBURNER_TOP_SPEED);
            if (!world.isRemote) {
                // Base fuelConsumption is already drained once per tick by
                // BasePlaneEntity#tickFuel — burn 2x more here for a 3x total.
                fuelSystem.consumeFuel(fuelConsumption * 2f);
            }
            afterburnerTicksLeft--;
            if (afterburnerTicksLeft <= 0 || fuelSystem.isEmpty()) {
                afterburnerActive        = false;
                afterburnerCooldownTicks = AFTERBURNER_COOLDOWN;
            }
        } else if (speed > maxSpeed) {
            // Passive bleed-off back down to normal top speed once the boost ends
            speed = Math.max(maxSpeed, speed - 1.0f);
        }
    }

    /** Called server-side by {@code PacketSpecialAction} when the pilot presses G. */
    public void tryActivateAfterburner() {
        if (world.isRemote) return;
        if (afterburnerActive || afterburnerCooldownTicks > 0) return;
        if (fuelSystem.isEmpty()) return;
        afterburnerActive    = true;
        afterburnerTicksLeft = AFTERBURNER_DURATION;
        AirArsenal.LOGGER.debug("Fighter Jet afterburner engaged");
    }

    public boolean isAfterburnerActive() { return afterburnerActive; }

    // ── Sonic boom ────────────────────────────────────────────────────────────

    private void tickSonicBoom() {
        if (speed <= SONIC_BOOM_THRESHOLD) {
            sonicBoomTicks = 0;
            return;
        }
        sonicBoomTicks++;
        if (sonicBoomTicks >= SONIC_BOOM_INTERVAL) {
            sonicBoomTicks = 0;
            AirArsenal.proxy.spawnSonicBoomRing(world, posX, posY, posZ);
        }
    }
}
