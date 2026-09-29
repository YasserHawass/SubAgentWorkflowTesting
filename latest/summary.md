# Test run 🟡 green-with-known-bugs

Commit `38b602e` · 2026-09-29T15:45:52+00:00 · [workflow run](https://github.com/YasserHawass/SubAgentWorkflowTesting/actions/runs/36591906330)

**55 passed · 1 failed · 0 skipped** (0 new, 1 known product bugs)

| Class | Passed | Failed | Skipped |
|---|---|---|---|
| AccountApiTest | 12 | 0 | 0 |
| AuthApiTest | 11 | 0 | 0 |
| LoginTest | 12 | 0 | 0 |
| LogoutTest | 7 | 1 | 0 |
| RegisterTest | 12 | 0 | 0 |
| SmokeSanityTest | 1 | 0 | 0 |

## Failures

| TC id | Test | Known bug | Message |
|---|---|---|---|
| TC-LOGOUT-08 | LogoutTest.tcLogout08_logoutWhileLoggedOut | tests/reports/bugs/logout-1.md | Expecting actual: "KeyError at /logout" not to contain: "KeyError" |
