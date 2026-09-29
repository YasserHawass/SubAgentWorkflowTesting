# Test cases: products

Plan: tests/plans/products.md

Common setup for every case unless stated: no account and no login; tests run logged out in a fresh browser context. Ads blocked via `BaseUiTest`; call `Ads.dismissIfPresent` before clicking. Product ids and names come from `GET /api/productsList` at test time (assert responseCode 200 first); never hardcode names or prices. Nothing is created server-side, so there is no cleanup. Do not click "Add to cart" in this feature (owned by plan `cart`); only check that it exists. Transient Cloudflare 520 / 503 pages are classified env or flake, not product bugs.

Items marked "probe" are unknown until the live site is observed. The automation-writer records the observed behaviour and the test asserts that single outcome (no either/or assertions). Exact texts and URLs appear in Expected only where site TC8 or docs/site-map.md states them.

## TC-PRODUCTS-01 Open All Products from the home header (site TC8)  [P1] [ui] [smoke]
Preconditions: none
Steps:
1. Open the home page and confirm it is visible
2. Click "Products" in the header
Expected:
- URL is /products (site-map route)
- Heading for the All Products page is visible; probe: record the exact heading text (TC8 says "ALL PRODUCTS", site-map does not give casing) and assert that single text
- The products list is visible: at least one product card is shown
Data: none
Cleanup: none

## TC-PRODUCTS-02 View Product of the first card opens its detail page (site TC8)  [P1] [ui] [smoke]
Preconditions: none
Steps:
1. Open /products
2. Click "View Product" on the first card
Expected:
- URL matches /product_details/<id> (site-map route)
- Product name, category, price, availability, condition and brand are all visible and non-empty (TC8)
Data: first product on the grid
Cleanup: none

## TC-PRODUCTS-03 Grid count and names match GET /api/productsList  [P1] [ui] [api] [regression]
Preconditions: none
Steps:
1. Call `GET /api/productsList`; parse `products` from the body
2. Open /products and collect all card names
Expected:
- API responseCode is 200 (site-map)
- Number of cards equals `products.length`
- Set of card names equals set of API product names
- Probe: record whether the grid is paginated or lazy-loaded; the test asserts the single observed count behaviour (all cards on one page, or the observed page size)
Data: API product list
Cleanup: none

## TC-PRODUCTS-04 Every card shows image, price, name, Add to cart and View Product  [P2] [ui] [regression]
Preconditions: none
Steps:
1. Open /products
2. Inspect every product card (do not click "Add to cart")
Expected:
- Each card has an image, a price text starting "Rs. " (site-map shows "Rs. 500"), a non-empty name, an "Add to cart" button and a "View Product" link
Data: none
Cleanup: none

## TC-PRODUCTS-05 Detail page name and price match the clicked card  [P2] [ui] [regression]
Preconditions: none
Steps:
1. Open /products; note name and price of the first card
2. Click "View Product" on that card
Expected:
- Name on the detail page equals the noted card name
- Price on the detail page equals the noted card price (string comparison)
Data: first product on the grid
Cleanup: none

## TC-PRODUCTS-06 Detail page labels for category, availability, condition, brand  [P2] [ui] [regression]
Preconditions: none
Steps:
1. Open the detail page of the first product from `/api/productsList` via /product_details/<id>
2. Read the category, availability, condition and brand entries
Expected:
- Category, availability, condition and brand each have a non-empty value (TC8)
- Probe: record the exact label texts ("Availability:", "Condition:", "Brand:") and the category format (plan expects like "Women > Tops"); the test asserts those recorded texts
Data: first API product id
Cleanup: none

## TC-PRODUCTS-07 Quantity input on the detail page  [P2] [ui] [regression]
Preconditions: none
Steps:
1. Open /product_details/<id> for the first API product
2. Read the quantity input; type 3 into it
Expected:
- Default quantity value is "1"
- An "Add to cart" button is visible (not clicked; site-map)
- After typing 3 the input value is "3"
- Probe: record what the input holds after entering 0 (or a negative number) and after entering letters; not asserted beyond the single observed value per input
Data: first API product id
Cleanup: none

## TC-PRODUCTS-08 Every API product id opens a populated detail page  [P1] [ui] [api] [regression]
Preconditions: none
Steps:
1. Call `GET /api/productsList`; collect every product id (ids may not be contiguous)
2. Open /product_details/<id> for each id
Expected:
- Every page loads with no 404 and no server error
- Name and price are non-empty on each page
Data: API product list
Cleanup: none

## TC-PRODUCTS-09 Nonexistent product id  [P2] [ui] [regression]
Preconditions: none
Steps:
1. Navigate directly to /product_details/999999
Expected:
- Probe: record the observed outcome (landing URL and page content: empty detail, redirect or error page); the test asserts that single outcome
- No HTTP 500, no stack trace; the site header is rendered and shows "Signup / Login" (site-map)
Data: id 999999
Cleanup: none

## TC-PRODUCTS-10 Malformed product ids  [P3] [ui] [regression]
Preconditions: none
Steps:
1. Navigate directly to /product_details/abc
2. Navigate directly to /product_details/0
Expected:
- Probe: record the observed landing URL and content for each id; the test asserts that single outcome per id
- Neither response is an HTTP 500 or a Django debug page / stack trace (compare bug logout-1 where `/logout` returned 500); site header is rendered
Data: ids "abc" and "0"
Cleanup: none
