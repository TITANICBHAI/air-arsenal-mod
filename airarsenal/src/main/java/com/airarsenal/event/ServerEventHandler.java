package com.airarsenal.event;

import com.airarsenal.AirArsenal;
import com.airarsenal.entity.projectile.EMPWorldData;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Server-side event handler registered via {@code @Mod.EventBusSubscriber}.
 *
 * <p>Current responsibilities:</p>
 * <ul>
 *   <li>Tick the {@link EMPWorldData} every server world tick to expire
 *       EMP-disabled block entries when their duration elapses.</li>
 * </ul>
 */
@Mod.EventBusSubscriber(modid = AirArsenal.MODID, value = Side.SERVER)
public class ServerEventHandler {

    /**
     * Tick EMP world data each server world tick so expired entries are cleaned up.
     * Only runs on the server (Side.SERVER guard on the class).
     */
    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.world instanceof WorldServer)) return;

        EMPWorldData.get(event.world).tickAndCleanup(event.world);
    }
}
