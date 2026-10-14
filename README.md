# Flywheel — Minecraft 26.3

Flywheel, the rendering engine that draws large amounts of animated geometry, ported to
**Minecraft 26.3** on Fabric.

This repository is a standalone module. It was split out of the Create port, where Flywheel previously
lived as the package `com.zurrtum.create.client.flywheel` and was compiled into Create's single jar.
Package names were deliberately left unchanged so Create and the addon trees need no import churn.

## Requirements

- Java 25
- No Gradle install needed — the wrapper (`gradlew` / `gradlew.bat`) is included

## Build

```bash
./gradlew build            # jar lands in build/libs/
./gradlew runClient        # dev client
```

On Windows, `compile.ps1` (compile only, reports the *unique* javac error count) and `build.ps1` are
provided as thin wrappers.

## Notes

- **Client-only.** Flywheel has no server-side code and declares `"environment": "client"`.
- Its backend is **GL-only**. Minecraft 26.3 exposes raw GL (`GlDevice`, the active texture unit,
  `RenderType.create`) only through an access widener, so `src/main/resources/flywheel.classtweaker` is
  load-bearing rather than decorative. Its header records how the entry set was derived.
- Sodium and Iris integration is **compile-only** and detected at runtime; the model-emitter mixins are
  gated on Fabric's renderer API being present, and are returned from `FlwMixinPlugin` rather than listed
  in `flywheel.mixins.json` (that array is applied unconditionally).

## Status

Ported and compiling at **0 javac errors**, and this backend was previously observed running
(`Loaded 81 shader sources`, 10 worker threads, no Flywheel warnings or errors) while it was still part
of Create's jar. **It has not been run since the split**, and compilation is not evidence of correct
rendering on 26.3 — 26.3 fails silently. Treat the split as structurally verified, not visually verified.

## Licence

MIT — see [LICENSE](LICENSE). Flywheel is by Jozufozu and the Engine Room.