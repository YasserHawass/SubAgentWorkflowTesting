# automationexercise-tests

Automated test suite for https://www.automationexercise.com/ (UI + public API), built with **Playwright for Java + JUnit 5 + Maven**, developed through a multi-agent workflow.

## How work flows here

Work is delegated to specialised subagents in `.claude/agents/`. The main session is the **orchestrator**: it plans, dispatches, reads reports, and decides. It does **not** write test code itself.

Every handoff between agents is a **file on disk**, never a chat summary. Agents receive paths and return paths.

```
feature name (see docs/site-map.md "Features")
  -> spec-reader        -> tests/plans/<feature>.md
  -> case-designer      -> tests/cases/<feature>.md
  -> automation-writer  -> src/test/java/.../specs/ui/<Feature>Test.java (+ pages/, fixtures/)
  -> selector-auditor   -> tests/reports/<feature>-selectors.md
  -> runner-triager     -> tests/reports/<feature>-run.json
       red? -> test-bug   -> automation-writer again (max 2 retries, with the log path)
            -> product-bug -> tests/reports/bugs/<feature>-<n>.md, stop
            -> flake       -> flake-investigator -> tests/reports/<feature>-flake.md
            -> env         -> stop, tell the user
  -> coverage-reviewer  -> tests/reports/<feature>-coverage.md
  -> reporter           -> tests/reports/<feature>-summary.md
```

Slash commands drive this: `/test-feature <name>` and `/regression`.

## Skills (`.claude/skills/`)

Reusable know-how, loaded on demand by the agents that need it (each agent file says which):

| Skill | Holds | Used by |
|---|---|---|
| `playwright-java-api` | Exact Java API shapes (locators, assertions, dialogs, downloads, form-encoded API calls) | writer, auditor, flake-investigator |
| `inspect-live-page` | How to discover real selectors/texts without guessing (curl, scratch dump test, codegen) | writer |
| `page-object-template` | Canonical page object + UI test + API test shape for this repo | writer |
| `surefire-results` | Parsing Surefire XML, mapping methods to TC ids, message→classification hints | triager, flake-investigator |

Rule of thumb for adding one: knowledge that is long, procedural, and needed by more than one agent or more than one feature. Site-specific facts go in `docs/site-map.md`, coding rules in `tests/CONVENTIONS.md` — not in skills.

## Non-negotiable rules for every agent

1. Read `tests/CONVENTIONS.md` before touching any test code.
2. Read `docs/site-map.md` before planning or writing tests for a feature.
3. Never invent selectors from memory. Inspect the live page (`mvn exec:java -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="codegen https://www.automationexercise.com"`, or a browser MCP) or reuse an existing page object.
4. The agent that writes tests never judges its own failures. Triage is a separate agent.
5. Reports use the exact schemas in `tests/reports/SCHEMAS.md`. No prose where JSON is required.
6. Retries are capped by the orchestrator, not by the agent. An agent does one pass and returns.
7. No real personal data. Test accounts come from `fixtures/TestUsers.java` (generated, disposable emails).
8. Keep responses short: return the path(s) written and a <10-line summary. The orchestrator will read the file.

## Orchestrator rules (main session)

- Announce the plan in one line, then dispatch. Don't narrate.
- Pass **paths**, not content. "Implement `tests/cases/login.md`" not a pasted list.
- When a run is red, read `tests/reports/<feature>-run.json` and branch on `classification`. Don't guess.
- Product bugs are reported, not worked around in tests.
- Stop after max retries and report which stage failed and why.
- Model tiering: Opus is for the orchestrator only. Sonnet implements. Haiku reviews/triages/reports (set in each agent's frontmatter).

## Commands

```
mvn -q compile test-compile                          # type-check only
mvn test                                             # full suite
mvn test -Dgroups=smoke                              # tagged @Tag("smoke")
mvn test -Dtest=LoginTest                            # one class
mvn test -Dtest=LoginTest -Dplaywright.headless=false
mvn exec:java -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install chromium"
mvn exec:java -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="show-trace target/traces/<name>.zip"
```

Results: `target/surefire-reports/` (XML + txt), traces/screenshots in `target/traces/` and `target/screenshots/`.

## Target under test

Base URL: `https://www.automationexercise.com`  
API base: `https://www.automationexercise.com/api`  
It is a public demo site: expect occasional slowness and ads in the DOM. Ads are a known noise source — see CONVENTIONS "Dealing with ads".
