package com.airarsenal;

import com.airarsenal.registry.ModBlocks;
import com.airarsenal.registry.ModEntities;
import com.airarsenal.registry.ModItems;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
    modid = AirArsenal.MODID,
    name  = AirArsenal.MOD_NAME,
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
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        LOGGER.info("Air Arsenal postInit starting");
        proxy.postInit(event);
    }
}
