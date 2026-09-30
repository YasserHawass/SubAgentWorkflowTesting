# Test plan: api-products

## Scope
In: `GET /api/productsList` contract (shape, ids, count) and wrong-method handling on the same path (site-map API rows 1-2).
Out: UI-vs-API cross-checks (already ProductsTest TC-PRODUCTS-03/08), search, brands.

## Preconditions & data
- No account, no cart. Extend BaseApiTest, use `api` field. No cleanup needed.
- Catalogue is fixed at 34 products (site-map "products" section).

## Scenarios
| # | Scenario | Expected | Priority | Type |
|---|---|---|---|---|
| 1 | GET `/api/productsList` | HTTP 200; body has `products` array; probe live and record whether body also carries `responseCode` 200 | P1 | api |
| 2 | Shape of every entry | Each has `id` (number), `name` (non-empty), `price` matching "Rs. N", `brand` (non-empty), `category.usertype.usertype` and `category.category` (non-empty) | P1 | api |
| 3 | Count and uniqueness | Exactly 34 products; all `id` values unique; all names non-blank | P1 | api |
| 4 | POST `/api/productsList` | Body `responseCode` 405 and `message` "This request method is not supported."; probe live and record the HTTP status (site-map says 200 for verifyLogin-style endpoints, real 405 for others) | P2 | api |
| 5 | PUT and DELETE `/api/productsList` | Same as 4; probe live and record whether the format is body-405 or real HTTP 405 with DRF body `{"detail":"Method \"X\" not allowed."}`. Assert what is observed, per method | P2 | api |
| 6 | What could realistically break: category values | Every `category.usertype.usertype` is one of Women / Men / Kids (probe live and record the actual set) and no product has a null or empty `brand` | P2 | api |

## Risks & quirks
- HTTP status is often 200 even when body `responseCode` is an error. Assert `responseCode` first, then `message`, then shape (CONVENTIONS "API tests").
- Wrong-method format differs between endpoints (body 405 vs real HTTP 405 with DRF `detail`). Do not assume; record it.
- Cloudflare HTTP 520 (non-JSON) and 503 "heavy load" HTML are transient; classify as env/flake, not product bug.
- `price` is a string "Rs. N", not a number. Parse after stripping the prefix if comparing.
- Gson parse of a non-JSON body throws; check content type or catch at the assertion level, not silently.

## Out of scope / open questions
- Is `id` contiguous 1..34? Probe live and record; do not assert unless stable.
- Does GET with query params (e.g. `?x=1`) change the result? Probe live and record.
- Response ordering is not asserted.
