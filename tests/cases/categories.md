# Test cases: categories

Plan: tests/plans/categories.md

Common setup for every case unless stated: fresh browser context (logged out, empty cart), no account, no server-side state. Ads blocked via `BaseUiTest`; call `Ads.dismissIfPresent` before clicking, and watch for `#google_vignette` in the URL. Sidebar is an accordion: the parent click must finish before a subcategory link is clicked. Ground truth for products is `GET /api/productsList` (parse with Gson from `text()`); field `category.usertype.usertype` (Women/Men/Kids) and `category.category` (Tops, Dress, ...). Category ids, subcategory names and counts are never hardcoded; they are read from the sidebar links and the API. Card names are read from `.productinfo` (the hover overlay duplicates them).

Items marked "probe" are unknown until the live site is observed. The automation-writer records the observed behaviour and the test asserts that single outcome. Site TC18 contradicts itself (it clicks Dress but expects "WOMEN - TOPS PRODUCTS"); no case asserts TOPS after clicking a different subcategory. The heading always names the subcategory that was actually clicked.

## TC-CATEGORIES-01 Women subcategory page from the home sidebar (site TC18, part 1)  [P1] [ui] [smoke]
Preconditions: logged out
Steps:
1. Open the home page and verify the "Category" sidebar is visible with Women, Men and Kids
2. Click "Women" to expand it
3. Click the first visible Women subcategory link; note its name (for example Dress or Tops)
Expected:
- Sidebar shows Women, Men and Kids in step 1
- URL matches `/category_products/<id>`
- Heading names the clicked subcategory: "WOMEN - <NOTED SUBCATEGORY> PRODUCTS" (probe: record whether the DOM text is mixed case and only rendered upper case; assert that single form). The heading does not assert TOPS unless Tops is the clicked subcategory.
- At least 1 product card is visible
Data: first Women subcategory link found in the live sidebar
Cleanup: none

## TC-CATEGORIES-02 Switch from Women to a Men subcategory (site TC18, part 2)  [P1] [ui] [regression]
Preconditions: logged out
Steps:
1. Open the home page; expand "Women" and click its first subcategory; note the URL
2. Expand "Men" and click the first Men subcategory; note its name
Expected:
- URL is a different `/category_products/<id>` than in step 1
- Heading starts with "MEN - " and names the clicked Men subcategory
- At least 1 product card is visible
- Products of the Women page from step 1 are not listed
Data: first Women and first Men subcategory links from the live sidebar
Cleanup: none

## TC-CATEGORIES-03 Listed products belong to the category  [P1] [ui] [api] [regression]
Preconditions: logged out; API product list fetched via `page.request()` GET `/api/productsList`
Steps:
1. Open home; expand Women and click a subcategory; note usertype and subcategory from the heading
2. Collect the product names shown on the page
3. From the API list, select products whose `category.usertype.usertype` and `category.category` equal the heading's values
Expected:
- The set of card names on the page equals the set of names of the matched API products
- The card count equals the matched API count
- At least 1 card (probe: if some subcategory is empty, record which one; this case uses a non-empty one)
Data: first Women subcategory; API productsList
Cleanup: none

## TC-CATEGORIES-04 Category sidebar on /products leads to the same kind of page  [P2] [ui] [regression]
Preconditions: logged out
Steps:
1. Open /products and verify the "Category" sidebar shows Women, Men and Kids
2. Expand "Kids" and click its first subcategory; note its name
Expected:
- URL matches `/category_products/<id>`
- Heading is "KIDS - <NOTED SUBCATEGORY> PRODUCTS" (same pattern as on home)
- Probe: number of cards for the subcategory is recorded (Kids may be empty); the test asserts that observed count and that it equals the API count for that usertype and category
Data: first Kids subcategory link
Cleanup: none

## TC-CATEGORIES-05 Accordion expands and collapses  [P3] [ui] [regression]
Preconditions: logged out
Steps:
1. Open /products; note that no subcategory links are visible
2. Click "Women"
3. Click "Men"
Expected:
- After step 1: no Women subcategory link is visible
- After step 2: at least one Women subcategory link is visible
- After step 3: Men subcategory links are visible. Probe: whether the Women links are still visible; record the observed state and assert that single state.
Data: none
Cleanup: none

## TC-CATEGORIES-06 Nonexistent category id 9999 shows a clean page  [P2] [ui] [regression]
Preconditions: logged out
Steps:
1. Navigate directly to /category_products/9999
Expected:
- HTTP status is below 500
- The page contains neither "DEBUG = True", "URLconf" nor a stack trace
- Site chrome (header nav, Category sidebar) is visible, and zero product cards are listed
- Probe: record the heading text (empty, "not found" or a generic title) and assert that single observed text
Data: id 9999
Cleanup: none

## TC-CATEGORIES-07 Malformed category ids abc, 0 and -1 never show a DEBUG page  [P2] [ui] [regression]
Preconditions: logged out
Steps:
1. Navigate to /category_products/abc
2. Navigate to /category_products/0
3. Navigate to /category_products/-1
Expected:
- For each id: HTTP status is below 500, and the page contains neither "DEBUG = True", "URLconf" nor a stack trace
- For each id: the result is a clean not-found or an empty product page, with zero cards listed
- Probe: the exact observed status and heading per id are recorded and each is asserted as its single outcome. A Django DEBUG page (as seen for /product_details/abc) is a product bug, so this case is expected to stay red for `abc` until fixed.
Data: ids abc, 0, -1 (one test per id)
Cleanup: none

## TC-CATEGORIES-08 Every sidebar subcategory link resolves and lists products  [P1] [ui] [api] [regression]
Preconditions: logged out; API product list fetched
Steps:
1. Open /products; expand Women, Men and Kids; collect every subcategory link href and label
2. Request each href (page.request GET) and open it in the browser in turn
3. Sum the card counts across all subcategories
Expected:
- Each link returns HTTP 200 (not 404/500), shows a heading "<USERTYPE> - <SUBCATEGORY> PRODUCTS" and its cards
- Each page's card count equals the API count for its usertype and category
- Probe: whether the summed count equals the total API product count (34 at last observation) or is smaller because some product has no category or appears twice; record and assert the observed sum
Data: all sidebar subcategory links; API productsList
Cleanup: none

## Observed values (automation-writer, 2026-09-30) - asserted as the single outcome
- Sidebar: Women = Dress(1), Tops(2), Saree(7); Men = Tshirts(3), Jeans(6); Kids = Dress(4), Tops & Shirts(5). Toggle link accessible name is the glyph U+F067 plus the text (CSS renders it upper case).
- Heading DOM text is mixed case ("Women - Dress Products", "Men - Tshirts Products"); CSS renders it upper case. Assert the mixed-case DOM text.
- Accordion (TC-05): all panels start hidden; opening Men collapses Women (single-open). Women links are hidden after step 3.
- Kids is not empty (TC-04): Dress = 6 cards, equal to the API count. Per-subcategory counts sum to 34 = total API products (TC-08).
- /category_products/9999 and /0: HTTP 200, chrome present, heading DOM text " -  Products" (empty usertype and subcategory), 0 cards.
- /category_products/abc and /-1: HTTP 404 with the Django DEBUG page ("DEBUG = True", URLconf). PRODUCT BUG, TC-07 abc and -1 stay red (07a, 07c). Only `0` (07b) passes.
- Implemented as tcCategories07a_ (abc), 07b_ (0), 07c_ (-1).
