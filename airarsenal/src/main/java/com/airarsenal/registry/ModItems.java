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

    // ── Chunk 7 ───────────────────────────────────────────────────────────────
    @ObjectHolder("brahmos_targeter")
    public static final Item BRAHMOS_TARGETER = null;

    // ── Chunk 8 ───────────────────────────────────────────────────────────────
    @ObjectHolder("manpads")
    public static final Item MANPADS = null;

    @ObjectHolder("mortar_shell")
    public static final Item MORTAR_SHELL = null;

    @ObjectHolder("laser_designator")
    public static final Item LASER_DESIGNATOR = null;

    @ObjectHolder("rocket_pod")
    public static final Item ROCKET_POD = null;

    @ObjectHolder("heavy_round")
    public static final Item HEAVY_ROUND = null;

    // ── Chunk 9 ───────────────────────────────────────────────────────────────
    @ObjectHolder("jet_fuel")
    public static final Item JET_FUEL = null;

    @ObjectHolder("drone_controller")
    public static final Item DRONE_CONTROLLER = null;

    // ── Chunk 11 — Orbital Cannon ────────────────────────────────────────────
    @ObjectHolder("orbital_designator")
    public static final Item ORBITAL_DESIGNATOR = null;

    @ObjectHolder("satellite_uplink_card")
    public static final Item SATELLITE_UPLINK_CARD = null;

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

        // Chunk 7 — was previously missing from registration; fixed here.
        registry.register(new ItemBrahMosTargeter());

        // Chunk 8 — Artillery & AA
        registry.register(new ItemMANPADS());
        registry.register(new ItemMortarShell());
        registry.register(new ItemLaserDesignator());
        registry.register(new ItemRocketPod());
        registry.register(new ItemHeavyRound());

        // Chunk 9 — Fuel system & Predator Drone remote pilot
        registry.register(new ItemJetFuel());
        registry.register(new ItemDroneController());

        // Chunk 11 — Orbital Cannon
        registry.register(new ItemOrbitalDesignator());
        registry.register(new ItemSatelliteUplinkCard());
    }
}
