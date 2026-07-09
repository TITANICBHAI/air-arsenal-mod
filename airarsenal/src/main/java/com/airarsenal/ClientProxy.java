package com.airarsenal;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        // Register renderers and model loaders here (Chunk 10)
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        // Bind textures, key bindings here (Chunk 5)
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
    }

    // ── Propeller particle effects ────────────────────────────────────────────
    // All methods guard with world.isRemote — they must never be called server-side.

    /**
     * DAMAGED state: small CRIT sparks in a spinning ring around the prop disc.
     */
    @Override
    public void spawnPropellerSparks(World world, double x, double y, double z) {
        if (!world.isRemote) return;
        int count = 4;
        for (int i = 0; i < count; i++) {
            double angle  = (2 * Math.PI / count) * i
                          + (System.currentTimeMillis() % 1000) * 0.006283; // slow spin offset
            double radius = 0.3;
            double ox     = Math.cos(angle) * radius;
            double oz     = Math.sin(angle) * radius;
            world.spawnParticle(EnumParticleTypes.CRIT,
                x + ox, y, z + oz,
                ox * 0.05, 0.02, oz * 0.05
            );
        }
    }

    /**
     * HEAVY_DAMAGE state: large smoke columns rising from the prop.
     */
    @Override
    public void spawnPropellerSmoke(World world, double x, double y, double z) {
        if (!world.isRemote) return;
        for (int i = 0; i < 3; i++) {
            double ox = (world.rand.nextDouble() - 0.5) * 0.4;
            double oz = (world.rand.nextDouble() - 0.5) * 0.4;
            world.spawnParticle(EnumParticleTypes.SMOKE_LARGE,
                x + ox, y, z + oz,
                0, 0.06, 0
            );
        }
    }

    /**
     * CRITICAL state: intermittent flame particles at the prop nose.
     */
    @Override
    public void spawnPropellerFire(World world, double x, double y, double z) {
        if (!world.isRemote) return;
        // Only spawn flame ~50% of ticks to give an intermittent flicker
        if (world.rand.nextBoolean()) {
            for (int i = 0; i < 2; i++) {
                double ox = (world.rand.nextDouble() - 0.5) * 0.3;
                double oz = (world.rand.nextDouble() - 0.5) * 0.3;
                world.spawnParticle(EnumParticleTypes.FLAME,
                    x + ox, y + 0.1, z + oz,
                    0, 0.04, 0
                );
            }
        }
    }
}
