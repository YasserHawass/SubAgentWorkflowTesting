# Test plan: invoice

Source: site TC24 "Download Invoice after purchase order". Steps 15-20 matter here: place order, "Verify success message 'Your order has been placed successfully!'", "Click 'Download Invoice' button and verify invoice is downloaded successfully.", "Click 'Continue' button".

## Scope
In: the "Download Invoice" action on `/payment_done/<id>`, the downloaded file (name, extension, content), repeat download, direct fetch of the invoice URL, "Continue" after download.
Out: cart, checkout form and payment validation (plans `cart`, `checkout`, `order`). Order flow is setup only.

## Preconditions & data
- Fresh user per test: `TestUsers.newUser()`, created via `AccountApi`, registered with `registerForCleanup(user)`. Log in through the UI or session, then add one known product (price known from `/api/productsList`), checkout, pay with any dummy card.
- Reuse the order flow from the `order` plan/page objects as setup only. Do not assert on it beyond reaching `/payment_done/<id>`.
- Scenario 5 needs a second user with its own order (two orders in total). Delete both accounts in cleanup.
- Record the order id from the `/payment_done/<id>` URL and the invoice link href.

## Scenarios
| # | Scenario | Expected | Priority | Type |
|---|---|---|---|---|
| 1 | After paying, click "Download Invoice" and capture the download | A download event fires. Filename and `.txt` extension recorded (probe live and record the exact name pattern, e.g. `invoice.txt`). Content is non-empty | P1 | ui |
| 2 | Read the downloaded text | Contains the account's full name and the order total for the product. Exact format unknown: probe live and record (greeting line, amount wording, currency `Rs.`?). Assert on the name and the numeric total, not full-line equality, until the format is recorded | P1 | ui |
| 3 | Click "Download Invoice" twice in a row | Both downloads succeed with identical content. Record whether the filename differs. Page stays on `/payment_done/<id>` | P2 | ui |
| 4 | Click "Continue" after downloading | Leaves `/payment_done/<id>` (probe live and record destination, expected home `/`). Header still shows "Logged in as <name>" | P2 | ui |
| 5 | Fetch the invoice URL directly with `page.request()` (same session), then with the order id tampered (id +/- 1, another user's order id, non-numeric, 999999) | Own id: 200 with same content as scenario 2. Other ids: probe live and record status and body. FLAG AS DATA EXPOSURE (bug report) if another user's name or total is served. Non-numeric may yield the Django DEBUG page (see products/logout quirks) | P1 | api |
| 6 | Fetch the invoice URL with a fresh, unauthenticated `APIRequestContext` (no cookies) | Probe live and record: redirect to `/login`, error, or content served. Content served without a session is a bug to flag | P2 | api |
| 7 | What could realistically break: order with quantity 2 or two products | Invoice total equals the sum of line totals (site shows no cart grand total, so compute from row totals). Probe live and record whether the invoice lists items | P3 | ui |

## Risks & quirks
- The download is a `.txt` served by a link; use Playwright's download wait around the click. Cloudflare or ad overlays may intercept: dismiss with `Ads.dismissIfPresent(page)` first.
- The invoice link may be a GET on a non-obvious path with the order id. Read the href from the live page, do not guess the path.
- The order flow is long and slow. Each test builds its own order (no shared state), so keep the count low. Consider one order feeding scenarios 1, 2, 3, 4 only if the writer keeps them as separate asserts in a single-purpose test class per convention.
- Transient Cloudflare 520 / 503 pages: classify as env or flake.
- DEBUG is on in production: bad ids may return Django debug pages with stack info. Note it as a bug candidate.
- Amounts render as "Rs. N". `+` and encoding issues do not apply (GET).

## Out of scope / open questions
- Exact invoice format, filename and any date or address lines: probe live and record.
- Whether the invoice is retrievable after logout or after account deletion: probe live and record (optional).
- Whether `/payment_done/<id>` itself can be opened for another user's id: probe live and record (same exposure class as scenario 5).
- PDF rendering or parsing: not applicable, the file is plain text.
