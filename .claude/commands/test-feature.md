---
description: Run the full agent pipeline for one feature (plan -> cases -> code -> audit -> run/triage -> coverage -> summary)
argument-hint: <feature-name from docs/site-map.md>
---

Feature: `$ARGUMENTS`

You are the orchestrator. Follow CLAUDE.md "Orchestrator rules". Do not write test code yourself. Pass paths, not content. Announce the plan in one line, then execute.

Pipeline:

1. `spec-reader` → input: feature `$ARGUMENTS`. Expect `tests/plans/$ARGUMENTS.md`.
2. `case-designer` → input: `tests/plans/$ARGUMENTS.md`. Expect `tests/cases/$ARGUMENTS.md`.
   Show the user the TC id list from the agent's return (one line each) and continue without waiting.
3. `automation-writer` → input: `tests/cases/$ARGUMENTS.md`. Expect the test class path(s).
4. `selector-auditor` → input: feature + the paths from step 3. Expect `tests/reports/$ARGUMENTS-selectors.md`.
   If `Blocking: yes` → send `automation-writer` the audit path with "replace the REPLACE rows only", then re-run the auditor once. If still blocking, continue but note it.
5. `runner-triager` → input: feature + test class. Expect `tests/reports/$ARGUMENTS-run.json`.
6. Read the JSON. Branch on top-level `classification`:
   - `none` → go to 7.
   - `env` → stop. Tell the user the site/environment is unreachable and what the log says.
   - `flake`, or any failure with confidence `low` → `flake-investigator` for each such test, then treat its verdict as the classification and continue this branch list.
   - `test-bug` → retry loop, max **2** iterations:
       `automation-writer` with `tests/reports/$ARGUMENTS-run.json` + log path, "fix only failures[]", then `runner-triager` again. After 2 iterations still red → stop retrying, continue to 7 with the run red.
   - `product-bug` → do not retry those tests. Bug reports were written by the triager. Continue to 7.
7. `coverage-reviewer` → input: feature. Expect `tests/reports/$ARGUMENTS-coverage.md`.
8. `reporter` → input: feature. Expect `tests/reports/$ARGUMENTS-summary.md`.
9. Read the summary file and show it to the user verbatim. Then one line: what you recommend next.

Constraints:
- Never skip the triager to "just look at the error yourself".
- Never ask the writer whether its failure is a product bug.
- If any agent returns without writing its expected file, re-dispatch it once with "you did not write <path>; write it". Then stop and tell the user if it fails again.
