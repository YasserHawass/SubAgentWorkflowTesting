# Test cases: register

Plan: tests/plans/register.md

Common setup for every case unless stated: fresh user from `TestUsers.newUser()`; call `registerForCleanup(user)` BEFORE submitting any signup form; cleanup must tolerate an already-deleted account ("Account not found"). Start logged out. Ads blocked via `BaseUiTest`; dismiss interstitials with `Ads.dismissIfPresent`. Where a case needs an existing account, create it via `AccountApi` and assert responseCode 201, "User created!" first. "Record observed behaviour" items: the writer probes the live site once, then the test asserts that single recorded outcome (no either/or).

## TC-REGISTER-01 Full registration flow (site TC1, plan scenarios 1-3)  [P1] [ui] [smoke]
Preconditions: none (user not yet registered)
Steps:
1. Open home page; verify it is visible; click "Signup / Login"
2. Verify "New User Signup!" is visible; enter name and fresh email; click Signup
3. Verify "ENTER ACCOUNT INFORMATION" is visible on /signup
4. Fill title, password, date of birth; tick "Sign up for our newsletter!" and "Receive special offers from our partners!"; fill first name, last name, company, address, address2, country, state, city, zipcode, mobile number
5. Click Create Account
6. Verify "ACCOUNT CREATED!" is shown on /account_created; click Continue
Expected:
- After step 3: URL is /signup and "ENTER ACCOUNT INFORMATION" is visible
- After step 6 (before Continue): URL is /account_created and "ACCOUNT CREATED!" is visible
- After Continue: header shows "Logged in as <name>"; "Logout" and "Delete Account" links visible; "Signup / Login" link not visible
Data: TestUsers.newUser() (all form fields); country value from the dropdown's fixed list
Cleanup: delete account via AccountApi.delete(email, password)

## TC-REGISTER-02 Delete account via UI, then login with same credentials fails (site TC1 tail, plan scenarios 4-5)  [P1] [ui] [smoke]
Preconditions: a registered user created via AccountApi, logged in through the UI (/login)
Steps:
1. Click "Delete Account" in the header
2. Verify the deletion confirmation; click Continue
3. Open /login; enter the deleted user's email and password in the Login form; click Login
Expected:
- After step 1: URL is /delete_account and "ACCOUNT DELETED!" is visible
- After step 2: URL is no longer /delete_account; header shows "Signup / Login"
- After step 3: "Your email or password is incorrect!" is visible; URL stays /login; header shows "Signup / Login"
Data: TestUsers.newUser()
Cleanup: none beyond the UI delete; registerForCleanup delete must not fail the test

## TC-REGISTER-03 Signup with an existing email shows error (site TC5, plan scenario 6)  [P1] [ui] [regression]
Preconditions: an account created via AccountApi
Steps:
1. Open /login
2. In the "New User Signup!" form enter any name and the existing account's email; click Signup
Expected:
- Text "Email Address already exist!" is visible
- URL is /signup (form POST target); login page re-rendered with the error
- "ENTER ACCOUNT INFORMATION" is not shown (no /signup form)
Data: TestUsers.newUser() (account created via API)
Cleanup: delete account via API

## TC-REGISTER-04 Signup with existing email in different letter case (record observed behaviour)  [P3] [ui] [regression]
Preconditions: an account created via AccountApi with an email containing letters
Steps:
1. Open /login
2. In the Signup form enter any name and the existing email upper-cased; click Signup
3. Probe live once; record the outcome
Expected:
- Record observed behaviour; test asserts that single outcome (either the "Email Address already exist!" error on /login, or the "ENTER ACCOUNT INFORMATION" form on /signup; the writer fixes whichever is observed)
- If the account form is reached, no account is completed in this test and no second account is created
Data: TestUsers.newUser(); email upper-cased
Cleanup: delete account via API (also delete the upper-cased email variant via API if it was somehow created)

## TC-REGISTER-05 Signup form blocks submit for empty name, empty email, malformed email (plan scenarios 8-10)  [P2] [ui] [regression]
Preconditions: none (no account needed); on /login
Steps (data variants, each on a freshly loaded /login; fill the Signup form fields, not the Login form fields):
1. Name empty, email filled; click Signup
2. Name filled, email empty; click Signup
3. Name filled, email "notanemail" (no "@"); click Signup
Expected:
- Each variant: URL stays /login; "ENTER ACCOUNT INFORMATION" is not shown; "Email Address already exist!" is not shown (browser-native validation blocked submit; do not assert native message text)
Data: name from TestUsers.newUser(); generated valid-format email; "notanemail"
Cleanup: none

## TC-REGISTER-06 Required fields on /signup (plan scenarios 11-13, record observed behaviour)  [P2] [ui] [regression]
Preconditions: on /signup reached via a fresh name + email from /login; no account created yet
Steps (one variant per field, each starting from a fresh /login -> /signup pass, all other fields valid):
1. Leave password empty; click Create Account
2. Leave each of first name, last name, address, state, city, zipcode, mobile number empty one at a time; click Create Account
3. Leave company and address2 empty (all else valid); click Create Account
4. Probe live once; record which fields are required
Expected:
- Password empty: not created; URL stays /signup; "ACCOUNT CREATED!" not shown
- For each field recorded as required: same as above (submit blocked, stays on /signup)
- For each field recorded as optional (expected: company, address2): record observed behaviour; test asserts that single outcome (expected: "ACCOUNT CREATED!" visible on /account_created)
- The writer fixes the required/optional split from the probe before asserting
Data: TestUsers.newUser() fields
Cleanup: delete account via API (needed for any variant that creates the account)

## TC-REGISTER-07 Special characters, unicode, long and HTML names (plan scenarios 14-15, record observed behaviour)  [P3] [ui] [regression]
Preconditions: none; each variant runs the full signup flow with a fresh email
Steps:
1. Sign up (full flow, click Continue on /account_created) with name "Zoë O'Neil"
2. Repeat with a 100+ character name
3. Repeat with name `<b>x</b>`
4. Probe live once; record outcome for each
Expected:
- For each variant record observed behaviour; test asserts that single outcome (accepted as typed, rejected, or truncated), with the header "Logged in as <name>" matching the recorded name
- HTML name: no JS dialog and no script executes; header shows the text escaped or stripped as recorded; "<b>" does not render as bold markup
Data: names as above; fresh TestUsers.newUser() per variant
Cleanup: delete each created account via API (use the created email/password)

## TC-REGISTER-08 /signup prefill and date-of-birth boundaries (plan scenarios 16-17, record observed behaviour)  [P3] [ui] [regression]
Preconditions: none; reach /signup from /login with a fresh name and email
Steps:
1. On /signup observe the name and email fields; note whether they are prefilled and whether email is read-only
2. Note the first and last year offered in the DOB year dropdown
3. Select 29 Feb of a non-leap year, then 31 in a 30-day month; complete the other required fields; click Create Account
4. Probe live once; record outcome
Expected:
- Name and email equal the values entered on /login (record observed behaviour; test asserts that single outcome for read-only or editable email)
- Record observed behaviour for invalid dates; test asserts that single outcome (blocked, or account created with the date accepted)
Data: TestUsers.newUser(); DOB values above
Cleanup: delete account via API (if created)

## TC-REGISTER-09 Session persists after signup; logged-in user opening /login is redirected (plan scenarios 18, 20)  [P2] [ui] [regression]
Preconditions: a user completing UI signup (or created via API and logged in; writer picks the faster reliable path but the account must be logged in via the UI)
Steps:
1. Complete signup and click Continue on /account_created
2. Navigate to /products; reload
3. Navigate directly to /login
Expected:
- After step 1, on /products and after reload: header shows "Logged in as <name>"; "Signup / Login" not visible
- After step 3: URL is the home page (<baseUrl>/); "New User Signup!" heading is not visible; header still shows "Logged in as <name>"
Data: TestUsers.newUser()
Cleanup: delete account via API

## TC-REGISTER-10 Cart contents after registering (plan scenario 19, record observed behaviour)  [P2] [ui] [regression]
Preconditions: logged out with empty cart
Steps:
1. Open /products; add the first product to the cart; note its name; click "Continue Shopping"
2. Open /login; complete signup with a fresh user; click Continue on /account_created
3. Open /view_cart
4. Probe live once; record whether the product is still in the cart
Expected:
- Header shows "Logged in as <name>"
- Record observed behaviour; test asserts that single outcome (cart table contains the noted product, or cart is empty; site TC20 keeps it after login)
Data: TestUsers.newUser(); first listed product
Cleanup: remove cart item if present; delete account via API

## TC-REGISTER-11 Back navigation and reload after account creation (plan scenario 21, record observed behaviour)  [P2] [ui] [regression]
Preconditions: none; complete signup through Create Account so the account exists
Steps:
1. Complete signup up to "ACCOUNT CREATED!"
2. Use browser Back and, if the account form is shown, click Create Account again; then reload /signup
3. Probe live once; record outcome
Expected:
- Record observed behaviour; test asserts that single outcome (duplicate error, stale form, or redirect)
- In every case, exactly one account exists for the email: AccountApi/`/api/getUserDetailByEmail` returns responseCode 200 with one user record; no second account
Data: TestUsers.newUser()
Cleanup: delete account via API

## TC-REGISTER-12 API cross-check: details entered in UI signup are stored (plan scenario 22)  [P2] [api] [regression]
Preconditions: none; UI signup completed with known form values (name, title, DOB, first/last name, company, address, address2, country, state, city, zipcode, mobile)
Steps:
1. Complete UI signup to "ACCOUNT CREATED!"
2. GET /api/getUserDetailByEmail?email=<email>
Expected:
- JSON body responseCode is 200
- `user` name, email, first_name, last_name, address1, city, state, zipcode, country match the form values
Data: TestUsers.newUser()
Cleanup: delete account via API
