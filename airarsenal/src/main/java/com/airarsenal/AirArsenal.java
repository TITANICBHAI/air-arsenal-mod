package com.airarsenal;

import com.airarsenal.registry.ModEntities;
import com.airarsenal.registry.ModItems;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
    modid   = AirArsenal.MODID,
    name    = AirArsenal.MOD_NAME,
    version = AirArsenal.VERSION,
    acceptedMinecraftVersions = "[1.12.2]"
)
public class AirArsenal {

    public static final String MODID    = "airarsenal";
    public static final String MOD_NAME = "Air Arsenal";
    public static final String VERSION  = "1.0.0";

    public static final Logger LOGGER = LogManager.getLogger(MODID);

    @Instance(MODID)
    public static AirArsenal instance;

    @SidedProxy(
        clientSide = "com.airarsenal.ClientProxy",
        serverSide = "com.airarsenal.CommonProxy"
    )
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER.info("Air Arsenal preInit starting");
        proxy.preInit(event);
        ModEntities.register();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        LOGGER.info("Air Arsenal init starting");
        proxy.init(event);
        registerRecipes();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        LOGGER.info("Air Arsenal postInit starting");
        proxy.postInit(event);
    }

    // ── Crafting recipes ──────────────────────────────────────────────────────

    private void registerRecipes() {

        // ── Wood Biplane ──────────────────────────────────────────────────────
        // [ ]  [S]  [ ]
        // [P]  [P]  [P]
        // [~]  [P]  [~]
        // P = Oak Plank, S = Stick, ~ = String
        GameRegistry.addShapedRecipe(
            new ResourceLocation(MODID, "wood_biplane"),
            null,
            new ItemStack(ModItems.WOOD_BIPLANE),
            " S ",
            "PPP",
            "~P~",
            'P', new ItemStack(Blocks.PLANKS, 1, 0),
            'S', new ItemStack(Items.STICK),
            '~', new ItemStack(Items.STRING)
        );

        // ── Iron Monoplane ────────────────────────────────────────────────────
        // [ ]  [G]  [ ]
        // [I]  [I]  [I]
        // [L]  [I]  [L]
        // I = Iron Ingot, G = Glass Pane, L = Leather
        GameRegistry.addShapedRecipe(
            new ResourceLocation(MODID, "iron_monoplane"),
            null,
            new ItemStack(ModItems.IRON_MONOPLANE),
            " G ",
            "III",
            "LIL",
            'I', new ItemStack(Items.IRON_INGOT),
            'G', new ItemStack(Blocks.GLASS_PANE),
            'L', new ItemStack(Items.LEATHER)
        );

        // CHUNK 9+: PropellerFighter, FighterJet, StealthBomber recipes here
    }
}
