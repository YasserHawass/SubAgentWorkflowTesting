# Test plan: search

## Scope
In: `/products` search box (`#search_product`, `#submit_search`), the results view and its heading, matching rules, URL/query param, POST `/api/searchProduct` cross-check (site-map API rows 5-6). Source: site TC9 Search Product.
Out: product grid/detail (plan `products`), add-to-cart and TC20 search + cart after login (plan `cart`), category/brand filters, full API contract (`api-search`).

## Preconditions & data
- No account needed; all scenarios run logged out. No cleanup needed.
- Known-good terms: "Tshirt", "Dress", "Jeans" (confirm they return results: probe live and record).
- Reference product list via `GET /api/productsList` for the API cross-check and to derive expected match sets.
- Site TC9 steps: open home, click Products, verify ALL PRODUCTS page, enter product name and click search, verify 'SEARCHED PRODUCTS' visible, verify all products related to search are visible.

## Scenarios
| # | Scenario | Expected | Priority | Type |
|---|---|---|---|---|
| 1 | Happy path (TC9): from `/products`, search "Tshirt" | Heading "Searched Products" visible (site TC text is "SEARCHED PRODUCTS"; probe live and record exact casing); at least one product card shown | P1 | ui |
| 2 | Every result matches the term: search "Dress" and read all card names | Each name contains the term (probe live: does it also match category/brand? record); count > 0 | P1 | ui |
| 3 | Case-insensitivity: search "tshirt", "TSHIRT", "Tshirt" | Same result count and names for all three (probe live and record) | P2 | ui |
| 4 | Partial term: search "shirt" | Results include names containing "shirt"; count >= count for "Tshirt" | P2 | ui |
| 5 | No-result term: search "zzzxqv123" | Heading still shown (probe live and record); zero product cards; no error page | P2 | ui |
| 6 | Empty search: click search with empty box | Probe live and record: expected either stays on `/products` with all products, or shows heading with all/none. Assert observed behaviour | P3 | ui |
| 7 | Special characters and injection: `<b>x</b>`, `'; --`, `%`, `&` | No 500/error page; page renders; term is not rendered as HTML; zero or recorded results | P2 | ui |
| 8 | URL/query param: after a search, read the URL; then reload | Probe live and record: form likely uses `/products?search=<term>` (GET). Reload keeps the same results and heading | P2 | ui |
| 9 | API cross-check: POST `/api/searchProduct` form `search_product=top` | `responseCode` 200; body has `products` array; each name/category matches; count equals UI count for the same term (probe live and record) | P2 | api |
| 10 | API missing param: POST `/api/searchProduct` with no body | `responseCode` 400, message "Bad request, search_product parameter is missing in POST request." | P2 | api |
| 11 | What could realistically break: search after previous search, and search does not lose the sidebar/header | Second term replaces the first result set (no stale cards from term one); category and brand sidebars still visible | P3 | ui |

## Risks & quirks
- Ads: iframes and occasional interstitial on `/products`; use `Ads.dismissIfPresent` before the action.
- Product card name has no stable hook (bare `<p>` inside `.productinfo`, first `<h2>` is the price); see site-map "Discovered during login".
- API is form-encoded; HTTP status is often 200, assert `responseCode` in the body. Transient Cloudflare 520 / 503 pages are `env` or `flake`, not product bugs.
- `/products` shows the "Searched Products" heading only after a search; the default heading is "All Products".
- Result counts may change if the site catalog changes; compare relatively (UI vs API, term vs superset) rather than hardcoding counts.

## Out of scope / open questions
- Does search match category or brand text, or only product name? Probe live and record.
- Are leading/trailing spaces trimmed? Probe live and record.
- Max input length and unicode handling: not planned; probe live if time allows.
- Does POST `/api/searchProduct` with an empty `search_product` return 200 with all/none or 400? Probe live and record.
- Search then add to cart, and TC20: in `cart` plan.
