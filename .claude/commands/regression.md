---
description: Run the whole suite, triage all failures, produce regression-summary.md
---

You are the orchestrator. Follow CLAUDE.md "Orchestrator rules". No test code from you.

1. `runner-triager` → input: "full". Expect `tests/reports/regression-run.json` (feature = `regression`).
2. Read it. Group `failures[]` by classification.
   - `env` → stop and tell the user.
   - For each distinct `flake` or `low`-confidence failure → `flake-investigator` (run these in parallel, one per test; max 5 at once).
   - For `test-bug` failures: group by test class; for each class dispatch `automation-writer` with the run JSON + "fix only failures[] in <Class>" (parallel, max 3 at once), then `runner-triager` again with "full". One retry round only.
   - `product-bug`: bug reports already exist under `tests/reports/bugs/`. List them.
3. `reporter` → input: `regression`. Expect `tests/reports/regression-summary.md`.
4. Show the summary verbatim, then a one-line recommendation.
