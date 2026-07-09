package com.airarsenal;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        // Server-side pre-init logic goes here
    }

    public void init(FMLInitializationEvent event) {
        // Server-side init logic goes here
    }

    public void postInit(FMLPostInitializationEvent event) {
        // Server-side post-init logic goes here
    }
}
