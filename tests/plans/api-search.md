# Test plan: api-search

## Scope
In: `POST /api/searchProduct` matching semantics, result shape, edge inputs, wrong methods, body encoding (site-map API rows 5-6).
Out: UI search box (SearchTest) and UI-vs-API search cross-check (TC-SEARCH-09) and missing-param 400 (TC-SEARCH-10); do not duplicate.

## Preconditions & data
- No account. Extend BaseApiTest; bodies are form-encoded (`search_product`).
- Known UI counts to reuse as expected values: "Tshirt" 6, "Dress" 9, "shirt" 13, "top" 14 (site-map "search"). Probe live and record that the API gives the same numbers.

## Scenarios
| # | Scenario | Expected | Priority | Type |
|---|---|---|---|---|
| 1 | Name/category match | POST `search_product=top`: body `responseCode` 200, `products` non-empty; every product contains "top" (case-insensitive) in name OR category; count probe live and record (UI says 14) | P1 | api |
| 2 | Case-insensitive and category match | "TOP", "top" and "Top" return identical id sets; "Dress" returns a product whose name lacks "dress" but category is Dress | P1 | api |
| 3 | Result shape equals productsList | Each result entry has the same keys and value formats as a productsList entry (id, name, "Rs. N" price, brand, category.usertype.usertype, category.category) and equals the productsList entry with the same id | P2 | api |
| 4 | No match and special characters | `zzzxqv123`, `<b>x</b>`, `'; --`, `%`: probe live and record whether the body is `products: []` with 200 or another code; no HTTP 500; input is not echoed unescaped | P2 | api |
| 5 | Empty `search_product=` | Probe live and record: either 200 with all 34 products (as UI) or 400 "Bad request, search_product parameter is missing in POST request." (empty string may count as present, as in verifyLogin) | P2 | api |
| 6 | Wrong methods GET/PUT/DELETE | Probe live and record per method: body `responseCode` 405 "This request method is not supported." or real HTTP 405 DRF `{"detail":"Method \"X\" not allowed."}` | P2 | api |
| 7 | What could realistically break: JSON body | Send `{"search_product":"top"}` as JSON instead of form: probe live and record (expected 400 "Bad request, search_product parameter is missing in POST request." since only form is read) | P3 | api |

## Risks & quirks
- HTTP status is often 200 even when body `responseCode` is 400/405. Assert body first.
- Site-map notes `&` is not encoded by the UI form; via the API, send it through Playwright `FormData` (encodes correctly). A raw `+` in `curl -d` decodes to a space.
- Wrong-method format differs between endpoints; do not assume.
- Transient Cloudflare 520 / 503 HTML: classify as env/flake.
- Result order is not specified; compare as sets by id.

## Out of scope / open questions
- Multi-word terms ("Blue Top"): probe live and record whether the match is substring on the whole phrase.
- Whether brand is also searched (site-map says name OR category only). Probe live and record with a brand-only term such as "Polo".
- Very long input (1000+ chars): not planned; add if scenario 4 shows fragility.
