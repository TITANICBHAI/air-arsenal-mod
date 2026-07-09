package com.airarsenal.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

/**
 * Pure, stateless utility class for Air Arsenal's armor-penetration damage formula.
 *
 * <p>No entities are modified here — every method is a pure calculation that can
 * be unit-tested independently of the game loop.</p>
 *
 * <h3>Armor Defense Rating (DR) per full set:</h3>
 * <pre>
 *  None       DR  0   (2 per piece × 0 pieces)
 *  Leather    DR  8   (2 per piece × 4)
 *  Gold       DR 12   (3 per piece × 4)
 *  Chainmail  DR 16   (4 per piece × 4)
 *  Iron       DR 24   (6 per piece × 4)
 *  Diamond    DR 36   (9 per piece × 4)
 * </pre>
 */
public final class DamageCalculator {

    private DamageCalculator() {} // utility class — no instances

    // ── DR table (per armor piece) ────────────────────────────────────────────

    private static int drPerPiece(ItemArmor.ArmorMaterial mat) {
        switch (mat) {
            case LEATHER:  return 2;
            case GOLD:     return 3;
            case CHAIN:    return 4;
            case IRON:     return 6;
            case DIAMOND:  return 9;
            default:       return 0;
        }
    }

    /**
     * Returns the total Defense Rating for a player based on all equipped armor pieces.
     *
     * @param player The player whose armor is inspected.
     * @return Total DR (0 if unarmored).
     */
    public static int getArmorDR(EntityPlayer player) {
        int dr = 0;
        for (EntityEquipmentSlot slot : new EntityEquipmentSlot[]{
                EntityEquipmentSlot.HEAD,
                EntityEquipmentSlot.CHEST,
                EntityEquipmentSlot.LEGS,
                EntityEquipmentSlot.FEET}) {
            ItemStack stack = player.getItemStackFromSlot(slot);
            if (!stack.isEmpty() && stack.getItem() instanceof ItemArmor) {
                ItemArmor armor = (ItemArmor) stack.getItem();
                dr += drPerPiece(armor.getArmorMaterial());
            }
        }
        return dr;
    }

    // ── Core AP/DR formula ────────────────────────────────────────────────────

    /**
     * Calculates bullet damage after armor penetration and hit-location modifier.
     *
     * <pre>
     *   penetrationRatio = min(bulletAP / armorDR, 1.0)   [if armorDR == 0: ratio = 1.0]
     *   actualDamage     = baseDamage × penetrationRatio × locationMultiplier
     * </pre>
     *
     * <p>Examples:
     * <ul>
     *   <li>Light round (AP 12) vs diamond (DR 36): 12/36 = 0.333 → 4 × 0.333 = 1.33 ♥</li>
     *   <li>Light round vs no armor (DR 0): full 4 ♥</li>
     *   <li>Head shot multiplier applied on top of penetration result.</li>
     * </ul></p>
     *
     * @param baseDamage         Raw damage of the bullet.
     * @param bulletAP           Armor penetration rating of the bullet.
     * @param armorDR            Target's defense rating (0 = unarmored).
     * @param locationMultiplier Hit-location modifier (head ×1.8, torso ×1.0, limb ×0.6).
     * @return Final damage to apply.
     */
    public static float calculateBulletDamage(float baseDamage, int bulletAP,
                                              int armorDR, float locationMultiplier) {
        float penetrationRatio = (armorDR == 0)
            ? 1.0f
            : Math.min(bulletAP / (float) armorDR, 1.0f);
        return baseDamage * penetrationRatio * locationMultiplier;
    }

    /**
     * Convenience overload without a location multiplier (multiplier = 1.0).
     * Used for non-bullet damage types (explosions, vehicle rams, etc.).
     *
     * @param baseDamage Raw damage.
     * @param ap         Armor penetration.
     * @param dr         Target defense rating.
     * @return Final damage to apply.
     */
    public static float calculateDamage(float baseDamage, int ap, int dr) {
        return calculateBulletDamage(baseDamage, ap, dr, 1.0f);
    }

    // ── Hit-location detection ────────────────────────────────────────────────

    /**
     * Determines a hit-location multiplier based on where the ray intersected the
     * target's bounding box.
     *
     * <ul>
     *   <li><b>Head</b>  (×1.8) — hit Y above {@code posY + height × 0.75}</li>
     *   <li><b>Torso</b> (×1.0) — hit Y between 40 % and 75 % of entity height</li>
     *   <li><b>Limbs</b> (×0.6) — hit Y below 40 % of entity height</li>
     * </ul>
     *
     * @param target The entity that was struck.
     * @param hitPos World-space position where the bullet ray hit.
     * @return Location multiplier (0.6, 1.0, or 1.8).
     */
    public static float getLocationMultiplier(Entity target, Vec3d hitPos) {
        double entityBottom = target.posY;
        double entityHeight = target.height;

        double hitRelative = hitPos.y - entityBottom; // 0 = feet, height = top of head

        if (hitRelative > entityHeight * 0.75) {
            return 1.8f; // head
        } else if (hitRelative > entityHeight * 0.40) {
            return 1.0f; // torso
        } else {
            return 0.6f; // limbs / feet
        }
    }
}
