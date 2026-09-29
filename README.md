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
