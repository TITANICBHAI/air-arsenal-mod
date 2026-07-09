package com.airarsenal.registry;

import com.airarsenal.item.*;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.IForgeRegistry;

@ObjectHolder("airarsenal")
@Mod.EventBusSubscriber
public class ModItems {

    // ── Chunk 2 ───────────────────────────────────────────────────────────────
    @ObjectHolder("wood_biplane")
    public static final Item WOOD_BIPLANE = null;

    // ── Chunk 4 ───────────────────────────────────────────────────────────────
    @ObjectHolder("iron_monoplane")
    public static final Item IRON_MONOPLANE = null;

    // ── Chunk 6 ───────────────────────────────────────────────────────────────
    @ObjectHolder("iron_bomb")
    public static final Item IRON_BOMB = null;

    @ObjectHolder("heavy_bomb")
    public static final Item HEAVY_BOMB = null;

    @ObjectHolder("napalm_canister")
    public static final Item NAPALM_CANISTER = null;

    @SubscribeEvent
    public static void register(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();

        // Chunk 2
        registry.register(new ItemWoodBiplane());

        // Chunk 4
        registry.register(new ItemIronMonoplane());

        // Chunk 6
        registry.register(new ItemIronBomb());
        registry.register(new ItemHeavyBomb());
        registry.register(new ItemNapalmCanister());

        // CHUNK 9+: PropellerFighter, FighterJet, StealthBomber spawn items
    }
}
