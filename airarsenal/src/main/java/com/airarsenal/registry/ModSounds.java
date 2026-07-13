package com.airarsenal.registry;

import com.airarsenal.AirArsenal;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * Sound event registry (Chunk 10).
 *
 * <p>Every event here has a matching entry in {@code sounds.json}. Where no dedicated
 * {@code .ogg} file exists yet, the {@code sounds.json} entry points at a vanilla sound
 * file so the game has something to actually play — see the class javadoc on
 * {@code sounds.json} itself. Swap in real files later by dropping them into
 * {@code assets/airarsenal/sounds/} and updating the matching {@code sounds.json} entry;
 * nothing here needs to change.</p>
 */
@Mod.EventBusSubscriber
public class ModSounds {

    // ── Plane engines ─────────────────────────────────────────────────────────
    public static final SoundEvent PLANE_ENGINE_BIPLANE    = create("plane.engine.biplane");
    public static final SoundEvent PLANE_ENGINE_MONOPLANE  = create("plane.engine.monoplane");
    public static final SoundEvent PLANE_ENGINE_JET        = create("plane.engine.jet");
    public static final SoundEvent PLANE_ENGINE_HELICOPTER = create("plane.engine.helicopter");

    // ── Propeller ─────────────────────────────────────────────────────────────
    public static final SoundEvent PLANE_PROPELLER_DAMAGE    = create("plane.propeller.damage");
    public static final SoundEvent PLANE_PROPELLER_DESTROYED = create("plane.propeller.destroyed");

    // ── Weapons ───────────────────────────────────────────────────────────────
    public static final SoundEvent WEAPON_MACHINEGUN_FIRE = create("weapon.machinegun.fire");
    public static final SoundEvent WEAPON_CANNON_FIRE     = create("weapon.cannon.fire");
    public static final SoundEvent WEAPON_MISSILE_LAUNCH  = create("weapon.missile.launch");
    public static final SoundEvent WEAPON_MISSILE_GUIDANCE = create("weapon.missile.guidance");
    public static final SoundEvent WEAPON_BOMB_DROP   = create("weapon.bomb.drop");
    public static final SoundEvent WEAPON_BOMB_NAPALM = create("weapon.bomb.napalm");
    public static final SoundEvent WEAPON_BOMB_EMP    = create("weapon.bomb.emp");

    // ── Artillery ─────────────────────────────────────────────────────────────
    public static final SoundEvent ARTILLERY_AAGUN_FIRE  = create("artillery.aagun.fire");
    public static final SoundEvent ARTILLERY_FLAK_BURST  = create("artillery.flak.burst");

    private static SoundEvent create(String path) {
        ResourceLocation id = new ResourceLocation(AirArsenal.MODID, path);
        return new SoundEvent(id).setRegistryName(id);
    }

    @SubscribeEvent
    public static void register(RegistryEvent.Register<SoundEvent> event) {
        IForgeRegistry<SoundEvent> registry = event.getRegistry();

        registry.register(PLANE_ENGINE_BIPLANE);
        registry.register(PLANE_ENGINE_MONOPLANE);
        registry.register(PLANE_ENGINE_JET);
        registry.register(PLANE_ENGINE_HELICOPTER);

        registry.register(PLANE_PROPELLER_DAMAGE);
        registry.register(PLANE_PROPELLER_DESTROYED);

        registry.register(WEAPON_MACHINEGUN_FIRE);
        registry.register(WEAPON_CANNON_FIRE);
        registry.register(WEAPON_MISSILE_LAUNCH);
        registry.register(WEAPON_MISSILE_GUIDANCE);
        registry.register(WEAPON_BOMB_DROP);
        registry.register(WEAPON_BOMB_NAPALM);
        registry.register(WEAPON_BOMB_EMP);

        registry.register(ARTILLERY_AAGUN_FIRE);
        registry.register(ARTILLERY_FLAK_BURST);
    }
}
