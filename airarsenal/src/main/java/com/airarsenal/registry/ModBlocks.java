package com.airarsenal.registry;

import com.airarsenal.block.artillery.AACannonBlock;
import com.airarsenal.block.artillery.FlakBatteryBlock;
import com.airarsenal.block.artillery.HowitzerBlock;
import com.airarsenal.block.artillery.MLRSBlock;
import com.airarsenal.block.artillery.MortarBlock;
import com.airarsenal.block.artillery.StaticMissileBatteryBlock;
import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.IForgeRegistry;

@ObjectHolder("airarsenal")
@Mod.EventBusSubscriber
public class ModBlocks {

    // ── Chunk 8 — Artillery & AA (first real block registrations) ─────────────
    @ObjectHolder("aa_cannon")
    public static final Block AA_CANNON = null;

    @ObjectHolder("flak_battery")
    public static final Block FLAK_BATTERY = null;

    @ObjectHolder("mortar")
    public static final Block MORTAR = null;

    @ObjectHolder("howitzer")
    public static final Block HOWITZER = null;

    @ObjectHolder("mlrs")
    public static final Block MLRS = null;

    @ObjectHolder("static_missile_battery")
    public static final Block STATIC_MISSILE_BATTERY = null;

    @SubscribeEvent
    public static void register(RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();

        // Chunk 8 — Artillery & AA
        registry.register(new AACannonBlock());
        registry.register(new FlakBatteryBlock());
        registry.register(new MortarBlock());
        registry.register(new HowitzerBlock());
        registry.register(new MLRSBlock());
        registry.register(new StaticMissileBatteryBlock());

        // CHUNK 9+: register further blocks here
    }
}
