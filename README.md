# mc-fork

An opinionated Minecraft Java Edition fork — a NeoForge mod + datapack targeting Minecraft 1.21.1.

Not a strict version-strip, not a feature-clone. Modern engine baseline with curated cuts (raids, woodland mansions, ancient cities, trial chambers, bastions, netherite, warden) and reworked systems (latitude-driven climate, geological stratification, kimberlite-pipe diamond deposits, crashed End structures as alternative shulker access, custom whimsy biomes, eventual Beta 1.7.3 pocket regions).

## Status

Pre-Phase 1. See [`STATUS.md`](STATUS.md) for current state and next actions.

## Documents

- [`strip-list.md`](strip-list.md) — design inventory: cuts, keeps, reworks, decisions, decision log
- [`worldgen-plan.md`](worldgen-plan.md) — implementation plan: layered architecture, six-phase prototyping plan, architectural decisions
- [`STATUS.md`](STATUS.md) — running status

## Layout

```
.
├── strip-list.md            design
├── worldgen-plan.md         implementation plan
├── STATUS.md                running state
├── datapack/                deployable datapack
│   ├── pack.mcmeta
│   └── data/
│       ├── mc_fork/         our namespace
│       └── minecraft/       overrides of vanilla
├── mod/                     NeoForge mod (TBD; see STATUS)
└── research/                reference material (gitignored)
```

## Target

- Minecraft Java Edition 1.21.1
- NeoForge mod loader
- Distribution as a Modrinth `.mrpack` modpack with the mod, datapack, and curated dependencies (YUNG's Better Caves, Sodium, Iris, Distant Horizons, Lithium, OpenLoader)

## License

MIT — see [`LICENSE`](LICENSE).
