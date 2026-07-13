# Air Arsenal

Minecraft 1.12.2 Forge mod (Java) adding planes, guided missiles, bombs, and ground/anti-air artillery for tactical aerial combat. The mod's source lives in `airarsenal/` (its own Gradle project); see `airarsenal/README.md` for build/compile instructions.

## Run & Operate

- The mod is not a Replit-hosted web app — it's a Gradle/Forge project meant to be compiled into a JAR and run in a Minecraft client/server.
- Build requires JDK 8 (Forge 1.12.2 constraint) and a `./gradlew setupDecompWorkspace && ./gradlew build` pass — see `airarsenal/README.md`.
- The `artifacts/`, `lib/`, `pnpm-workspace.yaml`, etc. at the repo root are leftover Replit import scaffolding, unrelated to the mod's actual code or stack.

## Stack

- Java 8, Gradle (via wrapper), ForgeGradle, Minecraft Forge 1.12.2-14.23.5.2860

## Where things live

- `airarsenal/src/main/java/com/airarsenal/` — mod source
  - `entity/plane/` — flyable aircraft (Wood Biplane, Iron Monoplane, Fighter Jet, Stealth Bomber, Predator Drone, Attack Helicopter)
  - `entity/vehicle/` — ground vehicles (Missile Truck, Armored Truck, Tank)
  - `entity/projectile/` — bombs, shells, guided missiles
  - `block/artillery/` — static artillery (AA Cannon, Flak Battery, Howitzer, Mortar, MLRS, Static Missile Battery)
  - `combat/weapon/` — plane hardpoint weapons
  - `registry/` — item/entity/sound registration
  - `event/` — Forge event bus subscribers (loot table injection, server tick)
- `attached_assets/` — the original chunked build spec (Chunk 1–11) this mod was built from; Chunk 10 (polish/sounds/loot/compile guide) is complete, Chunk 11 (Orbital Cannon) is next.

## Architecture decisions

- Planes/vehicles extend vanilla `Entity`/`EntityCreature` directly rather than using a boat/minecart base, for full control over flight physics and weapon hardpoints.
- Loot table injection uses `LootTableLoadEvent` (Forge 1.12.2 has no `data/` datapack format — that's 1.16+).
- Spawn eggs for non-craftable planes/vehicles are registered via `EntityRegistry.registerModEntity`'s egg-color overload, not separate `Item` classes.

## Product

Adds a full aerial/ground combat layer to vanilla Minecraft: craftable early-game planes and bombs, elite non-craftable aircraft (loot/creative only), guided missile systems with lock-on and manual steering, and static AA/artillery defenses.

## User preferences

- Continue the mod build chunk-by-chunk following `attached_assets/` specs in order; don't jump ahead to Chunk 11 (Orbital Cannon) without being asked.

## Gotchas

- Requires JDK 8 specifically — Forge 1.12.2 will not build on newer JDKs (the sandbox's default Java is 19, so a real build must target JDK 8 separately).
- `./gradlew setupDecompWorkspace` needs network access to Forge's Maven and takes 5–10 minutes the first time.

## Pointers

- See `airarsenal/README.md` for compile/install/testing instructions.
