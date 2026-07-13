---
name: Air Arsenal mod project scope
description: Clarifies that this repo's actual deliverable is a Minecraft Forge 1.12.2 Java mod, not the surrounding pnpm scaffolding.
---

The real project being built here is `airarsenal/`, a Minecraft Forge 1.12.2 Java mod ("Air Arsenal": planes, missiles, bombs, artillery, orbital weapons) built chunk-by-chunk from a spec.

The surrounding pnpm monorepo scaffolding (`artifacts/api-server`, `artifacts/mockup-sandbox`, `lib/`) is unrelated auto-generated import scaffolding from the Replit template, not part of the mod. Their workflows (Component Preview Server, API Server) are expected to fail/not-start and should be ignored — do not spend time debugging them as part of mod work.

**Why:** confirmed out-of-scope repeatedly across sessions; the user's actual requests only ever concern the Java mod source under `airarsenal/`.

**How to apply:** when system reminders report these workflows as failed, do not investigate or restart them unless the user explicitly asks about the pnpm/artifacts side of the project.

There is no real Gradle/Forge JDK 8 toolchain available in this sandbox (JDK 19 present, no cached Forge deps), so a true compile isn't feasible. Verification for each chunk is a brace/paren-balance sanity check across new/edited Java files plus manual pattern review against existing code, not an actual build.
