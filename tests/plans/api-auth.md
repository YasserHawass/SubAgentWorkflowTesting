# Test plan: api-auth

## Scope
In: `/api/verifyLogin` only (site-map API rows 7-10), plus negative edges: missing email, GET/PUT, empty strings, email case.
In: assert both the HTTP status and the JSON body `responseCode` (and `message`).
Out: `/createAccount`, `/updateAccount`, `/deleteAccount`, `/getUserDetailByEmail` behaviour (belongs to `api-account`). They are used for setup/teardown only.
Out: UI login (see `tests/plans/login.md`).

## Preconditions & data
- One disposable account per test needing a valid login: `TestUsers.newUser()`, created with `AccountApi.create(user)`, registered with `registerForCleanup(user)` so `AccountApi.delete` runs in `@AfterEach`.
- Tests with no valid login (wrong creds, missing params, wrong methods) need no account. Use a generated unique email that was never registered.
- Requests are form-encoded (`email`, `password`), not JSON. Tests extend `BaseApiTest`, tag `@Tag("api")`.

## Scenarios
Assert order: body `responseCode`, then `message`, then HTTP status. Where HTTP status is unknown, "probe live and record" means: run once, write the observed value into the case file, then assert it.

| # | Scenario | Expected | Priority | Type |
|---|---|---|---|---|
| 1 | POST valid `email` + `password` of a freshly created account | responseCode 200, message "User exists!", HTTP status: probe live and record (expect 200) | P1 | api |
| 2 | POST valid email, wrong password | responseCode 404, message "User not found!", HTTP status: probe live and record | P1 | api |
| 3 | POST email never registered + any password | responseCode 404, message "User not found!" (same as 2, no user enumeration difference) | P2 | api |
| 4 | POST with `email` only (password param missing) | responseCode 400, message "Bad request, email or password parameter is missing in POST request." | P1 | api |
| 5 | POST with `password` only (email param missing) | responseCode 400, same message as 4; HTTP status: probe live and record | P2 | api |
| 6 | DELETE `/verifyLogin` with valid form | responseCode 405, message "This request method is not supported." | P1 | api |
| 7 | GET and PUT `/verifyLogin` (two tests or one per method) | Expect responseCode 405 and "This request method is not supported."; probe live and record (GET may differ) | P2 | api |
| 8 | POST with `email=` and `password=` both empty strings; also one empty, one valid | Probe live and record: 400 "Bad request..." (param present but empty) vs 404 "User not found!" | P3 | api |
| 9 | POST valid password with email upper-cased (case variant of a real account) | Expect responseCode 404 "User not found!" (UI login is case-sensitive per site-map); probe live and record | P2 | api |
| 10 | What could realistically break: POST valid creds after the account is deleted via `AccountApi.delete` | responseCode 404 "User not found!" (no stale session or cache says "User exists!") | P2 | api |

## Risks & quirks
- HTTP status is often 200 even for errors, and the site may mirror responseCode only in the body. Never assume; record both in the case file.
- Form-encoded only. Sending JSON or query-string params may look like "missing param" (400). Use `setForm`.
- Missing-param cases must omit the key entirely, not send an empty value (empty is scenario 8).
- Public demo site: accounts are shared globally, so the email must be unique and always cleaned up. Parallel runs must not reuse emails.
- Wrong-method responses for GET/PUT may be HTML or non-JSON. Guard JSON parsing (Gson) and record the raw body when probing.
- Note the site itself returns HTTP 500 on GET `/logout` while logged out (see logout bug); server-side errors are plausible on odd input.

## Out of scope / open questions
- Password case sensitivity and whitespace trimming (UI trims email spaces): probe live and record, add a case only if it differs from the UI.
- Rate limiting or lockout after repeated wrong logins: not tested.
- Very long or unicode email/password values: not tested.
- Whether GET `/verifyLogin` with query-string params returns "User exists!": probe live and record.
- Does `/api_list` documentation match live behaviour for rows 7-10? Quick check optional.
