# Test cases: api-account

Plan: tests/plans/api-account.md

Common rules for every case: requests are form-encoded via `setForm` (GET uses query param `email`), base `/api`, class extends `BaseApiTest`, tagged api. Assert order: JSON body `responseCode`, then `message`, then shape, then HTTP status. Guard JSON parsing with Gson (a Cloudflare HTTP 520 with a non-JSON body is env/flake, not a product bug). Every case uses its own unique account from `TestUsers.newUser()` and calls `registerForCleanup(user)` so `AccountApi.delete(email, password)` runs in `@AfterEach` even when the API under test created the account; cleanup must tolerate 404 "Account not found!" when the test already deleted the account. Emails are case-sensitive: use the exact generated case throughout. Full create field set: name, email, password, title, birth_date, birth_month, birth_year, firstname, lastname, company, address1, address2, country, zipcode, state, city, mobile_number.

Value sourcing: responseCode and message are copied verbatim from the docs/site-map.md API table (rows 7-14) or the "Discovered during api-auth" section. Anything else was probed live (2026-09-29) and the observed value is written into the case; the test asserts that single outcome. HTTP status is 200 per site-map for all responses, including error bodies.

## TC-APIACCOUNT-01 Create account with all fields returns "User created!"  [P1] [api] [smoke]
Preconditions: a fresh unique user; the account does not exist yet
Steps:
1. POST /api/createAccount with the full field set
2. POST /api/verifyLogin with the user's email and password (cross-check)
Expected:
- Step 1 body responseCode is 201
- Step 1 body message is "User created!"
- Step 1 HTTP status is 200 (per site-map)
- Step 2 body responseCode is 200 and message is "User exists!"
Data: TestUsers.newUser() with all 17 fields populated
Cleanup: delete account via AccountApi.delete(email, password)

## TC-APIACCOUNT-02 Get user detail returns every sent field  [P1] [api] [smoke]
Preconditions: a user created via AccountApi.create with the full field set (assert 201, "User created!")
Steps:
1. GET /api/getUserDetailByEmail?email=<user email>
Expected:
- Body responseCode is 200
- Body has a `user` object
- `user` values match what was sent for name, email, title, birth_date, birth_month, birth_year, firstname, lastname, company, address1, address2, country, zipcode, state, city
- Observed: `user` keys are id, name, email, title, birth_day (sent as birth_date), birth_month, birth_year, first_name, last_name (sent as firstname/lastname), company, address1, address2, country, state, city, zipcode. `password` and `mobile_number` are NOT returned
- HTTP status is 200 (per site-map)
Data: TestUsers.newUser()
Cleanup: delete account via AccountApi.delete(email, password)

## TC-APIACCOUNT-03 Update account changes name, city and address1  [P1] [api] [smoke]
Preconditions: a user created via AccountApi.create with the full field set
Steps:
1. PUT /api/updateAccount with all fields resent, changing name, city and address1 to new values
2. GET /api/getUserDetailByEmail?email=<user email>
Expected:
- Step 1 body responseCode is 200
- Step 1 body message is "User updated!"
- Step 1 HTTP status is 200 (per site-map)
- Step 2 `user` shows the new name, city and address1
Data: TestUsers.newUser(); new values "Updated Name", "Updated City", "1 Updated Street"
Cleanup: delete account via AccountApi.delete(email, password)

## TC-APIACCOUNT-04 Lifecycle: create, get, update, get, delete, get  [P1] [api] [smoke]
Preconditions: a fresh unique user; nothing created in advance
Steps:
1. POST /api/createAccount with the full field set
2. GET /api/getUserDetailByEmail
3. PUT /api/updateAccount with all fields resent, changing name and city
4. GET /api/getUserDetailByEmail
5. DELETE /api/deleteAccount with email and password
6. GET /api/getUserDetailByEmail
Expected:
- Step 1 responseCode 201, message "User created!"
- Step 2 responseCode 200, `user` has the original values
- Step 3 responseCode 200, message "User updated!"
- Step 4 responseCode 200; `user` shows the updated name and city; all other sent fields unchanged
- Step 5 responseCode 200, message "Account deleted!"
- Step 6 observed: responseCode 404, message "Account not found with this email, try another email!", HTTP 200; no `user` object
- HTTP status is 200 at every step (per site-map)
Data: TestUsers.newUser()
Cleanup: none beyond the delete in step 5 (registerForCleanup may delete again; tolerate 404 "Account not found!")

## TC-APIACCOUNT-05 Delete account with correct credentials  [P1] [api] [smoke]
Preconditions: a user created via AccountApi.create
Steps:
1. DELETE /api/deleteAccount with the user's email and password
2. POST /api/verifyLogin with the same email and password
Expected:
- Step 1 body responseCode is 200
- Step 1 body message is "Account deleted!"
- Step 1 HTTP status is 200 (per site-map)
- Step 2 body responseCode is 404 and message is "User not found!"
Data: TestUsers.newUser()
Cleanup: none beyond the delete in step 1 (tolerate 404 "Account not found!" from registerForCleanup)

## TC-APIACCOUNT-06 Create twice with the same email (record observed behaviour)  [P1] [api] [smoke]
Preconditions: a user created via AccountApi.create with name "Original Name"
Steps:
1. POST /api/createAccount again with the same email but name "Second Name" (other fields any valid values)
2. GET /api/getUserDetailByEmail?email=<user email>
Expected:
- Step 1 observed: responseCode 400, message "Email already exists!" (rejected)
- Step 1 HTTP status is 200 (per site-map)
- Step 2 body responseCode is 200 and `user` name is still "Original Name" (first account intact)
Data: TestUsers.newUser(); name "Original Name", second name "Second Name"
Cleanup: delete account via AccountApi.delete(email, password)

## TC-APIACCOUNT-07 Create with a missing field (record observed behaviour)  [P2] [api] [regression]
Preconditions: none; each variant uses its own unique unregistered email
Steps (data variants, each asserted separately with its own recorded outcome):
1. POST /api/createAccount with the full field set except `email`
2. POST with the full field set except `password`
3. POST with the full field set except `name`
4. POST with the full field set except `company` (optional field)
Expected:
- Variants 1-3 observed, HTTP 200: responseCode 400 with "Bad request, email parameter is missing in POST request." / "Bad request, password parameter is missing in POST request." / "Bad request, name parameter is missing in POST request."
- Variant 4 observed: account created, responseCode 201 "User created!" (company stored as "")
- Any variant that creates an account must have it removed in cleanup
Data: TestUsers.newUser() for each variant, one field removed from the form
Cleanup: registerForCleanup for every variant's user; tolerate 404 "Account not found!" for variants that create nothing

## TC-APIACCOUNT-08 Delete with correct email and wrong password (record observed behaviour)  [P1] [api] [smoke]
Preconditions: a user created via AccountApi.create
Steps:
1. DELETE /api/deleteAccount with the user's email and password "Wrong#Pass1"
2. POST /api/verifyLogin with the user's email and the real password
Expected:
- Step 1 observed: responseCode 404, message "Account not found!" (rejected)
- Step 1 HTTP status is 200 (per site-map)
- Step 2 body responseCode is 200 and message is "User exists!" (account still exists)
Data: TestUsers.newUser(); wrong password "Wrong#Pass1" (ensure it differs from the real one)
Cleanup: delete account via AccountApi.delete(email, password)

## TC-APIACCOUNT-09 Get detail for unknown email and missing email param (record observed behaviour)  [P2] [api] [regression]
Preconditions: none; a generated valid-format email that was never registered
Steps (data variants, each asserted separately):
1. GET /api/getUserDetailByEmail?email=<unregistered email>
2. GET /api/getUserDetailByEmail with no `email` param
Expected:
- Variant 1 observed: responseCode 404, message "Account not found with this email, try another email!", HTTP 200; no `user` object
- Variant 2 observed: responseCode 400, message "Bad request, email parameter is missing in GET request.", HTTP 200; no `user` object
Data: TestUsers.newUser().email() without registering it
Cleanup: none

## TC-APIACCOUNT-10 Wrong HTTP methods on the account endpoints (record observed behaviour)  [P2] [api] [regression]
Preconditions: none; an unregistered generated email and valid-looking form data are sent; no account is created or touched
Steps (data variants, each asserted separately with the raw body recorded first):
1. GET, then PUT, on /api/createAccount
2. POST, then GET, on /api/deleteAccount
3. POST, then DELETE, on /api/updateAccount
4. POST, then DELETE, on /api/getUserDetailByEmail
Expected:
- Observed: GET/PUT createAccount, POST/GET deleteAccount, POST/DELETE updateAccount return real HTTP 405 with JSON `{"detail": "Method \"<M>\" not allowed."}` (no responseCode). POST/DELETE getUserDetailByEmail return HTTP 200 with body responseCode 405 "This request method is not supported." (same as api-auth)
- Comparison point (not assumed): the api-auth methods return 405 "This request method is not supported."
Data: generated unregistered email; password "SomePass1"
Cleanup: none; if any combination unexpectedly creates an account, delete it via AccountApi.delete

## TC-APIACCOUNT-11 Update with unknown email and with wrong password (record observed behaviour)  [P2] [api] [regression]
Preconditions: a user created via AccountApi.create (used only by the wrong-password variant); a second generated email never registered for the unknown-email variant
Steps (data variants, each asserted separately):
1. PUT /api/updateAccount for the unregistered email with the full field set
2. GET /api/getUserDetailByEmail for that unregistered email
3. PUT /api/updateAccount for the real user's email with password "Wrong#Pass1" and name "Hacked Name"
4. GET /api/getUserDetailByEmail for the real user
Expected:
- Step 1 observed: responseCode 404, message "Account not found!", HTTP 200
- Step 2 observed: responseCode 404 "Account not found with this email, try another email!" (no account created)
- Step 3 observed: responseCode 404, message "Account not found!" (rejected)
- Step 4 `user` name is still the original name, not "Hacked Name" (record if the site instead changed it)
Data: TestUsers.newUser() for the real user; TestUsers.newUser().email() unregistered
Cleanup: delete the real account via AccountApi.delete(email, password); delete the unregistered email too if step 1 created it

## TC-APIACCOUNT-12 Password change via update persists (record observed behaviour)  [P3] [api] [regression]
Preconditions: a user created via AccountApi.create with password "OldPass#1"
Steps:
1. PUT /api/updateAccount with all fields resent and password changed to "NewPass#2"
2. POST /api/verifyLogin with the email and "NewPass#2"
3. POST /api/verifyLogin with the email and "OldPass#1"
Expected:
- Step 1 observed: responseCode 404, message "Account not found!" (update authenticates with the current password, so a new password is treated as wrong; the password is not changed)
- Step 2 observed: responseCode 404, message "User not found!"
- Step 3 observed: responseCode 200, message "User exists!"
- HTTP status is 200 (per site-map)
Data: TestUsers.newUser(); old password "OldPass#1", new password "NewPass#2"
Cleanup: delete account via AccountApi.delete(email, current password); the teardown must use the password in effect at the end of the test
