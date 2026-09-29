# Test cases: login

Plan: tests/plans/login.md

Common setup for every case unless stated: create a user via `AccountApi` using `TestUsers.newUser()` (assert responseCode 201, "User created!"), call `registerForCleanup(user)`, start logged out. Ads blocked via `BaseUiTest`; dismiss interstitials with `Ads.dismissIfPresent`.

## TC-LOGIN-01 Login with valid credentials (site TC2, account created moments earlier)  [P1] [ui] [smoke]
Preconditions: a registered user created via AccountApi immediately before the UI step (also covers plan scenario 17: no stale-account failure)
Steps:
1. Open home page, click "Signup / Login"
2. Verify "Login to your account" is visible
3. Enter the user's email and password in the Login form; click Login
Expected:
- URL is the home page
- Header shows "Logged in as <name>"
- "Logout" and "Delete Account" links are visible
- "Signup / Login" link is no longer visible
Data: TestUsers.newUser()
Cleanup: delete account via AccountApi.delete(email, password)

## TC-LOGIN-02 Login with unregistered email shows error (site TC3)  [P1] [ui] [smoke]
Preconditions: none needing a server account; generate a valid-format email that was never registered
Steps:
1. Open /login
2. Enter the unregistered email and any password; click Login
Expected:
- Text "Your email or password is incorrect!" is visible
- URL is still /login
- Header still shows "Signup / Login"
Data: TestUsers.newUser().email() without registering it
Cleanup: none

## TC-LOGIN-03 Wrong password is rejected, including different-case password  [P1] [ui] [regression]
Preconditions: a registered user
Steps:
1. Open /login
2. Enter the registered email with a wrong password; click Login
3. Reload /login; enter the registered email with the correct password in altered letter case (swap case); click Login
Expected:
- After step 2 and after step 3: "Your email or password is incorrect!" is visible
- URL is /login; header shows "Signup / Login" (not logged in)
Data: TestUsers.newUser(); wrong password "Wrong#Pass1"; case-swapped variant of user's password (ensure it differs from original)
Cleanup: delete account via API

## TC-LOGIN-04 Required and format validation blocks submit  [P2] [ui] [regression]
Preconditions: none (no account needed); on /login
Steps (run as data variants, each on a freshly loaded /login):
1. Leave both fields empty; click Login
2. Enter email only; click Login
3. Enter password only; click Login
4. Enter "notanemail" (no "@") and a password; click Login
Expected:
- For each variant: URL stays /login, header shows "Signup / Login", "Your email or password is incorrect!" is NOT shown (browser-native validation blocked the submit; do not assert on native message text)
Data: email "notanemail"; password "SomePass1"; other email a generated valid-format one
Cleanup: none

## TC-LOGIN-05 Hostile and oversized input does not log in or break the page  [P2] [ui] [regression]
Preconditions: none; on /login
Steps:
1. Enter email `' OR '1'='1` and password `' OR '1'='1`; click Login
2. Reload /login; enter email `<script>alert(1)</script>@example.com` and password `<script>alert(1)</script>`; click Login
3. Reload /login; enter a 300+ character email (local part padded, valid format) and a 300+ character password; click Login
Expected:
- Each attempt: not logged in (header shows "Signup / Login")
- Either the page shows "Your email or password is incorrect!" or submit is blocked by format validation (stays on /login)
- No JS dialog appears; the login form is still visible and usable
Data: strings as above
Cleanup: none

## TC-LOGIN-06 Session persists across pages and reload  [P1] [ui] [regression]
Preconditions: a registered user
Steps:
1. Log in via /login
2. Navigate to /products
3. Reload the page
Expected:
- After login, on /products, and after reload: header shows "Logged in as <name>"
- "Signup / Login" link is not visible on any of them
Data: TestUsers.newUser()
Cleanup: delete account via API

## TC-LOGIN-07 Cart item added while logged out is kept after login (site TC20 behaviour)  [P2] [ui] [regression]
Preconditions: a registered user; logged out with empty cart
Steps:
1. Open /products; add the first product to the cart; note its name; dismiss the "Added!" modal via "Continue Shopping"
2. Open /login and log in with the user's credentials
3. Open /view_cart
Expected:
- Header shows "Logged in as <name>"
- Cart table contains the noted product
Data: TestUsers.newUser(); first listed product
Cleanup: remove item from cart if the page allows; delete account via API

## TC-LOGIN-08 Correct login succeeds after a failed attempt on the same page  [P2] [ui] [regression]
Preconditions: a registered user
Steps:
1. Open /login; enter the registered email and a wrong password; click Login
2. Verify the error is shown
3. Without leaving the page, replace the password with the correct one; click Login
Expected:
- After step 2: "Your email or password is incorrect!" visible
- After step 3: header shows "Logged in as <name>"; URL is the home page
Data: TestUsers.newUser(); wrong password "Wrong#Pass1"
Cleanup: delete account via API

## TC-LOGIN-09 Deleted account cannot log in  [P2] [ui] [regression]
Preconditions: a registered user created then deleted via AccountApi.delete before the UI step (assert responseCode 200, "Account deleted!")
Steps:
1. Open /login
2. Enter the deleted user's email and password; click Login
Expected:
- "Your email or password is incorrect!" visible
- URL is /login; header shows "Signup / Login"
Data: TestUsers.newUser()
Cleanup: none beyond the delete already done (registerForCleanup may still call delete; tolerate "Account not found")

## TC-LOGIN-10 Email letter case and surrounding spaces (record actual behaviour)  [P3] [ui] [regression]
Preconditions: a registered user with a mixed-case-capable email (letters present)
Steps:
1. Open /login; enter the registered email upper-cased with the correct password; click Login; observe outcome
2. Log out state reset (use a fresh context or open /login after clearing cookies); enter the email wrapped in leading and trailing spaces with the correct password; click Login; observe outcome
Expected:
- Test asserts the behaviour observed on the live site for each step (either "Logged in as <name>" or "Your email or password is incorrect!"); the automation writer records the observed result in a comment and asserts it. Neither outcome may show a server error page.
Data: TestUsers.newUser()
Cleanup: delete account via API

## TC-LOGIN-11 Logged-in user opens /login directly (record actual behaviour)  [P3] [ui] [regression]
Preconditions: a registered user, logged in via the UI
Steps:
1. Navigate directly to /login
Expected:
- Header still shows "Logged in as <name>" (session not lost)
- Observed behaviour: the user is redirected to the home page (URL is <baseUrl>/), the login form heading is not shown (plan expected the form to still be shown)
Data: TestUsers.newUser()
Cleanup: delete account via API

## TC-LOGIN-12 API verifyLogin agrees with UI login for the same credentials  [P3] [api] [regression]
Preconditions: a registered user
Steps:
1. POST /api/verifyLogin with form fields email and password of the user
2. Log in via the UI with the same credentials
Expected:
- API JSON body responseCode is 200 and message is "User exists!"
- UI shows "Logged in as <name>" in the header
Data: TestUsers.newUser()
Cleanup: delete account via API
