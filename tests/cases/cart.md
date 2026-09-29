# Test cases: cart

Plan: tests/plans/cart.md

Common setup for every case unless stated: fresh browser context (empty cart, logged out), start on home (/). Ads blocked via `BaseUiTest`; call `Ads.dismissIfPresent` before clicking. Products are picked from the live grid by name; prices are read from the page ("Rs. N" strings, parse digits), never hardcoded. The "Added!" modal is always left via its buttons before the next action.

Items marked "probe" are unknown until the live site is observed. The automation-writer records the observed behaviour and the test asserts that single outcome. Exact texts and URLs appear in Expected only where a site TC (12/13/17/20/22) or docs/site-map.md states them.

## TC-CART-01 Add two products and view both in the cart (site TC12)  [P1] [ui] [smoke]
Preconditions: empty cart, logged out
Steps:
1. Verify the home page is visible
2. Click "Products"
3. Add the first product to the cart; note its name and price
4. In the modal click "Continue Shopping"
5. Add the second product to the cart; note its name and price
6. In the modal click "View Cart"
Expected:
- After each add, the modal shows "Added!"
- The cart lists both noted products
- Each row shows price x quantity = total (quantity 1, total equals the noted price)
- "Proceed To Checkout" is visible
Data: first and second product of the live /products grid
Cleanup: none (session cart only)

## TC-CART-02 "View Cart" in the modal opens the cart with quantity 1  [P1] [ui] [regression]
Preconditions: empty cart, logged out
Steps:
1. Open /products; add one product; note its name
2. In the modal click "View Cart"
Expected:
- URL is /view_cart
- The cart contains the noted product with quantity 1
Data: first product on /products
Cleanup: none

## TC-CART-03 "Continue Shopping" closes the modal and stays on the page  [P2] [ui] [regression]
Preconditions: empty cart, logged out
Steps:
1. Open /products; add one product
2. In the modal click "Continue Shopping"
Expected:
- The modal is no longer visible
- URL is still /products
- Observed: no cart count anywhere in the header; the Cart link text stays exactly "Cart" (asserted).
Data: first product on /products
Cleanup: none

## TC-CART-04 Set quantity 4 on the detail page and see it in the cart (site TC13)  [P1] [ui] [smoke]
Preconditions: empty cart, logged out
Steps:
1. Verify the home page is visible
2. Click "View Product" for any product on home
3. Verify the product detail page is open; note the price
4. Set quantity to 4
5. Click "Add to cart"
6. In the modal click "View Cart"
Expected:
- The detail page (/product_details/<id>) opens in step 2
- The cart row shows quantity 4
- The row total equals the noted price x 4
Data: any home product; quantity 4
Cleanup: none

## TC-CART-05 Add the same product twice from the list  [P2] [ui] [regression]
Preconditions: empty cart, logged out
Steps:
1. Open /products; add the first product; click "Continue Shopping"
2. Add the same product again; click "View Cart"
Expected:
- Observed: one row with quantity 2 (summed), not two rows (asserted).
- The total shown equals the noted price x the observed quantity for the row(s)
Data: first product on /products
Cleanup: none

## TC-CART-06 Remove a product from the cart (site TC17)  [P1] [ui] [smoke]
Preconditions: empty cart, logged out; two different products added via /products
Steps:
1. Open /view_cart and verify the cart page is displayed
2. Click X on the first product's row
3. Click X on the remaining product's row
Expected:
- After step 2: the removed product's row is gone and the other product's row is still listed
- After step 3: "Cart is empty!" is shown (confirmed live) and "Proceed To Checkout" is present in the DOM but hidden (observed after deleting the last row; asserted as count 1 + hidden)
Data: first and second product on /products
Cleanup: none

## TC-CART-07 Cart page with nothing added shows the empty state  [P2] [ui] [regression]
Preconditions: empty cart, logged out
Steps:
1. Navigate directly to /view_cart
Expected:
- "Cart is empty!" is visible (site-map)
- Observed: full text "Cart is empty! Click here to buy products."; "here" is a link with href /products (asserted).
- "Proceed To Checkout" is absent from the DOM on a fresh empty cart (observed; count 0 asserted). It differs from the after-delete state, where it is present but hidden.
Data: none
Cleanup: none

## TC-CART-08 Add a recommended item to the cart (site TC22)  [P1] [ui] [regression]
Preconditions: empty cart, logged out
Steps:
1. Scroll to the bottom of the home page and verify "RECOMMENDED ITEMS" is visible
2. Add a visible recommended product; note its name
3. In the modal click "View Cart"
Expected:
- "RECOMMENDED ITEMS" is visible in step 1
- The noted product appears in the cart
- Observed: the first slide shows 3 cards on load (Stylish Dress, Winter Top, Summer White Top); the carousel auto-advances within ~6 s (asserted: 3 visible cards on load; cards are anchored by name so the click survives a slide change). Quirk: in the recommended markup the `<p>` of product 3 holds "Rs. 1000" instead of its name.
Data: first visible recommended item
Cleanup: none

## TC-CART-09 Quantity boundaries on the detail page  [P3] [ui] [regression]
Preconditions: empty cart, logged out
Steps:
1. Open a product detail page
2. Enter quantity 0; click "Add to cart"; record the outcome
3. Repeat in fresh contexts for -1, a non-numeric value ("abc") and 999
Expected:
- Observed on product 1 (Blue Top, Rs. 500), one test per value:
  - 0: accepted; row quantity 0, total Rs. 0 (asserted; product-quality concern)
  - -1: negative quantity rejected; no negative cart totals (currently fails, see bugs)
  - "abc": the number input stays empty; the click sends GET /add_to_cart/1?quantity= and the server answers HTTP 500, no modal, cart stays empty. The test asserts the sane outcome (response status < 500) and is RED: product bug candidate
  - 999: accepted; quantity 999, total Rs. 499500 (asserted)
Data: quantities 0, -1, "abc", 999
Cleanup: none

## TC-CART-10 Guest cart survives login (site TC20)  [P1] [ui] [smoke]
Preconditions: a registered user (create via `AccountApi` with `TestUsers.newUser()`, assert responseCode 201 "User created!", `registerForCleanup(user)`); empty cart, logged out
Steps:
1. Click "Products" and verify the ALL PRODUCTS page is visible
2. Search for a product name; verify "SEARCHED PRODUCTS" is visible and results are related to the search
3. Add all the results to the cart
4. Click "Cart" and verify the products are in the cart; note names
5. Click "Signup / Login" and log in with the API-created account
6. Open /view_cart
Expected:
- "SEARCHED PRODUCTS" is visible in step 2
- After login and in /view_cart, the same noted products are still in the cart
- Probe: if the account had a previously saved cart, merging is not testable with a fresh account; not asserted.
Data: TestUsers.newUser(); search term taken from the first product name on /products
Cleanup: delete account via AccountApi.delete(email, password)

## TC-CART-11 Cart contents are stable across reload and Back/Forward  [P2] [ui] [regression]
Preconditions: empty cart, logged out
Steps:
1. Add one product from /products; open /view_cart; note the rows and totals
2. Reload the page
3. Navigate to /products, then press Back, then Forward
Expected:
- After reload and after Back/Forward, the cart shows the same rows, quantities and totals as noted
- No duplicate rows appear
Data: first product on /products
Cleanup: none

## TC-CART-12 Two products with different quantities have correct row totals  [P2] [ui] [regression]
Preconditions: empty cart, logged out
Steps:
1. On the detail page of product A, set quantity 1, click "Add to cart", then "Continue Shopping"
2. On the detail page of product B, set quantity 3, click "Add to cart", then "View Cart"
Expected:
- Product A row: quantity 1, total = price x 1
- Product B row: quantity 3, total = price x 3
- Observed: no grand total is shown, only the per-row Total column (asserted: no "grand total / cart total / subtotal / total amount" text).
Data: two different products; quantities 1 and 3
Cleanup: none
