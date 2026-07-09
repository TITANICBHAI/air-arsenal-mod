package com.airarsenal.entity.plane;

import com.airarsenal.combat.weapon.LightMachineGun;
import com.airarsenal.entity.plane.component.PropellerComponent;
import net.minecraft.world.World;

/**
 * Iron Monoplane — the first armed plane in Air Arsenal.
 *
 * Stats:
 * <ul>
 *   <li>Top speed:          35 blocks/second</li>
 *   <li>Structural health:  60 HP</li>
 *   <li>Propeller health:   45 HP</li>
 *   <li>Hardpoints:         1 (nose-mounted Light Machine Gun)</li>
 *   <li>Fuel:               unlimited (fuel system added in Chunk 9)</li>
 * </ul>
 *
 * <p>The LMG fires automatically every 5 ticks toward the plane's look vector.
 * Proper player-input gating (fire-key, Tac Mode) is added in Chunk 5.</p>
 */
public class IronMonoplaneEntity extends BasePlaneEntity {

    public IronMonoplaneEntity(World world) {
        super(world);
        this.maxSpeed       = 35f;
        this.maxPlaneHealth = 60f;
        this.planeHealth    = this.maxPlaneHealth;

        // Override the default propeller (30 HP from BasePlaneEntity) with 45 HP
        this.propeller = new PropellerComponent(45f);

        // One hardpoint: fixed nose LMG
        this.weapons.add(new LightMachineGun());
    }

    @Override
    public String getPlaneType() {
        return "iron_monoplane";
    }

    @Override
    public void onUpdate() {
        super.onUpdate(); // handles propeller contact, particles
        applyFlightPhysics();

        // ── Auto-fire all weapons toward look vector (server side only) ───────
        // Chunk 5 will replace this placeholder with proper client-driven input gating.
        if (!world.isRemote) {
            for (com.airarsenal.combat.weapon.IPlaneWeapon weapon : weapons) {
                weapon.tryFire(world, this, getLookVec());
            }
        }
    }
}
