# Test plan: api-account

## Scope
In: `/api/createAccount` (POST), `/api/deleteAccount` (DELETE), `/api/updateAccount` (PUT), `/api/getUserDetailByEmail` (GET) - site-map API rows 11-14.
In: lifecycle, duplicate email, missing fields, wrong password, unknown email, wrong methods, field round-trip.
Out: `/api/verifyLogin` behaviour (see `tests/plans/api-auth.md`); used here only as a cross-check.
Out: UI register/login (see `register.md`, `login.md`).

## Preconditions & data
- Unique disposable user per test: `TestUsers.newUser()`. Register with `registerForCleanup(user)` so `AccountApi.delete` runs in `@AfterEach`, even for tests that create the account through the API under test.
- Tests extend `BaseApiTest`, tag `@Tag("api")`. Bodies are form-encoded (`setForm`); GET uses a query param `email`.
- Full create field set: name, email, password, title, birth_date, birth_month, birth_year, firstname, lastname, company, address1, address2, country, zipcode, state, city, mobile_number.
- Assert order: body `responseCode`, then `message`, then shape, then HTTP status. Documented on /api_list: create 201 "User created!", delete 200 "Account deleted!", update 200 "User updated!", get 200 with "User Detail".

## Scenarios
| # | Scenario | Expected | Priority | Type |
|---|---|---|---|---|
| 1 | POST createAccount with all fields, fresh email | responseCode 201, message "User created!"; HTTP status: probe live and record. Cross-check: verifyLogin returns "User exists!" | P1 | api |
| 2 | GET getUserDetailByEmail for the account from 1 | responseCode 200, body has `user` object; every sent field (name, email, title, birth_date/month/year, firstname, lastname, company, address1, address2, country, zipcode, state, city) matches. Whether `password` and `mobile_number` are returned, and key names: probe live and record | P1 | api |
| 3 | PUT updateAccount for existing account with changed name, city, address1 (all fields resent) | responseCode 200, message "User updated!"; HTTP status: probe live and record | P1 | api |
| 4 | Lifecycle: create -> get -> update -> get -> delete -> get | Second get shows updated values, unchanged fields intact; delete gives 200 "Account deleted!"; final get returns not-found body (probe live and record code and message) | P1 | api |
| 5 | DELETE deleteAccount with correct email + password | responseCode 200, message "Account deleted!"; then verifyLogin gives 404 "User not found!" | P1 | api |
| 6 | POST createAccount twice with same email | Second call: probe live and record (expect 400 "Email already exists!"). First account must remain intact (get still returns original name) | P1 | api |
| 7 | POST createAccount missing one required field at a time (email, password, name); also omit an optional one (company) | Probe live and record: expect 400 with a "Bad request, ... parameter is missing in POST request." style message; record exact text per field. Optional-field outcome: probe live and record | P2 | api |
| 8 | DELETE deleteAccount with correct email, wrong password | Probe live and record (expect 404 "Account not found!"); account must still exist (verifyLogin "User exists!" with real password) | P1 | api |
| 9 | GET getUserDetailByEmail for never-registered email; also missing `email` param | Probe live and record (expect 404 "Account not found with this email, try another email!" and a 400 for missing param) | P2 | api |
| 10 | Wrong methods: GET/PUT on createAccount, POST/GET on deleteAccount, POST/DELETE on updateAccount, POST/DELETE on getUserDetailByEmail | Expect responseCode 405, message "This request method is not supported."; probe live and record per combination, plus HTTP status | P2 | api |
| 11 | What could realistically break: PUT updateAccount for an unknown email, and PUT with wrong password | Probe live and record (expect 404 "Account not found!"); must not create an account or change an existing one | P2 | api |
| 12 | Update persistence: change password via PUT, then verifyLogin with old and new password | Probe live and record; expect new password 200 "User exists!", old 404 "User not found!". Teardown must use the current password | P3 | api |

## Risks & quirks
- HTTP status is always 200 on api-auth even for 400/404/405 bodies. Assume the same here, but record it, do not assume.
- Cloudflare sometimes returns HTTP 520 with non-JSON body. Guard Gson parsing; classify as env/flake, not a product bug.
- Email is case-sensitive (see api-auth); use the exact generated case throughout.
- Create returns 201 in body; the wrong-method messages may be non-JSON. Record raw body on first probe.
- Global shared site: unique emails and guaranteed cleanup are mandatory. If scenario 12 changes the password, `AccountApi.delete` needs the new one.
- `getUserDetailByEmail` may leak the password (security smell). Record, do not assert as required.
- Update may require all fields or ignore omitted ones (partial PUT): probe live and record.

## Out of scope / open questions
- Partial update semantics, email change via PUT, and title values other than Mr/Mrs/Miss: probe live and record only if time allows.
- Unicode, long, or HTML input in create fields (covered in UI register).
- Invalid birth dates: UI accepts 29 Feb 2019; whether API does is unknown.
- Does live behaviour match /api_list for rows 11-14? Record differences in `docs/site-map.md`.
