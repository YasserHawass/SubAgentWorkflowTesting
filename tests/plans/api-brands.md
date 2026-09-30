# Test plan: api-brands

## Scope
In: `GET /api/brandsList` contract (shape, ids, coverage of product brands) and wrong-method handling (site-map API rows 3-4).
Out: UI brand sidebar and `/brand_products/<brand>` (feature `brands`), products contract (api-products), search.

## Preconditions & data
- No account, no cart. Extend BaseApiTest, use `api` field.
- Scenario 3 also reads `GET /api/productsList` to collect product brands.

## Scenarios
| # | Scenario | Expected | Priority | Type |
|---|---|---|---|---|
| 1 | GET `/api/brandsList` | HTTP 200; body has `brands` array, non-empty; probe live and record whether `responseCode` 200 is also present and the brand count | P1 | api |
| 2 | Shape and uniqueness | Each entry has `id` (number) and `brand` (non-empty string); all `id` values unique; probe live and record whether brand names are unique (duplicates possible) | P1 | api |
| 3 | Product brands covered | Every distinct `brand` in productsList appears in brandsList (compare exact strings; probe live and record any mismatch in case/spacing and report as a finding, not a workaround) | P2 | api |
| 4 | PUT `/api/brandsList` | Body `responseCode` 405 and `message` "This request method is not supported."; probe live and record the HTTP status | P2 | api |
| 5 | POST and DELETE `/api/brandsList` | Probe live and record: body-405 with the message above, or real HTTP 405 with DRF `{"detail":"Method \"X\" not allowed."}`. Assert what is observed, per method | P2 | api |
| 6 | What could realistically break: brands with no products | Probe live and record whether any listed brand has zero products in productsList (orphan brand); report the list, assert only if stable | P3 | api |

## Risks & quirks
- HTTP status is often 200 with the error only in body `responseCode`. Assert body first.
- Wrong-method format varies per endpoint (see site-map "Discovered during api-account"). Do not assume PUT is the only tested method.
- Transient Cloudflare 520 / 503 HTML: classify as env/flake.
- Brand strings may contain odd casing or spaces (for example "H&M", "Mast & Harbour"); compare exactly, do not normalise silently.

## Out of scope / open questions
- Exact brand count is unknown. Probe live and record before hardcoding; the UI sidebar list is the cross-reference.
- Whether `id` is contiguous. Probe live and record.
- Brand-to-UI parity (sidebar) belongs to the `brands` feature.
