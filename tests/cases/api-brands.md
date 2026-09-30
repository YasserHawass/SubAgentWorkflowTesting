# Test cases: api-brands

Plan: tests/plans/api-brands.md

Common rules for every case: base `/api`, class extends `BaseApiTest`, tagged api. No account, no cart, no server-side state, so no cleanup. Assert order: JSON body `responseCode`, then `message`, then shape, then HTTP status. Guard JSON parsing with Gson; a Cloudflare HTTP 520 with a non-JSON body or an HTTP 503 "heavy load" HTML page is env/flake, not a product bug. Compare brand strings exactly, never normalise silently.

Value sourcing: responseCode and message come verbatim from the docs/site-map.md API table (rows 3-4). OBSERVE convention: values were probed live on 2026-09-30 and are recorded inline; no unresolved OBSERVE tokens remain.

## TC-APIBRANDS-01 GET brandsList returns the brands array  [P1] [api] [smoke]
Preconditions: none
Steps:
1. GET /api/brandsList
Expected:
- Body responseCode is 200 (site-map row 3)
- Body has a `brands` array that is non-empty
- Brand count is 34 entries (observed), of which only 8 distinct names
- HTTP status is 200 (observed; Content-Type text/html); body also carries responseCode 200
Data: none
Cleanup: none

## TC-APIBRANDS-02 Each brand has an id and a name, ids are unique  [P1] [api] [smoke]
Preconditions: none
Steps:
1. GET /api/brandsList
2. Inspect every entry of `brands`
Expected:
- `id` is a number and `brand` is a non-empty string in every entry
- All `id` values are unique
- Brand names are NOT unique (observed: 34 entries, 8 distinct names, e.g. "Madame" x5); assert names are non-empty and the distinct set is {Polo, H&M, Madame, Mast & Harbour, Babyhug, Allen Solly Junior, Kookie Kids, Biba}
- ids are NOT contiguous (observed: 1-8, 11-16, 18-24, 28-31, 33, 35, 37-43); do not assert contiguity
Data: none
Cleanup: none

## TC-APIBRANDS-03 Every product brand appears in brandsList  [P2] [api] [regression]
Preconditions: none
Steps:
1. GET /api/productsList and collect the distinct `brand` strings
2. GET /api/brandsList and collect the `brand` strings
Expected:
- Every distinct product brand appears in brandsList by exact string match
- Observed: no mismatch in case or spacing; the distinct product brands equal the distinct brandsList names exactly. A future mismatch must be reported in the failure message, not worked around
Data: none
Cleanup: none

## TC-APIBRANDS-04 PUT brandsList is rejected as unsupported  [P2] [api] [regression]
Preconditions: none
Steps:
1. PUT /api/brandsList (empty form body)
Expected:
- Body responseCode is 405 (site-map row 4)
- Body message is "This request method is not supported."
- HTTP status is 200 (observed; not a real 405)
Data: none
Cleanup: none

## TC-APIBRANDS-05 POST and DELETE brandsList wrong-method format  [P2] [api] [regression]
Preconditions: none
Steps (data variants, each asserted separately with the raw body recorded first):
1. POST /api/brandsList
2. DELETE /api/brandsList
Expected:
- Observed for both POST and DELETE: HTTP 200 with body responseCode 405 "This request method is not supported." (no DRF detail body)
- No `brands` array is returned
Data: none
Cleanup: none

## TC-APIBRANDS-06 Brands with no products (orphans)  [P3] [api] [regression]
Preconditions: none
Steps:
1. GET /api/brandsList
2. GET /api/productsList
3. For each listed brand, count the products carrying that brand
Expected:
- Observed: no orphan brands (the orphan set is empty)
Data: none
Cleanup: none
