# Test plan: checkout

## Scope
In: guest "Proceed To Checkout" prompt (TC14 steps 7-8 only); `/checkout` as logged-in user: delivery + billing address, order review, comment textarea, "Place Order" present (TC23); `/checkout` with empty cart; `/checkout` while logged out.
Out: clicking "Place Order", `/payment`, `/payment_done`, invoice (plans: `order`, `invoice`); registering during checkout past account creation (plan: `register`); cart editing (plan: `cart`).

## Preconditions & data
- Logged-in scenarios: create account via `AccountApi` with `TestUsers.newUser()` (expect 201 "User created!"), `registerForCleanup(user)`, log in via UI. Address fields come from the TestUsers object (title, first/last name, company, address1, address2, city, state, zipcode, country, mobile).
- Cart: add 1-2 products via UI; read names/prices/quantities from `/view_cart`, never hardcode.
- Guest scenarios start with a fresh context (empty cart, logged out).

## Scenarios
| # | Scenario | Expected | Priority | Type |
|---|---|---|---|---|
| 1 | Guest: add product, open `/view_cart`, click "Proceed To Checkout" | Modal prompts to register or login. Probe live and record exact text, and that it offers "Register / Login" link and a "Continue On Cart" button | P1 | ui |
| 2 | Guest: in that modal click "Register / Login" | Lands on `/login` showing "New User Signup!" and "Login to your account". Cart still holds the product after login (TC14 flow; site-map says cart survives) | P1 | ui |
| 3 | Guest: in the modal click "Continue On Cart" (probe live for exact label) | Modal closes, stays on `/view_cart`, cart unchanged | P3 | ui |
| 4 | TC23: logged-in user with product in cart clicks "Proceed To Checkout" | On `/checkout`, "Address Details" and "Review Your Order" visible; delivery address block matches account: title + first/last name, company, address1, address2, "city state zipcode", country, mobile. Probe live and record exact line layout | P1 | ui |
| 5 | TC23: billing address block on same page | Same values as registered address and as delivery block. Probe live and record whether title/name are prefixed | P1 | ui |
| 6 | Order review table with 2 products (quantities 1 and 3) | One row per cart row: same name, price, quantity, total (price x quantity). Probe live and record whether a grand total row exists (site-map: none on `/view_cart`; `/checkout` for guest shows Rs. 0) and that it equals the sum | P1 | ui |
| 7 | Comment textarea and "Place Order" | Textarea visible, accepts typed text incl. 500+ chars and `<b>x</b>` without error on the page; "Place Order" visible and enabled. Do not click. Probe live and record placeholder/label | P2 | ui |
| 8 | Logged in with empty cart, open `/checkout` directly | Probe live and record: redirect, empty review table, or addresses with no rows; is "Place Order" still shown | P2 | ui |
| 9 | Guest opens `/checkout` directly, no login | No redirect (site-map): empty page with "Address Details", Rs. 0, "Place Order", logged-out header. Record whether address blocks are empty | P2 | ui |
| 10 | What could break: logged-in user, add product, `/checkout`, reload, then Back to `/view_cart` | Addresses and rows unchanged after reload; no duplicated rows; cart intact on Back | P2 | ui |

## Risks & quirks
- Both address blocks render as lists of plain lines with few stable hooks; needs live probing, tier-5 justification comments likely.
- The `/view_cart` title is "Automation Exercise - Checkout"; do not use page title to identify `/checkout`.
- The guest modal may be overlaid by ads; use `Ads.dismissIfPresent`. Modal text and buttons unverified.
- Prices are "Rs. N" strings; parse digits. Order rows may duplicate cart ids or have different cell classes than `/view_cart`.
- Country display and state/city ordering may differ from form input; compare via probed layout.
- Account creation via API omits birth date only; address fields must all be sent (see site-map `/createAccount` form).

## Out of scope / open questions
- Probe live and record: modal exact text/buttons, address line layout, grand total presence, empty-cart `/checkout` behaviour, whether unicode/apostrophe names render escaped.
- Register-during-checkout full flow (TC14 steps 9-14) is covered by `order`/`register` plans.
- "Place Order" click outcome belongs to `order`.
