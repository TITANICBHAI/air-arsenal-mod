package com.airarsenal.registry;

import com.airarsenal.AirArsenal;
import com.airarsenal.entity.plane.IronMonoplaneEntity;
import com.airarsenal.entity.plane.WoodBiplaneEntity;
import com.airarsenal.entity.projectile.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class ModEntities {

    /**
     * Entity ID registry — must never change once released (chunk roadmap):
     *
     *  1  WoodBiplaneEntity          Chunk 2
     *  2  PropellerShardEntity       Chunk 3
     *  3  IronMonoplaneEntity        Chunk 4
     *  4  BulletEntity               Chunk 4
     *  5  IronBombEntity             Chunk 6
     *  6  HeavyBombEntity            Chunk 6
     *  7  NapalmBombEntity           Chunk 6
     *  8  ClusterBombEntity          Chunk 6
     *  9  EMPBombEntity              Chunk 6
     * 10  SmokeBombEntity            Chunk 6
     * 11  DepthChargeEntity          Chunk 6
     * 12  FuelAirBombEntity          Chunk 6
     * 13  HellfireEntity             Chunk 7
     * 14  BrahMosEntity              Chunk 7
     * 15  PredatorMissileEntity      Chunk 7
     * 16+ reserved for Chunk 8–10
     */
    public static void register() {

        // ── Chunk 2 ───────────────────────────────────────────────────────────
        EntityRegistry.registerModEntity(
            new ResourceLocation("airarsenal", "wood_biplane"),
            WoodBiplaneEntity.class, "wood_biplane", 1,
            AirArsenal.instance, 80, 3, true);

        // ── Chunk 3 ───────────────────────────────────────────────────────────
        EntityRegistry.registerModEntity(
            new ResourceLocation("airarsenal", "propeller_shard"),
            PropellerShardEntity.class, "propeller_shard", 2,
            AirArsenal.instance, 64, 5, true);

        // ── Chunk 4 ───────────────────────────────────────────────────────────
        EntityRegistry.registerModEntity(
            new ResourceLocation("airarsenal", "iron_monoplane"),
            IronMonoplaneEntity.class, "iron_monoplane", 3,
            AirArsenal.instance, 80, 3, true);

        EntityRegistry.registerModEntity(
            new ResourceLocation("airarsenal", "bullet"),
            BulletEntity.class, "bullet", 4,
            AirArsenal.instance, 64, 2, true);

        // ── Chunk 6 — Bombs ───────────────────────────────────────────────────
        EntityRegistry.registerModEntity(
            new ResourceLocation("airarsenal", "iron_bomb"),
            IronBombEntity.class, "iron_bomb", 5,
            AirArsenal.instance, 64, 3, true);

        EntityRegistry.registerModEntity(
            new ResourceLocation("airarsenal", "heavy_bomb"),
            HeavyBombEntity.class, "heavy_bomb", 6,
            AirArsenal.instance, 64, 3, true);

        EntityRegistry.registerModEntity(
            new ResourceLocation("airarsenal", "napalm_bomb"),
            NapalmBombEntity.class, "napalm_bomb", 7,
            AirArsenal.instance, 64, 3, true);

        EntityRegistry.registerModEntity(
            new ResourceLocation("airarsenal", "cluster_bomb"),
            ClusterBombEntity.class, "cluster_bomb", 8,
            AirArsenal.instance, 64, 3, true);

        EntityRegistry.registerModEntity(
            new ResourceLocation("airarsenal", "emp_bomb"),
            EMPBombEntity.class, "emp_bomb", 9,
            AirArsenal.instance, 64, 3, true);

        EntityRegistry.registerModEntity(
            new ResourceLocation("airarsenal", "smoke_bomb"),
            SmokeBombEntity.class, "smoke_bomb", 10,
            AirArsenal.instance, 64, 3, true);

        EntityRegistry.registerModEntity(
            new ResourceLocation("airarsenal", "depth_charge"),
            DepthChargeEntity.class, "depth_charge", 11,
            AirArsenal.instance, 64, 3, true);

        EntityRegistry.registerModEntity(
            new ResourceLocation("airarsenal", "fuel_air_bomb"),
            FuelAirBombEntity.class, "fuel_air_bomb", 12,
            AirArsenal.instance, 64, 3, true);

        // ── Chunk 7 — Guided Missiles ─────────────────────────────────────────
        EntityRegistry.registerModEntity(
            new ResourceLocation("airarsenal", "hellfire"),
            HellfireEntity.class, "hellfire", 13,
            AirArsenal.instance, 128, 2, true);

        EntityRegistry.registerModEntity(
            new ResourceLocation("airarsenal", "brahmos"),
            BrahMosEntity.class, "brahmos", 14,
            AirArsenal.instance, 256, 2, true);  // long range — high tracking range

        EntityRegistry.registerModEntity(
            new ResourceLocation("airarsenal", "predator_missile"),
            PredatorMissileEntity.class, "predator_missile", 15,
            AirArsenal.instance, 128, 1, true);  // freq=1 for responsive steering
    }
}
