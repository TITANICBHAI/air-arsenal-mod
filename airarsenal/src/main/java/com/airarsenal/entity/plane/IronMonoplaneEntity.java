package com.airarsenal.entity.plane;

import com.airarsenal.combat.weapon.LightMachineGun;
import com.airarsenal.entity.plane.component.PropellerComponent;
import com.airarsenal.registry.ModSounds;
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
 * </ul>
 *
 * <p>Weapon firing is driven by {@link com.airarsenal.network.PacketWeaponFire}
 * (Chunk 5). The auto-fire placeholder from Chunk 4 has been removed.</p>
 */
public class IronMonoplaneEntity extends BasePlaneEntity {

    public IronMonoplaneEntity(World world) {
        super(world);
        this.maxSpeed       = 35f;
        this.maxPlaneHealth = 60f;
        this.planeHealth    = this.maxPlaneHealth;
        // Override the default 30 HP propeller with the Iron Monoplane's 45 HP one
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
        super.onUpdate(); // propeller contact check + particles
        applyFlightPhysics();
        playEngineLoopSound(ModSounds.PLANE_ENGINE_MONOPLANE);
        // Weapon firing is now handled server-side by PacketWeaponFire.Handler
    }
}
