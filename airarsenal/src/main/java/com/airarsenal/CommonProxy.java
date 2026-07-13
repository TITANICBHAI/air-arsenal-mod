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

    // ── Chunk 9 particle stubs (no-op on server) ──────────────────────────────

    /**
     * Spawns a ring of sonic-boom particles around a Fighter Jet exceeding 160 b/s.
     * No-op on the server side; overridden in {@link ClientProxy}.
     */
    public void spawnSonicBoomRing(World world, double x, double y, double z) {}

    /**
     * Spawns fire and a large explosion particle at a destroyed jet engine
     * (Fighter Jet / Predator Drone). No-op on the server side; overridden in
     * {@link ClientProxy}.
     */
    public void spawnJetEngineFire(World world, double x, double y, double z) {}

    // ── Chunk 10 particle polish stubs (no-op on server) ──────────────────────

    /**
     * Spawns flame + smoke from a jet's exhaust position each tick while moving
     * (Fighter Jet, Stealth Bomber). No-op on the server side; overridden in
     * {@link ClientProxy}.
     */
    public void spawnJetExhaust(World world, double x, double y, double z) {}

    /**
     * Spawns a disk of downwash cloud particles below a hovering helicopter's
     * rotor. No-op on the server side; overridden in {@link ClientProxy}.
     */
    public void spawnRotorDownwash(World world, double x, double y, double z) {}

    /**
     * Spawns smoke + flame at a missile's previous tick position, forming a
     * trail. No-op on the server side; overridden in {@link ClientProxy}.
     */
    public void spawnMissileTrail(World world, double x, double y, double z) {}

    /**
     * BrahMos-specific: spawns an explosion-particle arc along the trail every
     * 5 ticks. No-op on the server side; overridden in {@link ClientProxy}.
     */
    public void spawnBrahMosTrailBurst(World world, double x, double y, double z) {}
}
