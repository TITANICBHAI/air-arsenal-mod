package com.airarsenal.item;

import com.airarsenal.AirArsenal;
import com.airarsenal.creativetab.AirArsenalTab;
import com.airarsenal.entity.plane.BasePlaneEntity;
import com.airarsenal.entity.projectile.ManpadsEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.item.EnumAction;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

/**
 * MANPADS — shoulder-fired anti-air missile launcher.
 *
 * <p>Hold right-click to begin a 40-tick lock-on (see {@link #onUsingTick}); releasing
 * early cancels the lock. Releasing after the lock completes fires a
 * {@link ManpadsEntity} at the nearest {@link BasePlaneEntity} within a 30° cone,
 * 100-block range. After firing, the item enters a 200-tick reload — checked via
 * a tick-timestamp stored in the stack's NBT.</p>
 *
 * Non-craftable — creative tab / loot only.
 */
public class ItemMANPADS extends Item {

    public static final int LOCK_ON_TICKS = 40;
    public static final int RELOAD_TICKS  = 200;
    private static final double RANGE     = 100.0;
    private static final double LOCK_CONE_COS = Math.cos(Math.toRadians(30.0));

    public ItemMANPADS() {
        setMaxStackSize(1);
        setCreativeTab(AirArsenalTab.INSTANCE);
        setMaxDamage(0);
        setTranslationKey("airarsenal.manpads");
        setRegistryName("airarsenal", "manpads");
    }

    // ── Begin use (start lock-on) ─────────────────────────────────────────────

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        NBTTagCompound tag = stack.getOrCreateSubCompound("airarsenal");

        long readyAtTick = tag.getLong("ReadyAtTick");
        if (world.getTotalWorldTime() < readyAtTick) {
            // Still reloading
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }

        player.setActiveHand(hand);
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) { return EnumAction.BOW; }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) { return 72000; } // effectively unlimited hold

    // ── While holding (lock-on progress) ──────────────────────────────────────

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
        // count = ticks REMAINING until getMaxItemUseDuration expires — ticks held = max - count
        // No per-tick action needed here; ManpadsHUD reads getItemInUseCount() directly for the
        // lock-on ring animation. Actual firing happens in onPlayerStoppedUsing.
    }

    // ── Release (fire if locked) ───────────────────────────────────────────────

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entityLiving, int timeLeft) {
        if (!(entityLiving instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) entityLiving;

        int maxDuration = getMaxItemUseDuration(stack);
        int ticksHeld = maxDuration - timeLeft;

        if (ticksHeld < LOCK_ON_TICKS) {
            // Released too early — lock interrupted, no shot fired
            return;
        }

        if (world.isRemote) return;

        BasePlaneEntity target = findLockTarget(player);
        if (target == null) {
            player.sendMessage(new net.minecraft.util.text.TextComponentString(
                net.minecraft.util.text.TextFormatting.RED + "[MANPADS] Lock lost — no target"));
            return;
        }

        Vec3d dir = new Vec3d(
            target.posX - player.posX,
            target.posY + target.height * 0.5 - (player.posY + player.getEyeHeight()),
            target.posZ - player.posZ
        ).normalize();

        ManpadsEntity missile = new ManpadsEntity(world, player, target, dir);
        world.spawnEntity(missile);
        AirArsenal.LOGGER.info("{} fired MANPADS at {}", player.getName(), target.getPlaneType());

        // Start reload cooldown
        NBTTagCompound tag = stack.getOrCreateSubCompound("airarsenal");
        tag.setLong("ReadyAtTick", world.getTotalWorldTime() + RELOAD_TICKS);
    }

    // ── Target acquisition ────────────────────────────────────────────────────

    private BasePlaneEntity findLockTarget(EntityPlayer player) {
        Vec3d look = player.getLookVec();
        List<BasePlaneEntity> nearby = player.world.getEntitiesWithinAABB(
            BasePlaneEntity.class, player.getEntityBoundingBox().grow(RANGE));

        BasePlaneEntity best = null;
        double bestDist = Double.MAX_VALUE;
        for (BasePlaneEntity plane : nearby) {
            Vec3d toPlane = new Vec3d(
                plane.posX - player.posX,
                plane.posY + plane.height * 0.5 - player.posY,
                plane.posZ - player.posZ
            ).normalize();
            if (look.dotProduct(toPlane) >= LOCK_CONE_COS) {
                double dist = player.getDistanceSq(plane);
                if (dist < bestDist) { bestDist = dist; best = plane; }
            }
        }
        return best;
    }
}
