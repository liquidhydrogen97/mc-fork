# mc-fork — Tasteful Tweaks of Modern Minecraft

> **Project frame.** Modern Minecraft engine baseline (1.21.x as of writing). Cut content that's bad, anachronistic, or breaks the project's vibe. Rework systems we want differently. Keep most modern content — this is *not* a strict 1.9 strip; it's an opinionated fork.
>
> **Progression cap:** Diamond. (Netherite cut.)
>
> **Legend:**
> - `[CUT]` — definite removal
> - `[KEEP]` — definite retain (notable keeps only listed; vanilla 1.9 content assumed kept)
> - `[?]` — pending decision
> - `<!-- comment -->` blocks open for back-and-forth

---

## §1 Inventory: Cuts

### Mobs

- `[CUT]` Husks, strays, polar bears (1.10)
- `[CUT]` Vindicators, evokers, vexes (1.11) — illager combat trio
- `[CUT]` Pillagers, ravagers (1.14) — raid mobs
- `[CUT]` Allay (1.19)
- `[CUT]` Warden (1.19)
- `[CUT]` Sniffer (1.20)
- `[CUT]` Bogged (1.21)
- `[CUT]` Breeze (1.21)
- `[CUT]` Creaking (~1.21.4)

### Blocks

- `[CUT]` All sculk family: sculk, vein, catalyst, shrieker, sensor, calibrated sensor (1.19) — *see §3.5 alternative source*
- `[CUT]` Reinforced deepslate (1.19) — ancient-city only, no other use
- `[CUT]` Trial spawners, vaults, heavy core (1.21)
- `[CUT]` Tuff variants: bricks, stairs, slabs, walls, chiseled (1.21) — trial chamber palette
- `[CUT]` Pale oak wood set, pale moss (~1.21.4)
- `[CUT]` **Netherite block, ancient debris, netherite scrap, netherite ingot (1.16)** — full removal

### Items

- `[CUT]` All netherite gear: tools, armor (1.16)
- `[CUT]` Wind charge, mace (1.21)
- `[CUT]` Smithing templates + armor trim materials (1.20) — entire trim system
- `[CUT]` Echo shards (1.19) — recovery compass is loot-only, no longer needs ingredient

### Biomes

- `[CUT]` Deep dark (1.19)
- `[CUT]` Pale garden (~1.21.4)

### Structures

- `[CUT]` Woodland mansions (1.11)
- `[CUT]` Pillager outposts (1.14)
- `[CUT]` Bastion remnants (1.16) — *see §3.5 for gilded blackstone / pigstep rehoming*
- `[CUT]` Ancient cities (1.19) — *see §3.5 for echo shards*
- `[CUT]` Trial chambers (1.21)

### Mechanical systems

- `[CUT]` Raids + Bad Omen + Hero of the Village (1.14)
- `[CUT]` Sculk shrieker → warden summon (1.19)
- `[CUT]` Trial spawner waves, vault key system (1.21)
- `[CUT]` Mace fall-damage scaling, wind charge launching (1.21)
- `[CUT]` Armor trim system (1.20)
- `[CUT]` Smithing template upgrade pipeline (1.20)
- `[CUT]` Allay item-following + note block bonding (1.19)

### Status effects

- `[CUT]` Bad Omen (1.14) — raid trigger
- `[CUT]` Hero of the Village (1.14) — raids cut, redesign abandoned
- `[CUT]` Darkness (1.19) — warden vision pulse
- `[CUT]` Trial Omen (1.21) — ominous-mode activator
- `[CUT]` Raid Omen (1.21) — instant raid trigger
- `[CUT]` Wind Charged (1.21) — death-wind-burst debuff (breeze cut)

---

## §2 Inventory: Notable Keeps

Things explicitly kept that are post-1.9 and worth flagging:

### Mobs (kept against initial-cut instinct)

- `[KEEP]` Llamas + trader llamas (1.11)
- `[KEEP]` Parrots (1.12)
- `[KEEP]` Phantoms (1.13) — phantoms-on-no-sleep mechanic also kept
- `[KEEP]` Drowned, turtles, dolphins (1.13)
- `[KEEP]` Cod / salmon / pufferfish / tropical fish as entities (1.13)
- `[KEEP]` Wandering trader (1.14)
- `[KEEP]` Pandas, foxes (1.14)
- `[KEEP]` Cats as separate species (1.14)
- `[KEEP]` Bees (1.15)
- `[KEEP]` Piglins, piglin brutes (1.16)
- `[KEEP]` Hoglins, zoglins (1.16) — *see §3.4 for behavior tweak*
- `[KEEP]` Striders (1.16)
- `[KEEP]` Glow squid, goats, axolotls (1.17)
- `[KEEP]` Frogs + tadpoles (1.19)
- `[KEEP]` Camel (1.20)
- `[KEEP]` Armadillos (1.21)

### Blocks (palette expansion kept wholesale)

- `[KEEP]` Magma block, bone block, nether wart block, red nether brick (1.10)
- `[KEEP]` Concrete + concrete powder, glazed terracotta (1.12)
- `[KEEP]` All coral + coral fans (1.13)
- `[KEEP]` Kelp, sea pickles, dried kelp blocks, blue ice (1.13)
- `[KEEP]` Village job-site blocks: smoker, blast furnace, stonecutter, cartography table, fletching table, grindstone, smithing table (job-site only, see §3.7), loom, lectern, composter, barrel, bell (1.14)
- `[KEEP]` Sweet berry bush, bamboo, bamboo block (1.14)
- `[KEEP]` Honey block, honeycomb block (1.15)
- `[KEEP]` All Nether biome blocks: crimson + warped wood sets, nylium, shroomlight, weeping/twisting vines, all fungi (1.16)
- `[KEEP]` Soul fire, soul torch, soul lantern, soul campfire (1.16)
- `[KEEP]` Lodestone, respawn anchor, target block, crying obsidian, basalt, polished basalt (1.16) — *lodestone recipe needs §3.7 update*
- `[KEEP]` All copper blocks, oxidation, waxed variants, lightning rods (1.17 / 1.21)
- `[KEEP]` Amethyst, amethyst clusters, tinted glass (1.17)
- `[KEEP]` Raw iron / copper / gold blocks (1.17)
- `[KEEP]` Candles, powder snow (1.17)
- `[KEEP]` All lush cave blocks: moss, glow berries, dripleaf, spore blossom, cave vines, hanging roots, azaleas (1.18)
- `[KEEP]` All mangrove wood blocks, mud + packed mud + mud bricks, frog lights (1.19)
- `[KEEP]` All cherry wood blocks, bamboo wood set, pink petals (1.20)
- `[KEEP]` Decorated pots (1.20) — *smashing mechanic cut, pots themselves stay*
- `[KEEP]` Hanging signs (1.20)
- `[KEEP]` Observer block (1.11)
- `[KEEP]` Stripped logs + all-bark wood (1.13)
- `[KEEP]` Lantern, scaffolding (1.14)
- `[KEEP]` Chains (1.16)

### Items

- `[KEEP]` Iron nuggets (1.11)
- `[KEEP]` Crossbow (1.14)
- `[KEEP]` Trident, Heart of the Sea, Conduit (1.13) — full underwater track
- `[KEEP]` Spyglass (1.17)
- `[KEEP]` Goat horns (1.19)
- `[KEEP]` Recovery compass (1.19) — loot-only, uncraftable (see §3.5)
- `[KEEP]` Suspicious stew (1.14)
- `[KEEP]` Sweet berries (1.14), glow berries (1.18)
- `[KEEP]` Recipe book UI (1.12)
- `[KEEP]` Advancements infrastructure (1.12) — content rebuilt as 1.9 achievement list
- `[KEEP]` All bed colors via direct craft (1.12)
- `[KEEP]` Elytra firework rocket boost (1.11)
- `[KEEP]` Wolf armor, armadillo scutes (1.21)
- `[KEEP]` All archaeology: pottery sherds, brush, suspicious sand, suspicious gravel (1.20) — sniffer mob still cut, but seeds discoverable via archaeology
- `[KEEP]` Honey bottle (1.15)

### Biomes / structures kept

- `[KEEP]` Bamboo jungle (1.14)
- `[KEEP]` All 1.16 Nether biomes: Crimson Forest, Warped Forest, Soulsand Valley, Basalt Deltas
- `[KEEP]` 1.18 mountain split: Frozen/Jagged Peaks, Snowy Slopes, Stony Peaks, Meadow, Grove — *to be reshaped by §3.1 terrain rework*
- `[KEEP]` Lush caves (1.18) — primary cave biome retained
- `[KEEP]` Dripstone caves (1.18) — kept but rare per §3.2 cave rework
- `[KEEP]` Mangrove swamp (1.19)
- `[KEEP]` Cherry grove (1.20)
- `[KEEP]` Buried treasure, shipwrecks, underwater ruins (1.13)
- `[KEEP]` Trail ruins (1.20) — kept with archaeology system
- `[KEEP]` Ruined portals (1.16)
- `[KEEP]` 1.14 village rework with old villager-by-type trades

### Mechanical systems kept

- `[KEEP]` Conduit Power (1.13) — both the block and the status effect
- `[KEEP]` Phantoms-on-no-sleep mechanic (1.13)
- `[KEEP]` Suspicious stew effects (1.14)
- `[KEEP]` Bee pollination + honey block stickiness (1.15)
- `[KEEP]` Powder snow freezing damage (1.17)
- `[KEEP]` Lightning rod redirection (1.17)
- `[KEEP]` Copper oxidation (1.17)
- `[KEEP]` 1.14 villager profession / workstation rework
- `[KEEP]` Multi-player sleep skip (1.16)
- `[KEEP]` Striders as mounts (1.16)
- `[KEEP]` Decorated pot mechanics — full 1.21 storage behavior (hold one stack, survive break)
- `[KEEP]` Archaeology mechanics — brushing suspicious blocks; sniffer seeds (torchflower, pitcher) now found via archaeology rather than sniffer mob

### Status effects kept (post-1.9)

- `[KEEP]` Slow Falling (1.13) — phantoms kept, membrane source intact
- `[KEEP]` Dolphin's Grace (1.13)
- `[KEEP]` Weaving (1.21) — reassigned to cave spider attack
- `[KEEP]` Oozing (1.21) — reassigned to slime hit-effect
- `[KEEP]` Infested (1.21) — reassigned to silverfish hit-effect

### Engine plumbing

- `[KEEP]` Modern command system (Brigadier, 1.13)
- `[KEEP]` Datapacks, data-driven recipes, tags (1.12 / 1.13)
- `[KEEP]` DataFixerUpper (1.13)
- `[KEEP]` Modern resource pack format
- `[KEEP]` -64/320 Y range — full height, used in §3.2 geology
- `[KEEP]` Modern launcher (Microsoft Minecraft Launcher app, post-2020)
- `[KEEP]` Performance / rendering improvements — Distant Horizons compat target, see §4
- `[KEEP]` Particle and sound system extensions
- `[KEEP]` F3 debug screen improvements
- `[KEEP]` World save format and conversion

---

## §3 Reworks (the design work)

This section is where the project actually lives. Each thread is a real design conversation.

### §3.1 Terrain generation philosophy

**Operator's consolidated direction:**

- **Latitude-based climate.** z=0 is equator (tropical/warm). |z|>300k all cold. |z|>400k powder-snow/glacier nullzone with no plants or passive mobs. Spawn in temperate zone.
- **Altitude affects temperature** in addition to latitude — already vanilla behavior via peaks_and_valleys, just verify it composes with our latitude bias.
- **Continental layout.** 30-40k-block continents with rivers/lakes/sub-biomes obeying temperature gradients. Earth-like heightmap heuristics. Mountains appear as ranges that divide biomes within a continent.
- **Geology + biome + terrain are intertwined** — geology system underpins terrain feel.
- **Beta 1.7.3 pockets.** Rare regions within modern biomes that use Beta-style generation (chaotic, overhangs, less smooth) for whimsy.
- **Whimsy biomes.** Occasional rare extreme biomes for surprise/charm — sky islands, mushroom megabiome, crystal forest, etc. Not abundant; flavor.

**Status:** active design — prototyping plan below.

### §3.2 Underground geology rework

**Operator's stated direction:**
- Communicate sedimentary / igneous / metamorphic distinction
- Ore distribution tied to rock type
- Caves populated with stalactites, stalagmites, mineable formations
- Vibe: intimate caves like Beta/1.9, more clutter, mining as the loop
- Giant cave systems become rare treats, not the default
- Leaning toward cutting deepslate entirely

**Status:** active design thread. Proposal sketched below — edit freely.

**Reference points:**
- *Geolosys* — geological cluster deposits, biome/depth/stone-type gated ~ they are large, biome, depth, and stone-type gated
- *Real Geology* — granitic intrusions, sedimentary layers, metamorphic zones ~ this is more the vibe
- *TerraFirmaCraft* — ~25 rock types, real strata, ore-by-rock-type. Maximalist version. ~ we don't want to add new blocks, probbaly just reusing what we have.
- *Quark* — adds limestone, shale, marble, jasper as decorative (no ore tie-in)
- *YUNG's Better Caves* — cave variety reference ~ would like to know more about this

#### Rock roster — 9 categories mapped to vanilla blocks

Constraint per operator: no new blocks; repurpose vanilla. Updated mapping:

| Project rock | Vanilla block | Category | Visual fit | Role |
|---|---|---|---|---|
| Limestone | **Stone** | Sedimentary | Generic grey, fits "common rock" | Common surface-to-mid; hosts coal + iron |
| Shale | **Blackstone** | Sedimentary | Dark, near-black, fractured, decorative variants exist | Sedimentary deep; hosts coal + clay; gilded blackstone now a rare natural variant |
| Sandstone | Sandstone, red sandstone | Sedimentary | Already vanilla | Deserts, beaches, river beds |
| Granite | **Granite** | Igneous (intrusive) | Already vanilla | Continental igneous, hosts redstone |
| Basalt | **Basalt** | Igneous (volcanic) | Already vanilla, also nether | Volcanic surface, hosts copper |
| Gabbro | **Andesite** | Igneous (deep intrusive) | Grey speckled, generic | Deep igneous, hosts diamond kimberlite pipes |
| Slate | **Deepslate** *(rebrand, retained)* | Metamorphic | Dark, layered look | Metamorphic deep; narrower distribution than vanilla |
| Marble | **Calcite** | Metamorphic (light) | White, polished, vanilla geode block | Metamorphic mid; hosts emerald + lapis |
| Gneiss | **Diorite** | Metamorphic (banded) | Light speckled, banded | Metamorphic banded; hosts gold-quartz |
| *(flavor)* | **Tuff** | Igneous-adjacent | Volcanic ash, mottled | Rare patches in volcanic regions; no primary stratum role |

**Deepslate fate revised:** *kept but renamed/narrowed* — no longer fills all of Y<0; instead is the "slate" block within a varied metamorphic transition zone. The original "monotonous deep zone" complaint is solved by adding calcite + diorite + occasional granite/andesite variation, not by cutting deepslate.

**Operator's design note (verbatim):** repurpose vanilla, augment ore generation by geology, slightly attenuate base spawns. Don't make finding ores frustrating — the system should be playable on its own without other opinionated changes. No specific cues required (i.e., the player doesn't need a tooltip).

#### Y-band strata (with vanilla mapping)

Density-function noise so layers fold/dip/interleave, not hard horizontal lines:

```
Y > 64:         soil, gravel, surface stone (limestone), sandstone in deserts
Y 64 to ~30:    sedimentary dominant — Stone (limestone), Blackstone (shale)
Y ~30 to ~-15:  metamorphic transition — Deepslate (slate) dominant,
                Calcite (marble) + Diorite (gneiss) veins
Y -15 to -50:   igneous deep — Granite, Andesite (gabbro), Basalt intrusions
Y -50 to -64:   bedrock zone, mostly Andesite + Basalt
```

Tuff appears as rare ~5-block patches in volcanic continental regions, not on the primary Y-band schedule.

Continental modifiers (ties into §3.1 terrain):
- Volcanic regions push igneous closer to surface
- Old shields expose metamorphic (calcite + diorite + deepslate) in mountains
- Sedimentary basins are stone-dominant top to Y=0

#### Mining hardness variation

Real-world Mohs-ish gradient applied to vanilla blocks. Vanilla stone hardness is 1.5; deepslate is 3.0. Proposed values:

| Block | Project name | Hardness | vs. vanilla | Why |
|---|---|---|---|---|
| Sandstone | Sandstone | 0.8 | unchanged | Soft sedimentary |
| Stone | Limestone | 1.0 | down from 1.5 | Soft sedimentary |
| Blackstone | Shale | 1.0 | down from 1.5 | Soft sedimentary, layered |
| Tuff | Tuff (flavor) | 1.0 | down from 1.5 | Volcanic ash, soft |
| Calcite | Marble | 1.5 | unchanged | Soft metamorphic |
| Diorite | Gneiss | 2.0 | up from 1.5 | Hard banded metamorphic |
| Granite | Granite | 2.0 | up from 1.5 | Hard intrusive igneous |
| Basalt | Basalt | 2.0 | up from 1.25 | Hard volcanic igneous |
| Andesite | Gabbro | 2.5 | up from 1.5 | Very hard deep igneous |
| Deepslate | Slate | 3.0 | unchanged | Hardest metamorphic deep |

Net effect: surface mining gets faster (sedimentary), deep mining stays slow or gets slower. Diamond pickaxe genuinely matters for the andesite-deep zone. Shovels and stone pickaxes work fine in surface limestone.

#### Sand and gravel

Both stay as gravity-overlay surface deposits, not part of the strata system:

- **Sand**: surface in deserts, beaches, near rivers, lake bottoms. Already does this. Suspicious sand kept (archaeology in trail ruins, desert temples).
- **Gravel**: river beds, mountain bases, shallow cave patches. Already does this. Suspicious gravel kept (archaeology in trail ruins, ocean ruins).
- **Alluvial gravel idea**: gravel patches in cave streams could occasionally drop iron/gold flakes when broken — real-world placer-gold deposits. Optional flavor. Could reuse the existing flint drop with an extension.

These overlay the strata rather than replacing it — under a gravel patch you still hit limestone/tuff/whatever per the Y-band rules.

#### Ore-by-rock-type mapping (vanilla blocks)

**Operator decision:** base ore rates are NOT attenuated. Vanilla rates remain everywhere. Geology features (kimberlite pipes, etc.) ADD bonus caches as reliable/predictable sources. Casual-gameplay framing — players should feel rich.

| Ore | Bonus / augmented in | Notes |
|---|---|---|
| Coal | Blackstone, Stone | Sedimentary bonus on top of vanilla rate |
| Iron | All rock types | Vanilla rate, unmodified |
| Copper | Basalt, Andesite | Volcanic bonus |
| Gold | Diorite quartz veins, alluvial gravel patches | Vein/placer bonuses |
| Lapis | Calcite | Marble bonus |
| Emerald | Diorite in mountain biomes | Vanilla restriction preserved, slight bonus |
| Diamond | Vanilla rate everywhere + kimberlite pipes | Pipes are the windfall layer |
| Redstone | Granite, deep igneous | Igneous bonus |
| Quartz | Diorite veins | Bonus deposit |

#### Cave philosophy implementation

- Cave noise tuned toward many small twisting passages, not cathedrals. ~1.16-era cave noise as starting reference.
- Large caverns become rare landmarks (aquifers, lush caves, occasional great chamber).
- Cave clutter — every passage should have visible features:
  - Stalactites + stalagmites generalized beyond dripstone caves; reskin/extend pointed dripstone, add per-rock-type variants (calcite in limestone, basalt columns in volcanic)
  - Mineral incrustations — ore patches embedded visibly in cave walls/formations
  - Rubble piles at intersections, sometimes ore-bearing
  - Geodes generalized — quartz geodes, lapis geodes, small crystal pockets in addition to existing amethyst
  - Underground pools with mineral rim deposits
  - Cave moss patches in damp areas (reuse lush cave assets)

**Mining loop becomes:** see cave → identify rock type → infer likely ores → mine formations and walls. Geology is gameplay.

#### Resolved design positions

1. **Rock count:** 9, all mapped to vanilla blocks (resolved)
2. **Hardness variation:** yes, gradient from soft sedimentary to hard deep igneous (table above)
3. **Continental geology variation:** continuous noise via density functions, modulated by §3.1 terrain (discrete continents will dictate their own dominant geology)
4. **Visible rock type:** no F3/tooltip required — learn-by-eye plus playable-by-default
5. **Ore cues:** no special cues; ore generation augmented by host rock, base rate attenuated slightly elsewhere
6. **Deepslate:** kept but rebranded/narrowed as "slate" within metamorphic zone (not cut)

#### Cave generation approach — RESOLVED: lean on YUNG's Better Caves

For v1, ship YUNG's Better Caves as a dependency / compatibility target. Saves writing cave noise from scratch, inherits well-tested cave shapes that match our "small twisting passages" philosophy. Configurable via JSON; we tune frequency/biome distribution per our needs. Compose with our geology layer (YUNG carves in stone → we replace stone with our 9-rock palette → walls show appropriate rock types).

If YUNG's mod becomes unmaintained later, fork it. Probably saves 2-3 months of solo cave-noise engineering.

#### Kimberlite pipes — diamond delivery

Real geology: narrow vertical volcanic intrusions that brought diamonds up from the mantle. Reframed for game design:

**Geometry:**
- 4-7 block diameter cylindrical column, slight noise so not perfectly circular
- Vertical extent: Y≈10 down to Y≈-50 (occasionally surface-breaching)
- 0-15° tilt off vertical axis for naturalism

**Composition:**
- Andesite (gabbro) host throughout the pipe, distinguishing from surrounding rock
- Diamond ore densely embedded — 30-60 ore blocks per pipe (vs. vanilla ~3-8 per chunk in normal generation)
- Embedded extras: occasional emerald, rare lapis, small chance of a uniquely-rich diamond block at the bottom

**Surface signature:**
- Small andesite outcrop or shallow depression on the surface above
- Occasionally a single exposed diamond ore at surface (the "outcrop find")
- Subtle but discoverable for attentive players

**Frequency and positioning:**
- 1 per ~5,000-10,000 chunks — rare but findable, casual-rich-gameplay framing
- Positioned in andesite-deep continental interior biomes (not coastal, not volcanic islands)
- Some pipes have visible surface signature (andesite outcrop or surface diamond ore); others fully buried
- Bias toward biomes with stable old-shield geology (mountains, plateaus, taiga interior)

**Implementation approach:**
Fork the amethyst geode generator into a vertical-cylinder feature. Same code path as geodes, different shape function. Place via biome modifier with extreme rarity. Datapack-tunable frequency.

#### Other open implementation questions

- Stalactite/stalagmite generalization: extend pointed dripstone block, or new variants per rock type?
- How to color/differentiate ores in alternative host rocks (e.g., does iron in blackstone look different from iron in stone, or just same iron-ore texture)?

### §3.3 Storage rework — Crashed End structures (replaces backpack idea)

**Operator's pivot:** abandon backpack design. Instead, introduce **crashed/ruined End structures** in the overworld as the earlier-access route to shulker boxes. Lore implication: End cities/ships sometimes get torn from the End and crash into the overworld.

**Status:** active design thread.

#### Concept

A new overworld structure: a partially-buried, debris-scattered fragment of an End city or End ship. **Operator direction:** these should be self-contained End micro-ecosystems — include End mobs (shulkers, occasional endermen) AND End plants (chorus plant patches around the wreck). Players can experience all the End's components without needing to fight the dragon — accessible without getting too sweaty.

Contains shulker boxes (decorative + occasionally looted), occasional elytra (rare), totem of undying (alternate source per §3.5), end stone, purpur blocks, end city loot remnants, plus a small chorus garden and 1-3 shulker mobs. Visible from a distance as alien debris in a crater.

#### Generation logistics

Modern Minecraft structures are defined via:
- A datapack `structure_set` (spawn rules: spacing, salt, biome filter, frequency)
- One or more NBT structure files (the actual geometry, built and saved with structure blocks)
- Optionally a `jigsaw` system for procedurally-assembled structures (villages, ancient cities, trial chambers all use this)

For crashed end cities specifically:
- **Approach A — static NBT structure:** build 3–5 hand-designed crash variants in an editor, save as NBT, datapack picks randomly per spawn. Simplest. Each crash is unique-looking but the same layouts repeat.
- **Approach B — jigsaw assembly:** define a set of "crash piece" templates (broken column, fallen ship hull, debris field, intact shulker room) and let jigsaw assemble them. More variety, more authoring work.
- **Recommend A** for first pass; B if it's worth the polish later.

#### Where do they spawn?

Options:
- **Anywhere on land, very rare** (~1 per 10,000 chunks). Most natural.
- **Biome-filtered** to avoid oceans, deep oceans, rivers (the structure needs ground)
- **Avoid villages, strongholds, other structures** via spawn rules
- **Surface + crater**: terrain is locally modified to create a small impact crater. This means the structure isn't just placed *on* terrain, it carves into it. Doable via terrain-adapting placement rules but requires more than a static NBT.

**Operator-confirmed direction:** they crash on the ground (impact site, debris field).

#### Loot

Tuned to make finding one feel like a real reward without trivializing the End:

- **Always**: scattered end stone, purpur blocks, broken purpur stairs/columns
- **Common loot**: 1–3 shulker boxes (some empty, some with end city loot tables)
- **Uncommon**: ender pearls, chorus fruit, eyes of ender (small chance)
- **Rare**: 1 elytra (~25% of crash sites have one)
- **Rare**: 1 totem of undying (~15% of crash sites) — alternate source per §3.5
- **Atmospheric**: occasional shulker mob (rare, doesn't always spawn)

#### Open questions

- Frequency exactly: how rare? Lean: rare enough that finding one in a 50-100 hour playthrough is exciting, not guaranteed
- Crater terrain: simple flat-spawn or actual crater? Lean: crater, but acceptable to skip on first pass
- Variant count: 3–5 hand-designed crash layouts feels right for first pass
- Spawn biomes: all overworld surface, or specific ones (mountains, deserts for preservation)?
- Mob spawning rules inside: prevent normal hostile spawns, allow shulker-only?

<!-- operator comments: -->

### §3.4 Mob behavior tweaks — RESOLVED: no changes

Operator cancelled hoglin AI tweak. Vanilla AI throughout.

### §3.5 Item source redistribution

Items whose only source is now cut content, needing alternative loot tables:

| Item | Original source(s) | Status / alt source needed |
|---|---|---|
| Totem of Undying | Evokers (cut) in woodland mansions (cut) | **Resolved**: rare loot in crashed end structures (§3.3), ~15% of crash sites |
| Elytra | End ships (kept) | Additional rare loot in crashed end structures (§3.3), ~25% of crash sites. Still primarily End-side. |
| Sculk blocks | Sculk catalyst (cut) | **Resolved**: 4 soul soil + 1 redstone block (shapeless) → 1 sculk |
| Echo shards | Ancient cities (cut) | Cut entirely — no longer needed as craft ingredient |
| Recovery compass | Crafted from compass + echo shards (1.19) | **Loot-only** in dungeons/structures, not craftable |
| Gilded blackstone | Bastions (cut) | **Resolved**: now a rare natural variant within blackstone strata in the overworld geology (§3.2) — auto-rehomed via shale mapping |
| Pigstep music disc | Bastions (cut) | Rehome to dungeon loot tables |
| Snout banner pattern | Bastions (cut) | Accept as gone |
| Lodestone (recipe) | Chiseled stone + netherite ingot | New recipe TBD (netherite cut). Lean: chiseled stone + diamond. |
| Soul Speed enchantment | Piglin bartering (kept) + bastions (cut) | Kept via bartering only |

<!-- comments: -->

### §3.6 Piglin bartering — RESOLVED: keep

Bartering is piglins' entire interaction loop, and the only remaining source for Soul Speed enchanted books (bastions cut).

### §3.7 Smithing table fate — RESOLVED: keep as job-site only

With netherite + trim system cut, smithing table has no player-facing function but remains as a smith villager job-site block. Visually/economically meaningless beyond villager spawning. Cut from §1.

### §3.8 Redstone trial-chamber blocks — RESOLVED: keep

- **Crafter (1.21):** keep. Automation fits the project.
- **Copper bulb (1.21):** keep. Useful redstone-toggleable light.

### §3.9 Striders & honey bottle — RESOLVED: keep both

- Striders kept as lava mounts (riding mechanic intact)
- Honey bottle kept as food + Poison cure

### §3.10 Decorated pot mechanics — RESOLVED: keep full 1.21 storage behavior

Pots hold one stack, survive being broken (silk-touch-like). Full mechanics kept.

---

## §4 Modlist (compatibility targets)

Mods we want to be compatible with (or build on top of):

- **Sodium** — render performance
- **Lithium** — server-side performance
- **Starlight** — lighting engine rewrite, faster + cleaner
- **Distant Horizons** — LOD terrain rendering at extreme distances. Operator-confirmed target.
- **Iris / Oculus** — shader support
- **Mod loader:** Fabric or NeoForge — to be decided based on Distant Horizons / mod ecosystem fit at our target version

<!-- pending decision: Fabric vs NeoForge as primary loader -->

---

## §5 Open questions (consolidated)

Decisions still pending:

1. **§3.1 Terrain generation** — prototype several heuristics, intertwine with §3.2 geology + biome temperature system ~ yes
2. **§3.2 Underground geology** — stalactite/stalagmite generalization details, ore-texture-per-host-rock decision ~ yes
3. **§3.3 Crashed end structures** — frequency, crater terrain, variant count, biome rules ~ your proposal?
4. **§3.5 Lodestone recipe** — chiseled stone + diamond (lean) ~ needs to be more expensive
5. **§4 Mod loader** — Fabric or NeoForge (let research/logistics decide per operator) ~ research

---

## §6 Decision log

Latest session additions:
- Ore base rates: NOT attenuated. Vanilla rates everywhere; geology features add bonuses on top. Casual-rich framing.
- Kimberlite frequency revised: ~1 per 5,000-10,000 chunks (was 50-100k). Positioned in continental-interior andesite zones.
- Kimberlite surface signature: some visible (andesite outcrop / surface diamond), some buried.
- Crashed End structures expanded: now self-contained End micro-ecosystems with End mobs (shulkers, occasional endermen) and chorus plant gardens. Accessible alternate End experience without dragon fight.
- Altitude affects temperature (vanilla peaks_and_valleys behavior preserved, composes with latitude bias).
- Whimsy biomes confirmed: rare extreme biomes scattered as flavor treats.
- Beta 1.7.3 confirmed: rare pockets within modern biomes using Beta-style generation.
- Next focus: world generator design + prototyping (this session). After consensus + playtesting, move to opinionated additions (food spoilage, seasons, crops).

Resolved cumulative:
- Trial chambers, ancient cities, bastion remnants, woodland mansions, pillager outposts: CUT
- Netherite (full progression): CUT (smithing table reduced to job-site only; lodestone recipe rework pending)
- Piglin bartering: KEEP
- Crafter, copper bulb: KEEP
- Conduit (block + status effect): KEEP
- Recovery compass: KEEP as loot-only; echo shards CUT
- Wolf armor, armadillo scutes, archaeology system, honey bottle: KEEP
- Underwater ruins, trail ruins, dripstone caves (rare): KEEP
- Decorated pot mechanics (full 1.21 storage): KEEP
- Striders as mounts, sniffer-pipeline-via-archaeology: KEEP (sniffer mob still cut)
- Slow Falling, Dolphin's Grace, Hero of the Village (redesigned), Weaving, Oozing, Infested: KEEP
- Hoglin AI tweak: CANCELLED, vanilla AI throughout
- Smithing table: KEEP as job-site only (no player function)
- Geology mapping: 9 categories mapped to vanilla blocks (Stone/Blackstone/Sandstone/Granite/Basalt/Andesite/Deepslate/Calcite/Diorite + Tuff as flavor); deepslate kept as "slate"; hardness gradient table; ore augmentation by rock type with attenuation elsewhere
- Backpack design: ABANDONED — replaced by Crashed End Structures (§3.3)
- Sculk recipe: 4 soul soil + 1 redstone block (shapeless) → 1 sculk
- Totem of Undying / Elytra: rehome to crashed end structures as alternate sources
- Gilded blackstone: auto-rehomed as rare variant within blackstone (shale) strata
- Pigstep: dungeon loot
- Cave generation: lean on YUNG's Better Caves as v1 dependency
- Kimberlite pipes: fork amethyst geode generator into vertical-cylinder feature, andesite host with dense diamond loading, ~1 per 50-100k chunks
- Hero of the Village: CUT entirely (redesign abandoned)
- Weaving/Oozing/Infested: source reassignment confirmed (cave spider / slime / silverfish)

<!-- next session continues from open questions in §5 -->
