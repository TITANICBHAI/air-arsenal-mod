package com.airarsenal;

import com.airarsenal.registry.ModGuiHandler;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(AirArsenal.instance, new ModGuiHandler());
    }

    public void init(FMLInitializationEvent event) {}

    public void postInit(FMLPostInitializationEvent event) {}

    // ── Propeller particle stubs (no-op on server) ────────────────────────────

    /**
     * Spawns CRIT-style spark particles in a spin pattern around the propeller nose.
     * No-op on the server side; overridden in {@link ClientProxy}.
     */
    public void spawnPropellerSparks(World world, double x, double y, double z) {}

    /**
     * Spawns large smoke particles rising from the propeller nose.
     * No-op on the server side; overridden in {@link ClientProxy}.
     */
    public void spawnPropellerSmoke(World world, double x, double y, double z) {}

    /**
     * Spawns flame particles intermittently at the propeller nose.
     * No-op on the server side; overridden in {@link ClientProxy}.
     */
    public void spawnPropellerFire(World world, double x, double y, double z) {}
}
