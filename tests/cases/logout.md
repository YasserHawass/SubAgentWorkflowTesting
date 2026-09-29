# Test cases: logout

Plan: tests/plans/logout.md

Common setup for every case unless stated: create a user via `AccountApi` using `TestUsers.newUser()` (assert responseCode 201, "User created!"), call `registerForCleanup(user)`. Start logged out on home, then log in through the UI (site TC4 steps 1-8: home visible, click "Signup / Login", "Login to your account" visible, enter credentials, click Login) and confirm "Logged in as <name>" is visible before acting. Ads blocked via `BaseUiTest`; call `Ads.dismissIfPresent` before clicking "Logout". Cleanup in every case: delete account via `AccountApi.delete(email, password)` (logout does not affect the account).

Items marked "probe" are unknown until the live site is observed. The automation-writer / case owner records the observed behaviour and the test asserts that single outcome (no either/or assertions).

## TC-LOGOUT-01 Logout from home ends on the login page (site TC4)  [P1] [ui] [smoke]
Preconditions: a registered user, logged in via the UI (common setup)
Steps:
1. Click "Logout" in the header
Expected:
- URL is /login (site-map: `/logout` redirects to `/login`; assert the URL, not just the heading)
- "Login to your account" is visible
Data: TestUsers.newUser()
Cleanup: delete account via API

## TC-LOGOUT-02 Header returns to logged-out state after logout  [P1] [ui] [regression]
Preconditions: a registered user, logged in via the UI
Steps:
1. Click "Logout"
2. Inspect the header
Expected:
- "Signup / Login" link is visible
- "Logout" link, "Delete Account" link and "Logged in as <name>" are not visible
Data: TestUsers.newUser()
Cleanup: delete account via API

## TC-LOGOUT-03 Checkout is not accessible after logout  [P2] [ui] [regression]
Preconditions: a registered user, logged in via the UI, then logged out via "Logout"
Steps:
1. Navigate directly to /checkout
Expected:
- Probe: record the observed landing URL and page content (site-map says `/checkout` requires login; whether it redirects to /login or shows something else is unverified). Test asserts that single observed outcome.
- Header does not show "Logged in as <name>"; "Signup / Login" is visible
Data: TestUsers.newUser()
Cleanup: delete account via API

## TC-LOGOUT-04 Browser Back after logout does not restore the session  [P2] [ui] [regression]
Preconditions: a registered user, logged in via the UI, on the home page
Steps:
1. Click "Logout"
2. Press the browser Back button
Expected:
- Probe: record which page is shown (URL and content) and the header state after Back. Test asserts that single observed outcome.
- Intended check: header does not show "Logged in as <name>" (assert only if the live site agrees; if the site shows a stale logged-in header, record it as a product finding instead of asserting)
Data: TestUsers.newUser()
Cleanup: delete account via API

## TC-LOGOUT-05 Cart contents after logout (record actual behaviour)  [P2] [ui] [regression]
Preconditions: a registered user, logged in via the UI
Steps:
1. Open /products; add the first product to the cart; note its name; dismiss the "Added!" modal via "Continue Shopping"
2. Click "Logout"
3. Open /view_cart
Expected:
- Probe: record whether the cart table still contains the noted product (server-side cart) or is empty (session cart). Test asserts that single observed outcome.
- Header shows "Signup / Login" (user not logged in)
Data: TestUsers.newUser(); first listed product
Cleanup: remove item from cart if possible; delete account via API

## TC-LOGOUT-06 Log in again with the same credentials after logout  [P2] [ui] [regression]
Preconditions: a registered user, logged in via the UI, then logged out via "Logout"
Steps:
1. On /login, enter the same email and password in the Login form; click Login
Expected:
- Header shows "Logged in as <name>"
- "Logout" and "Delete Account" links are visible; "Signup / Login" is not visible
- "Login to your account" heading is not shown
Data: TestUsers.newUser()
Cleanup: delete account via API

## TC-LOGOUT-07 Logout from a non-home page, then reload  [P2] [ui] [regression]
Preconditions: a registered user, logged in via the UI
Steps:
1. Navigate to /products
2. Click "Logout"
3. Reload the page
Expected:
- After step 2: probe: record the landing URL when logging out from /products (site-map states `/logout` redirects to `/login`; confirm it holds from a non-home page). Test asserts that single observed outcome.
- After step 3 (reload): header shows "Signup / Login"; "Logged in as <name>" is not visible
Data: TestUsers.newUser()
Cleanup: delete account via API

## TC-LOGOUT-08 Direct GET of /logout while logged out  [P3] [ui] [regression]
Preconditions: none needing a server account; fresh browser context, not logged in
Steps:
1. Navigate directly to /logout
Expected:
- no server error; logged-out header ('Signup / Login') visible
Data: none
Cleanup: none
