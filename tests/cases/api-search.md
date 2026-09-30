# Test cases: api-search

Plan: tests/plans/api-search.md

Common rules for every case: `POST /api/searchProduct` with a form-encoded body (`FormData.create().set("search_product", ...)`, which encodes `&`, `+` and `%` correctly), base `/api`, class extends `BaseApiTest`, tagged api. No account, no server-side state, so no cleanup. Assert order: JSON body `responseCode`, then `message`, then shape, then HTTP status. Guard JSON parsing with Gson; a Cloudflare HTTP 520 with a non-JSON body or an HTTP 503 "heavy load" HTML page is env/flake, not a product bug. Compare result sets by id, never by order.

Out of scope (already covered): UI-vs-API search cross-check (TC-SEARCH-09) and the missing-param 400 (TC-SEARCH-10).

Value sourcing: responseCode and message come verbatim from the docs/site-map.md API table (rows 5-6). OBSERVE convention: values were probed live on 2026-09-30 and are recorded inline; no unresolved OBSERVE tokens remain. The UI counts (Tshirt 6, Dress 9, shirt 13, top 14) are reference values only; the API count is OBSERVE.

## TC-APISEARCH-01 Search "top" matches by name or category  [P1] [api] [smoke]
Preconditions: none
Steps:
1. POST /api/searchProduct with search_product=top
Expected:
- Body responseCode is 200 (site-map row 5)
- `products` is non-empty
- Every product contains "top" (case-insensitive) in its name OR its category
- Result count is 14 (observed; ids 1,5,6,7,8,11,12,13,14,15,16,18,24,42)
- HTTP status is 200 (observed); body also carries responseCode 200
Data: search_product "top"
Cleanup: none

## TC-APISEARCH-02 Search is case-insensitive and matches category  [P1] [api] [smoke]
Preconditions: none
Steps:
1. POST search_product=TOP, then top, then Top
2. POST search_product=Dress
Expected:
- The three "top" variants return identical sets of product ids
- "Dress" returns at least one product whose name lacks "dress" but whose category is Dress (the UI shows "Sleeves Top and Short" in category Dress)
- "Dress" result count is 9 (observed; ids 3,4,16,19,20,21,22,23,38)
Data: search terms "TOP", "top", "Top", "Dress"
Cleanup: none

## TC-APISEARCH-03 Result entries equal their productsList entries  [P2] [api] [regression]
Preconditions: none
Steps:
1. GET /api/productsList and index the entries by id
2. POST /api/searchProduct with search_product=top
Expected:
- Each result entry has the same keys as a productsList entry (id, name, price, brand, category.usertype.usertype, category.category)
- `price` matches "Rs. N"
- Each result entry equals the productsList entry with the same id
Data: search_product "top"
Cleanup: none

## TC-APISEARCH-04 No-match and special-character input  [P2] [api] [regression]
Preconditions: none
Steps (data variants, each asserted separately):
1. POST search_product=zzzxqv123
2. POST search_product=<b>x</b>
3. POST search_product='; --
4. POST search_product=%
Expected:
- Observed for all four variants: HTTP 200, responseCode 200, `products: []`
- No HTTP 500 and the body is valid JSON
- The input is not echoed back unescaped in the body
- UI reference: these inputs give 0 cards
Data: the four terms above
Cleanup: none

## TC-APISEARCH-05 Empty search_product  [P2] [api] [regression]
Preconditions: none
Steps:
1. POST /api/searchProduct with search_product=""
Expected:
- Observed: responseCode 200 with all 34 products (empty string counts as present, as in the UI)
- HTTP status is 200
Data: search_product ""
Cleanup: none

## TC-APISEARCH-06 GET, PUT and DELETE on searchProduct  [P2] [api] [regression]
Preconditions: none
Steps (data variants, each asserted separately with the raw body recorded first):
1. GET /api/searchProduct
2. PUT /api/searchProduct
3. DELETE /api/searchProduct
Expected:
- Observed for GET, PUT and DELETE: HTTP 200 with body responseCode 405 "This request method is not supported." (no DRF detail body)
- No `products` array is returned
Data: none
Cleanup: none

## TC-APISEARCH-07 JSON body instead of form  [P3] [api] [regression]
Preconditions: none
Steps:
1. POST /api/searchProduct with a JSON body {"search_product":"top"} and Content-Type application/json
Expected:
- Observed: HTTP 200 with responseCode 400 "Bad request, search_product parameter is missing in POST request." (only form data is read)
Data: JSON body above
Cleanup: none
