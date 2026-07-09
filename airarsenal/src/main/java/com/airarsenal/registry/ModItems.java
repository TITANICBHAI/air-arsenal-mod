package com.airarsenal.registry;

import com.airarsenal.item.ItemIronMonoplane;
import com.airarsenal.item.ItemWoodBiplane;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.IForgeRegistry;

@ObjectHolder("airarsenal")
@Mod.EventBusSubscriber
public class ModItems {

    /** Populated automatically by Forge's @ObjectHolder after registration. */
    @ObjectHolder("wood_biplane")
    public static final Item WOOD_BIPLANE = null;

    @ObjectHolder("iron_monoplane")
    public static final Item IRON_MONOPLANE = null;

    @SubscribeEvent
    public static void register(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();

        // ── Chunk 2 ───────────────────────────────────────────────────────────
        registry.register(new ItemWoodBiplane());

        // ── Chunk 4 ───────────────────────────────────────────────────────────
        registry.register(new ItemIronMonoplane());

        // CHUNK 9+: register PropellerFighter, FighterJet, etc. here
    }
}
