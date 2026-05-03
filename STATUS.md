# Project Status — mc-fork

## Active phase

**Phase 1 — Latitude temperature system.** Per `worldgen-plan.md` §Phase 1.

D1.1 (NeoForge mod skeleton) accepted 2026-05-03 — all 5 reviewers passed. D1.2 (z_clamped_gradient density function) ready to start.

## Tech debt logged

- **Plugin: ModDevGradle `disableRecompilation = true`** — adopted in D1.1 because the host's 7.5GiB RAM cannot run NeoGradle's standard Vineflower decompile pipeline (kernel OOM at ~5.4GB RSS). Tradeoff: no Mojang sources jar in IDE; on-demand single-class decompile via IntelliJ is the documented fallback. **Trigger to revisit:** when CI runs on a runner with ≥16GiB RAM, or when we provision a beefier dev box. Re-evaluate flipping `disableRecompilation` back to default at that point. (See `mod/build.gradle:31-34`, contracts/D1.1.md Revision History 2026-05-03.)
- **`mod/build.gradle:37-40`** — Parchment `minecraftVersion` and `mappingsVersion` are hardcoded inside the build file rather than `gradle.properties` like all other version pins. Extract to `parchment_mc_version` / `parchment_mappings_version` properties. One-line fix; fold into D1.2 housekeeping. (Code reviewer finding, D1.1 review.)
- **`mod/build.gradle:78-80`** — `runtimeClasspath.extendsFrom localRuntime` is cargo-culted MDG boilerplate; `localRuntime` is never defined, silent no-op, latent confusion vector. Either remove or add a comment explaining intent. Fold into D1.2 housekeeping.

## What's in place

- `strip-list.md` — design inventory (cuts, keeps, reworks, decisions)
- `worldgen-plan.md` — implementation plan, layered architecture, phase 1-6 prototype plan, architectural decisions
- `datapack/` — Phase 1 datapack scaffold
  - `pack.mcmeta` — pack format 48 (1.21.1)
  - `data/mc_fork/worldgen/density_function/latitude_bias.json` — placeholder, returns 0
  - `data/minecraft/worldgen/density_function/overworld/temperature.json` — override of vanilla, composes vanilla noise + latitude_bias
- `mod/` — NeoForge 1.21.1 mod skeleton (D1.1 accepted)
  - `McForkMod.java` entry point, `SmokeTest.java`, ModDevGradle 2.0.141 build, JaCoCo + JUnit 5 wiring
  - All run configs (`runClient`, `runServer`, `runData`) verified to load mod under FML
- `contracts/D1.1.md` — accepted (5/5 reviewers pass)
- `contracts/D1.2.md` — z_clamped_gradient, draft, ready to assign
- `research/` — beta-extractor agent working here (parallel)

## Phase 1 finding: pure datapack is not sufficient

Initial research (worldgen-plan.md §Architectural decisions Q4) concluded Phase 1 was datapack-only. **On implementation, this was incomplete.**

**The gap.** Vanilla density function types provide Y-coordinate access (`y_clamped_gradient`) but NOT Z-coordinate access. The available types are: `abs`, `add`, `blend_alpha`, `blend_density`, `blend_offset`, `cache_2d`, `cache_all_in_cell`, `cache_once`, `clamp`, `constant`, `cube`, `end_islands`, `flat_cache`, `half_negative`, `interpolated`, `max`, `min`, `mul`, `noise`, `old_blended_noise`, `quarter_negative`, `range_choice`, `shift`, `shift_a`, `shift_b`, `shifted_noise`, `slide`, `spline`, `square`, `weird_scaled_sampler`, `y_clamped_gradient`.

There is no `z_clamped_gradient`. Noise-based approximations don't work because noise is pseudorandom, not gradient-shaped.

**Fix.** Register a small custom density function type in Java: `mc_fork:z_clamped_gradient`. Mirrors vanilla `y_clamped_gradient` but reads Z instead of Y. Estimated 30-60 LoC plus minimal NeoForge mod boilerplate.

**Schedule impact.** Phase 1 still small but now has a Java component. The Java mod's first commit slips earlier than the research suggested (Phase 1, not Phase 5). Not a major shift — just one tiny DF type — but the project is hybrid Java+datapack from Phase 1 onward, not "datapack-first hybrid."

## Next concrete actions

In rough priority order:

1. Decide repo layout for the Java mod side. Options:
   - Sibling directory: `mod/` next to `datapack/` (single repo, two artifacts)
   - Separate repo entirely
   - Operator preference solicited
2. Set up minimal NeoForge 1.21.1 mod skeleton (Gradle, NeoForge MDK, single src tree)
3. Implement `ZClampedGradientDensityFunction` — mirror of `YClampedGradient` from vanilla, reading Z
4. Register the type in mod init under `mc_fork:z_clamped_gradient`
5. Update `latitude_bias.json` from constant placeholder to use the new type with the piecewise constants from worldgen-plan.md
6. Set up biome map renderer (Cubiomes-Viewer or similar) and produce a 1Mx1M render
7. Iterate constants until gradient feels right

## Background work

- **beta-extractor agent** running in `research/`. Task: clone Moderner Beta, document architecture, identify extractable kernel for Phase 7+ Beta-pocket implementation. Independent of Phase 1.

## Open questions for operator

1. Repo layout — single vs split?
2. Git: `git init` here? Now or later?
3. NeoForge MDK template preferences — vanilla-bare or use someone's modder template?
