package com.airarsenal;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        // Client-side pre-init: register renderers, models, etc.
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        // Client-side init: bind textures, key bindings, etc.
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
        // Client-side post-init logic goes here
    }
}
