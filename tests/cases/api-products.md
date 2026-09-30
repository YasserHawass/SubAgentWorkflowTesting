# Test cases: api-products

Plan: tests/plans/api-products.md

Common rules for every case: base `/api`, class extends `BaseApiTest`, tagged api. No account, no cart, no server-side state, so no cleanup. Assert order: JSON body `responseCode`, then `message`, then shape, then HTTP status. Guard JSON parsing with Gson; a Cloudflare HTTP 520 with a non-JSON body or an HTTP 503 "heavy load" HTML page is env/flake, not a product bug.

Value sourcing: responseCode and message come verbatim from the docs/site-map.md API table (rows 1-2). OBSERVE convention: values were probed live on 2026-09-30 and are recorded inline; no unresolved OBSERVE tokens remain. The 34-product catalogue size comes from the site-map "products" section.

## TC-APIPRODUCTS-01 GET productsList returns the products array  [P1] [api] [smoke]
Preconditions: none
Steps:
1. GET /api/productsList
Expected:
- Body responseCode is 200 (site-map row 1)
- Body has a `products` array that is non-empty
- HTTP status is 200 (observed; Content-Type is text/html, not application/json)
- The body itself carries `responseCode` 200 (observed)
Data: none
Cleanup: none

## TC-APIPRODUCTS-02 Every product has the documented shape  [P1] [api] [smoke]
Preconditions: none
Steps:
1. GET /api/productsList
2. Inspect every entry of `products`
Expected:
- `id` is a number
- `name` is a non-empty string
- `price` is a string matching "Rs. N" (N digits)
- `brand` is a non-empty string
- `category.usertype.usertype` and `category.category` are non-empty strings
Data: none
Cleanup: none

## TC-APIPRODUCTS-03 Catalogue has 34 unique products  [P1] [api] [smoke]
Preconditions: none
Steps:
1. GET /api/productsList
Expected:
- `products` has exactly 34 entries (site-map "products")
- All `id` values are unique
- All names are non-blank
- ids are NOT contiguous 1..34 (observed: 1-8, 11-16, 18-24, 28-31, 33, 35, 37-43), so do not assert contiguity
Data: none
Cleanup: none

## TC-APIPRODUCTS-04 POST productsList is rejected as unsupported  [P2] [api] [regression]
Preconditions: none
Steps:
1. POST /api/productsList (empty form body)
Expected:
- Body responseCode is 405 (site-map row 2)
- Body message is "This request method is not supported."
- HTTP status is 200 (observed; not a real 405)
Data: none
Cleanup: none

## TC-APIPRODUCTS-05 PUT and DELETE productsList wrong-method format  [P2] [api] [regression]
Preconditions: none
Steps (data variants, each asserted separately with the raw body recorded first):
1. PUT /api/productsList
2. DELETE /api/productsList
Expected:
- Observed for both PUT and DELETE: HTTP 200 with body responseCode 405 "This request method is not supported." (no DRF detail body)
- No `products` array is returned
Data: none
Cleanup: none

## TC-APIPRODUCTS-06 Category and brand values are well-formed  [P2] [api] [regression]
Preconditions: none
Steps:
1. GET /api/productsList
2. Collect the distinct `category.usertype.usertype` values and every `brand`
Expected:
- Distinct usertype set is exactly {Women, Men, Kids} (observed)
- No product has a null, missing or empty `brand`
- No product has a null or empty `category.category`
Data: none
Cleanup: none
