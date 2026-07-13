package com.airarsenal.combat.weapon;

import com.airarsenal.entity.projectile.BrahMosEntity;
import com.airarsenal.entity.plane.BasePlaneEntity;
import com.airarsenal.item.ItemBrahMosTargeter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Hardpoint-mounted BrahMos cruise-missile launcher fitted to the Fighter Jet
 * (Chunk 9). Reads the waypoint from the pilot's {@link ItemBrahMosTargeter}
 * saved target (Chunk 7) — the pilot must set a target before boarding.
 * Refuses to fire if no waypoint is set or the shooter has no player pilot.
 *
 * <ul>
 *   <li>Fire rate: 1 missile every 200 ticks (10 seconds).</li>
 *   <li>Default ammo: 1 missile per hardpoint.</li>
 * </ul>
 */
public class BrahMosLauncher implements IPlaneWeapon {

    private static final int FIRE_RATE_TICKS = 200;
    private static final int DEFAULT_AMMO    = 1;

    private int cooldownTicks = 0;
    private int ammo          = DEFAULT_AMMO;

    @Override
    public boolean tryFire(World world, Entity shooter, Vec3d direction) {
        if (cooldownTicks > 0) {
            cooldownTicks--;
            return false;
        }
        if (ammo == 0) {
            return false;
        }

        EntityPlayer pilot = findPilot(shooter);
        if (pilot == null) return false;

        BlockPos target = ItemBrahMosTargeter.getTarget(pilot);
        if (target == null) return false;

        if (!world.isRemote) {
            BrahMosEntity missile = new BrahMosEntity(world, shooter, target);
            world.spawnEntity(missile);
        }

        if (ammo != -1) ammo--;
        cooldownTicks = FIRE_RATE_TICKS;
        return true;
    }

    private EntityPlayer findPilot(Entity shooter) {
        if (!(shooter instanceof BasePlaneEntity)) return null;
        Entity controller = ((BasePlaneEntity) shooter).getControllingPassenger();
        return (controller instanceof EntityPlayer) ? (EntityPlayer) controller : null;
    }

    @Override
    public String getDisplayName() { return "BrahMos Launcher"; }

    @Override
    public int getAmmoCount() { return ammo; }

    public void setAmmo(int ammo) { this.ammo = ammo; }
}
