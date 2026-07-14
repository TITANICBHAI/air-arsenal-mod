package com.airarsenal.event;

import com.airarsenal.AirArsenal;
import com.airarsenal.registry.ModItems;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.SetNBT;
import net.minecraft.world.storage.loot.RandomValueRange;

/**
 * Chunk 10 — injects Air Arsenal items and spawn eggs into vanilla loot tables.
 *
 * <p>Forge 1.12.2 has no {@code data/} datapack format (that's 1.16+), so custom
 * loot injection is done here by listening for {@link LootTableLoadEvent} and adding
 * a new {@link LootPool} to the vanilla table as it loads. Pools use no kill
 * condition — weight alone controls rarity, matching every other vanilla pool.</p>
 */
@Mod.EventBusSubscriber(modid = AirArsenal.MODID)
public class LootTableHandler {

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        String name = event.getName().toString();

        if (name.equals(LootTableList.CHESTS_END_CITY_TREASURE.toString())) {
            event.getTable().addPool(buildEggPool("fighter_jet", 1));
            event.getTable().addPool(buildItemPool(ModItems.BRAHMOS_TARGETER, 2, "brahmos_targeter"));
        } else if (name.equals(LootTableList.CHESTS_WOODLAND_MANSION.toString())) {
            event.getTable().addPool(buildEggPool("predator_drone", 2));
            event.getTable().addPool(buildItemPool(ModItems.DRONE_CONTROLLER, 1, "drone_controller"));
        } else if (name.equals(LootTableList.CHESTS_STRONGHOLD_LIBRARY.toString())) {
            event.getTable().addPool(buildEggPool("stealth_bomber", 1));
        } else if (name.equals(LootTableList.CHESTS_DESERT_PYRAMID.toString())) {
            event.getTable().addPool(buildItemPool(ModItems.MANPADS, 3, "manpads"));
        } else if (name.equals(LootTableList.CHESTS_ABANDONED_MINESHAFT.toString())) {
            event.getTable().addPool(buildEggPool("armored_truck", 3));
        } else if (name.equals(LootTableList.CHESTS_VILLAGE_BLACKSMITH.toString())) {
            event.getTable().addPool(buildEggPool("armored_truck", 2));
        } else if (name.equals(LootTableList.CHESTS_STRONGHOLD_CORRIDOR.toString())) {
            event.getTable().addPool(buildEggPool("tank", 1));
        } else if (name.equals(LootTableList.CHESTS_NETHER_BRIDGE.toString())) {
            event.getTable().addPool(buildEggPool("tank", 2));
        }
    }

    /** Builds a single-entry pool dropping a plain Air Arsenal item. */
    private static LootPool buildItemPool(Item item, int weight, String poolId) {
        LootEntryItem entry = new LootEntryItem(item, weight, 0,
            new LootFunction[0], new LootCondition[0], "airarsenal_" + poolId);
        return new LootPool(new LootEntry[]{ entry }, new LootCondition[0],
            new RandomValueRange(1), new RandomValueRange(0), "airarsenal_pool_" + poolId);
    }

    /**
     * Builds a single-entry pool dropping the vanilla spawn egg item pre-tagged
     * with {@code EntityTag.id} so it summons the given Air Arsenal entity —
     * the same mechanism vanilla spawn eggs use internally.
     */
    private static LootPool buildEggPool(String entityName, int weight) {
        NBTTagCompound entityTag = new NBTTagCompound();
        entityTag.setString("id", AirArsenal.MODID + ":" + entityName);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setTag("EntityTag", entityTag);

        LootFunction setNbt = new SetNBT(new LootCondition[0], tag);
        LootEntryItem entry = new LootEntryItem(Items.SPAWN_EGG, weight, 0,
            new LootFunction[]{ setNbt }, new LootCondition[0], "airarsenal_egg_" + entityName);
        return new LootPool(new LootEntry[]{ entry }, new LootCondition[0],
            new RandomValueRange(1), new RandomValueRange(0), "airarsenal_egg_pool_" + entityName);
    }
}
