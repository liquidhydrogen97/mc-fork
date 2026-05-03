# Operator Profile (Shared)

This file stores the system's evolving model of the operator -- their stated preferences,
behaviorally inferred patterns, and current session context. It is a constitutional file:
every agent reads it at session start, and updates flow through the operator-modeler agent,
not through individual agents or the orchestrator.

This shared profile contains cross-cutting preferences that apply regardless of mode.
Mode-specific taste preferences live in separate files:
- `operator-profile-dev.md` -- development taste (Layer 1)

---

## Layer 1: Explicit Preferences (Shared)

```yaml
preferences:
  autonomy_level:
    value: "full -- decompose without asking unless genuine blocker"
    description: "How much autonomy the orchestrator has in decomposing tasks"
    options: ["full -- decompose without asking unless genuine blocker", "partial -- ask for major scope decisions", "conservative -- ask for every task"]
    source: "operator statement during /hitch-init"
    timestamp: "2026-05-03"
    confirmed_count: 1
    version_history: []

  escalation_threshold:
    value: "only genuine blockers"
    description: "When the system should escalate to the operator"
    options: ["only genuine blockers", "important decision points", "frequently for feedback"]
    source: "derived from autonomy_level=full during /hitch-init"
    timestamp: "2026-05-03"
    confirmed_count: 1
    version_history: []

  communication_density:
    value: "milestone summaries only"
    description: "How status updates should work"
    options: ["milestone summaries only", "after each contract completes", "continuous progress"]
    source: "operator statement during /hitch-init"
    timestamp: "2026-05-03"
    confirmed_count: 1
    version_history: []
```

---

## Layer 2: Inferred Patterns

```yaml
inferences: []
```

---

## Layer 3: Context Buffer

```yaml
context:
  current_session:
    focus_area: "Phase 1 worldgen — latitude temperature density function"
    apparent_urgency: "active development"
    recent_corrections: []
    session_start: "2026-05-03"
    interaction_count: 0

  recent_interactions: []
```

---

## Confidence Scoring Reference

See plugin template at `.claude-plugin/looptech-hitch/constitutions/operator-profile.md` for full scoring formula and parameter table. Reproduced here for agent reference:

| Parameter               | Value | Effect |
|-------------------------|-------|--------|
| `initial_confidence`    | 0.30  | Below display threshold; needs confirmation |
| `confirmation_bonus`    | +0.12 | ~5 confirmations to promotion threshold |
| `contradiction_penalty` | -0.25 | Contradictions weigh ~2x confirmations |
| `correction_bonus`      | +0.35 | Single correction nearly reaches display threshold |
| `decay_rate`            | -0.03 | Per session without observation |
| `confidence_floor`      | 0.05  | Minimum |
| `confidence_ceiling`    | 0.95  | Maximum |

| Threshold              | Value |
|------------------------|-------|
| Display threshold      | 0.40  |
| Promotion threshold    | 0.85  |
| Pruning threshold      | 0.15  |
