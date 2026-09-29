# Test plan: cart

## Scope
In: "Add to cart" from home, `/products`, recommended items and `/product_details/<id>`; "Added!" modal; `/view_cart` table; quantity; removal; empty cart; cart after login. Site TC12, TC13, TC17, TC20 (search only as a step), TC22.
Out: checkout, payment, orders (stop at "Proceed To Checkout" being visible); search behaviour (plan: `search`); product listing/detail content (plan: `products`); subscription box in cart footer (plan: `subscription`); cart-dropped-on-logout (plan: `logout`).

## Preconditions & data
- Start logged out on home unless stated. Products are picked from the live grid by name; do not hardcode prices, read them from the page.
- Scenario 10 needs an account: `AccountApi` create with `TestUsers.newUser()` (expect 201 "User created!") and `registerForCleanup(user)`.
- Each test starts with an empty cart (fresh context). No shared state.

## Scenarios
| # | Scenario | Expected | Priority | Type |
|---|---|---|---|---|
| 1 | TC12: home visible, open "Products", add first product, click "Continue Shopping", add second product, click "View Cart" | Modal shows "Added!" each time; `/view_cart` lists both products; each row: price x quantity = total; "Proceed To Checkout" visible | P1 | ui |
| 2 | Add a product; in modal click "View Cart" | Lands on `/view_cart` with that product, quantity 1 | P1 | ui |
| 3 | Add a product; in modal click "Continue Shopping" | Modal closes, stays on the same page. Probe live and record whether a cart count is shown | P2 | ui |
| 4 | TC13: from home click "View Product", set quantity to 4, "Add to cart", "View Cart" | Detail page opens; cart row quantity is 4; total = price x 4 | P1 | ui |
| 5 | Add the same product twice from the list | Probe live and record: one row with quantity 2 (expected) vs two rows; total matches quantity | P2 | ui |
| 6 | TC17: add products, open cart, click X on one row | Row removed; other rows stay. Removing the only product shows "Cart is empty!" (from site-map; confirm live) | P1 | ui |
| 7 | Open `/view_cart` with nothing added | Empty state "Cart is empty!" (probe live for full text and any link to products); no "Proceed To Checkout" | P2 | ui |
| 8 | TC22: scroll to bottom of home, "RECOMMENDED ITEMS" visible, add a recommended product, "View Cart" | Product appears in cart. Probe live and record carousel behaviour | P1 | ui |
| 9 | Boundary: detail page quantity 0, negative, non-numeric, very large (e.g. 999) | Probe live and record: rejected, coerced, or added; note resulting cart quantity | P3 | ui |
| 10 | TC20: as guest open "Products", search a product name, "SEARCHED PRODUCTS" visible, add results, "View Cart", click "Signup / Login", log in with the API-created account, open `/view_cart` | Same products still in cart after login (site-map: cart survives login) | P1 | ui |
| 11 | What could break: add product, open `/view_cart`, reload, then Back/forward | Contents unchanged after reload; no duplicate rows; totals not recalculated wrongly | P2 | ui |
| 12 | Add two different products with quantities 1 and 3 (via detail page) | Each row total = price x quantity. Probe live whether a grand total is shown | P2 | ui |

## Risks & quirks
- "Added!" modal must be dismissed or left via its buttons before the next action; it can intercept clicks.
- Listing "Add to cart" buttons sit in a hover overlay and may be covered. Ad overlays can block clicks: use `Ads.dismissIfPresent`.
- Product names have no stable hook (site-map "Discovered during login"); the first heading in a card is the price. Cart table `#cart_info_table` is tier 5 and needs a justification comment.
- Prices are "Rs. N" strings; parse digits for arithmetic.
- `/view_cart` page title is "Automation Exercise - Checkout"; do not assert "Cart".
- Cart is session-based: dropped on logout, survives registration and login. State is per browser context.
- Recommended items are a carousel; visible slide may vary.

## Out of scope / open questions
- Checkout, order, invoice: other plans.
- Probe live and record: same-product-twice behaviour, quantity boundaries, exact empty-cart text, cart count in header.
- Merge with an account's previously saved cart: probe live if feasible.
