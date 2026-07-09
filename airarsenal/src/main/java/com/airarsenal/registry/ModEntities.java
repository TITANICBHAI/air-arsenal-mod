package com.airarsenal.registry;

import com.airarsenal.AirArsenal;
import com.airarsenal.entity.plane.IronMonoplaneEntity;
import com.airarsenal.entity.plane.WoodBiplaneEntity;
import com.airarsenal.entity.projectile.BulletEntity;
import com.airarsenal.entity.projectile.PropellerShardEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class ModEntities {

    /**
     * Register all mod entities with Forge.
     * Called during FMLPreInitializationEvent in AirArsenal.java.
     *
     * Entity registration roadmap:
     * CHUNK 2: WoodBiplaneEntity          ← registered
     * CHUNK 3: PropellerShardEntity       ← registered
     * CHUNK 4: IronMonoplaneEntity, BulletEntity  ← registered
     * CHUNK 6: BombEntity
     * CHUNK 7: HellfireEntity, PredatorStrikeEntity
     * CHUNK 9: FighterJetEntity, PredatorDroneEntity, StealthBomberEntity
     */
    public static void register() {

        // ── Chunk 2 ───────────────────────────────────────────────────────────
        EntityRegistry.registerModEntity(
            new ResourceLocation("airarsenal", "wood_biplane"),
            WoodBiplaneEntity.class,
            "wood_biplane",
            1,
            AirArsenal.instance,
            80, 3, true
        );

        // ── Chunk 3 ───────────────────────────────────────────────────────────
        EntityRegistry.registerModEntity(
            new ResourceLocation("airarsenal", "propeller_shard"),
            PropellerShardEntity.class,
            "propeller_shard",
            2,
            AirArsenal.instance,
            64, 5, true
        );

        // ── Chunk 4 ───────────────────────────────────────────────────────────
        EntityRegistry.registerModEntity(
            new ResourceLocation("airarsenal", "iron_monoplane"),
            IronMonoplaneEntity.class,
            "iron_monoplane",
            3,
            AirArsenal.instance,
            80, 3, true
        );

        EntityRegistry.registerModEntity(
            new ResourceLocation("airarsenal", "bullet"),
            BulletEntity.class,
            "bullet",
            4,
            AirArsenal.instance,
            64, 2, true  // high update frequency — bullets move fast
        );
    }
}
