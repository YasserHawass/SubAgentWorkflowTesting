# Test cases: checkout

Plan: tests/plans/checkout.md

Common setup for every case unless stated: fresh browser context (empty cart, logged out), start on home (/). Ads blocked via `BaseUiTest`; call `Ads.dismissIfPresent` before clicking. Products are picked from the live grid by name; names, prices and quantities are read from /view_cart ("Rs. N" strings, parse digits), never hardcoded. The "Added!" modal is always left via its buttons before the next action. No case clicks "Place Order" (that belongs to `order`).

Logged-in cases: create the account via `AccountApi` with `TestUsers.newUser()` (assert responseCode 201 and "User created!"), call `registerForCleanup(user)`, log in via the UI. The TestUsers object (title, first/last name, company, address1, address2, city, state, zipcode, country, mobile) is the source of truth for every address check. Address values are never read back from the site to compare against itself.

Items marked "probe" are unknown until the live site is observed. The automation-writer records the observed behaviour and the test asserts that single outcome. Exact texts and URLs appear in Expected only where site TC14/TC23 or docs/site-map.md states them.

## TC-CHECKOUT-01 Guest "Proceed To Checkout" prompts to register or login (site TC14 steps 7-8)  [P1] [ui] [smoke]
Preconditions: empty cart, logged out
Steps:
1. Add a product to the cart from /products; click "View Cart" in the modal
2. Verify the cart page is displayed
3. Click "Proceed To Checkout"
Expected:
- A prompt appears offering a "Register / Login" link (label stated by site TC14)
- The URL is still /view_cart (probe: record and assert that single outcome)
- Probe: record the exact prompt text and the label of the second button; the test asserts the recorded values
Data: first product on /products
Cleanup: none (session cart only)
Observed (probe): in-page modal titled "Checkout", text "Register / Login account to proceed on checkout.", link "Register / Login" (href /login), dismiss button "Continue On Cart". URL stays /view_cart. The handler is bound at the end of the body, so the test waits for the load state before clicking.

## TC-CHECKOUT-02 Guest prompt "Register / Login" leads to /login and the cart is kept (site TC14 step 8)  [P1] [ui] [regression]
Preconditions: a registered user (API-created via `AccountApi`, `registerForCleanup(user)`); empty cart, logged out
Steps:
1. Add a product to the cart; open /view_cart; note the product name
2. Click "Proceed To Checkout", then click "Register / Login" in the prompt
3. On the login page, log in with the API-created account
4. Open /view_cart
Expected:
- After step 2 the page shows "New User Signup!" and "Login to your account" (site-map: both forms on /login)
- After login, the header shows "Logged in as <name>"
- The noted product is still in the cart (site-map: cart survives login)
Data: TestUsers.newUser(); first product on /products
Cleanup: delete account via AccountApi.delete(email, password)
Observed: the login page shows both headings at URL /login; the cart product is still in /view_cart after login.

## TC-CHECKOUT-03 Guest prompt second button closes it and leaves the cart unchanged  [P3] [ui] [regression]
Preconditions: empty cart, logged out
Steps:
1. Add a product to the cart; open /view_cart; note the row and total
2. Click "Proceed To Checkout"
3. Click the prompt's dismiss button (probe: label recorded in TC-CHECKOUT-01; plan suggests "Continue On Cart")
Expected:
- The prompt is no longer visible
- The URL is /view_cart
- The cart row, quantity and total equal the noted values
Data: first product on /products
Cleanup: none
Observed: "Continue On Cart" hides the modal; URL stays /view_cart; row, quantity 1 and total unchanged.

## TC-CHECKOUT-04 Delivery address matches the registered account (site TC23)  [P1] [ui] [smoke]
Preconditions: a registered user (API-created via `AccountApi` with all address fields sent, `registerForCleanup(user)`); logged in via the UI; one product in the cart
Steps:
1. Open /view_cart and click "Proceed To Checkout"
2. Verify "Address Details" and "Review Your Order" are visible
3. Read the delivery address block
Expected:
- URL is /checkout
- "Address Details" and "Review Your Order" are visible (site TC14)
- The delivery block contains the TestUsers values: first and last name, company, address1, address2, city, state, zipcode, country and mobile. Each value is asserted against the TestUsers object, not against the billing block
- Probe: record the exact line layout (title prefix, order of city/state/zipcode, country display); the test asserts that single layout using the TestUsers values
Data: TestUsers.newUser() with every address field populated
Cleanup: delete account via AccountApi.delete(email, password)
Observed (probe): lines in order: title "Your delivery address", "Mr. Auto Tester" (title + "." prefix, first last), company, address1, address2, "San Jose California 95101" (city state zipcode, one line), "United States", mobile as sent ("+10000000000"). The heading text is upper-cased by CSS; assert textContent.

## TC-CHECKOUT-05 Billing address matches the registered account and the delivery block (site TC23)  [P1] [ui] [regression]
Preconditions: as TC-CHECKOUT-04
Steps:
1. Open /view_cart and click "Proceed To Checkout"
2. Read the billing address block
Expected:
- The billing block contains the same TestUsers values as TC-CHECKOUT-04: first and last name, company, address1, address2, city, state, zipcode, country and mobile
- Each value is asserted against the TestUsers object
- Probe: record whether title and name are prefixed in the billing block; the test asserts that single outcome
- Probe: record whether the billing lines are identical to the delivery lines; the test asserts that single outcome
Data: TestUsers.newUser() with every address field populated
Cleanup: delete account via AccountApi.delete(email, password)
Observed (probe): billing block has the same layout as delivery, title "Your billing address", and the name IS prefixed with the title ("Mr. Auto Tester"). All lines after the title are identical to the delivery block.

## TC-CHECKOUT-06 Order review lists each cart row with correct totals  [P1] [ui] [regression]
Preconditions: API-created account, logged in; two different products in the cart with quantities 1 and 3 (added from their detail pages)
Steps:
1. Open /view_cart; note name, price, quantity and total for each row
2. Click "Proceed To Checkout"
3. Read the "Review Your Order" table
Expected:
- One row per cart row, no duplicates
- Each row shows the same name, price and quantity as noted, and total = price x quantity
- Probe: record whether a grand total row exists; the test asserts that single outcome, and if it exists, that it equals the sum of the row totals
Data: TestUsers.newUser(); two products; quantities 1 and 3
Cleanup: delete account via AccountApi.delete(email, password)
Observed (probe): a "Total Amount" row exists and equals the sum of the row totals (price x quantity per row). Row cells use .cart_price/.cart_quantity/.cart_total_price like /view_cart.

## TC-CHECKOUT-07 Comment textarea accepts text and "Place Order" is available  [P2] [ui] [regression]
Preconditions: API-created account, logged in; one product in the cart; on /checkout
Steps:
1. Type a 500+ character comment containing `<b>x</b>` into the comment textarea
2. Do not click "Place Order"
Expected:
- The textarea is visible and holds the typed text
- The page shows no error and stays on /checkout
- "Place Order" is visible and enabled (site TC14/site-map)
- Probe: record the textarea placeholder or label; the test asserts that single outcome
Data: TestUsers.newUser(); comment of 500+ chars including `<b>x</b>`
Cleanup: delete account via AccountApi.delete(email, password)
Observed (probe): no placeholder and no linked label; the text above the box is "If you would like to add a comment about your order, please write it in the field below." A 500+ char comment with <b>x</b> is accepted, URL stays /checkout, "Place Order" (a link to /payment) is visible and enabled.

## TC-CHECKOUT-08 Logged-in user opens /checkout with an empty cart  [P2] [ui] [regression]
Preconditions: API-created account, logged in; cart empty
Steps:
1. Navigate directly to /checkout
Expected:
- Probe: record whether the site redirects, shows an empty review table, or shows addresses with no rows; the test asserts that single outcome
- Probe: record whether "Place Order" is still shown; the test asserts that single outcome
- Header shows "Logged in as <name>"
Data: TestUsers.newUser()
Cleanup: delete account via AccountApi.delete(email, password)
Observed (probe): no redirect. /checkout shows both address blocks filled from the account, zero product rows, "Total Amount" Rs. 0 and "Place Order" visible. Flagged: an empty cart can still reach Place Order.

## TC-CHECKOUT-09 Guest opens /checkout directly  [P2] [ui] [regression]
Preconditions: empty cart, logged out
Steps:
1. Navigate directly to /checkout
Expected:
- No redirect: the URL stays /checkout (site-map, logout section)
- "Address Details" is visible, the total shows Rs. 0, "Place Order" is visible and the header shows "Signup / Login" (site-map)
- Probe: record whether the address blocks are empty; the test asserts that single outcome
Data: none
Cleanup: none
Observed (probe): no redirect; both address blocks render with empty lines except the name line, which is a stray "."; Total Amount Rs. 0; "Place Order" visible; header "Signup / Login".

## TC-CHECKOUT-10 Checkout page is stable across reload and Back to the cart  [P2] [ui] [regression]
Preconditions: API-created account, logged in; one product in the cart
Steps:
1. Open /view_cart and click "Proceed To Checkout"; note the address blocks and review rows
2. Reload /checkout
3. Press Back to return to /view_cart
Expected:
- After reload, the addresses and review rows equal the noted values, with no duplicated rows
- After Back, the URL is /view_cart and the cart row and quantity are unchanged
Data: TestUsers.newUser(); first product on /products
Cleanup: delete account via AccountApi.delete(email, password)
Observed: after reload the address blocks and the single review row are identical; Back returns to /view_cart with the same row and quantity 1.
