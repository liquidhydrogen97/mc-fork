# Operator Profile: Development Taste

This file extends `operator-profile.md` with Layer 1 preferences specific to **development mode**.

---

## Layer 1: Development Taste Preferences

```yaml
preferences:
  implementation_philosophy:
    value: "balanced -- solid foundation with room to iterate, no gold-plating"
    description: "Overall approach to implementation -- ship fast or build thoroughly"
    options: ["minimal-ship -- smallest working implementation, iterate from user feedback", "balanced -- solid foundation with room to iterate, no gold-plating", "thorough-scale -- build for the next 3 requirements, not just the current one", "adaptive -- depends on the contract's risk and reversibility"]
    source: "operator statement during /hitch-init"
    timestamp: "2026-05-03"
    confirmed_count: 1
    version_history: []

  coverage_comfort:
    value: "80% -- the configured minimum is sufficient"
    description: "What test coverage percentage makes you stop worrying"
    options: ["80% -- the configured minimum is sufficient", "90% -- comfortable only with high coverage", "95%+ -- near-total coverage required for confidence", "it depends -- coverage matters more for some modules than others"]
    source: "operator statement during /hitch-init"
    timestamp: "2026-05-03"
    confirmed_count: 1
    version_history: []

  style_consistency:
    value: ~
    description: "How much style consistency matters vs. just correctness"
    options: ["strict -- consistent style across the entire codebase, enforced by linters", "pragmatic -- follow existing patterns in each file, don't reformat unrelated code", "correctness-only -- if it works and is readable, style is secondary"]
    source: ~
    timestamp: ~
    confirmed_count: 0
    version_history: []

  tech_debt_tolerance:
    value: "time-boxed -- acceptable if logged and scheduled for cleanup within N sprints"
    description: "When tech debt is acceptable in development contracts"
    options: ["never -- all contracts must leave the codebase cleaner than they found it", "time-boxed -- acceptable if logged and scheduled for cleanup within N sprints", "pragmatic -- acceptable when shipping speed matters more than long-term cleanliness", "strategic -- acceptable when it enables learning that informs the right abstraction later"]
    source: "operator statement during /hitch-init"
    timestamp: "2026-05-03"
    confirmed_count: 1
    version_history: []

  architecture_opinions:
    value: ~
    description: "Architectural preferences that inform contract decomposition and code review"
    format: "free-form text describing preferences (e.g., microservices vs monolith, ORM vs raw SQL, etc.)"
    source: ~
    timestamp: ~
    confirmed_count: 0
    version_history: []

  testing_philosophy:
    value: "pragmatic-tdd -- TDD for complex logic, test-after for straightforward code"
    description: "Approach to TDD and test strategy"
    options: ["strict-tdd -- tests always written first, no exceptions", "pragmatic-tdd -- TDD for complex logic, test-after for straightforward code", "coverage-driven -- write tests to hit coverage targets, order is secondary", "behavior-driven -- focus on integration and E2E tests over unit tests"]
    source: "operator statement during /hitch-init"
    timestamp: "2026-05-03"
    confirmed_count: 1
    version_history: []
```
