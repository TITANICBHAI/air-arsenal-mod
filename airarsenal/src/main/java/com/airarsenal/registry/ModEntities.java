package com.airarsenal.registry;

import com.airarsenal.AirArsenal;
import com.airarsenal.entity.plane.IronMonoplaneEntity;
import com.airarsenal.entity.plane.WoodBiplaneEntity;
import com.airarsenal.entity.projectile.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class ModEntities {

    /**
     * Register all mod entities with Forge.
     * Called during FMLPreInitializationEvent in AirArsenal.java.
     *
     * CHUNK 2:  WoodBiplaneEntity          ID 1
     * CHUNK 3:  PropellerShardEntity        ID 2
     * CHUNK 4:  IronMonoplaneEntity, BulletEntity   IDs 3–4
     * CHUNK 6:  8 × bomb entities           IDs 5–12
     * CHUNK 7:  HellfireEntity, PredatorStrikeEntity
     * CHUNK 9:  FighterJetEntity, PredatorDroneEntity, StealthBomberEntity
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
    }
}
