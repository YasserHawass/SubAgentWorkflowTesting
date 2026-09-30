# Test cases: brands

Plan: tests/plans/brands.md

Common setup for every case unless stated: fresh browser context (logged out, empty cart), no account, no server-side state. Ads blocked via `BaseUiTest`; call `Ads.dismissIfPresent` before clicking, and watch for `#google_vignette` in the URL. Ground truth is `GET /api/productsList` (field `brand` per product; parse with Gson from `text()`) and `GET /api/brandsList`. Brand names and counts are never hardcoded; they are read from the sidebar links and the API. Sidebar link text contains the count, so brand names are matched by prefix/regex. Card names are read from `.productinfo`; the hover overlay duplicates "Add to cart", so click the one inside `.productinfo` scope.

Items marked "probe" are unknown until the live site is observed. The automation-writer records the observed behaviour and the test asserts that single outcome.

## TC-BRANDS-01 Brands sidebar on /products (site TC19, part 1)  [P1] [ui] [smoke]
Preconditions: logged out
Steps:
1. Open the home page and click "Products"
2. Verify the "Brands" sidebar
Expected:
- URL is /products
- The "Brands" sidebar is visible with at least one brand link
- Each link shows a product count in its text (probe: record the format, for example "(6)", and assert that format)
Data: none
Cleanup: none

## TC-BRANDS-02 Click a brand and see its page (site TC19, part 2)  [P1] [ui] [smoke]
Preconditions: logged out
Steps:
1. Open /products and click the first brand link in the sidebar; note the brand name
Expected:
- URL matches `/brand_products/<brand>`
- Heading names the brand (probe: record the exact text, likely "<BRAND> PRODUCTS", and whether upper case is DOM or rendered; assert that single form)
- At least 1 product card is visible
Data: first brand link in the live sidebar
Cleanup: none

## TC-BRANDS-03 Switch to a different brand (site TC19, part 3)  [P1] [ui] [regression]
Preconditions: logged out
Steps:
1. Open /products, click the first brand; note its name and card names
2. Click a different brand link from the sidebar on that page; note its name
Expected:
- URL and heading change to the second brand
- At least 1 card is visible
- None of the first brand's card names are listed (names are unique per API product)
Data: first and second brand links in the live sidebar
Cleanup: none

## TC-BRANDS-04 Only the chosen brand's products are listed  [P1] [ui] [api] [regression]
Preconditions: logged out; API product list fetched via `page.request()` GET `/api/productsList`
Steps:
1. Open /products and click a brand; note the brand name
2. Collect the product names on the page
3. From the API list, select products whose `brand` equals the noted brand
Expected:
- The set of card names equals the set of names of the matched API products
- The card count equals the matched API count
Data: first brand in the sidebar; API productsList
Cleanup: none

## TC-BRANDS-05 Sidebar count matches listed count and API count  [P2] [ui] [api] [regression]
Preconditions: logged out; API product list fetched
Steps:
1. Open /products; for every sidebar brand read the number in its link text
2. Open each brand page and count the cards
3. Count API products for the brand
Expected:
- For each brand the sidebar number, the card count and the API count are equal
- Probe: any mismatch is a product bug; record the brand and the three numbers, and the test asserts equality (red on mismatch)
Data: all sidebar brands; API productsList
Cleanup: none

## TC-BRANDS-06 Unknown brand shows a clean page  [P2] [ui] [regression]
Preconditions: logged out
Steps:
1. Navigate directly to /brand_products/NoSuchBrand
Expected:
- HTTP status is below 500
- The page contains neither "DEBUG = True", "URLconf" nor a stack trace
- Site chrome (header nav, Brands sidebar) is visible, and zero product cards are listed
- Probe: record the heading text (empty, generic or echoing the name) and assert that single observed text
- Probe: record whether `/brand_products/polo` behaves like `/brand_products/Polo` (case sensitivity) using a real brand from the sidebar; assert that single observed outcome
Data: brand "NoSuchBrand"; a real brand name in lower case
Cleanup: none

## TC-BRANDS-07 Add a brand product to the cart and view it  [P2] [ui] [regression]
Preconditions: logged out, empty cart
Steps:
1. Open /products, click the first brand; note the first card's name
2. Click "Add to cart" on the first card
3. In the modal click "View Cart"
Expected:
- The modal shows "Added!" after step 2
- URL is /view_cart
- The cart has exactly one row with the noted product name and quantity 1
Data: first brand, first card
Cleanup: none (session cart only)

## TC-BRANDS-08 Brands with spaces or & in the name resolve and list products  [P1] [ui] [api] [regression]
Preconditions: logged out; API brand and product lists fetched
Steps:
1. From `GET /api/brandsList`, read every brand name; note those with spaces or `&`
2. Open /products and click each brand link in the sidebar in turn
Expected:
- Every API brand has a sidebar link, and each link opens a page that is not 404/500 and shows at least 1 card
- Each page's card count equals the API product count for that brand
- Probe: record how spaces and `&` are encoded in the observed hrefs; assert that encoding. A multi-word or `&` brand that lists nothing or errors is a product bug (red).
Data: all brands from the API
Cleanup: none

## Observed values (automation-writer, 2026-09-30) - asserted as the single outcome
- Sidebar has 8 distinct brands (Polo 6, H&M 5, Madame 5, Mast & Harbour 3, Babyhug 4, Allen Solly Junior 3, Kookie Kids 3, Biba 5). Link text is "(N)Name" with N = product count, e.g. "(6)Polo"; CSS upper-cases the rendering.
- Brand page heading DOM text is mixed case: "Brand - Polo Products"; CSS renders it upper case.
- hrefs are NOT percent-encoded: spaces and & stay literal, e.g. /brand_products/H&M and /brand_products/Mast & Harbour (both resolve with HTTP 200 and list the right cards).
- Sidebar count = card count = API count for all 8 brands (no mismatch).
- /brand_products/NoSuchBrand: HTTP 200, heading echoes the name ("Brand - NoSuchBrand Products"), 0 cards, chrome present, no DEBUG page.
- Brand names are case sensitive in the URL: /brand_products/polo is HTTP 200, heading "Brand - polo Products", 0 cards.
- Implemented as tcBrands06a_ (unknown brand) and tcBrands06b_ (lower-case real brand).
