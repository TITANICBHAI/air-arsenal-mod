package com.airarsenal.registry;

import com.airarsenal.AirArsenal;
import com.airarsenal.entity.plane.WoodBiplaneEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class ModEntities {

    /**
     * Register all mod entities with Forge.
     * Called during FMLPreInitializationEvent in AirArsenal.java.
     *
     * Entity registration roadmap:
     * CHUNK 2: WoodBiplaneEntity          ← registered below
     * CHUNK 3: PropellerEntity
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
            1,                  // unique entity ID within this mod
            AirArsenal.instance,
            80,                 // tracking range (blocks)
            3,                  // update frequency (ticks)
            true                // send velocity updates
        );
    }
}
