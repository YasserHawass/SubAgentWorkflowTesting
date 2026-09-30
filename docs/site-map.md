# Site map — automationexercise.com

Reference for spec-reader and case-designer. Agents update this file when they discover something new (append, don't rewrite).

## Routes (UI)

| Route | What's there | Notes |
|---|---|---|
| `/` | Home: header nav, carousel, category sidebar, brand sidebar, featured products, recommended items, subscription box in footer | Ad-heavy |
| `/login` | Two forms on one page: **Login** (email, password) and **Signup** (name, email) | Inputs use `data-qa="login-email"`, `login-password`, `login-button`, `signup-name`, `signup-email`, `signup-button` |
| `/signup` | Account info form after signup: title, password, DOB, newsletter/offers checkboxes, address fields | `data-qa` on most inputs, `create-account` button |
| `/account_created` | Confirmation after signup, "Continue" button | |
| `/delete_account` | Confirmation after deleting | `data-qa="account-deleted"` |
| `/logout` | Logs out, redirects to `/login` | |
| `/products` | All products grid, search box (`#search_product`, `#submit_search`), category + brand sidebars | |
| `/product_details/<id>` | Name, category, price, availability, condition, brand, quantity input, "Add to cart", review form | |
| `/category_products/<id>` | Products filtered by category (Women / Men / Kids subcats) | |
| `/brand_products/<brand>` | Products filtered by brand | |
| `/view_cart` | Cart table: product, price, quantity, total, delete (X); "Proceed To Checkout" | Subscription box in footer |
| `/checkout` | Delivery address, billing address, order review, comment textarea, "Place Order" | Requires login |
| `/payment` | Card name/number/CVC/expiry, "Pay and Confirm Order" | `data-qa="name-on-card"`, `card-number`, `cvc`, `expiry-month`, `expiry-year`, `pay-button` |
| `/payment_done/<id>` | Order placed confirmation, "Download Invoice", "Continue" | Invoice is a `.txt` download |
| `/contact_us` | Name, email, subject, message, file upload, Submit; triggers a JS `confirm` dialog | Success alert: "Success! Your details have been submitted successfully." |
| `/test_cases` | The site's own list of 26 manual test cases | Useful as a requirements source |
| `/api_list` | API documentation | |

## Header state

- Logged out: `Signup / Login` link.
- Logged in: `Logged in as <name>`, `Logout`, `Delete Account` links.

## Known behaviours / quirks

- Adding to cart shows a modal: "Added!" with "View Cart" and "Continue Shopping".
- Home page "Subscribe" success text: "You have been successfully subscribed!"
- Wrong login error text: "Your email or password is incorrect!"
- Existing email on signup: "Email Address already exist!"
- Ads: Google AdSense iframes; occasional full-page interstitial. See CONVENTIONS.
- Contact form file upload accepts any file; `confirm()` dialog must be accepted.
- Scroll-to-top arrow: `#scrollUp`.

## API (base `/api`)

All responses are JSON with `responseCode` and `message` in the body. HTTP status is often 200 even for errors — assert on `responseCode`.

| # | Method | Path | responseCode | message / body |
|---|---|---|---|---|
| 1 | GET | `/productsList` | 200 | `{ products: [...] }` |
| 2 | POST | `/productsList` | 405 | "This request method is not supported." |
| 3 | GET | `/brandsList` | 200 | `{ brands: [...] }` |
| 4 | PUT | `/brandsList` | 405 | "This request method is not supported." |
| 5 | POST | `/searchProduct` (form: `search_product`) | 200 | `{ products: [...] }` |
| 6 | POST | `/searchProduct` (no param) | 400 | "Bad request, search_product parameter is missing in POST request." |
| 7 | POST | `/verifyLogin` (form: `email`, `password`) | 200 | "User exists!" |
| 8 | POST | `/verifyLogin` (missing password) | 400 | "Bad request, email or password parameter is missing in POST request." |
| 9 | DELETE | `/verifyLogin` | 405 | "This request method is not supported." |
| 10 | POST | `/verifyLogin` (wrong creds) | 404 | "User not found!" |
| 11 | POST | `/createAccount` (form: name, email, password, title, birth_date, birth_month, birth_year, firstname, lastname, company, address1, address2, country, zipcode, state, city, mobile_number) | 201 | "User created!" |
| 12 | DELETE | `/deleteAccount` (form: email, password) | 200 | "Account deleted!" |
| 13 | PUT | `/updateAccount` (same fields as create) | 200 | "User updated!" |
| 14 | GET | `/getUserDetailByEmail?email=` | 200 | `{ user: {...} }` |

Request bodies are `application/x-www-form-urlencoded`, not JSON.

## Features (for `/test-feature <name>`)

Use these names so file paths stay consistent:

`register`, `login`, `logout`, `contact-us`, `products`, `search`, `subscription`, `cart`, `checkout`, `order`, `invoice`, `reviews`, `categories`, `brands`, `scroll`, `api-products`, `api-brands`, `api-search`, `api-auth`, `api-account`

## Site's own manual test cases (`/test_cases`)

TC1 Register User · TC2 Login correct · TC3 Login incorrect · TC4 Logout · TC5 Register existing email · TC6 Contact Us · TC7 Test Cases page · TC8 All products + detail · TC9 Search · TC10 Subscription home · TC11 Subscription cart · TC12 Add to cart · TC13 Quantity in cart · TC14 Order: register while checkout · TC15 Order: register before checkout · TC16 Order: login before checkout · TC17 Remove from cart · TC18 Category products · TC19 Brand products · TC20 Search + cart after login · TC21 Add review · TC22 Recommended items to cart · TC23 Address in checkout · TC24 Download invoice · TC25 Scroll with arrow · TC26 Scroll without arrow

## Discovered during `login` (2026-09-29)

- **Logged-in user opening `/login` is redirected to `/`** (home page). The header still shows `Logged in as <name>` and the "Login to your account" heading is absent. This was confirmed by `hasURL(baseUrl + "/")` passing twice (TC-LOGIN-11).
- **Product cards have no stable hook for the name.** On `/products` the name is a bare `<p>` inside `.productinfo`. There is no `data-qa` and no role. The card's first heading (`<h2>`) is the **price** ("Rs. 500"), not the name, so `getByRole(HEADING).first()` returns the price. The accepted locator is `card.locator("p")`, with a tier-5 justification comment.
- The cart table on `/view_cart` is `#cart_info_table`. It's the only stable hook (tier 5, needs a comment).
- **Login email handling (TC-LOGIN-10):** an upper-cased email is rejected with "Your email or password is incorrect!" (case-sensitive match); an email with leading/trailing spaces is trimmed and logs in successfully.

## Discovered during `register` (2026-09-29)

- **The Signup form on `/login` POSTs to `/signup`.** With an existing email, the site re-renders the login page **at URL `/signup`** and shows "Email Address already exist!". The URL does not stay `/login`.
- **Existing-email check is case-sensitive.** An upper-cased version of an existing email passes signup and reaches the account form. Emails appear to be unique only case-sensitively. Login is case-sensitive too (see login section).
- `/signup` `data-qa` values are `name`, `email`, `password`, `days`, `months`, `years`, `first_name`, `last_name`, `company`, `address`, `address2`, `country`, `state`, `city`, `zipcode`, `mobile_number` and `create-account`. `/account_created` has a `continue-button`.
- The title radios `#id_gender1`/`#id_gender2` and the checkboxes `#newsletter`/`#optin` have no `data-qa`, so tier-5 ids are used with a justification comment.
- On `/signup`, name and email are prefilled from the signup form. **Email is disabled** and name is editable.
- Required fields are password, first name, last name, address, state, city, zipcode and mobile. Company and address2 are optional.
- The DOB year select offers **2021 down to 1900**. There is no server-side date validation: 29 Feb 2019 and 31 Apr 2019 are both accepted.
- Names with unicode, apostrophes, 120 characters or HTML are all accepted. HTML is escaped in the header (`&lt;b&gt;`).
- The cart survives registration. Pressing Back after account creation shows the login page with "Email Address already exist!".

## Discovered during `logout` (2026-09-29)

- **BUG: `GET /logout` while logged out returns HTTP 500**, a Django "KeyError at /logout" debug page with no site chrome. See `tests/reports/bugs/logout-1.md`. TC-LOGOUT-08 is expected to stay red until the site fixes it.
- Logging out from any page (for example `/products`) lands on `/login`. After a reload the header is still logged out.
- Pressing Back after logout returns to `/` with a **logged-out** header, so no stale session is shown.
- **The cart is dropped on logout.** `/view_cart` then shows "Cart is empty!".
- **`/checkout` does not redirect when logged out.** It renders an empty checkout page (Address Details, Rs. 0, Place Order) with a logged-out header. site-map's "Requires login" means the order can't be placed, not that the route is guarded.
- The `/view_cart` page title is "Automation Exercise - Checkout".

## Discovered during `api-auth` (2026-09-29)

- **HTTP status is always 200** on `/api/verifyLogin`, even for 400, 404 and 405 body `responseCode`s. Assert the body.
- A **missing email** returns the same 400 message as a missing password: "Bad request, email or password parameter is missing in POST request."
- **GET and PUT** on `/verifyLogin` return 405 "This request method is not supported.", the same as DELETE.
- **Empty strings count as present**, so they get 404 "User not found!", not 400.
- The email is **case-sensitive**. An upper-cased email returns 404, consistent with UI login and signup.
- Deleting an already-deleted account (`DELETE /deleteAccount`) returns 404 "Account not found!".
- Cloudflare occasionally returns **HTTP 520 with a non-JSON body** ("error code: 520"). It's transient. Treat it as `env` or `flake`, never as a product bug.

## Discovered during `api-account` (2026-09-29)

- A **duplicate create** returns 400 "Email already exists!", and the first account stays intact.
- **Create with a missing `email`, `password` or `name`** returns 400 "Bad request, <field> parameter is missing in POST request.". `company` is optional.
- `getUserDetailByEmail` with an unknown or deleted email returns 404 "Account not found with this email, try another email!". With no `email` param it returns 400 "Bad request, email parameter is missing in GET request."
- **Delete with a wrong password** returns 404 "Account not found!". **Update with an unknown email or a wrong password** also returns 404 "Account not found!". Neither changes the account.
- **You can't change a password through the API.** `updateAccount` authenticates with the `password` field, so sending a new one gets 404 and the old password keeps working.
- **Wrong methods don't share one error format.** `createAccount`, `deleteAccount` and `updateAccount` return a *real* HTTP 405 with a DRF body `{"detail":"Method \"X\" not allowed."}` and no `responseCode`. `getUserDetailByEmail` (like `verifyLogin`) returns HTTP 200 with body 405 "This request method is not supported."
- The `user` object's keys differ from the create form's: `birth_day`, `first_name` and `last_name` (not `birth_date`, `firstname`, `lastname`), plus `id`. `password` and `mobile_number` are not returned.
- In a form body a raw `+` decodes to a space. For `curl -d`, URL-encode `+` as `%2B`. Playwright `FormData` encodes it correctly.
- A transient **HTTP 503 HTML "heavy load"** page is possible, like the Cloudflare 520. Treat both as `env` or `flake`.

## Discovered during `products` (2026-09-29)

- `/products` shows **all 34 products on one page**, with no pagination or lazy load. The heading DOM text is "All Products", which CSS renders as "ALL PRODUCTS" (use `innerText` for the rendered case).
- The card root is `.product-image-wrapper`. A hover overlay **duplicates** the name, price and Add to cart, so scope to `.productinfo`. The "Add to cart" `<a>` has no href, so it has no link role.
- On the detail page the wrapper is `.product-information`. The name is its only heading, and the price is a bare `<span>` ("Rs. N"). Rows read "Category: Women > Tops" (API `usertype > category`), "Availability: In Stock", "Condition: New", "Brand: Polo".
- `#quantity` is `type=number` with a default of 1. `fill("abc")` throws, so use `pressSequentially`. 0, -1 and 1.5 are kept as typed. Letters leave the field empty.
- `/product_details/999999` and `/product_details/0` return HTTP 200 with an **empty** detail page, not a 404.
- **BUG candidate: `/product_details/abc` returns 404 with the Django DEBUG page** ("DEBUG = True", URLconf listing). Same root cause as bugs/logout-1.md: DEBUG is on in production.
- `page.request()` GETs work for `/api/productsList` and `/product_details/<id>`, which is fast for bulk checks.

## Discovered during `search` (2026-09-29)

- Search is a GET to **`/products?search=<term>`**, and the URL works directly and survives a reload. The heading changes to "Searched Products" ("SEARCHED PRODUCTS" rendered).
- The box is `getByPlaceholder("Search Product")` (`#search_product`, name `search`). The button is an icon-only `<button id="submit_search">` with **no accessible name**, so it's tier 5.
- **Search matches the name OR the category**, case-insensitively. For example, "Dress" matches a "Sleeves Top and Short" product in category Dress. Counts: Tshirt 6, Dress 9, shirt 13, top 14.
- No-match input (`zzzxqv123`, `<b>x</b>`, `'; --`, `%`) gives 0 cards and keeps the heading. Markup is not rendered.
- An empty search (`?search=`) keeps "All Products" and lists all 34.
- **BUG (low): `&` is not URL-encoded** by the search form. `&` becomes `?search=&`, which is treated as an empty search and lists everything, so terms like "Tops & Shirts" break.
- `POST /api/searchProduct` returns HTTP 200 even when the body says 400.

## Discovered during `cart` (2026-09-29)

- Add to cart is an ajax **`GET /add_to_cart/<id>?quantity=N`**, followed by the "Added!" modal `#cartModal` (View Cart / Continue Shopping). Wait on the modal or on the response.
- Cart rows are `tr#product-<id>` with cells `.cart_price`, `.cart_quantity` (a **disabled button**, not an input, so the quantity can't be edited in the cart), `.cart_total_price` and `a.cart_quantity_delete`. **There is no grand total** on `/view_cart`.
- The same product added twice gives **one row with quantity 2**. The header shows **no cart count**.
- Empty cart: `#empty_cart` is always in the DOM, hidden when there are items. Its text is "Cart is empty! Click here to buy products." and "here" links to `/products`. On a fresh empty cart "Proceed To Checkout" is absent; after deleting the last row it's present but hidden.
- **BUG: an empty quantity** (`/add_to_cart/1?quantity=`) returns **HTTP 500**.
- **BUG: a negative quantity is accepted**: -1 creates a cart line with total Rs. -500. Quantity 0 is also accepted (total Rs. 0). 999 works (Rs. 499500).
- The recommended items carousel is `#recommended-item-carousel` with `.item.active`. It shows 3 cards per slide and **auto-advances about every 6s**, so locate cards by name, not position. In its markup, product 3's name `<p>` holds "Rs. 1000".
- Locator gotcha: a `has:` locator inside `filter()` resolves relative to each row. Don't repeat the `#cart_info_table` scope inside it.

## Discovered during `checkout` (2026-09-29)

- A guest clicking "Proceed To Checkout" opens an in-page modal `#checkoutModal` (title "Checkout", text "Register / Login account to proceed on checkout."). It has a "Register / Login" link (→ `/login`) and a "Continue On Cart" dismiss button. The URL stays `/view_cart`.
- **Timing gotcha:** the "Proceed To Checkout" click handler is bound by an inline script at the end of `<body>`, so a click before the `load` event does nothing. Use `page.waitForLoadState(LOAD)` first.
- When logged in, Proceed To Checkout does `window.location = '/checkout'`, so Back returns to `/view_cart`.
- Addresses: `ul#address_delivery` and `ul#address_invoice`. Lines in order are title+name ("Mr. Auto Tester"), company, address1, address2, "City State Zip", country, mobile. The two blocks are identical apart from their heading. Headings are CSS upper-cased, so `innerText` gives "YOUR DELIVERY ADDRESS"; use `textContent`.
- The review table has a **"Total Amount"** row equal to the sum of the row totals. There's still no grand total on `/view_cart` itself.
- The comment `<textarea>` has no placeholder or label. The text above it is "If you would like to add a comment about your order, please write it in the field below."
- `/checkout` **logged in with an empty cart** shows addresses, no rows, Rs. 0, **and "Place Order" is still offered**. Whether an empty order can be placed is checked in `order`.
- `/checkout` **as a guest** shows empty address blocks with a stray "." name line, Rs. 0, and a logged-out header.
- A guest cart merges into the account on login: a guest add plus a logged-in add of the same product gives quantity 2.

## Discovered during `order` (2026-09-29)

- After Pay, the success page `/payment_done/<id>` shows the heading `data-qa="order-placed"` "Order Placed!" and "Congratulations! Your order has been confirmed!". Download Invoice → `/download_invoice/<id>`. Continue → `/`, and the cart is then empty.
- **The "order id" is the cart total in Rs.**: `/payment_done/500` for one Blue Top, `/payment_done/0` for an empty cart. It isn't unique per order, so match it by pattern.
- `/payment` renders the card form for **anyone**, including logged out with an empty cart, with no redirect.
- The empty payment form is blocked client-side by HTML5 `required` on all 5 inputs.
- **No server-side card validation (BUGS):** a card number "abcd" or CVC "xyz" is accepted, expiry 01/2000 is accepted, and a logged-in user with an **empty cart can pay** (→ `/payment_done/0`). This is a demo site with no payment processor, but these are still spec violations.
- `/delete_account` shows `data-qa="account-deleted"` "Account Deleted!". Its page `<title>` is wrongly "Automation Exercise - Account Created". Continue → `/`, logged out.
- Back from `/payment_done` shows an empty `/payment` form; Forward returns to `/payment_done`. There's no error page.

## Discovered during `invoice` (2026-09-29)

- Download Invoice gives `invoice.txt` (`text/plain`, HTTP 200). The content is `Hi <first_name> <last_name>, Your total purchase amount is <N>. Thank you`, with no "Rs.". The name is the account's first and last name, not the signup name or the card name.
- **The invoice is built from the session name plus the amount in the URL.** User B fetching A's `/download_invoice/500` gets B's own name, so there's no cross-user leak. But **any amount can be forged** (`/download_invoice/<any N>`), and **with no session** it returns HTTP 200 "Hi , Your total purchase amount is 500. Thank you" (BUG: no auth guard).
- Tip: `TestUsers.newUser()` always uses "Auto Tester", so give users a unique first name when a test asserts on the invoice name.

## Discovered during `reviews` (2026-09-30)

- The review form `#review-form` is on every `/product_details/<id>`. Its fields have placeholders "Your Name", "Email Address" and "Add Review Here!" and no labels. The submit button is `#button-review`, and the tab text is "Write Your Review".
- All three fields are `required` and the email is `type=email`, so an invalid email is blocked by the browser.
- **Reviews are client-side only.** Submitting shows "Thank you for your review." in `#review-section` for about 2s, then clears the fields. **No request is sent, nothing is stored, and there is no review list anywhere.** A logged-in user's name and email are not prefilled.
- Every page POSTs a Cloudflare beacon to `/cdn-cgi/rum`. Exclude `/cdn-cgi/` when asserting "no requests".

## Discovered during `api-products` / `api-brands` / `api-search` (2026-09-30)

- Catalogue endpoints return a JSON body with **`Content-Type: text/html`**. Parse the text, don't rely on the content type.
- `productsList` has 34 products with usertypes {Women, Men, Kids}. **Ids are not contiguous**: 1-8, 11-16, 18-24, 28-31, 33, 35, 37-43.
- `brandsList` has **34 rows but only 8 distinct brand names** (e.g. Madame ×5). It's effectively one row per product. The ids are unique but not contiguous. Every product brand appears in the list, and there are no orphans.
- Wrong methods on `productsList`, `brandsList` and `searchProduct` return **HTTP 200 with body 405** "This request method is not supported.", the same as `verifyLogin`. Only create, delete and update account use a real HTTP 405 with a DRF body.
- `searchProduct`: "top" gives 14 (the same for TOP/Top) and "Dress" gives 9. No match gives `products: []`. An **empty `search_product=` returns all 34**. A JSON body gives 400 "Bad request, search_product parameter is missing in POST request.", because the endpoint only reads form data.

## Discovered during `contact-us` / `subscription` / `scroll` (2026-09-30)

- **Contact:** the confirm dialog text is "Press OK to proceed!" (shown once). Register `page.onDialog` *before* Submit. Dismissing it keeps the form values and shows no success text. "Home" after success is a **link**, not a button.
- Contact validation: **only Email is required** (HTML5). An empty name, subject or message, no file, a 3 MB file and a no-extension file are all accepted.
- **Strict-mode gotcha:** "Success! Your details have been submitted successfully." also exists **hidden in the footer subscribe box**, so scope the contact success to `#contact-page`.
- Timing gotcha: typing into the contact form before the `load` event can get wiped. Wait for LOAD first.
- The footer is `role=contentinfo` and is the same on `/` and `/view_cart`. The subscribe input is `getByPlaceholder("Your email address")`, and the icon-only button is `#subscribe`.
- Subscription: an empty or invalid email is blocked by browser validation with no site message. A repeat subscribe shows the same success again. The input is **not cleared**. The success text **auto-hides** after about 6s.
- Scroll: `#scrollUp` is in the DOM but hidden at the top, visible after scrolling down, and hidden again after clicking. It's also on `/products`. The home hero carousel has 3 slides with the same h2 "Full-Fledged practice website for Automation Engineers" and rotates about every 5–6s. Assert with `isInViewport()`, never pixel offsets.

## Discovered during `categories` / `brands` (2026-09-30)

- Category ids: Women → Dress **1**, Tops **2**, Saree **7**. Men → Tshirts **3**, Jeans **6**. Kids → Dress **4**, Tops & Shirts **5**. The per-subcategory counts add up to 34, matching the API.
- The listing heading DOM text is mixed case ("Women - Dress Products", "Brand - Polo Products"). Opening one accordion (Men) collapses the other (Women).
- **Accordion gotcha:** the toggle's accessible name is the glyph U+F067 plus the text, so `getByRole(LINK, "Men")` misses it. A non-anchored "Kids" also matches the brand link "Kookie Kids", so anchor your regex. Toggling before the `load` event does nothing.
- `/category_products/9999` and `/0` return HTTP 200 with the heading " -  Products" and 0 cards. **BUG: `/category_products/abc` and `/-1` return 404 with the Django DEBUG page**, the same root cause as products-1.
- The brand sidebar lists 8 brands, with link text "(N)Name". Hrefs are **not percent-encoded** (`/brand_products/H&M`, `/brand_products/Mast & Harbour`) but they resolve. Sidebar counts = card counts = API counts for all 8.
- Brand URLs are **case-sensitive**: `polo` and `NoSuchBrand` return 200, echo the name in the heading, and show 0 cards (no 404).
- Listing pages also show both sidebars.
- Playwright Java passes a `Pattern` to JS regex, so `Pattern.quote()` (`\Q..\E`) breaks. Use `ProductListingPage.esc`.
