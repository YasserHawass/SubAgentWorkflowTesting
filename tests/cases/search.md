# Test cases: search

Plan: tests/plans/search.md

Common setup for every case unless stated: no account needed, all cases run logged out in a fresh browser context; no server-side state is created, so no cleanup. UI cases start by opening the home page, clicking "Products" in the header and confirming the "All Products" page is shown (site TC9 steps 1-3), then use the search box. Ads blocked via `BaseUiTest`; call `Ads.dismissIfPresent` before acting. Product card names have no stable hook (site-map "Discovered during login"), so name reads use the accepted `card.locator("p")` approach.

Items marked "probe" are unknown until the live site is observed. The automation-writer records the observed behaviour and the test asserts that single outcome (no either/or assertions). Exact heading text, URL shape and result counts are not stated by site TC9 or docs/site-map.md beyond what is quoted below, so they are marked probe. Result counts are never hardcoded; compare relatively.

Not covered here: plan scenario 11 (second search replaces first results, sidebar/header kept) would be the 11th case; it exceeds the 8-10 target and is left for an append if wanted.

## TC-SEARCH-01 Search "Tshirt" shows the Searched Products view (site TC9)  [P1] [ui] [smoke]
Preconditions: none
Steps:
1. On /products, enter "Tshirt" in the search box and click the search button
Expected:
- A "Searched Products" heading is visible (site TC9 names it 'SEARCHED PRODUCTS'; probe: record the exact casing shown, test asserts that single text)
- At least one product card is shown (probe: confirm "Tshirt" returns results and record the count)
Data: term "Tshirt"
Cleanup: none

## TC-SEARCH-02 Every result relates to the searched term "Dress"  [P1] [ui] [smoke]
Preconditions: none
Steps:
1. On /products, search "Dress"
2. Read the name of every product card in the results
Expected:
- Card count is greater than 0
- Every result matches "Dress" in its name OR its category (observed: search matches name or category; e.g. id 16 "Sleeves Top and Short - Blue & Pink" is category Dress), verified via /api/productsList
- Probe: search a term that matches only a category or brand text (choose one from the live catalog) and record whether it returns results; test asserts that single outcome
Data: term "Dress"
Cleanup: none

## TC-SEARCH-03 Search is case-insensitive  [P2] [ui] [regression]
Preconditions: none
Steps:
1. On /products, search "tshirt"; record the card count and names
2. Search "TSHIRT"; record the count and names
3. Search "Tshirt"; record the count and names
Expected:
- Probe: record whether the three searches return the same count and names; test asserts that single observed outcome (expected to be identical, confirm live)
- "Searched Products" heading is visible after each search
Data: terms "tshirt", "TSHIRT", "Tshirt"
Cleanup: none

## TC-SEARCH-04 Partial term "shirt" is a superset of "Tshirt"  [P2] [ui] [regression]
Preconditions: none
Steps:
1. On /products, search "Tshirt"; record the count
2. Search "shirt"; read all card names and the count
Expected:
- Every result matches "shirt" in its name OR its category (observed; "Tops & Shirts" category items match)
- Count for "shirt" is greater than or equal to the count for "Tshirt"
Data: terms "Tshirt", "shirt"
Cleanup: none

## TC-SEARCH-05 Term with no matches shows no products and no error  [P2] [ui] [regression]
Preconditions: none
Steps:
1. On /products, search "zzzxqv123"
Expected:
- Zero product cards are shown
- No error page: header navigation and the search box are still visible
- Probe: record whether the "Searched Products" heading is still shown; test asserts that single observed outcome
Data: term "zzzxqv123"
Cleanup: none

## TC-SEARCH-06 Empty search (record actual behaviour)  [P3] [ui] [regression]
Preconditions: none
Steps:
1. On /products, leave the search box empty and click the search button
Expected:
- Probe: record the resulting URL, the heading text ("All Products" or "Searched Products") and the card count; test asserts that single observed outcome
- No error page; header is visible
Data: empty term
Cleanup: none

## TC-SEARCH-07 Special characters and markup are handled safely  [P2] [ui] [regression]
Preconditions: none
Steps:
1. On /products, search each of these terms in turn: `<b>x</b>`, `'; --`, `%`, `&`
2. After each search, inspect the page
Expected:
- No server error page (no HTTP 500 or debug page); header and search box are still visible after each term
- The `<b>x</b>` term is not rendered as HTML (no bold element created from the input)
- For `&`: the term is URL-encoded; results match the API search for '&' (currently fails: site sends ?search=&, see bugs)
- Probe: record the result count for each term; test asserts that single observed outcome per term
Data: terms `<b>x</b>`, `'; --`, `%`, `&`
Cleanup: none

## TC-SEARCH-08 Search URL and reload keep the results  [P2] [ui] [regression]
Preconditions: none
Steps:
1. On /products, search "Tshirt"; record the URL and the card names
2. Reload the page
Expected:
- Probe: record the URL shape after a search (plan expects a GET form such as `/products?search=<term>`); test asserts that single observed URL
- After reload the "Searched Products" heading is visible (probe: confirm exact text) and the card names equal those recorded in step 1
Data: term "Tshirt"
Cleanup: none

## TC-SEARCH-09 API search matches the UI results  [P2] [api] [regression]
Preconditions: none
Steps:
1. POST /api/searchProduct with form field `search_product=top`
2. In the UI on /products, search "top" and read the card names and count
Expected:
- Body `responseCode` is 200 (site-map API row 5)
- Body has a `products` array
- Probe: record whether each returned product name or category contains "top"; test asserts that single observed outcome
- Number of products in the API array equals the UI card count for "top" (probe: confirm equality and record)
Data: form `search_product=top`
Cleanup: none

## TC-SEARCH-10 API search without the search_product parameter  [P2] [api] [regression]
Preconditions: none
Steps:
1. POST /api/searchProduct with no body
Expected:
- Body `responseCode` is 400 (site-map API row 6)
- Body `message` is "Bad request, search_product parameter is missing in POST request."
- HTTP status: probe, record observed (site-map notes it is often 200); assert body first
Data: none
Cleanup: none
