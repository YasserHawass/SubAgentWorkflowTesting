# Test plan: products

## Scope
In: `/products` grid, card contents, "View Product" navigation, `/product_details/<id>` fields, invalid ids, `GET /api/productsList` cross-check; site TC8.
Out: search (plan `search`), cart and add-to-cart flows (plan `cart`; only note the button exists), reviews (TC21), categories/brands (TC18/19), api-products depth (plan `api-products`).

## Preconditions & data
- No account needed; tests run logged out. No cleanup.
- Product ids and names come from `GET /api/productsList` at test time; do not hardcode names or prices.
- Site TC8 steps: Home visible; click "Products"; verify ALL PRODUCTS page; "The products list is visible"; click "View Product" of first product; "User is landed to product detail page"; verify "product name, category, price, availability, condition, brand" visible.

## Scenarios
| # | Scenario | Expected | Priority | Type |
|---|---|---|---|---|
| 1 | TC8: From home click "Products" in header | URL is `/products`; heading "All Products" visible (probe live and record exact text); product cards visible | P1 | ui |
| 2 | TC8: On `/products`, click "View Product" of the first card | URL matches `/product_details/<id>`; name, category, price, availability, condition, brand all visible and non-empty | P1 | ui |
| 3 | Card contents: every card on the grid | Each card has image, price (starts "Rs. "), name, "Add to cart" button and "View Product" link; button exists, do not click it | P2 | ui |
| 4 | Grid count and names match `GET /api/productsList` | responseCode 200; number of cards equals `products.length`; set of card names equals set of API names (probe live: whether grid is paginated, record) | P1 | ui+api |
| 5 | Detail fields match the grid card for the same product (name, price) | Name and price on detail equal those on the card that was clicked | P2 | ui |
| 6 | Detail labels format: category, availability, condition, brand | Category like "Women > Tops"; "Availability:" "Condition:" "Brand:" have non-empty values (probe live and record exact label texts and values) | P2 | ui |
| 7 | Quantity input on detail page | Default value is "1"; "Add to cart" button visible (do not click); typing 3 keeps "3" (0, negative, letters: probe live and record) | P2 | ui |
| 8 | Nonexistent id, e.g. `/product_details/999999` | No crash; probe live and record (empty detail, redirect, or error page); assert observed behaviour, no HTTP 500 | P2 | ui |
| 9 | Malformed id, e.g. `/product_details/abc` and `/product_details/0` | Probe live and record; must not show a Django debug page or stack trace (compare with logout-1 bug: `/logout` returned 500) | P3 | ui |
| 10 | What could break: every id from `/api/productsList` opens a detail page with a non-empty name and price | All ids resolve; none blank or 404; ids in API may not be contiguous | P1 | ui+api |
| 11 | `POST /api/productsList` | responseCode 405, message "This request method is not supported." (owned by `api-products`; one smoke check only) | P3 | api |

## Risks & quirks
- Ads: iframes and interstitials can cover cards and "View Product"; rely on `BaseUiTest` blocking and `Ads.dismissIfPresent`. A vignette may change the URL (`#google_vignette`).
- Product card name has no stable hook: a bare `<p>` in the card; the first heading is the price ("Rs. 500"). See site-map "Discovered during `login`".
- Cards show hover overlay with a second "Add to cart"; do not click it here.
- `/api/productsList` HTTP status is often 200 even for errors; assert `responseCode` in body. Response may be served as text/html; parse with Gson from `text()`.
- Price text is "Rs. <n>"; compare strings, not numbers.
- Transient Cloudflare 520 / 503 pages: classify as env or flake.

## Out of scope / open questions
- Pagination or lazy loading on `/products`: probe live and record.
- Exact heading text and label wording on detail page: probe live and record.
- Behaviour for invalid ids (scenarios 8, 9): unknown, case-designer records observed result.
- Review form on detail page: plan `reviews`. Add to cart with quantity: plan `cart`.
