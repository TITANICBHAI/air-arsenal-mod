# Air Arsenal — Minecraft 1.12.2 Forge Mod

Planes, bombs, guided missiles, artillery, and tactical aerial combat for Minecraft 1.12.2.

## Prerequisites
- JDK 8 (NOT Java 9+ — Forge 1.12.2 requires Java 8)
- Gradle (bundled via wrapper — no install needed)
- Internet connection for first build (downloads Forge dependencies)

## Compile
```bash
./gradlew setupDecompWorkspace   # First time only — takes 5–10 minutes
./gradlew build
```
Output JAR: `build/libs/airarsenal-1.0.0.jar`

## Install
Copy JAR to your `.minecraft/mods/` folder.
Requires: Forge 1.12.2-14.23.5.2860 or higher.

## Testing on a local server
```bash
./gradlew runServer
```
Connect from Minecraft client at `localhost:25565`.

## Version branches
- `main` — 1.12.2 (this branch, most complete)
- `legacy-1.14` — 1.14.4 port (in progress)
- `modern-1.20` — 1.20.1 NeoForge port (planned)

## Adding sounds
Place `.ogg` files in `src/main/resources/assets/airarsenal/sounds/`
matching the keys in `sounds.json`.

## What's in the mod
- **Planes**: Wood Biplane, Iron Monoplane, Fighter Jet, Stealth Bomber, Predator Drone, Attack Helicopter
- **Ground vehicles**: Missile Truck, Armored Truck, Tank
- **Weapons**: machine guns, cannons, Hellfire/BrahMos/Predator/MANPADS/truck-guided missiles, bombs (iron, heavy, napalm, cluster, EMP, smoke, depth charge, fuel-air)
- **Static artillery**: AA Cannon, Flak Battery, Howitzer, Mortar, MLRS, Static Missile Battery
- **Tac Mode HUD** for in-cockpit weapon selection and targeting

## Roadmap
Chunk 11 (next) adds the Orbital Cannon — a multiblock structure that fires a
kinetic rod from y=300 at a player-designated target.
