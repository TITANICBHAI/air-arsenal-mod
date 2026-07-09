package com.airarsenal.entity.plane;

import net.minecraft.world.World;

import java.util.ArrayList;

/**
 * The Wood Biplane — the first and most basic plane in Air Arsenal.
 *
 * Stats:
 * <ul>
 *   <li>Top speed:   18 blocks/second</li>
 *   <li>Lift at:     10 blocks/second</li>
 *   <li>Health:      40 HP (20 hearts equivalent)</li>
 *   <li>Weapons:     none (0 hardpoints)</li>
 *   <li>Fuel:        unlimited (fuel system added in Chunk 9)</li>
 * </ul>
 */
public class WoodBiplaneEntity extends BasePlaneEntity {

    public WoodBiplaneEntity(World world) {
        super(world);
        this.maxSpeed       = 18f;
        this.maxPlaneHealth = 40f;
        this.planeHealth    = this.maxPlaneHealth;
        // Unarmed — no hardpoints
        this.weapons        = new ArrayList<>();
    }

    @Override
    public String getPlaneType() {
        return "wood_biplane";
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        // Only apply flight physics when the plane is in the air / has a rider
        // Full input handling via packets is wired in Chunk 5.
        // For now, physics run every tick so mounting and fall-under-gravity work correctly.
        applyFlightPhysics();
    }
}
