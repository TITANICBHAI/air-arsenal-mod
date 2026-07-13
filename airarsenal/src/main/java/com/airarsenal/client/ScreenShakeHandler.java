package com.airarsenal.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Client-only singleton driving camera shake and screen flash for the Orbital
 * Cannon's impact feedback (and any future general-purpose use).
 *
 * <p>Registered on {@code MinecraftForge.EVENT_BUS} from {@link com.airarsenal.ClientProxy#init}
 * only — never referenced on a dedicated server, since {@link EntityViewRenderEvent} is a
 * client-only class.</p>
 */
@SideOnly(Side.CLIENT)
public class ScreenShakeHandler {

    private static float intensity;
    private static int durationTicks;
    private static int remainingTicks;

    /** 1-tick full-white flash flag, consumed by an overlay render hook. */
    private static int flashTicks;

    public static void trigger(float newIntensity, int newDurationTicks) {
        intensity = newIntensity;
        durationTicks = newDurationTicks;
        remainingTicks = newDurationTicks;
    }

    public static void triggerFlash() {
        flashTicks = 1;
    }

    public static boolean isFlashing() {
        boolean flashing = flashTicks > 0;
        if (flashTicks > 0) flashTicks--;
        return flashing;
    }

    @SubscribeEvent
    public void onCameraSetup(EntityViewRenderEvent.CameraSetup event) {
        if (remainingTicks > 0) {
            float t = (float) remainingTicks / durationTicks;
            net.minecraft.world.World world = Minecraft.getMinecraft().world;
            float rand = world != null ? world.rand.nextFloat() : (float) Math.random();
            float offset = intensity * t * (rand - 0.5f) * 2f;
            event.setPitch((float) (event.getPitch() + offset));
            event.setYaw((float) (event.getYaw() + offset * 0.5f));
            remainingTicks--;
        }
    }
}
