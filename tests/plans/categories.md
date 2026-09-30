# Test plan: categories

## Scope
In: category sidebar on `/` and `/products`, expanding Women/Men/Kids, `/category_products/<id>` heading and product list, switching category, invalid ids; site TC18.
Out: brands (plan `brands`), search (plan `search`), add to cart (plan `cart`), product detail fields (plan `products`).

## Preconditions & data
- No account, logged out, no cleanup.
- Ground truth: `GET /api/productsList`; each product has `category.usertype.usertype` (Women/Men/Kids) and `category.category` (e.g. Tops, Dress). Do not hardcode names or counts.
- Category ids and subcategory names: probe live and record (links in the sidebar; ids are not assumed contiguous).
- Site TC18 steps: home; "Verify that categories are visible on left side bar"; click 'Women'; click a subcategory e.g. Dress; "Verify that category page is displayed and confirm text 'WOMEN - TOPS PRODUCTS'"; click a subcategory of 'Men'; "Verify that user is navigated to that category page".
- TC18 is inconsistent: it clicks Dress but expects 'WOMEN - TOPS PRODUCTS'. Expect the heading to name the clicked subcategory (probe live and record; case-designer must not assert TOPS after clicking Dress).

## Scenarios
| # | Scenario | Expected | Priority | Type |
|---|---|---|---|---|
| 1 | TC18: open `/`; sidebar visible; expand "Women", click a subcategory | "Category" sidebar visible with Women, Men, Kids; URL matches `/category_products/<id>`; heading "WOMEN - <SUBCAT> PRODUCTS" (probe live: DOM text vs rendered upper case) | P1 | ui |
| 2 | TC18: then expand "Men", click a Men subcategory | URL changes to another `/category_products/<id>`; heading starts "MEN - " and names that subcategory | P1 | ui |
| 3 | Listed products belong to the category | Card names on the page equal the set of API products whose usertype and category match the heading; count equal; at least 1 card (probe live: empty subcategories, record) | P1 | ui+api |
| 4 | Sidebar on `/products` behaves the same | Categories visible; click one subcategory lands on its `/category_products/<id>`; same heading pattern | P2 | ui |
| 5 | Expand/collapse accordion | Clicking "Women" reveals its subcategory links; probe live whether opening "Men" collapses "Women" and record | P3 | ui |
| 6 | Nonexistent id `/category_products/9999` | No crash; probe live and record (empty list, redirect or message); assert observed; never a DEBUG page, stack trace or HTTP 500 | P2 | ui |
| 7 | Malformed id `/category_products/abc` (also `0`, `-1`) | Probe live and record; a Django DEBUG page ("DEBUG = True", URLconf) is a product bug, not an accepted outcome (cf. `/product_details/abc`) | P2 | ui |
| 8 | What could break: every subcategory link in the sidebar | Each link resolves (not 404/500), shows a heading and cards; summed subcategory counts equal 34 unless a product has no category (probe live and record) | P1 | ui+api |

## Risks & quirks
- Ads: iframes and interstitials cover the sidebar; rely on `BaseUiTest` blocking and `Ads.dismissIfPresent`; a vignette may add `#google_vignette` to the URL.
- Sidebar is a Bootstrap accordion: subcategory links are hidden until the parent is clicked; the parent click must complete before the child is clickable.
- Heading is CSS upper-cased; use `textContent` for DOM case or `innerText` for rendered case (see products discovery).
- Card name is a bare `<p>` in `.productinfo`; hover overlay duplicates name/price (scope to `.productinfo`).
- Invalid ids in this Django app may raise the DEBUG page (site-map: products, logout bugs); expect scenario 7 to go red, report as product bug.
- `/api/productsList` may be served as text/html: parse with Gson from `text()`. 520/503 pages are env/flake.

## Out of scope / open questions
- Exact category ids, subcategory list, and whether Kids subcategories have products: probe live and record.
- Whether the same product can appear under two categories: probe live and record.
- Heading text for empty/unknown id: probe live and record.
