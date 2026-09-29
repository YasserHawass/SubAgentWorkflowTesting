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
