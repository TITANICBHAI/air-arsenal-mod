---
name: Air Arsenal registry ID conventions
description: How to allocate new packet discriminator IDs, entity IDs, and where to place Container/GUI classes when implementing a new mod chunk.
---

The mod's build spec files (one per "chunk") were written against an earlier snapshot of the codebase and often hardcode packet/entity IDs that collide with IDs already used by later chunks actually present in the code.

**Rule:** always read the live Javadoc ID tables in `network/ModNetwork.java` (packet discriminators) and `registry/ModEntities.java` (entity IDs) to find the actual next-free ID, and use that instead of whatever number the spec text says. Update both the registration call and the Javadoc table comment together — the table is documented as "must never change once released," so append, never renumber existing entries.

**Why:** specs are static text written before later chunks were built; trusting their literal numbers causes silent ID collisions with existing packets/entities.

**How to apply:** before registering any new `IMessage` packet or `Entity` subclass, grep the relevant registry file's existing table/registration calls for the highest ID in use, then allocate sequentially from there.

Also: this mod's convention puts data-only multiblock/TE Containers in `com.airarsenal.client.gui` alongside their `GuiScreen`/`GuiContainer` (e.g. `ContainerArtilleryEmpty`, `OrbitalCannonContainer`), not in a separate top-level `com.airarsenal.gui` package — follow existing package placement over what a spec literally states, when the two conflict.

For the first TileEntitySpecialRenderer / custom entity Renderer added to this mod (Chunk 11, Orbital Cannon), the established registration point is `ClientProxy.init()`, via `ClientRegistry.bindTileEntitySpecialRenderer(...)` and `RenderingRegistry.registerEntityRenderingHandler(...)` — follow this same location for future renderer registrations.
