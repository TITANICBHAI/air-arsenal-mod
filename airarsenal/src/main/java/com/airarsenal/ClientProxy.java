package com.airarsenal;

import com.airarsenal.block.OrbitalCannonTileEntity;
import com.airarsenal.client.KeyBindings;
import com.airarsenal.client.ScreenShakeHandler;
import com.airarsenal.client.TacModeController;
import com.airarsenal.client.gui.ManpadsHUD;
import com.airarsenal.client.gui.TacModeHUD;
import com.airarsenal.client.renderer.entity.OrbitalRodRenderer;
import com.airarsenal.client.renderer.tileentity.OrbitalCannonTESR;
import com.airarsenal.entity.projectile.OrbitalRodEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends CommonProxy {

    // Shared instance so HUD and Controller share weapon-index state
    private TacModeController tacModeController;

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        KeyBindings.register();
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);

        tacModeController = new TacModeController();
        MinecraftForge.EVENT_BUS.register(tacModeController);
        MinecraftForge.EVENT_BUS.register(new TacModeHUD(tacModeController));
        MinecraftForge.EVENT_BUS.register(new ManpadsHUD());

        // Register config-changed listener here (client only) — ConfigChangedEvent
        // is a client-only class and must NOT be referenced on a dedicated server.
        MinecraftForge.EVENT_BUS.register(this);

        // ── Chunk 11 — Orbital Cannon ────────────────────────────────────────
        MinecraftForge.EVENT_BUS.register(new ScreenShakeHandler());
        ClientRegistry.bindTileEntitySpecialRenderer(OrbitalCannonTileEntity.class, new OrbitalCannonTESR());
        RenderingRegistry.registerEntityRenderingHandler(OrbitalRodEntity.class, OrbitalRodRenderer::new);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
    }

    /** Re-syncs config when the in-game GUI saves changes. Client side only. */
    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(AirArsenal.MODID)) {
            ConfigManager.sync(AirArsenal.MODID, net.minecraftforge.common.config.Config.Type.INSTANCE);
        }
    }

    // ── Propeller particle effects ────────────────────────────────────────────

    @Override
    public void spawnPropellerSparks(World world, double x, double y, double z) {
        if (!world.isRemote) return;
        int count = 4;
        for (int i = 0; i < count; i++) {
            double angle  = (2 * Math.PI / count) * i
                          + (System.currentTimeMillis() % 1000) * 0.006283;
            double radius = 0.3;
            double ox     = Math.cos(angle) * radius;
            double oz     = Math.sin(angle) * radius;
            world.spawnParticle(EnumParticleTypes.CRIT,
                x + ox, y, z + oz,
                ox * 0.05, 0.02, oz * 0.05);
        }
    }

    @Override
    public void spawnPropellerSmoke(World world, double x, double y, double z) {
        if (!world.isRemote) return;
        for (int i = 0; i < 3; i++) {
            double ox = (world.rand.nextDouble() - 0.5) * 0.4;
            double oz = (world.rand.nextDouble() - 0.5) * 0.4;
            world.spawnParticle(EnumParticleTypes.SMOKE_LARGE,
                x + ox, y, z + oz, 0, 0.06, 0);
        }
    }

    @Override
    public void spawnPropellerFire(World world, double x, double y, double z) {
        if (!world.isRemote) return;
        if (world.rand.nextBoolean()) {
            for (int i = 0; i < 2; i++) {
                double ox = (world.rand.nextDouble() - 0.5) * 0.3;
                double oz = (world.rand.nextDouble() - 0.5) * 0.3;
                world.spawnParticle(EnumParticleTypes.FLAME,
                    x + ox, y + 0.1, z + oz, 0, 0.04, 0);
            }
        }
    }

    // ── Chunk 9 particle effects ───────────────────────────────────────────────

    @Override
    public void spawnSonicBoomRing(World world, double x, double y, double z) {
        if (!world.isRemote) return;
        int count = 16;
        double radius = 2.5;
        for (int i = 0; i < count; i++) {
            double angle = (2 * Math.PI / count) * i;
            double ox = Math.cos(angle) * radius;
            double oz = Math.sin(angle) * radius;
            world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL,
                x + ox, y, z + oz, 0, 0, 0);
        }
    }

    @Override
    public void spawnJetEngineFire(World world, double x, double y, double z) {
        if (!world.isRemote) return;
        world.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, x, y, z, 0, 0, 0);
        for (int i = 0; i < 6; i++) {
            double ox = (world.rand.nextDouble() - 0.5) * 0.6;
            double oz = (world.rand.nextDouble() - 0.5) * 0.6;
            world.spawnParticle(EnumParticleTypes.FLAME,
                x + ox, y + 0.2, z + oz, 0, 0.05, 0);
        }
    }

    // ── Chunk 10 particle polish ───────────────────────────────────────────────

    @Override
    public void spawnJetExhaust(World world, double x, double y, double z) {
        if (!world.isRemote) return;
        world.spawnParticle(EnumParticleTypes.FLAME, x, y, z, 0, 0, 0);
        world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL,
            x, y, z,
            (world.rand.nextDouble() - 0.5) * 0.05, 0.01, (world.rand.nextDouble() - 0.5) * 0.05);
    }

    @Override
    public void spawnRotorDownwash(World world, double x, double y, double z) {
        if (!world.isRemote) return;
        int count = 10;
        double radius = 1.8;
        for (int i = 0; i < count; i++) {
            double angle = (2 * Math.PI / count) * i
                         + (System.currentTimeMillis() % 1000) * 0.006283;
            double ox = Math.cos(angle) * radius;
            double oz = Math.sin(angle) * radius;
            world.spawnParticle(EnumParticleTypes.CLOUD,
                x + ox, y, z + oz, 0, -0.02, 0);
        }
    }

    @Override
    public void spawnMissileTrail(World world, double x, double y, double z) {
        if (!world.isRemote) return;
        world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, x, y, z, 0, 0, 0);
        world.spawnParticle(EnumParticleTypes.FLAME, x, y, z, 0, 0, 0);
    }

    @Override
    public void spawnBrahMosTrailBurst(World world, double x, double y, double z) {
        if (!world.isRemote) return;
        world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, x, y, z, 0, 0, 0);
    }
}
