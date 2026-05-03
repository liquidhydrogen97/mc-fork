# Beta 1.7.3 Worldgen Extraction — Architecture Analysis

> Reference analysis for Phase 7+ (Beta 1.7.3 pockets). The goal of this document is to map the surface of [Moderner Beta](https://codeberg.org/Nostalgica-Reverie/moderner-beta) closely enough that we can decide *what* to lift, *how* to make it routable per-chunk, and roughly *what it will cost*. No code has been moved into the project tree yet.

> **Source captured at**: commit `f65440d99d09d590a3277be79a49c54d614f2b3e` (`chore: Final 4.1.3`, 2026-04-27). Cloned into `/home/ebeckner/dev/mc-fork/research/moderner-beta/` for read-only reference.

---

## 1. Repo, license, attribution

- **Upstream**: https://codeberg.org/Nostalgica-Reverie/moderner-beta (note: Codeberg, not GitHub — the modrinth page implies a GitHub mirror but the active repo is on Codeberg/Forgejo).
- **License**: MIT, post commit `55519d1`. Copyright header reads: `Copyright (c) 2024-2025 BlueStaggo, 2021 B3spectacled`. The mod started life as a fork of [b3spectacled/modern-beta-fabric](https://github.com/b3spectacled/modern-beta-fabric) (LGPLv3 up to and including `55519d1`, MIT thereafter). Pre-MIT history exists — for any code we lift, we pull from the post-`55519d1` MIT line and avoid the LGPL ancestors.
- **Attribution requirement (MIT)**: include the verbatim copyright + permission notice in any source file or distributable that contains a substantial portion of the code. Easiest path: ship a `LICENSE-moderner-beta` (or `THIRD_PARTY_LICENSES.md`) in our repo carrying the MIT header verbatim, and add a `// Adapted from Moderner Beta (MIT, BlueStaggo / B3spectacled). See LICENSE-moderner-beta.` header to lifted files.
- **Active maintainer**: BlueStaggo (Nostalgica-Reverie org). The repo is alive — they maintain six concurrent Minecraft target branches via Stonecutter (1.20.1, 1.21.1, 1.21.6, 1.21.9, 1.21.11, 26.1).
- **Loader coverage**: dual-loader via subprojects `fabric/` and `forgelike/` (NeoForge + Forge), with shared logic in the root `src/main/`. NeoForge 1.21.1 is supported.

---

## 2. Build/source layout

The project uses [Stonecutter](https://github.com/kikugie/stonecutter) for multi-version preprocessing. The actual code lives in `src/main/java`; per-version overrides are inline `//? if >=1.21.2 { ... } else { /* ... */ }` comments that Stonecutter rewrites at build time. The `versions/<mc-version>/gradle.properties` files only carry per-version dependency pins; they do **not** contain version-specific Java sources. Result: lifting a class for 1.21.1 means resolving its `//? if` directives by hand (or running Stonecutter once locally).

Top-level layout:

```
src/main/java/mod/bluestaggo/modernerbeta/
├── api/level/                       — public-ish abstract types (ChunkProvider, NoiseProvider, BiomeProvider, etc.)
│   ├── biome/                       — BiomeProvider + ClimateSampler + BiomeResolver* interfaces
│   ├── chunk/                       — ChunkProvider hierarchy + NoiseSampler/NoiseProvider abstractions
│   └── ...
├── level/                           — concrete implementations
│   ├── chunk/
│   │   ├── ModernBetaChunkGenerator.java        (500 LoC) — extends NoiseBasedChunkGenerator
│   │   ├── ModernBetaChunkNoiseSampler.java     (121 LoC) — wraps modern NoiseChunk
│   │   ├── ModernBetaNoiseSettings.java         (21  LoC) — six predefined NoiseSettings (OVERWORLD_128 etc.)
│   │   ├── ModernBetaNoiseGeneratorSettings.java(160 LoC) — registers NoiseGeneratorSettings for datagen
│   │   └── provider/
│   │       ├── ChunkProviderNoise3D.java        (712 LoC) — the Beta noise stack
│   │       ├── ChunkProviderInfdev227.java      (351 LoC) — older Infdev variant
│   │       ├── ChunkProviderFinite2D.java       (621 LoC) — Indev/Classic finite worlds
│   │       └── indev/, island/                  — settings/shape helpers
│   ├── biome/
│   │   ├── ModernBetaBiomeSource.java           (310 LoC) — extends BiomeSource
│   │   ├── ModernBetaBiomes.java, …Colors, …Features, …IDs, …Mobs
│   │   ├── biomes/{alpha,beta,…}/               — 19 Beta biome definitions, plus alpha/indev/release variants
│   │   ├── injector/BiomeInjector.java          (239 LoC) — post-process biome assignment (oceans/caves/borders)
│   │   ├── provider/                            — six biome providers (Beta, BetaFractal, PE, Single, Voronoi, Fractal)
│   │   │   └── climate/                         — ClimateMap, ClimateMapping, ClimateType (the temp/humidity grid)
│   │   └── voronoi/
│   ├── carver/
│   │   ├── BetaCaveWorldCarver.java             (588 LoC) — port of Beta-era cave algorithm to modern WorldCarver API
│   │   ├── BetaCaveCarverConfiguration.java     (81 LoC)
│   │   └── ModernBetaCarvers.java               — registration
│   ├── feature/                                 — Beta-flavored features + tree placers + noise-based placement modifiers
│   ├── structure/                               — ocean shrine, etc. (mostly modern wrappers)
│   ├── preset/ModernBetaWorldPresets.java       — registers WorldPreset entries
│   └── ... (cavebiome, blocksource, spawn, etc.)
├── settings/                                    — codec-driven settings system; this is how presets work
│   └── component/                               — typed config components: NoiseScale, NoiseLandmass, NoiseSlide, NoiseGeneratorSettings, SurfaceProperties, CaveGeneration, ClimateScale, ClimateMapping, etc.
├── util/
│   ├── noise/                                   — PerlinNoise (362), PerlinOctaveNoise (206), SimplexNoise, SimplexOctaveNoise, SimpleDensityFunction, SimpleNoisePos
│   ├── chunk/                                   — ChunkCache, ChunkClimate, ChunkHeightmap, LevelChunkCache
│   └── random/mersenne/                         — MTRandom (PE-flavor RNG; not needed for Java Beta)
├── mixin/                                       — 41 mixin/accessor files; mostly reflection-style accessors
└── registry/                                    — IRegistryHandler, ModernBetaResourceKeys, ModernBetaRegistries

fabric/src/main/java/.../fabric/data/ModernBetaSettingsPresets.java  (2709 LoC — preset bootstrap, runs at datagen)
```

Notable absences: there is **no static JSON file for the Beta 1.7.3 preset**. Presets are constructed in Java in the data-generator (`ModernBetaSettingsPresets.java`) and emitted as `data/moderner_beta/moderner_beta/preset/beta.json` at build time. This matters for extraction — we either run the datagen or replicate `presetBeta(...)` at line 204 directly in code.

---

## 3. The Beta noise stack — exactly which classes do what

Critical design observation: **there is no class named "Beta1_7_3ChunkGenerator"**. The Beta noise stack is implemented as a single configurable class — `ChunkProviderNoise3D` — that is parameterized via codec components to produce Beta 1.7.3, Beta 1.8, Beta 1.1, Alpha, late-Beta, Release, etc. terrain. The "version" lives entirely in JSON-style settings.

### 3.1 Class hierarchy (terrain-relevant only)

```
ChunkProvider (api/level/chunk/ChunkProvider.java, 293 LoC, abstract)
 └── ChunkProviderNoise (api/level/chunk/ChunkProviderNoise.java, 666 LoC, abstract)
      ├── ChunkProviderInfdev227 (level/chunk/provider/, 351 LoC)
      └── ChunkProviderForcedHeight (api/level/chunk/, 132 LoC, abstract)
           └── ChunkProviderNoise3D (level/chunk/provider/, 712 LoC, concrete)

ChunkProviderFinite (api/level/chunk/, sibling; Indev/Classic)
 └── ChunkProviderFinite2D
```

What each layer does:

- **`ChunkProvider`** (abstract): defines the lifecycle — `provideChunk`, `provideSurface`, `provideSurfaceExtra`, `getHeight`, `getAquiferSampler`, `getFluidLevelSampler`, `skipChunk(chunkX, chunkZ, step)`, `initForestOctaveNoise`. Holds a back-reference to `ModernBetaChunkGenerator`, the seed, the resolved `ModernBetaSettings`, a `WorldgenRandom.Algorithm` random source, a `PositionalRandomFactory`, the list of `BlockSource`s, a `SurfaceBuilder`. **This is the routable interface** — everything we'd want to call "for one chunk" goes through these methods.
- **`ChunkProviderNoise`** (abstract): the bulk of the per-chunk machinery. Defines an 8×N×8 noise sub-cell grid (the legacy "noise resolution" scheme — `noiseResolutionVertical = noiseSizeVertical * 4`, `noiseResolutionHorizontal = noiseSizeHorizontal * 4`). Implements:
  - `provideChunk` — the chunk-fill entry point. Iterates sub-cells, trilinearly interpolates noise corners, calls `BlockSource` per block. **This is the call we want to make per-chunk from a routing generator.**
  - `generateTerrain` — the inner double loop.
  - `sampleHeightmap` — generates the chunk's heightmap independently from `provideChunk` (used by aquifer + surface calculation).
  - `getAquiferSampler` — wraps the modern `AquiferSamplerProvider` over Beta noise (so water inside Beta caves still works).
  - `getIslandOffset`, `applySlides`, `modifyEdgeDensity` — top/bottom/edge taper and skylands-style islands.
  - `noisePostProcessors` — pluggable list (currently only `NOISE_CAVES` post-processor) applied to density before the slides.
  - Two caches — `ChunkCache<NoiseProviderBase>` for `chunkX,chunkZ → noise array`, and `LevelChunkCache<ChunkHeightmap>` for heightmaps — both keyed by chunk coords. **Important for routing: any pocket-aware caller has to reuse or invalidate these caches correctly.**
  - `sampleNoiseColumn(double[] primaryBuffer, double[] heightmapBuffer, int startNoiseX, int startNoiseZ, int localNoiseX, int localNoiseZ)` — abstract; this is the per-noise-cell density formula. **This is where Beta vs. Infdev vs. Alpha actually differs.**
- **`ChunkProviderForcedHeight`** (abstract, in `api/`): adds biome-driven height modulation (depth/scale per biome, weighted-sample radius). This is the "biome influences terrain height" layer.
- **`ChunkProviderNoise3D`** (concrete): the actual Beta 1.7.3 noise formula — three octave-Perlin samplers (`minLimitOctaveNoise`, `maxLimitOctaveNoise`, `mainOctaveNoise`), a beach simplex (`beachOctaveNoise`), a surface noise (`surfacePerlinOctaveNoise` or `surfaceSimplexOctaveNoise`), optional scale/depth noises, and an additional forest noise. The blending of `min`/`max` via `main` (lines 580–649 of `ChunkProviderNoise3D.java`) is the canonical Beta density formula. Surface generation (top-block/filler/sandy-beach/gravel-beach/ocean-bed/bedrock) is in `provideSurface` (lines 130–327).

### 3.2 Noise resolution (Beta vs vanilla)

In `ModernBetaNoiseSettings.java`, `OVERWORLD_128 = NoiseSettings.create(-64, 192, 1, 2)` — minY −64, height 192, vertical sizeFactor 1, horizontal sizeFactor 2. With the `*4` multiplier in `ChunkProviderNoise`, this gives **4-block vertical sub-cells, 8-block horizontal sub-cells** (a 2×2×48 sub-cell grid per 16×16 chunk). Vanilla 1.21.1 uses 4-block vertical, 4-block horizontal (4×48×4) sub-cells.

So the Beta path here uses **half the horizontal resolution of modern terrain**. That's by design — it's what gives Beta its blockier corner artifacts. The world *height* is fully modern (192 blocks of generation). This is important: the Moderner Beta team has already solved the "Beta in modern Y range" problem by stretching the noise field, not by capping at Y=128.

This means our pocket regions can run at modern height. **The "vertical extent mismatch" hard-part from the prior research note is largely already solved upstream** — we don't need to crush 384 → 128 or stretch 128 → 384, because Moderner Beta already remapped Beta noise onto a 192-block band (or any other range). We pick a NoiseSettings that matches our world.

### 3.3 The supporting noise utilities (all self-contained)

Every Perlin/Simplex implementation Beta needs is in `util/noise/`:

- `PerlinNoise.java` (362 LoC) — single-octave Perlin
- `PerlinOctaveNoise.java` (206 LoC) — multi-octave Perlin matching Beta's seed-derivation
- `SimplexNoise.java` (205 LoC), `SimplexOctaveNoise.java` (48 LoC) — for biome climate sampling (Beta uses simplex for temp/rain)
- `SimpleDensityFunction.java`, `SimpleNoisePos.java`, `PerlinOctaveNoiseCombined.java` — small glue
- Total ~890 LoC, **zero dependencies on Moderner Beta's settings/registry/mixin layers**. Lifts cleanly.

---

## 4. The Beta biome system

`BiomeProviderBeta` (233 LoC) implements the legacy temp/humidity grid. The flow:

1. Two simplex octave samplers (temperature, rainfall) plus a third "detail" sampler are seeded from `seed * 9871L`, `seed * 39811L`, `seed * 543321L`.
2. `BetaClimateSampler.sampleNoise(x, z)` produces `Clime(temp, rain)` in `[0,1]²`, with the canonical Beta squashing functions (`temp = 1 − (1 − temp)²`, etc.).
3. A `ChunkCache<ChunkClimate>` caches the per-chunk 16×16 climate grid (Beta has 1×1 biome resolution — every block has its own biome).
4. `ClimateMap.getBiome(temp, rain, type)` consults a hard-coded 14×14 grid (in `ClimateMap.java`) of climate-type strings (`desert`, `forest`, `taiga`, `tundra`, `rainforest`, `savanna`, `shrubland`, `seasonal_forest`, `swampland`, `plains`, `ice_desert`) and looks up the configured `ClimateMapping → ResourceLocation`.
5. The mapping table is held in the codec component `CLIMATE_MAPPINGS` (a `Map<String, ClimateMapping>`); see `ModernBetaSettingsPresets.presetBeta` lines 218–263 for the canonical Beta 1.7.3 entries pointing to the 19 `ModernBetaBiomes.BETA_*` biomes (`level/biome/biomes/beta/`).
6. `BiomeInjector` (239 LoC) is a separate post-process that decides "is this position deep enough to be ocean" / "is this position deep enough to be cave biome" and rewrites the biome assignment in the `ChunkAccess` PalettedContainer directly. It's a layered rule system: `BiomeInjectionRules` is a list of `(predicate, resolver)` pairs (`PRE` step before surface, `POST` step after). This is where ocean-deep, deep-ocean, cave-biome, and out-of-bounds biomes get layered on top of the basic temp/rain assignment.

The 19 Beta biomes in `level/biome/biomes/beta/` are configurable `Biome` records (top-block, filler, mob spawns, features), registered to a vanilla biome registry. They're plain modern biomes — they integrate with vanilla biome tags, BiomeModifiers, etc.

**Key observation for routing**: the biome system is independent of the terrain noise. We can use modern multi-noise biomes inside Beta-terrain pockets, OR we can use Beta climate biomes inside modern-terrain regions — they don't have to move together. For our use case ("rare Beta-terrain pocket inside a modern biome"), we likely want **modern biomes everywhere** and only swap the terrain shape. That makes the biome integration much simpler than a wholesale Beta worldgen replacement.

---

## 5. The Beta cave carver

`BetaCaveWorldCarver` (588 LoC) extends modern `WorldCarver<BetaCaveCarverConfiguration>`. It's a faithful port of the Beta 1.7.3 cave algorithm (random walk through ellipsoidal tunnels, branching ravines), adapted to the modern carver interface (uses `CarvingMask`, `Aquifer`, `CarvingContext`, `posToBiome` lookup).

How the chunk generator hooks it:

1. The configured carver `ModernBetaConfiguredCarvers.BETA_CAVE` and `BETA_CAVE_DEEP` (and `BETA_CANYON`) live in the registry.
2. `ModernBetaChunkGenerator.applyCarvers` overrides vanilla and, when `CaveGeneration.forceBetaCaves()` is true, swaps the vanilla `CAVE` configured carver out for `BETA_CAVE` per-chunk during the carver loop (lines 313–349 of `ModernBetaChunkGenerator.java`). Same for canyons.
3. Seed handling for caves uses three modes (`BETA`, `EARLY_RELEASE`, `MODERN`, `BEDROCK`) to match historical Beta seeding quirks — see `CaveGeneration.SeedMethod`.

This is a very nice abstraction for our purposes: **carver replacement is already a per-chunk decision**. We can plug a "if (insidePocket) → BETA_CAVE else → vanilla CAVE" rule directly into the same swap, and use modern carvers outside pockets without modifying the carver itself.

The carver itself is largely self-contained: it depends on `BetaCaveCarverConfiguration` (a small record) and modern Minecraft types. ~700 LoC (carver + config + registration glue) lifts cleanly, with the only real coupling being the `useFixedCaves` and `useSurfaceRules` config booleans that the chunk generator sets per-call.

---

## 6. Beta-era decoration / feature placement

Beta-era worldgen has features that don't exist in modern (or that exist but use modern placement, not Beta's noise-based density):

- `BetaOreClayFeature.java` — Beta's clay-vein generator. Modern clay placement is different.
- `BetaSnowAndFreezeFeature.java` — Beta-style top-of-world freeze pass (different from modern's `SnowAndFreezeFeature`).
- `CaveInfdev325Feature.java` — Infdev cave variant (not directly relevant for 1.7.3).
- `level/feature/foliage/` and `level/feature/trunk/` — Beta-shaped trees (oak with the legacy Beta shape rather than modern's branched generator).
- `level/feature/placement/`:
  - `NoiseBasedCountPlacementModifier.java` — places features in "tree clusters" based on Beta's forest-density octave noise. This is *the* Beta tree distribution: clumpy forests with patches of plain.
  - `Infdev325CavePlacementModifier.java` — older variant.
  - `noise/` — additional placement modifiers using the chunk provider's forest noise.

The placement modifiers receive their noise from `ChunkProvider.initForestOctaveNoise()` via reflection through `PlacedFeatureAccessor` mixin (see `ChunkProvider.initForestOctaveNoise`, lines 214–233). For pockets, we'd either (a) accept that our pocket regions get modern feature placement (simplest), (b) tag specific configured features as "Beta-only" and place them only inside pockets via our own placement modifier.

For a v1 prototype, **(a) is fine**. Beta-style decorations is a polish item, not a correctness item.

---

## 7. How they integrate with modern MC's `ChunkGenerator`

`ModernBetaChunkGenerator extends NoiseBasedChunkGenerator`. It overrides the standard chunk-generation lifecycle methods:

| Vanilla `ChunkGenerator` method | What ModernBeta does |
|---|---|
| `createBiomes` | Falls through to standard noise-based path; biomes come from `ModernBetaBiomeSource`. |
| `fillFromNoise` | Delegates to `chunkProvider.provideChunk(...)`. **This is where terrain shape is generated.** |
| `buildSurface` | Runs `BiomeInjector` PRE → `chunkProvider.provideSurface(...)` (or vanilla surface rules + `provideSurfaceExtra`) → `BiomeInjector` POST. |
| `applyCarvers` | Custom logic that optionally swaps `CAVE`/`CANYON` for `BETA_CAVE`/`BETA_CANYON`. |
| `applyBiomeDecoration` | Falls through to vanilla, gated by `chunkProvider.skipChunk(... FEATURES)`. |
| `getBaseHeight` / `getBaseColumn` | Delegates to `chunkProvider.getHeight(...)`. |
| `getGenDepth`, `getMinY`, `getSeaLevel` | Read from chunk provider's settings. |
| `createNoiseChunk` | Returns a `ModernBetaChunkNoiseSampler` that wraps modern `NoiseChunk` but overrides `preliminarySurfaceLevel` to call `chunkProvider.getHeight` instead of querying vanilla density functions. |

`NoiseBasedChunkGeneratorAccessor` is used to set the `Holder<NoiseGeneratorSettings>` and the fluid picker via reflection — the parent constructor expects a real `Holder` but ModernBeta needs to compute the settings lazily from the resolved preset. This is the one piece of "fight the framework" code; we'd hit the same wall doing our own.

The codec roundtrip for the chunk generator is the standard pattern: a `MapCodec<ModernBetaChunkGenerator>` with `biome_source`, `provider_settings`, and registry getters. Worlds save the resolved `ModernBetaSettings` in `level.dat`, so seed + settings = deterministic terrain.

---

## 8. Existing abstractions that already help us

This is the biggest finding: Moderner Beta is **already structured for routability**, even though no one has built a routing generator on top of it.

1. **`ChunkProvider` is a per-chunk interface.** `provideChunk(Blender, StructureManager, ChunkAccess, RandomState) → CompletableFuture<ChunkAccess>` takes a chunk and fills it. There is nothing in the API surface that assumes "I am the only generator for the world." The provider has its own caches keyed on chunk coords.
2. **`skipChunk(chunkX, chunkZ, step)` already exists** (with a step-aware enum: `SURFACE`, `CARVERS`, `FEATURES`, `ENTITY_SPAWN`). It's used for world-border + carver-disable today, but the hook is exactly the one we'd need for "skip this chunk because it's a modern-terrain chunk, not a pocket."
3. **`BlockSource` is composable.** `ChunkProvider` accumulates an ordered list of `BlockSource`s. The base block source (noise → block) is the lowest priority; `BlockSourceDeepslate` is layered on top. Adding a "modern terrain block source" priority-ordered with the Beta one is the natural extension.
4. **`NoisePostProcessor` is composable.** Same shape as `BlockSource` — list of pluggable post-processors over the density buffer. Currently only `NOISE_CAVES`. We could add a "blend toward modern density at edges" post-processor without touching the noise formula.
5. **Carver swap is already a per-chunk decision.** Lines 313–349 of `ModernBetaChunkGenerator.applyCarvers` show the pattern: look up a configured carver, swap it for the Beta equivalent if a config bit is set. We can change the predicate from `caveSettings.forceBetaCaves()` to `pocketRouter.isPocket(chunkX, chunkZ, ModernBetaGenerationStep.CARVERS)`.
6. **Settings are codec-driven and per-resolved-preset.** The `ModernBetaSettings` system means a single `ChunkProvider` instance is configured by a single preset. If we want both Beta and modern coexisting, we either run two configured `ChunkProvider`s (one Beta-Noise3D, one no-op-modern-passthrough) or we don't use Beta's `ChunkProvider` at all for modern chunks (the simpler path).
7. **`BiomeInjector` rules are just predicates.** It's a list of `(Predicate<BiomeInjectionContext>, BiomeResolver)` pairs. Adding a "if (chunkInPocket && y in band) return betaBiome else fall through" rule is one line.

---

## 9. Extraction surface — minimal kernel

If the goal is **"call Beta noise → blocks for one chunk, given seed + chunkX + chunkZ + a few config knobs, from outside Moderner Beta's framework"**, the minimum extractable kernel is roughly 3,000–4,000 LoC depending on how aggressively we strip:

### Tier 1 — Hard kernel (must lift, very self-contained, ~1,500 LoC)

| File | LoC | Notes |
|---|---|---|
| `util/noise/PerlinNoise.java` | 362 | Standalone |
| `util/noise/PerlinOctaveNoise.java` | 206 | Depends on `PerlinNoise` + `PerlinNoiseSettings` (a small record we can inline) |
| `util/noise/SimplexNoise.java` | 205 | Standalone |
| `util/noise/SimplexOctaveNoise.java` | 48 | Standalone |
| `util/noise/SimpleNoisePos.java` | 45 | Standalone |
| `util/noise/SimpleDensityFunction.java` | 29 | Wraps to modern `DensityFunction` interface |
| `api/level/chunk/noise/NoiseProvider.java` | 44 | Abstract |
| `api/level/chunk/noise/NoiseProviderBase.java` | 78 | Concrete |
| `api/level/chunk/noise/NoiseSampler.java` | 75 | Trilinear interpolator |
| `api/level/chunk/noise/NoisePostProcessor.java` | small | Functional interface |
| `util/chunk/ChunkCache.java` | 99 | Generic cache; could be replaced with Caffeine or a Map |
| `util/chunk/ChunkHeightmap.java` | 72 | |

These have **zero dependencies** on Moderner Beta's settings/registry/mixin layers. They are pure math + small data records. Lift verbatim, attribute, done.

### Tier 2 — Beta noise formula + glue (~1,500 LoC)

| File | LoC | Notes |
|---|---|---|
| `api/level/chunk/ChunkProvider.java` | 293 | Abstract; we need a stripped subset |
| `api/level/chunk/ChunkProviderNoise.java` | 666 | The per-chunk fill machinery |
| `api/level/chunk/ChunkProviderForcedHeight.java` | 132 | Biome-driven height; could be skipped for v1 |
| `level/chunk/provider/ChunkProviderNoise3D.java` | 712 | The actual Beta density formula |

Plus the codec components they consume from `settings/component/`:
- `NoiseScale` (Beta noise scale knobs — coordinateScale, heightScale, mainNoiseScale, lowerLimit, upperLimit, baseSize, stretchY, etc.)
- `NoiseSlide` (top/bottom taper)
- `NoiseLandmass` (scale + depth sub-noises)
- `Noise3DSettings` (boolean toggles: simplexSurfaceNoise, oldInfdevTerrainNoise, climateHeightScaling, monoliths, pocketEditionRng, arraySurfaceNoise)
- `SurfaceProperties` (beach scales, bedrock holes, etc.)
- `IslesProperties`, `WorldBorderLocation`, `ForcedBiomeHeight`, `PerlinNoiseSettings`

These records are small (10–100 LoC each) but they're tightly coupled to the codec system. For a clean lift we'd inline them as POJOs/records initialized with hardcoded Beta 1.7.3 values from `ModernBetaSettingsPresets.presetBeta` (no codec roundtrip needed).

### Tier 3 — Beta cave carver (~700 LoC, optional for v1)

| File | LoC |
|---|---|
| `level/carver/BetaCaveWorldCarver.java` | 588 |
| `level/carver/BetaCaveCarverConfiguration.java` | 81 |

Drop-in modern `WorldCarver` extension. We register it ourselves and pick it up via `applyCarvers` swap.

### Tier 4 — Beta biomes + climate (optional for v1; ~600 LoC if we want them)

19 Beta biome records + `ClimateMap` + `BiomeProviderBeta` climate sampler + `BiomeInjector` ocean/cave injection. **For our use case, we likely skip this entirely** — the pocket uses *modern* biomes; only the terrain shape is Beta. The climate sampler is interesting only if we want Beta-style biome distribution inside pockets, which is a separate axis from Beta-style terrain.

### What we don't lift

- The settings/codec system (`settings/`) — replace with hardcoded constants or our own simpler config.
- The preset/datagen system (`fabric/.../ModernBetaSettingsPresets.java`).
- The Stonecutter `//? if` directives — resolve them against 1.21.1 once and ship clean code.
- 41 mixins — only `NoiseBasedChunkGeneratorAccessor` is load-bearing for the `NoiseBasedChunkGenerator(biomeSource, null)` trick. The rest are for features we don't need (foliage placer types, biome accessors for color etc.). We can implement our own routing generator that sidesteps the lazy-Holder hack.
- The fractal biome system, the PE Bedrock-flavor providers, the Indev/Classic finite providers, the Infdev227 provider, all the alpha/late-beta/early-release biome variants. We want **only Beta 1.7.3**.

**Total minimal extractable kernel for "Beta noise → chunk fill": ~3,000 LoC** (Tier 1 + Tier 2). With the carver: ~3,700. With biomes: ~4,300.

---

## 10. The routing problem — concrete mapping

### 10.1 Where the work happens

`ChunkProvider.provideChunk(Blender, StructureManager, ChunkAccess, RandomState) → CompletableFuture<ChunkAccess>`. In `ChunkProviderNoise.provideChunk`:

1. Acquires section locks on the chunk's `LevelChunkSection`s.
2. Calls `generateTerrain(chunk, structureAccessor, noiseConfig, minimumCellY, cellHeight)`:
   1. Looks up `NoiseProvider` for `(chunkX, chunkZ)` from `chunkCacheNoise` — populates if absent by calling `sampleNoiseColumn` for each of `(noiseSizeX+1)*(noiseSizeZ+1)` columns.
   2. Builds a composite `BlockSource` (base = noise→block; plus deepslate; plus any other registered).
   3. Loops sub-chunks → sub-cells → blocks; each block gets `blockSources.apply(x, y, z) → BlockState`; writes via `LevelChunkSection.setBlockState`.
   4. Updates `Heightmap.OCEAN_FLOOR_WG` and `WORLD_SURFACE_WG` per block.
   5. Calls aquifer's `scheduleFluidTick`.
3. Releases section locks.

### 10.2 Inputs

- **Seed** — `long`, set at construction; used to seed all the octave noises.
- **Chunk position** — `chunkX, chunkZ` (read from `chunk.getPos()`).
- **`ChunkAccess`** — the chunk we're filling. Used for `setBlockState`, heightmap accumulation, height-accessor metadata.
- **`StructureManager`** — for the beardifier (smooths terrain around structure starts).
- **`RandomState`** (modern noise router state) — used for aquifer sampling but **not** for the Beta density itself. Beta density is fully self-contained from the Perlin samplers.
- **`Blender`** — for region-edge blending with old-format chunks; usually `Blender.empty()` (the chunk generator passes empty in the override).
- **Settings** — `ModernBetaSettings` (resolved preset). For our extraction this becomes "Beta 1.7.3 NoiseScale + NoiseLandmass + Noise3DSettings constants, hardcoded."

### 10.3 Outputs

- The `ChunkAccess` is mutated in place — every block from minY to topY gets either `defaultBlock` (stone), `defaultFluid` (water below sea level), or `Blocks.AIR`.
- Heightmaps `OCEAN_FLOOR_WG` and `WORLD_SURFACE_WG` are populated.
- A `CompletableFuture<ChunkAccess>` resolves with the chunk.
- *Side effects on caches*: `NoiseProviderBase` cached for `(chunkX,chunkZ)`, heightmap will be cached lazily on later `getHeight` calls.

### 10.4 Modifications for per-chunk routing

**Approach A: routable `ChunkProvider`-as-component.** Our custom `ChunkGenerator` (let's call it `McForkChunkGenerator`, extending `NoiseBasedChunkGenerator` like Moderner Beta does) keeps an internal lifted `BetaChunkProvider` as a private field and a `PocketRouter` that decides per chunk.

```java
public CompletableFuture<ChunkAccess> fillFromNoise(...) {
    ChunkPos pos = chunk.getPos();
    if (pocketRouter.isBetaPocket(pos.x, pos.z)) {
        return betaChunkProvider.provideChunk(blender, sm, chunk, noiseConfig);
    }
    return super.fillFromNoise(blender, noiseConfig, sm, chunk);  // standard modern path
}
```

The pocket router is deterministic: `isBetaPocket(cx, cz) = hash(seed, cx >> N, cz >> N) < threshold`, where `N` controls cluster size (e.g. `N = 5` → pockets are quantized to 32-chunk regions). Same predicate is reused across all the per-chunk callbacks (`buildSurface`, `applyCarvers`, `applyBiomeDecoration`).

**This works without modifying the lifted Beta code at all.** The `ChunkProvider` contract is already chunk-scoped.

**Edge cases to handle:**
- `getBaseHeight`, `getBaseColumn` are world-position queries (not chunk-scoped). Routing here means: `if (pocketRouter.isBetaPocket(x>>4, z>>4)) return betaProvider.getHeight(...)` — but the *answer* must be the same as what `provideChunk` will produce, which means the height-cache has to be primed before any `getBaseHeight` call. The Beta heightmap cache (`LevelChunkCache<ChunkHeightmap>`) does that lazily on first call to `getHeight`, so we just route the call.
- `createNoiseChunk` — vanilla expects a `NoiseChunk` for surface-rule sampling. Beta's surface system bypasses it (`ChunkProviderNoise3D.provideSurface` writes directly to the chunk). For pocket chunks we either disable vanilla surface rules and run Beta's surface, or run vanilla surface rules over the Beta-shaped terrain (which would produce mismatched-but-plausible surfaces). Latter is probably better for v1 — Beta uses stone as default, so vanilla `overworld` surface rules will plant grass+dirt above sea level, sand on beaches, etc. Acceptable approximation.

### 10.5 Edge blending — the actual hard part

Hard chunk-boundary cliffs at pocket edges will be visually unacceptable. Three blending strategies, in order of complexity:

**Strategy 1 — soft predicate ramp (simplest, ~3-5 days).** The router exposes not a boolean but a `float pocketWeight ∈ [0,1]` per *block* (or per noise sub-cell). At weight 1 we run pure Beta, at weight 0 pure modern. We composite the densities:

```java
final_density = lerp(modern_density(x,y,z), beta_density(x,y,z), pocketWeight(x,z))
```

Because both density formulas are continuous, the lerp produces smooth transitions. The cost: we have to evaluate **both** densities in transition cells, doubling the work for ~5–10% of chunks. The pocket-weight function is a 2D smoothstep over distance to pocket center.

This is the recommended v1. It maps almost perfectly onto Moderner Beta's existing `NoisePostProcessor` abstraction — we add a "blend to modern density" post-processor that triggers in the transition band.

**Strategy 2 — heightmap match + density blend (medium, ~1-2 weeks).** Sample the modern heightmap at the pocket boundary, then warp Beta's `densityOffset` so the surface heights agree at the seam. This avoids visible cliffs even when noise frequencies differ. More expensive; only needed if Strategy 1 produces visible discontinuities at edges.

**Strategy 3 — full noise-router merge (hard, ~1+ months).** Rebuild Beta's three octave samplers as modern `DensityFunction` nodes registered in the noise router. Then both Beta and modern terrain compose in a single density-function evaluation, and standard 1.21 routing/lerping just works. This is the "right" long-term answer but it's a lot of work. Probably overkill until we know the simpler strategies aren't enough.

For Phase 7 prototype, **Strategy 1** is the call. If players say "the seams look bad," upgrade to 2.

---

## 11. Extraction vs cleanroom

### Extraction (lift Tier 1 + Tier 2 + attribute)

**Pros:**
- Beta noise formula is finicky — `ChunkProviderNoise3D.sampleNoiseColumn` (lines 469–664) is 200 lines of carefully-tuned magic-number math. The `oldInfdev` toggle, the `mainNoise` blend region, the `densityOffset` quirks, the `monoliths` flag, and the `negativeFlattening`/`negativeDampening` knobs in `NoiseLandmass` are subtle. Re-deriving this from a Beta decompile (which is what BlueStaggo did from Modern Beta which did it from the original Beta jar) is possible but error-prone.
- The Perlin/Simplex implementations match Beta's bit-exact RNG seeding (see `seed * 9871L`, etc.). Re-implementing means re-discovering those constants.
- 60-70% time savings on day one — exactly matches the prior estimate.
- MIT permits it, attribution is cheap.

**Cons:**
- Codec/settings system is intrusive — Tier 2 classes pull in `ModernBetaSettings.getOrDefault(SettingsComponentTypes.NOISE_3D_SETTINGS)` etc. We have to either rewrite those calls to read constants, or lift the settings system, or wrap the lifted code with a thin adapter that satisfies the lookups.
- Stonecutter `//? if` directives are scattered through the code. For 1.21.1 we resolve by deleting the wrong branches; for forward-porting later we lose the multi-version benefit (acceptable — we run our own Stonecutter or just port manually).
- 41 mixins and an `IRegistryHandler` indirection layer mean the lifted code expects to be registered into the Moderner Beta registries. We have to reproduce a subset of this, or refactor the lift to drop registry indirection (preferred).

### Cleanroom (re-implement using Moderner Beta as a spec)

**Pros:**
- We control the abstractions. We can write the routable interface from day one rather than retrofitting one.
- No license-attribution maintenance burden (though MIT's burden is genuinely tiny).
- We end up with code we fully understand line by line.

**Cons:**
- Re-deriving the Beta density formula and matching seed→noise output bit-exactly is real work. We might *not* get exact terrain match — and if our claim is "this is Beta 1.7.3 inside modern," partial-match terrain will feel wrong.
- 4× to 6× the time of extraction. Easily 4–6 weeks just to get a working noise stack to compare against.
- Bug-for-bug parity with Beta is a maintenance trap if we go cleanroom and BlueStaggo ships a bugfix.

### Recommendation: **hybrid, leaning extraction.**

1. **Lift Tier 1 verbatim** (the noise math). It's pure, self-contained, ~1,500 LoC, MIT-attributed. We will not improve this by rewriting it.
2. **Lift Tier 2 with strip-down**: keep the algorithmic spine of `ChunkProviderNoise` and `ChunkProviderNoise3D`, but replace the codec-driven settings access with a hand-built `BetaConstants` POJO holding the Beta 1.7.3 values from `ModernBetaSettingsPresets.presetBeta` lines 204–271. Drop `ChunkProviderForcedHeight`, `IslesProperties`, `WorldBorderLocation`, the settings/codec roundtrip, the `noisePostProcessors` list (we'll add our own), and the `pocketEditionRng` / `monoliths` / `oldInfdev` branches. This trims Tier 2 from ~1,500 LoC to ~800–900 LoC of clean Beta-1.7.3-only code with a tight interface.
3. **Write the routing layer cleanroom.** Our `McForkChunkGenerator`, the `PocketRouter`, the density-blend post-processor, and the boundary smoothing logic are all our own design. Beta's code knows nothing about pockets.
4. **Defer Tier 3 (cave carver) to v2.** Initial pockets can use modern carvers — caves will still be carved, just not with Beta's chaotic ravine flair. Add the Beta carver later when polishing.
5. **Skip Tier 4 (Beta biomes) entirely.** Pockets sit inside a host modern biome; they keep that biome assignment. Only the terrain shape changes.

This is a clean kernel. The lift carries clear MIT attribution, the routing is our own, and Beta's tangled settings system never enters our codebase.

---

## 12. Refined effort estimates

With architecture in front of us, the prior estimates from `worldgen-plan.md` §5 hold up but distribute differently:

### Naive prototype (hard chunk seams, no Beta caves, no Beta features)

**Revised: 1.5 to 2.5 weeks** (was 2-3).

- 2 days: clone, port-resolve Stonecutter directives for 1.21.1, lift Tier 1 (noise math) verbatim, get it compiling against our 1.21.1 NeoForge environment.
- 3-4 days: lift + strip Tier 2 (Beta noise formula + per-chunk fill), wire up the `BetaConstants` POJO, run a one-off "generate one chunk and dump blocks" test to verify byte-for-byte parity against an upstream Moderner Beta build.
- 2-3 days: write `McForkChunkGenerator`, `PocketRouter`, the routing branch in `fillFromNoise`/`buildSurface`/`applyCarvers`. Hard chunk seams, no blending.
- 2-3 days: codec, world preset, in-game test, dimension hookup, get a world to load.
- 2 days: debugging the inevitable `Holder<NoiseGeneratorSettings>` lifecycle issues that bit Moderner Beta.

**Where the time hides**: codec roundtrip and the `Holder<NoiseGeneratorSettings>` lazy-init dance. Moderner Beta has a 30-line accessor mixin (`NoiseBasedChunkGeneratorAccessor`) and a `DefferedDirectHolder` shim to make this work. We hit the same wall and likely write the same workaround.

### Decent v1 (smooth blending, Beta caves, modern biomes throughout)

**Revised: 4-7 weeks** (was 2-3 months).

- Naive prototype above (2 weeks).
- 1-2 weeks: implement Strategy-1 density blending (per-block pocket weight, dual evaluation in transition band). This is the technically interesting work — getting the smoothstep band to hide the noise-frequency difference takes iteration.
- 1 week: lift Tier 3 (Beta cave carver), wire into `applyCarvers` per-chunk swap based on pocket router. Should be straightforward — the carver swap pattern is already in place.
- 1 week: tuning. Pocket size distribution, edge-band width, noise scale calibration. Lots of "fly to a pocket, see if it looks right, adjust."
- 1 week: edge cases — pockets crossing biome boundaries, pockets crossing modern structures (villages, strongholds), pockets near origin, very rare pocket configurations.

### Polished flagship feature

**Revised: 3-5 months** (was 4-6).

Adds:
- Strategy-2 heightmap-match blending if Strategy 1 leaves visible seams (1-2 months).
- Beta-style decorations limited to pockets (Beta tree shape, Beta forest density). Lift Tier 4's foliage/trunk/placement modifier code. (3-4 weeks.)
- Pocket "discovery" structures — natural Beta-era ruins or signposts inside pockets to make them feel intentional. (2-3 weeks.)
- Beta-style biome sampling inside pockets if we want full visual differentiation (probably not — modern biomes inside pockets is fine and avoids inventing biome-blending rules).
- Performance tuning. Dual-density evaluation in transition cells could become a chunkgen hot path; profile and cache aggressively.

### Where the actual hard parts hide (revised)

The prior research listed six "hard parts." Reassessment:

1. **Chunk-boundary coherence** — *easier than feared*. Both density formulas are deterministic and stateless given seed+coords. Per-chunk routing is just a deterministic predicate. **Not actually hard.**
2. **Vertical extent mismatch** — *already solved upstream*. Moderner Beta's `OVERWORLD_128` pulls Beta noise into a `NoiseSettings`-shaped band; we can use `OVERWORLD_FULL` or any other Y range and re-tune `baseSize`/`stretchY`/`heightStretch` accordingly. **Not actually hard.**
3. **Edge blending** — *the real hard part*. Strategy 1 (density lerp) is straightforward but tuning the transition band to look natural at all pocket boundaries takes iteration. This is where the polish budget goes.
4. **Cave coherence** — *easy*. Carver swap is already per-chunk. Picking by seed-origin is a one-liner.
5. **Biome resolution mapping** — *we don't need this*. Pockets keep host biome; only terrain changes.
6. **Decoration porting** — *defer*. Modern decorations inside pockets are fine for v1.

**The genuinely hard work, ranked:**

1. (10/10) Edge blending tuning. Iteration-bound, hard to estimate, the sole reason "v1" runs 4-7 weeks not 2.
2. (7/10) The `Holder<NoiseGeneratorSettings>` lazy-init dance with `NoiseBasedChunkGenerator`. Tractable, but has to be debugged in-game and the failure modes are ugly (NPEs deep in vanilla).
3. (5/10) Stripping Beta's settings/codec infrastructure out of Tier 2 cleanly. Mostly mechanical; risk is missing a configuration knob that turns out to matter.
4. (4/10) Routing the per-chunk callbacks (`buildSurface`, `applyCarvers`, `applyBiomeDecoration`) consistently with `fillFromNoise`. Easy to get wrong, easy to fix.
5. (3/10) Forward-porting beyond 1.21.1. Stonecutter helps; we lose that on lift. Acceptable for prototype.

---

## 13. Summary recommendations

1. **Fork**: skip. The Stonecutter/multi-loader build complexity isn't worth the upstream-track benefit for our purposes.
2. **Lift Tier 1 verbatim** with MIT attribution into `mc-fork/src/main/java/.../worldgen/beta/noise/`. ~1,500 LoC.
3. **Lift Tier 2 with strip-down** into `mc-fork/src/main/java/.../worldgen/beta/`. ~800–900 LoC after stripping codec/settings infrastructure.
4. **Cleanroom routing layer**: `McForkChunkGenerator`, `PocketRouter`, density-blend post-processor.
5. **Defer**: Beta cave carver (Tier 3), Beta features (Tier 4), Beta biomes.
6. **Naive prototype**: ~2 weeks. Decent v1: ~5-6 weeks. Polished: ~3-4 months.
7. **Keep this as Phase 7+ stretch goal** as already planned — but the architecture analysis says it's *less* risky than the prior estimate suggested. The Moderner Beta team has incidentally already solved several of the things the prior research called out as hard parts.

---

## 14. Follow-ups (require running code or compiling, not done in this analysis)

- **Build verification**: confirm Moderner Beta 4.1.3 builds against 1.21.1 NeoForge with the current Stonecutter setup. Not validated here.
- **Byte-for-byte noise parity**: extract Tier 1 + a minimal Beta noise driver, generate a chunk, and compare block-for-block against an upstream Moderner Beta world with identical seed. This is the gate for "we lifted it correctly."
- **`NoiseSettings` choice**: validate `OVERWORLD_128` (Beta's choice — Y range -64..128) vs `OVERWORLD_FULL` (Y range -64..320). Visual difference in pocket character is significant; we likely want `OVERWORLD_FULL` so Beta pockets reach modern world heights, but that requires re-tuning `NoiseScale.baseSize` and may produce different surface character than upstream Moderner Beta.
- **Pocket router design**: pin down the pocket-shape function (circle? voronoi? noise-threshold blob?) and pocket frequency. Probably noise-threshold blob with hash-based seed offset; needs renderer-driven iteration before v1.
- **`Holder<NoiseGeneratorSettings>` workaround**: confirm whether `DefferedDirectHolder` is needed in our case or whether routing through vanilla's `NoiseBasedChunkGenerator` constructor with a real Holder works (since we have a single hardcoded Beta-or-modern split, not Moderner Beta's lazy preset resolution).
