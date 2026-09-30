# Test plan: brands

## Scope
In: brand sidebar (with product counts) on `/products` and `/`, `/brand_products/<brand>` list, switching brand, unknown brand, one light add-to-cart from a brand page; site TC19.
Out: categories (plan `categories`), search, cart behaviour in depth (plan `cart`), `GET /api/brandsList` depth (plan `api-brands`).

## Preconditions & data
- No account, logged out, no cleanup.
- Ground truth: `GET /api/productsList` (field `brand` per product) and `GET /api/brandsList`. Do not hardcode brand names or counts.
- Brand names, and how they appear in URLs (spaces, `&`): probe live and record.
- Site TC19 steps: home; click 'Products'; "Verify that Brands are visible on left side bar"; click any brand name; "Verify that user is navigated to brand page and brand products are displayed"; click any other brand link; "Verify that user is navigated to that brand page and can see products". The title mentions "Cart" but no step adds to cart; scenario 7 is a light extra.

## Scenarios
| # | Scenario | Expected | Priority | Type |
|---|---|---|---|---|
| 1 | TC19: from home click "Products"; brand sidebar | URL `/products`; "Brands" sidebar visible with at least one brand link, each with a count (probe live and record heading text and count format, e.g. "(6)") | P1 | ui |
| 2 | TC19: click a brand | URL matches `/brand_products/<brand>`; heading names the brand (probe live and record exact text, likely "<BRAND> PRODUCTS"); at least 1 card visible | P1 | ui |
| 3 | TC19: click a different brand | URL and heading change to the second brand; cards visible; first brand's products no longer listed | P1 | ui |
| 4 | Only that brand is listed | Card names equal the set of API products whose `brand` equals that brand; count equal | P1 | ui+api |
| 5 | Sidebar count vs listed count | For every sidebar brand the number in the sidebar equals the number of cards on its page and the API count (probe live: mismatch is a product bug, record) | P2 | ui+api |
| 6 | Unknown brand `/brand_products/NoSuchBrand` | No crash; probe live and record (empty list, message or redirect); assert observed; never a DEBUG page, stack trace or HTTP 500 | P2 | ui |
| 7 | Add a brand product to the cart (light) | On a brand page click "Add to cart" on the first card; modal "Added!" appears; "View Cart" shows that product's name (one row, quantity 1) | P2 | ui |
| 8 | What could break: brand names with spaces or `&` (e.g. multi-word brands from the API) | Sidebar link for every API brand resolves to a page listing its products; none 404/500 or empty (probe live: encoding of `&` and spaces, record) | P1 | ui+api |

## Risks & quirks
- Ads: iframes and interstitials cover the sidebar and cards; rely on `BaseUiTest` blocking and `Ads.dismissIfPresent`; watch for `#google_vignette` in the URL.
- Sidebar links contain the count inside the link text, so match brand name by prefix/regex, not exact text.
- Brand names with `&` or spaces may break in URLs (search form already fails on `&`, see site-map search); scenario 8 may be red, report as product bug.
- Card name is a bare `<p>` in `.productinfo`; hover overlay duplicates the "Add to cart" (scope to `.productinfo`; an overlay click is a known trap).
- Add to cart is ajax `GET /add_to_cart/<id>?quantity=1` then modal `#cartModal`; wait on the modal.
- `/api/productsList` may be served as text/html: parse with Gson from `text()`. 520/503 pages are env/flake.

## Out of scope / open questions
- Whether sidebar counts are correct: probe live and record.
- Case sensitivity of `/brand_products/polo` vs `Polo`: probe live and record.
- Heading text for an unknown brand: probe live and record.
