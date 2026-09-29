# Test plan: register

## Scope
In: Signup form on `/login` ("New User Signup!": name, email, Signup button); account info form on `/signup`; `/account_created`; header state after signup; UI account deletion (Delete Account link -> `/delete_account`); site TC1 and TC5.
Out: Login form (plan: `login`), logout (plan: `logout`), `/api/createAccount` contract (plan: `api-account`), checkout-time registration (site TC14/TC15, plan: `order`).

## Preconditions & data
- Fresh user per test from `TestUsers.newUser()` (unique email, never hardcoded). Call `registerForCleanup(user)` BEFORE submitting the form so `AccountApi.delete` runs even on failure. Scenario 1 deletes via UI, so cleanup must tolerate an already-deleted account.
- Scenario 2 and others needing an existing account create it via `AccountApi` (expect responseCode 201, "User created!") and check that first.
- Start logged out on home or `/login`.

## Scenarios
| # | Scenario | Expected | Priority | Type |
|---|---|---|---|---|
| 1 | TC1 full flow: home visible, click "Signup / Login", "New User Signup!" visible, enter name + fresh email, click Signup | "ENTER ACCOUNT INFORMATION" visible on `/signup` | P1 | ui |
| 2 | Continue TC1: fill title, password, DOB, tick "Sign up for our newsletter!" and "Receive special offers from our partners!", fill first/last name, company, address, address2, country, state, city, zipcode, mobile; click Create Account | "ACCOUNT CREATED!" visible on `/account_created` | P1 | ui |
| 3 | Click Continue on `/account_created` | Header shows "Logged in as <name>"; "Logout" and "Delete Account" visible; "Signup / Login" gone | P1 | ui |
| 4 | Click "Delete Account" while logged in | URL `/delete_account`; "ACCOUNT DELETED!" visible; Continue click leaves the page; header back to "Signup / Login" | P1 | ui |
| 5 | Deleted via UI then log in with same creds | "Your email or password is incorrect!" | P2 | ui |
| 6 | TC5: Signup with name + email of an account created via `AccountApi` | Error "Email Address already exist!" visible; stays on `/login`; no `/signup` form | P1 | ui |
| 7 | Signup with existing email in different letter case | Probe live and record (error shown or new account form reached); assert what the site does. Login is case-sensitive, so this may differ | P3 | ui |
| 8 | Signup name empty, email filled | Submit blocked by required validation; stays on `/login` | P2 | ui |
| 9 | Signup email empty, name filled | Submit blocked by required validation; stays on `/login` | P2 | ui |
| 10 | Signup email without "@" | Submit blocked by browser email-format validation; stays on `/login` | P2 | ui |
| 11 | On `/signup` submit with password empty | Not created; stays on `/signup`; no "ACCOUNT CREATED!" | P2 | ui |
| 12 | On `/signup` leave other required fields empty (first name, last name, address, state, city, zipcode, mobile) one at a time | Probe live and record which fields are required; for each required one, submit blocked | P2 | ui |
| 13 | Optional fields (company, address2) empty, all else valid | Probe live and record; expected account created | P3 | ui |
| 14 | Name with special chars/unicode (e.g. "Zoë O'Neil") and 100+ char name | Probe live and record: accepted, rejected or truncated; header shows "Logged in as <name>" accordingly | P3 | ui |
| 15 | HTML/script in name (e.g. `<b>x</b>`) | Probe live and record; no script executes; header text is escaped or stripped | P3 | ui |
| 16 | Prefilled values on `/signup`: name and email from step 1 shown | Probe live and record: name and email prefilled and email read-only or not | P3 | ui |
| 17 | DOB boundary: first/last year in dropdown, 29 Feb, 31 in a 30-day month | Probe live and record whether invalid dates are blocked | P3 | ui |
| 18 | State persistence: after signup Continue, navigate to `/products` and reload | Still "Logged in as <name>" | P2 | ui |
| 19 | State persistence: add item to cart while logged out, register, Continue | Probe live and record whether cart keeps item (site TC20 keeps it on login) | P2 | ui |
| 20 | Logged-in user opens `/login` | Redirected to `/` (site-map, confirmed in login); no signup form | P3 | ui |
| 21 | What could break: browser Back from `/account_created` and resubmit / reload `/signup` after creating | Probe live and record: duplicate error, or stale form; no second account with same email | P2 | ui |
| 22 | Cross-check: after UI signup, `/api/getUserDetailByEmail?email=` returns the entered details (name, address, city, etc.) | responseCode 200, `user` matches form values | P2 | api |

## Risks & quirks
- Ads: iframes and occasional interstitial can cover Signup, Create Account and Continue buttons; rely on `BaseUiTest` blocking and `Ads.dismissIfPresent`.
- `/login` holds two forms with similar inputs (Login and Signup); do not mix the email fields.
- Long form: title radio, DOB selects, country select; use stable `data-qa` hooks found by inspecting the live page. Do not guess.
- Country is a dropdown with a fixed list; use a value that exists.
- Native HTML5 validation gives no DOM text; assert "still on same URL, no account created".
- Cleanup: an account deleted in-test (scenario 4) makes API delete fail; cleanup must not fail the test.
- Public demo site: slow redirects after Create Account.

## Out of scope / open questions
- Password strength rules, email verification, captcha: not observed on the site; do not test unless found.
- Which `/signup` fields are truly required (scenarios 11-13): probe live and record, then fix expectations.
- Duplicate name (not email) allowed? Probe live and record with a second signup using same name, new email.
