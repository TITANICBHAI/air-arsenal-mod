package com.airarsenal.registry;

import com.airarsenal.AirArsenal;
import com.airarsenal.entity.plane.WoodBiplaneEntity;
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
     * CHUNK 4: IronMonoplaneEntity, BulletEntity
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
            1,                   // unique entity ID within this mod
            AirArsenal.instance,
            80,                  // tracking range (blocks)
            3,                   // update frequency (ticks)
            true                 // send velocity updates
        );

        // ── Chunk 3 ───────────────────────────────────────────────────────────
        EntityRegistry.registerModEntity(
            new ResourceLocation("airarsenal", "propeller_shard"),
            PropellerShardEntity.class,
            "propeller_shard",
            2,
            AirArsenal.instance,
            64,                  // tracking range — smaller; shard is short-lived
            5,
            true
        );
    }
}
