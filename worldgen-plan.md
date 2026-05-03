# Worldgen Plan — mc-fork

> Implementation plan for the world generation system described in `strip-list.md` §3.1 and §3.2. Specifies the layered architecture, a six-phase prototyping plan, and the open architectural questions that need research before Phase 1.
>
> **Project context:** see `strip-list.md` for the full design (cuts, keeps, reworks, decisions).

---

## Layered architecture

Modern Minecraft worldgen is a stack of seven layers. Each composes onto the next. We touch some, leave others alone, add one new layer:

```
Layer 7 — Decorations (trees, ores, dungeons, kimberlite pipes, end wrecks)  ← we ADD features
Layer 6 — Surface rules (grass on top, sand on beach, etc.)                  ← we EDIT for biomes
Layer 5 — Geology stratification (post-process: stone → 9 rock types)        ← NEW LAYER (post-DF)
Layer 4 — Carvers (caves, ravines)                                           ← lean on YUNG
Layer 3 — Density functions (3D block-or-air at every coordinate)            ← we MODIFY for terrain
Layer 2 — Biome selection (climate point → biome)                            ← we EDIT biome list
Layer 1 — Climate noise (temperature, humidity, continentalness, etc.)       ← we MODIFY heavily
```

Vanilla has 7 climate noise functions feeding biome selection: `temperature`, `humidity`, `continentalness`, `erosion`, `peaks_and_valleys`, `depth`, `weirdness`. Each is multi-octave Perlin. They feed a `multi_noise` biome source that maps 7D climate points to biome IDs.

### What we change at each layer

**Layer 1 — Climate noise (heaviest customization).**

*Temperature gets a latitude term:*

```
temperature_final = temperature_noise(x, z) + latitude_bias(z)

latitude_bias(z) = piecewise:
  |z| < 50,000:    0           (within tropical/temperate band, noise dominates)
  |z| 50k-150k:    -0.2 * (|z| - 50k) / 100k   (gentle cooling)
  |z| 150k-300k:   -0.2 to -0.7 (linear)        (steady cooling toward arctic)
  |z| > 300k:      clamp at -1.0                 (forced frozen)
  |z| > 400k:      sentinel value forces the polar nullzone biome
```

In modern MC this is a JSON density function — pure datapack, no Java.

*Continentalness gets a longer period.* Default vanilla period ~3000 blocks; we want continents 30-40k across, so period ~30000. Threshold tuning determines ocean fraction.

```
continentalness_final = perlin_noise(x, z, period=32768, octaves=4)
```

*Altitude composes naturally.* Vanilla `peaks_and_valleys` already biases toward "frozen" at high Y. Latitude bias adds to this, so a tall mountain at z=0 and a short hill at z=200k can hit the same effective temperature. Verify the addition doesn't double-clip at extremes.

**Layer 2 — Biome selection.**

- Cut: deep dark, pale garden, all the cut biomes from `strip-list.md` §1
- Add: **polar nullzone biome** — forced when |z| > 400k. Powder snow surface, no plants, no passive mobs, ambient particle of falling snow.
- Add: **3-5 whimsy biomes** as rare-weirdness variants — sky islands, mushroom megabiome, crystal forest, others TBD.
- Modify: existing biome temperature ranges so latitude system actually shifts which biomes generate at which z.

**Layer 3 — Density functions (terrain shape).**

Mostly inherit vanilla 1.18+ shapes. Two modifications:
- Continentalness amplification (above)
- Beta 1.7.3 pocket override (see Layer 7)

**Layer 4 — Carvers (caves).**

Lean on YUNG's Better Caves. No custom work in v1.

**Layer 5 — Geology stratification (NEW LAYER).**

Post-density-function block replacement at chunk generation time. Logic per `strip-list.md` §3.2:
- Read the placed block's coordinates
- Determine Y-band
- Determine continental-modifier (volcanic / shield / sedimentary basin) from continentalness + a "geology noise" we add
- Replace `minecraft:stone` (and `minecraft:deepslate`, dirt/gravel substrate) with the appropriate rock from our 9-rock palette

Executed as either:
- Datapack `surface_rule` extension if it fits
- Custom Java mod hooking chunk generation post-density-function

Java path more flexible. Datapack path simpler. Probably need Java for the variation logic to compose properly.

**Layer 6 — Surface rules.**

Edit per biome — cold biomes get snow/ice top, deserts sand, beaches sand. Mostly inherit vanilla, modify for the polar nullzone biome.

**Layer 7 — Features (decorations).**

Add custom features:
- **Kimberlite pipes** — fork amethyst geode generator, vertical cylinder, andesite host, dense diamonds, biome-restricted
- **Crashed End structures** — datapack `structure_set` with NBT structures + biome filter + spawn rules + chorus gardens + shulker spawns
- **Beta 1.7.3 pocket regions** — special feature triggering local chunk regen with Beta-style noise. *Hardest layer; defer to Phase 6.*
- **Whimsy biome features** — per-biome decorations for the rare odd biomes

Standard configured_feature + placed_feature plumbing.

---

## Six-phase prototyping plan

Each phase ends with a playtestable artifact. Don't move to next phase until previous one feels right.

### Phase 1 — Latitude temperature system (~1-2 weeks)

**Goal:** verify the climate gradient feels right when traveling from spawn outward.

**Build:** datapack overriding vanilla temperature density function with our latitude-bias version. Cut all biome list changes for now — use vanilla biomes, just shifted by latitude.

**Validate:**
- Spawn at z=0 — should be tropical/warm
- Travel z+10k — should still be warm
- Travel z+50k — should hit gradient toward cool
- Travel z+200k — should be solidly cold
- Travel z+400k — should be vanilla snowy biomes (polar zone not yet implemented)

**Tooling:** biome map renderer (Cubiomes-Viewer or fork) to generate 1Mx1M biome map and visually verify gradient. Iterate density function constants until band sizes feel right.

**Decision gate:** does this feel like a real climate system, or vanilla with a slight tilt? Adjust constants.

### Phase 2 — Continental layout (~1-2 weeks)

**Goal:** big continents with deep ocean basins between, instead of vanilla's mostly-land world.

**Build:** override continentalness density function with longer period + different threshold.

**Validate:**
- Render 1Mx1M map showing land vs. ocean
- Continents 30-40k blocks across
- Ocean basins 5-15k blocks across between
- Continent edges should have realistic shape (not perfect circles)

**Tooling:** same biome renderer, with continentalness visualization mode.

**Decision gate:** do continents feel like real landmasses?

### Phase 3 — Polar nullzone biome (~3-5 days)

**Goal:** add the |z|>400k forced-cold biome.

**Build:** datapack biome JSON for polar nullzone (powder snow surface, no plants, no passive mobs, ambient snow particles), plus biome modifier forcing it at high |z|.

**Validate:**
- Travel to z=400k — biome should be the nullzone
- Should feel hostile and remote
- No grass, no trees, no animals
- Powder snow underfoot, occasional ice blocks

**Decision gate:** is the nullzone hostile-but-traversable? Tune severity.

### Phase 4 — Geology stratification layer (~2-3 weeks)

**Goal:** the 9-rock geology system from `strip-list.md` §3.2.

**Build:** Java mod hooking chunk generation post-density-function. Replace stone-class blocks with our 9-rock palette per Y-band + continental-modifier rules.

**Validate:**
- Surface mining shows sandstone in deserts, stone elsewhere
- Cave at Y=20 shows interleaved stone (limestone) + blackstone (shale)
- Cave at Y=-20 shows deepslate + calcite + diorite mix
- Cave at Y=-50 shows andesite + granite + basalt intrusions
- Hardness gradient: surface mining feels fast, deep mining feels slow
- Continental modifiers shift rock distribution: volcanic continents push igneous up, shields expose metamorphic in mountains

**Decision gate:** does rock variety read as geological, or as "weird random rocks"?

### Phase 5 — Custom features: kimberlite + crashed end (~2-3 weeks)

**Goal:** the two showcase features.

**Build:**
- Kimberlite pipe feature (fork amethyst geode generator into vertical cylinder)
- 3-5 hand-built NBT crashed-End structures with chorus gardens and shulker spawns
- Datapack spawn rules for both

**Validate:**
- Kimberlite: explore ~5k chunks, find at least one pipe; should feel like a windfall
- Crashed End: explore ~5k chunks, find at least one; should feel like a major discovery; loot tables work; chorus garden grows; shulkers present

**Decision gate:** rarities match the design intent? Tunable via JSON constants.

### Phase 6 — Whimsy biomes + Beta 1.7.3 pockets (~2-4 weeks)

**Goal:** the surprise/charm layer.

**Build:**
- 3-5 whimsy biomes as rare-weirdness variants
- Beta 1.7.3 pocket implementation (likely needs custom Java to override density functions for pocket regions)

**Validate:**
- Travel ~10k chunks, find at least one whimsy biome
- Find at least one Beta pocket — terrain inside visibly chaotic vs. surrounding modern biomes; transition at border reasonable

**Decision gate:** does whimsy feel like *charm* or *random nonsense*?

---

## Artifacts after Phase 1-6

- Datapack: climate, biome list, biome modifiers, surface rules, biome JSON, density functions, feature configs, structure sets
- Small Java mod: geology stratification post-processor, kimberlite pipe feature, possibly Beta pocket generator
- NBT structure file pack: 3-5 crashed End variants
- Modlist with YUNG's Better Caves as dependency

Shippable as Modrinth modpack at end of Phase 6. Estimate 3-4 months focused work.

---

## Architectural decisions (research-resolved 2026-05-03)

### Version-numbering note (important context)

Mojang changed Java Edition's numbering scheme in late 2025. The 1.21 line continued to receive sub-drops (1.21.9, 1.21.10, 1.21.11) into 2026. The next year-numbered drop **1.26.1 "Chaos Cubed" lands June 2026**. Anything started now will be one major version behind by ship. This is normal modding lifecycle — port forward from a stable base after the prototype is working, not while building it.

### 1. Mod loader — RESOLVED: NeoForge

- All target dependencies dual-publish: YUNG's Better Caves, Sodium, Iris, Distant Horizons, Lithium, Starlight.
- NeoForge advantages for this project:
  - The "opinionated tweaks/fork" audience overwhelmingly runs NeoForge
  - `BiomeModifier` JSON eliminates Java code for our polar nullzone / whimsy biome injection
  - Better API stability across 1.21.x minors than Fabric during the same window
- Fabric is a defensible second choice — both can do everything we need. Decision made for ecosystem fit, not capability.

### 2. Target Minecraft version — RESOLVED: 1.21.1

- Long-tail stable target with the deepest mod library.
- 1.21.5 introduced backend churn (registry refactor, performance regression breaking shaders) that many maintainers skipped.
- YUNG's Better Caves confirmed at 1.21.1; not yet confirmed at 1.21.11.
- 1.21.1 pre-dates the 1.21.5 churn, so prototyping is on a settled API.
- **Port forward** to 1.26.x or 1.21.11 once that ecosystem stabilizes and our prototypes are validated. Porting from a stable base is much cheaper than chasing a moving target.

### 3. Distribution — RESOLVED: Modrinth `.mrpack` primary, CurseForge secondary

- `.mrpack` is the de facto standard for hybrid datapack + mod + dep distributions in 2026.
- Format: `modrinth.index.json` declaring third-party deps by URL+hash, `overrides/` directory shipping bundled content (our datapack, our Java mod, configs).
- Use **[OpenLoader](https://modrinth.com/mod/openloader)** mod (Fabric + NeoForge) to apply our datapack globally to all worlds without per-world setup.
- Publish our Java mod separately on Modrinth as its own entry; modpack references it by URL like any other dep. Gets us analytics, version pinning, clean dependency declarations.
- Dual-publish to CurseForge via `mc-publish` GitHub Action for reach.
- **Do not** ship the datapack baked into the Java mod's jar resources — keep them separate.

### 4. Datapack vs Java split — RESOLVED per layer

| Layer | Verdict | Notes |
|---|---|---|
| Latitude temperature DF | **Datapack-only** | Vanilla density-function type set is sufficient (`shifted_noise`, `add`, `mul`, `cache_2d`) |
| Continentalness period | **Datapack-only** | Override DF with custom `worldgen/noise/*.json` |
| Polar nullzone biome | **Datapack-only** | New biome JSON + multi-noise parameter list entry |
| Whimsy biomes | **Datapack-only** | NeoForge `BiomeModifier` for injection |
| **Geology stratification** | **Hybrid, try datapack first** | Surface rules at 1.21 are richer than initially assumed: `condition: y_above`, `vertical_gradient`, `noise_threshold`, `biome` predicates compose. **Limit:** surface rules run during surface-rule pass against the chunk's `default_block` — they replace stone *as placed* but do NOT run inside cave-carved volumes. **Decision rule:** try datapack-only first; fall back to Java post-processor only if cave-wall geology coverage is required for the experience |
| Kimberlite pipe feature shape | **Java required** | Vanilla has no vertical-cylinder feature type. Register custom `Feature<KimberliteConfig>` (~50-100 LoC). Prior art: `alcatrazEscapee/ore-veins` ships a vertical column vein type as reference |
| Kimberlite config + placement | Datapack | Standard configured_feature + placed_feature plumbing |
| Crashed End — static templates | **Datapack-only** | NBT structure + structure JSON + placement rules |
| Crashed End — procedural damage | Java | Only if we want per-instance variation beyond template variants |

**Implication:** Phase 1 (latitude) and Phase 2 (continentalness) are pure datapack. The Java mod's first commit doesn't happen until Phase 5 (kimberlite) — assuming Phase 4 datapack-only path works.

### 5. Beta 1.7.3 pockets — RESOLVED: defer past Phase 6, fork Moderner Beta

**Prior art finding:** every existing Beta-style worldgen project (Moderner Beta, OldGenerator, Modern Beta, Beta Renewed, Beta 1.7.3 Reimagined) implements Beta as a **whole-world ChunkGenerator replacement**, selected at world creation. **No project does "Beta as a region within a modern world."** This is novel work.

**Hard parts:**
1. Chunk-boundary coherence — modern is 16×16×384 with deterministic noise sampling, Beta is 16×16×128 with 4×4×8 sub-chunk noise grid; per-chunk routing decision must be deterministic and frontier-coherent.
2. Vertical extent mismatch (128 vs 384) — crush or stretch.
3. Edge blending — players will not tolerate hard chunk-boundary cliffs at pocket edges; need 3-5-chunk smooth-blend region.
4. Cave coherence — both regimes have carvers; pick by seed-origin.
5. Biome resolution mapping — Beta's 11 biomes via 1×1 temp/humidity grid → modern multi-noise.
6. Decoration porting — Beta features need to fire only inside pockets.

**Effort estimates (revised after architecture analysis — see `research/beta-extraction.md`):**
- Naive prototype (hard chunk seams, no caves, no decorations): **~2 weeks**
- Decent v1 (smooth blending, biome mapping; Beta caves deferred to v2): **~5-6 weeks**
- Polished flagship feature: **~3-4 months**

**Starting point:** fork **Moderner Beta** ([Codeberg](https://codeberg.org/Nostalgica-Reverie/moderner-beta) — note: Modrinth's GitHub link is a mirror; active dev is on Codeberg). MIT-licensed, dual-loader, supports 1.21.1+. Three architecture findings reduced effort estimates significantly:

1. **The ChunkProvider abstraction is already structurally routable.** `provideChunk(blender, sm, chunk, noiseConfig)` is per-chunk; `skipChunk(chunkX, chunkZ, step)` exists with a step enum. We don't need to retrofit per-chunk routing — only build the routing layer that decides which provider to call.
2. **Vertical extent already solved.** Moderner Beta has `OVERWORLD_FULL` (Y -64..320) alongside the default `OVERWORLD_128`. No need to crush or stretch Beta noise.
3. **Recommended approach: hybrid extract + cleanroom.** Lift Tier 1 (~1,500 LoC pure noise math) verbatim with MIT attribution. Lift Tier 2 (~800-900 LoC of the Beta noise formula). Write the routing layer cleanroom. Skip Beta biomes (use modern biomes inside pockets — only terrain shape changes). Defer Beta cave carver to v2.

**The actually-hard part:** edge blending tuning — density lerp in transition band. ~3-4 weeks of iteration. The other "hard parts" mostly dissolve under analysis.

**Decision:** keep as Phase 7+ stretch goal. The rest of the system stable first.

---

## Status

- Phase: pre-Phase 1 (architecture resolved, ready to begin)
- Next action: project skeleton (`.mrpack` layout, NeoForge mod stub, datapack scaffold), then Phase 1 datapack
- Phase 1 deliverable: latitude temperature density function + biome map renders showing the gradient
