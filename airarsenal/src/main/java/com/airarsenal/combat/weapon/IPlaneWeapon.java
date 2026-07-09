package com.airarsenal.combat.weapon;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Interface implemented by every weapon that can be mounted on a plane hardpoint.
 * Concrete implementations are added in Chunks 4, 6, and 7.
 */
public interface IPlaneWeapon {

    /**
     * Attempt to fire this weapon.
     *
     * @param world  The world the plane is in.
     * @param shooter The plane entity doing the firing.
     * @param direction Normalised direction vector the projectile should travel.
     * @return {@code true} if the weapon actually fired (e.g. not on cooldown, ammo available).
     */
    boolean tryFire(World world, Entity shooter, Vec3d direction);

    /**
     * Human-readable name shown in the HUD / tooltip.
     */
    String getDisplayName();
}
