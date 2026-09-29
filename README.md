# automationexercise-tests

Playwright Java + JUnit 5 test suite for https://www.automationexercise.com, built through a multi-agent Claude Code workflow. See `CLAUDE.md` for how the agents collaborate and `START_PROMPT.md` for how to kick off the first run.

## Setup

```
# Java 17+, Maven 3.9+
mvn -q test-compile
mvn exec:java -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install chromium"
mvn test -Dtest=SmokeSanityTest
```

Then open Claude Code in this folder and paste the block from `START_PROMPT.md`.

## Layout

```
.claude/agents/      8 subagents (spec-reader ... reporter)
.claude/commands/    /test-feature <name>, /regression
CLAUDE.md            orchestrator + agent rules
docs/site-map.md     what the site has (routes, texts, API)
tests/CONVENTIONS.md coding rules every agent reads first
tests/plans, cases   agent outputs (markdown)
tests/reports        run JSON, audits, summaries, bugs/
src/test/java/com/ae fixtures, pages, specs
```

Reports and `target/` are git-ignored except `tests/reports/SCHEMAS.md` and `tests/reports/bugs/`.

## CI

`.github/workflows/tests.yml` runs the full suite on every push to `master`, on PRs, nightly at 03:00 UTC, and on demand (Actions → tests → Run workflow; an optional `groups` input such as `smoke` or `api` limits the run).

- **Red/green:** failures are compared with `tests/known-failures.txt`, which lists the TC ids of filed product bugs. The build is red only for a **new** failure. Known bugs still run and still show in the report as 🟡. Remove a line from that file when the site fixes the bug.
- **Reports branch:** every non-PR run is published to the orphan `reports` branch as `runs/<date>_<run>_<sha>/` (summary.md/json, Surefire XML, the Maven log, and screenshots of failed tests), plus `latest/` and a `README.md` index of the last 30 runs.
- **Artifact:** the same report plus Playwright traces is attached to each workflow run for 14 days (open a trace with `show-trace`, see CLAUDE.md).
- The summary is also written to the run's job summary page.
