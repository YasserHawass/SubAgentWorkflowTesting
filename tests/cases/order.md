# Test cases: order

Plan: tests/plans/order.md

Common setup for every case unless stated: fresh browser context (empty cart), start on home (/). Ads blocked via `BaseUiTest`; call `Ads.dismissIfPresent` before clicking on /view_cart, /checkout and /payment. Every case uses its own fresh user from `TestUsers.newUser()`, either created via `AccountApi` (assert responseCode 201 "User created!") or registered in-flow (TC-ORDER-01 and TC-ORDER-03). Each user is passed to `registerForCleanup(user)` right after creation so cleanup runs even on failure. Cleanup calls `AccountApi.delete(email, password)` and must tolerate an already-deleted account (API returns 404 "Account not found!").

Card data is fake only: name on card "Auto Tester", number 4111111111111111, CVC 123, expiry month 12, expiry year = current year + 2. Product for orders: product id 1 (added through the UI, or via `/add_to_cart/1?quantity=1`). The comment on /checkout is any short string.

Items marked "probe" are unknown until the live site is observed. The automation-writer records the observed behaviour and the test asserts that single outcome. Exact texts and URLs appear in Expected only where docs/site-map.md states them. The site TC success and "account deleted" texts are probes until confirmed live. The order id in /payment_done/<id> is dynamic: match the URL by pattern, never a fixed id. Use `waitForURL` on /payment and /payment_done, not sleeps.

## TC-ORDER-01 Register before checkout, then place an order (site TC15)  [P1] [ui] [smoke]
Preconditions: logged out, empty cart, no account exists for the generated user
Steps:
1. Verify the home page is visible
2. Click "Signup / Login", sign up with the generated name and email, fill the account form, create the account
3. Verify the account-created confirmation is shown (probe: record the exact text); click "Continue"
4. Verify the header shows "Logged in as <name>"
5. Add product 1 to the cart and open the cart
6. Click "Proceed To Checkout"; enter a comment; click "Place Order"
7. On /payment fill the fake card data and click "Pay and Confirm Order"
Expected:
- Header shows "Logged in as <name>" in step 4
- URL matches /payment_done/<id> after step 7
- Probe: the confirmation text. Site TC says "Your order has been placed successfully!", the live site may differ. Record the exact live text; the test asserts that single text
- "Download Invoice" and "Continue" are visible on /payment_done
Data: TestUsers.newUser(); product 1; fake card as above
Cleanup: `registerForCleanup(user)`; delete account via AccountApi.delete(email, password)
Observed (live, 2026-09-29): Signup confirmation heading "Account Created!". /payment_done/500 (id = cart total in Rs.). Heading (data-qa order-placed) "Order Placed!", text "Congratulations! Your order has been confirmed!" (not the site TC15 wording). Download Invoice links to /download_invoice/500, Continue links to /. Card name used: "Auto Tester".

## TC-ORDER-02 Log in before checkout, place an order, then delete the account (site TC16)  [P1] [ui] [smoke]
Preconditions: a registered user created via `AccountApi` with `TestUsers.newUser()`, `registerForCleanup(user)`; logged out, empty cart
Steps:
1. Verify the home page is visible
2. Click "Signup / Login" and log in with the API-created account
3. Verify the header shows "Logged in as <name>"
4. Add product 1 to the cart and open the cart
5. Click "Proceed To Checkout"; enter a comment; click "Place Order"
6. On /payment fill the fake card data and click "Pay and Confirm Order"
7. Click "Delete Account" in the header
Expected:
- Header shows "Logged in as <name>" in step 3
- URL matches /payment_done/<id> after step 6; the confirmation text is the one recorded in TC-ORDER-01 (probe)
- After step 7: URL is /delete_account and the account-deleted element (data-qa "account-deleted") is visible; probe: record its exact text
- Probe: where "Continue" on the account-deleted page leads; record it and assert that single outcome, with the header showing the logged-out state ("Signup / Login")
Data: TestUsers.newUser(); product 1; fake card as above
Cleanup: `registerForCleanup(user)`; the account is already deleted by the flow, so cleanup must tolerate 404 "Account not found!"
Observed (live, 2026-09-29): Same success text as TC-ORDER-01. After Delete Account: /delete_account, data-qa account-deleted text "Account Deleted!", page also says "Your account has been permanently deleted!". Continue leads to / with the logged-out header ("Signup / Login").

## TC-ORDER-03 Register during checkout, then place an order and delete the account (site TC14)  [P1] [ui] [regression]
Preconditions: logged out, empty cart, no account exists for the generated user
Steps:
1. Verify the home page is visible
2. Add product 1 to the cart and open the cart
3. Click "Proceed To Checkout"
4. Probe: record whether a checkout-prompt modal with a "Register / Login" link appears while logged out. If it appears, click "Register / Login"; otherwise click "Signup / Login" in the header. The test asserts that single recorded outcome
5. Sign up with the generated name and email, fill the account form, create the account, click "Continue"
6. Open the cart again
7. Click "Proceed To Checkout"; enter a comment; click "Place Order"
8. On /payment fill the fake card data and click "Pay and Confirm Order"
9. Click "Delete Account" in the header
Expected:
- The cart still contains product 1 after registration (site-map: the cart survives registration)
- Header shows "Logged in as <name>" after step 5
- URL matches /payment_done/<id> after step 8; the confirmation text is the one recorded in TC-ORDER-01 (probe)
- After step 9: URL is /delete_account and the account-deleted element (data-qa "account-deleted") is visible; text as recorded in TC-ORDER-02 (probe)
Data: TestUsers.newUser(); product 1; fake card as above
Cleanup: `registerForCleanup(user)`; the account is already deleted by the flow, so cleanup must tolerate 404 "Account not found!"
Observed (live, 2026-09-29): Guest "Proceed To Checkout" shows the in-page modal with a "Register / Login" link (URL stays /view_cart); it leads to /login. Cart keeps Blue Top after registration. Rest as TC-ORDER-01/02.

## TC-ORDER-04 Continue after an order returns to the site and the cart is empty  [P1] [ui] [regression]
Preconditions: a registered user created via `AccountApi`, `registerForCleanup(user)`; logged in via the UI; product 1 in the cart
Steps:
1. Open /view_cart, click "Proceed To Checkout", click "Place Order", fill the fake card data, click "Pay and Confirm Order"
2. On /payment_done/<id> click "Continue"
3. Open /view_cart
Expected:
- Probe: the URL reached after "Continue"; record it (likely home) and assert that single outcome
- After step 3 "Cart is empty! Click here to buy products." is visible (site-map: text observed on an empty cart)
- Header still shows "Logged in as <name>"
Data: TestUsers.newUser(); product 1; fake card as above
Cleanup: delete account via AccountApi.delete(email, password)
Observed (live, 2026-09-29): Continue leads to /. /view_cart then shows "Cart is empty! Click here to buy products."; header stays logged in.

## TC-ORDER-05 Submit /payment with all fields empty  [P2] [ui] [regression]
Preconditions: a registered user created via `AccountApi`, `registerForCleanup(user)`; logged in via the UI; product 1 in the cart; on /payment via cart, "Proceed To Checkout" and "Place Order"
Steps:
1. Leave all card fields empty
2. Click "Pay and Confirm Order"
Expected:
- Probe: record whether HTML5 required validation blocks the submit (URL stays /payment) or the order goes through. The test asserts that single recorded outcome
- If blocked: no /payment_done URL is reached. If the order goes through: log as a product bug candidate (no validation)
Data: empty card fields
Cleanup: delete account via AccountApi.delete(email, password)
Observed (live, 2026-09-29): HTML5 required blocks the submit (all five inputs have required, valueMissing true); URL stays /payment, no request sent. Sane.

## TC-ORDER-06 Non-numeric card number and CVC  [P2] [ui] [regression]
Preconditions: a registered user created via `AccountApi`, `registerForCleanup(user)`; logged in via the UI; product 1 in the cart; on /payment via cart, "Proceed To Checkout" and "Place Order"
Steps:
1. Enter name "Test User", card number "abcd", CVC "xyz", expiry month 12, expiry year = current year + 2
2. Click "Pay and Confirm Order"
Expected:
- Probe: record whether the input is rejected (URL stays /payment) or accepted (URL matches /payment_done/<id>). The test asserts that single recorded outcome
- If accepted: log as a product bug candidate (no card format validation)
- If a Django debug page appears, record it as a product bug (DEBUG is on, see bugs/logout-1.md)
Data: card number "abcd", CVC "xyz"
Cleanup: delete account via AccountApi.delete(email, password)
Observed (live, 2026-09-29): PRODUCT BUG CANDIDATE: "abcd"/"xyz" is accepted, URL /payment_done/500. Test asserts rejection (stays on /payment) and is red.

## TC-ORDER-07 Expiry date in the past  [P2] [ui] [regression]
Preconditions: a registered user created via `AccountApi`, `registerForCleanup(user)`; logged in via the UI; product 1 in the cart; on /payment via cart, "Proceed To Checkout" and "Place Order"
Steps:
1. Enter name "Test User", card number 4111111111111111, CVC 123, expiry month 01, expiry year 2000
2. Click "Pay and Confirm Order"
Expected:
- Probe: record whether the past expiry is rejected (URL stays /payment) or accepted (URL matches /payment_done/<id>). The test asserts that single recorded outcome
- If accepted: log as a product bug candidate (no expiry validation)
Data: expiry 01/2000, otherwise valid fake card
Cleanup: delete account via AccountApi.delete(email, password)
Observed (live, 2026-09-29): PRODUCT BUG CANDIDATE: expiry 01/2000 is accepted, URL /payment_done/500. Test asserts rejection and is red.

## TC-ORDER-08 Open /payment directly while logged out with an empty cart  [P2] [ui] [regression]
Preconditions: a user created via `AccountApi` only to keep the case's account rule (`registerForCleanup(user)`); the browser stays logged out; empty cart
Steps:
1. Navigate directly to /payment
Expected:
- Probe: record the outcome (redirect, empty page, or error page) and assert that single outcome. Compare with /checkout, which renders empty when logged out (site-map)
- No order confirmation is shown and the URL does not match /payment_done/<id>
- If a Django debug page appears, record it as a product bug
Data: none
Cleanup: delete account via AccountApi.delete(email, password)
Observed (live, 2026-09-29): /payment renders the empty card form with a logged-out header, no redirect, no error page, no confirmation. Test asserts that (flagged: payment form offered to a logged-out user).

## TC-ORDER-09 Open /payment directly while logged in with an empty cart, then pay  [P2] [ui] [regression]
Preconditions: a registered user created via `AccountApi`, `registerForCleanup(user)`; logged in via the UI; empty cart
Steps:
1. Navigate directly to /payment
2. Probe: record what the page shows. If the form is present, fill the fake card data and click "Pay and Confirm Order"
Expected:
- Probe: record whether an order is created with nothing in it (URL matches /payment_done/<id>) or the request is rejected. The test asserts that single recorded outcome
- If reached, record the /payment_done text; an order with an empty cart is a product bug candidate
Data: fake card as above
Cleanup: delete account via AccountApi.delete(email, password)
Observed (live, 2026-09-29): PRODUCT BUG CANDIDATE: /payment renders the form for an empty cart and paying creates an order: /payment_done/0 with the normal confirmation. Test asserts rejection and is red.

## TC-ORDER-10 Reload /payment_done and go Back after ordering  [P3] [ui] [regression]
Preconditions: a registered user created via `AccountApi`, `registerForCleanup(user)`; logged in via the UI; product 1 ordered through the full flow, now on /payment_done/<id>
Steps:
1. Note the URL, then reload the page
2. Press Back, then Forward
3. Open /view_cart
Expected:
- Probe: record the page after reload and after Back/Forward (no server error page, no second confirmation flow). The test asserts that single outcome
- After step 3 "Cart is empty! Click here to buy products." is visible (cart stays empty)
- Probe: no duplicate order is observable in the UI; record if any order list exists (none known)
Data: TestUsers.newUser(); product 1; fake card as above
Cleanup: delete account via AccountApi.delete(email, password)
Observed (live, 2026-09-29): Reload keeps /payment_done/500 with the same confirmation. Back goes to the empty /payment form, Forward returns to /payment_done/500. No error page. Cart stays empty. No order list exists in the UI.
