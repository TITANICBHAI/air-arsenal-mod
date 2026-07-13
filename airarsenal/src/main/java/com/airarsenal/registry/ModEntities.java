package com.airarsenal.registry;

import com.airarsenal.AirArsenal;
import com.airarsenal.entity.plane.AttackHelicopterEntity;
import com.airarsenal.entity.plane.FighterJetEntity;
import com.airarsenal.entity.plane.IronMonoplaneEntity;
import com.airarsenal.entity.plane.PredatorDroneEntity;
import com.airarsenal.entity.plane.StealthBomberEntity;
import com.airarsenal.entity.plane.WoodBiplaneEntity;
import com.airarsenal.entity.projectile.*;
import com.airarsenal.entity.vehicle.ArmoredTruckEntity;
import com.airarsenal.entity.vehicle.MissileTruckEntity;
import com.airarsenal.entity.vehicle.TankEntity;
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
     * 16  AAShellEntity              Chunk 8
     * 17  FlakShellEntity            Chunk 8
     * 18  ManpadsEntity              Chunk 8
     * 19  MortarShellEntity          Chunk 8
     * 20  HowitzerShellEntity        Chunk 8
     * 21  MLRSRocketEntity           Chunk 8
     * 22  MissileTruckEntity         Chunk 8
     * 23  TruckGuidedMissileEntity   Chunk 8
     * 24  ArmoredTruckEntity         Chunk 8
     * 25  TankEntity                 Chunk 8
     * 26  TankShellEntity            Chunk 8
     * 27  FighterJetEntity           Chunk 9
     * 28  PredatorDroneEntity        Chunk 9
     * 29  StealthBomberEntity        Chunk 9
     * 30  AttackHelicopterEntity     Chunk 9
     * 31+ reserved for Chunk 10–11
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

        // ── Chunk 8 — Artillery & AA ──────────────────────────────────────────
        EntityRegistry.registerModEntity(
            new ResourceLocation("airarsenal", "aa_shell"),
            AAShellEntity.class, "aa_shell", 16,
            AirArsenal.instance, 128, 2, true);

        EntityRegistry.registerModEntity(
            new ResourceLocation("airarsenal", "flak_shell"),
            FlakShellEntity.class, "flak_shell", 17,
            AirArsenal.instance, 128, 2, true);

        EntityRegistry.registerModEntity(
            new ResourceLocation("airarsenal", "manpads_missile"),
            ManpadsEntity.class, "manpads_missile", 18,
            AirArsenal.instance, 128, 1, true);  // freq=1 for guidance responsiveness

        EntityRegistry.registerModEntity(
            new ResourceLocation("airarsenal", "mortar_shell"),
            MortarShellEntity.class, "mortar_shell", 19,
            AirArsenal.instance, 128, 3, true);

        EntityRegistry.registerModEntity(
            new ResourceLocation("airarsenal", "howitzer_shell"),
            HowitzerShellEntity.class, "howitzer_shell", 20,
            AirArsenal.instance, 160, 2, true);

        EntityRegistry.registerModEntity(
            new ResourceLocation("airarsenal", "mlrs_rocket"),
            MLRSRocketEntity.class, "mlrs_rocket", 21,
            AirArsenal.instance, 128, 3, true);

        EntityRegistry.registerModEntity(
            new ResourceLocation("airarsenal", "missile_truck"),
            MissileTruckEntity.class, "missile_truck", 22,
            AirArsenal.instance, 80, 3, true);

        EntityRegistry.registerModEntity(
            new ResourceLocation("airarsenal", "truck_guided_missile"),
            TruckGuidedMissileEntity.class, "truck_guided_missile", 23,
            AirArsenal.instance, 160, 1, true);  // freq=1 for guidance responsiveness

        EntityRegistry.registerModEntity(
            new ResourceLocation("airarsenal", "armored_truck"),
            ArmoredTruckEntity.class, "armored_truck", 24,
            AirArsenal.instance, 80, 3, true);

        EntityRegistry.registerModEntity(
            new ResourceLocation("airarsenal", "tank"),
            TankEntity.class, "tank", 25,
            AirArsenal.instance, 80, 3, true);

        EntityRegistry.registerModEntity(
            new ResourceLocation("airarsenal", "tank_shell"),
            TankShellEntity.class, "tank_shell", 26,
            AirArsenal.instance, 128, 3, true);

        // ── Chunk 9 — Advanced Planes & Fuel System ─────────────────────────────
        EntityRegistry.registerModEntity(
            new ResourceLocation("airarsenal", "fighter_jet"),
            FighterJetEntity.class, "fighter_jet", 27,
            AirArsenal.instance, 80, 3, true);

        EntityRegistry.registerModEntity(
            new ResourceLocation("airarsenal", "predator_drone"),
            PredatorDroneEntity.class, "predator_drone", 28,
            AirArsenal.instance, 80, 3, true);

        EntityRegistry.registerModEntity(
            new ResourceLocation("airarsenal", "stealth_bomber"),
            StealthBomberEntity.class, "stealth_bomber", 29,
            AirArsenal.instance, 80, 3, true);

        EntityRegistry.registerModEntity(
            new ResourceLocation("airarsenal", "attack_helicopter"),
            AttackHelicopterEntity.class, "attack_helicopter", 30,
            AirArsenal.instance, 80, 3, true);
    }
}
