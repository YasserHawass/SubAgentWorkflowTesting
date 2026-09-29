# Report schemas

Agents return exactly these shapes. The orchestrator parses them; deviation breaks the loop.

## `tests/reports/<feature>-run.json` (runner-triager)

```json
{
  "feature": "login",
  "command": "mvn test -Dtest=LoginTest",
  "startedAt": "2026-09-29T09:00:00Z",
  "durationMs": 48211,
  "totals": { "passed": 4, "failed": 1, "skipped": 0, "flaky": 0 },
  "classification": "test-bug",
  "failures": [
    {
      "testId": "TC-LOGIN-02",
      "title": "TC-LOGIN-02 shows error for wrong password",
      "file": "src/test/java/com/ae/specs/ui/LoginTest.java",
      "line": 31,
      "classification": "test-bug",
      "confidence": "high",
      "errorSummary": "Locator getByText('Your email or password is incorrect') not found; page shows 'Your email or password is incorrect!' (trailing !)",
      "evidence": ["target/surefire-reports/com.ae.specs.ui.LoginTest.txt", "target/traces/LoginTest-tcLogin02.zip"],
      "suggestedFix": "Match exact text including trailing '!'"
    }
  ],
  "logPath": "tests/reports/login-run.log"
}
```

`classification` (top-level) is the **worst** classification among failures, by this order: `product-bug` > `test-bug` > `env` > `flake` > `none`.

Per-failure `classification` values and what they mean:

| value | meaning | typical evidence |
|---|---|---|
| `test-bug` | The test is wrong: bad selector, wrong expected text, missing wait, bad data setup | Element exists under different locator; expected text differs by punctuation; timing |
| `product-bug` | The site behaves differently from the spec/plan and the test is correct | Screenshot shows wrong behaviour reproducibly |
| `flake` | Passed on re-run with no change | Passed 2/3 reruns |
| `env` | Network, site down, rate-limited, browser install issue | 5xx, timeouts on every test, DNS |
| `none` | No failures | |

`confidence`: `high` | `medium` | `low`. Low confidence -> orchestrator asks flake-investigator before acting.

## `tests/reports/<feature>-selectors.md` (selector-auditor)

```
# Selector audit: <feature>

Files: src/test/java/com/ae/pages/LoginPage.java, src/test/java/com/ae/specs/ui/LoginTest.java

| Location | Selector | Priority tier | Verdict | Suggested replacement |
|---|---|---|---|---|
| LoginPage.java:18 | page.locator("div > form > input:nth-child(2)") | 5 | REPLACE | page.getByPlaceholder("Email Address") |

Verdict values: OK, REPLACE, JUSTIFY (tier 5 without comment).
Summary: N OK, N REPLACE, N JUSTIFY. Blocking: yes|no (yes if any REPLACE).
```

## `tests/reports/<feature>-coverage.md` (coverage-reviewer)

```
# Coverage: <feature>

Source of truth: tests/cases/<feature>.md
Implemented: src/test/java/com/ae/specs/ui/<Feature>Test.java

| TC id | Implemented | Notes |
|---|---|---|
| TC-LOGIN-01 | yes | |
| TC-LOGIN-04 | no | not in spec file |

Gaps not in the cases file (things the site does that nobody planned for):
- ...

Coverage: N / M cases. Blocking gaps: none | list
```

## `tests/reports/bugs/<feature>-<n>.md` (runner-triager, on product-bug)

```
# BUG: <one-line title>

Feature: <feature>   Test: <TC id>   Severity: high|medium|low
Environment: chromium, <date>

## Steps
1. ...

## Expected
(quote from tests/cases/<feature>.md)

## Actual
(what happened, with evidence paths)

## Evidence
- target/screenshots/<Class>-<method>.png
- target/traces/<Class>-<method>.zip
```

## `tests/reports/<feature>-flake.md` (flake-investigator)

```
# Flake investigation: <TC id>

Reruns: 5   Passed: 3   Failed: 2
Category: timing | data | order-dependent | network | not-flaky
Root cause: ...
Fix: (what to change in the test, or "none — product bug, see bugs/...")
```

## `tests/reports/<feature>-summary.md` (reporter)

Human-readable, under 30 lines. Sections: Result (green/red), What was tested, Failures (if any, with classification), Bugs filed, Coverage, Next step.
