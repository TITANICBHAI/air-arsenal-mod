package com.airarsenal.registry;

import com.airarsenal.item.ItemWoodBiplane;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.IForgeRegistry;

@ObjectHolder("airarsenal")
@Mod.EventBusSubscriber
public class ModItems {

    /** Populated automatically by Forge's @ObjectHolder after registration. */
    @ObjectHolder("wood_biplane")
    public static final Item WOOD_BIPLANE = null;

    @SubscribeEvent
    public static void register(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();

        registry.register(new ItemWoodBiplane());

        // CHUNK 4+: register plane items and ammo items here
    }
}
