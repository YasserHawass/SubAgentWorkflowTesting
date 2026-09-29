# Test plan: login

## Scope
In: Login form on `/login` (email, password, login button); header state after login; error on bad credentials; session persistence across pages and reload; site TC2 and TC3.
Out: Signup form, logout flow (own plan: `logout`), `/api/verifyLogin` (plan: `api-auth`), password reset (not present on site).

## Preconditions & data
- Each test creates its own account via `AccountApi` using `TestUsers.newUser()` (unique email), and calls `registerForCleanup(user)`. Never hardcode an email.
- Unknown-user tests use a generated email that was never registered.
- Start logged out, on `/login` (heading "Login to your account").

## Scenarios
| # | Scenario | Expected | Priority | Type |
|---|---|---|---|---|
| 1 | TC2: Open home, click "Signup / Login", verify "Login to your account" visible, enter valid email + password, click login | Header shows "Logged in as <name>"; "Logout" and "Delete Account" links visible; "Signup / Login" gone; URL is home | P1 | ui |
| 2 | TC3: Valid-format email that is not registered, any password | Error "Your email or password is incorrect!" visible; stays on `/login`; header still shows "Signup / Login" | P1 | ui |
| 3 | Registered email, wrong password | Error "Your email or password is incorrect!" visible; not logged in | P1 | ui |
| 4 | Both fields empty, click login | No login; stays on `/login`; browser required-field validation blocks submit (no server error text) | P2 | ui |
| 5 | Email filled, password empty | Submit blocked by required validation; not logged in | P2 | ui |
| 6 | Password filled, email empty | Submit blocked by required validation; not logged in | P2 | ui |
| 7 | Email without "@" (e.g. "notanemail") with a password | Submit blocked by browser email-format validation; not logged in | P2 | ui |
| 8 | Email in different letter case than registered | Record actual behaviour (login succeeds or shows "Your email or password is incorrect!"); assert what the site does | P3 | ui |
| 9 | Password with different case than registered | Error "Your email or password is incorrect!" (passwords are case-sensitive) | P2 | ui |
| 10 | Leading/trailing spaces around valid email | Record actual behaviour; assert it | P3 | ui |
| 11 | Very long email (300+ chars) and password | No crash; either blocked by validation or "Your email or password is incorrect!"; page stays usable | P3 | ui |
| 12 | SQL-like/HTML input in email and password (e.g. `' OR '1'='1`) | Not logged in; "Your email or password is incorrect!" or format validation; no script executed | P2 | ui |
| 13 | State persistence: log in, navigate to `/products`, then reload | Still "Logged in as <name>" on each page | P1 | ui |
| 14 | State persistence: add item to cart while logged out, then log in | Cart still holds the item after login (site TC20 behaviour); verify on `/view_cart` | P2 | ui |
| 15 | Logged in user opens `/login` directly | Record actual behaviour (form still shown on this site); assert header remains logged-in | P3 | ui |
| 16 | Failed login then correct login in same page | Second attempt succeeds; header shows "Logged in as <name>" | P2 | ui |
| 17 | What could break: login right after account creation via `AccountApi` (create then immediately login in UI) | Login works with no stale/eventual-consistency failure; if it fails, report as env/product, not test bug | P1 | ui |
| 18 | Deleted account cannot log in: create via API, delete via `AccountApi.delete`, then attempt login | Error "Your email or password is incorrect!" | P2 | ui |
| 19 | Cross-check: `/api/verifyLogin` with same valid creds agrees with UI login | responseCode 200, message "User exists!" | P3 | api |

## Risks & quirks
- Ads: iframes and occasional full-page interstitial can cover the login button; use ad blocking from `BaseUiTest` and `Ads.dismissIfPresent`.
- `/login` holds two forms (Login and Signup) with similar inputs; do not confuse the two email fields.
- HTML5 validation messages are browser-native, not DOM text; assert on "still on `/login`, not logged in", not on message text.
- Account setup must use the form-encoded `/api/createAccount` (responseCode 201, "User created!"); check it succeeded before the UI step.
- Public demo site: occasional slowness after login redirect.
- Default Playwright waits only; no fixed sleeps.

## Out of scope / open questions
- Rate limiting or lockout after repeated failures: unknown, not tested.
- Whether email is case-insensitive and trimmed (scenarios 8, 10): behaviour undocumented; case-designer should record observed result.
- "Remember me" or password reset: not on the site.
- Site TC2 steps 9-10 (Delete Account, "ACCOUNT DELETED!") are teardown; cover via cleanup or the `register` plan.
