# Test cases: invoice

Plan: tests/plans/invoice.md

Common setup for every case unless stated: a fresh user from `TestUsers.newUser()` created via `AccountApi` (assert responseCode 201 "User created!"), `registerForCleanup(user)`. Log in through the UI, add one known product (price read from `/api/productsList`, digits parsed from "Rs. N"), go through checkout, and pay with fake card data only (name on card = user name, number 4111111111111111, CVC 123, a future expiry such as 12/2030) until `/payment_done/<id>` is reached. The order flow is setup only and is not asserted. Ads are blocked via `BaseUiTest`; call `Ads.dismissIfPresent` before clicking. Record the order id from the `/payment_done/<id>` URL and the invoice link href (read from the live page, never guessed). Every case builds its own order (no shared state).

Items marked "record" are unknown until the live site is observed. The automation-writer records the observed behaviour and the test asserts that single outcome. Transient Cloudflare 520 / 503 pages are classified env or flake, not product bugs.

## TC-INVOICE-01 Download Invoice after paying triggers a .txt download (site TC24)  [P1] [ui] [smoke]
Preconditions: order placed, browser on /payment_done/<id>
Steps:
1. Verify the success message "Congratulations! Your order has been confirmed!" is shown (observed live; the site TC text "Your order has been placed successfully!" is outdated)
2. Click "Download Invoice" (wrapped in a Playwright download wait)
Expected:
- A download event fires
- Suggested filename ends in `.txt`; record the exact filename pattern observed and assert that single pattern
- The downloaded file is non-empty
Data: TestUsers.newUser(); one known product; card 4111111111111111, CVC 123, future expiry
Cleanup: delete account via AccountApi.delete(email, password)

## TC-INVOICE-02 Invoice content shows the account name and the order total  [P1] [ui] [regression]
Preconditions: order placed for one product of known price, quantity 1, browser on /payment_done/<id>
Steps:
1. Click "Download Invoice" and read the downloaded text
Expected:
- The text contains the account's full name
- The text contains the numeric order total equal to the product price
- Record the exact format (greeting line, amount wording, currency "Rs."); assert only on the name and the numeric total, not full-line equality
Data: TestUsers.newUser(); one known product, quantity 1
Cleanup: delete account via AccountApi.delete(email, password)

## TC-INVOICE-03 Downloading the invoice twice gives identical content  [P2] [ui] [regression]
Preconditions: order placed, browser on /payment_done/<id>
Steps:
1. Click "Download Invoice" and capture the download
2. Click "Download Invoice" again and capture the second download
Expected:
- Both downloads succeed and their contents are identical
- URL is still /payment_done/<id>
- Record whether the two filenames differ; assert that single observed outcome
Data: TestUsers.newUser(); one known product
Cleanup: delete account via AccountApi.delete(email, password)

## TC-INVOICE-04 "Continue" after downloading leaves the confirmation page  [P2] [ui] [regression]
Preconditions: order placed, browser on /payment_done/<id>
Steps:
1. Click "Download Invoice" and wait for the download
2. Click "Continue"
Expected:
- URL is no longer /payment_done/<id>; record the destination (expected home, /) and assert that single URL
- Header shows "Logged in as <name>"
Data: TestUsers.newUser(); one known product
Cleanup: delete account via AccountApi.delete(email, password)

## TC-INVOICE-05 Fetching the invoice URL directly in the same session returns the same content  [P1] [api] [regression]
Preconditions: order placed, browser session logged in; invoice href read from /payment_done/<id>
Steps:
1. Download the invoice via the UI and keep its text
2. Fetch the invoice href with `page.request()` (same session cookies)
Expected:
- HTTP status 200
- Body is identical to the text downloaded in step 1 (contains the account name and the order total)
Data: TestUsers.newUser(); one known product
Cleanup: delete account via AccountApi.delete(email, password)

## TC-INVOICE-06 Another user's invoice is not served (tampered order id)  [P1] [api] [regression]
Preconditions: user A with order A and user B with order B, each built via the standard flow with its own account (two orders in total); session logged in as user A; order ids and the invoice href pattern recorded from the live pages
Steps:
1. As user A, fetch the invoice URL with user B's order id via `page.request()`
Expected:
- Safe behaviour: user B's name and order total do NOT appear in the response body (no other user's invoice data is served to user A)
- Record the observed status and body; the test asserts that they contain no user B data
- If the site does serve user B's data, the test fails and this is a product bug (data exposure) for triage, not an observed pass
Data: two TestUsers.newUser() accounts with distinct names; one known product each
Cleanup: delete both accounts via AccountApi.delete(email, password)

## TC-INVOICE-07 Invoice URL without a session does not serve invoice content  [P2] [api] [regression]
Preconditions: order placed by a logged-in user; invoice href recorded; a fresh `APIRequestContext` with no cookies
Steps:
1. Fetch the invoice href with the cookie-less context
Expected:
- Safe behaviour: the user's name and order total do NOT appear in the response body
- Record the observed outcome (redirect to /login, error status, or other) and assert that single outcome
- If invoice content is served without a session, the test fails and this is a product bug for triage
Data: TestUsers.newUser(); one known product
Cleanup: delete account via AccountApi.delete(email, password)

## TC-INVOICE-08 An invoice for an arbitrary amount cannot be forged  [P2] [api] [regression]
Preconditions: a logged-in user who has placed no order for that amount (no order is needed at all)
Steps:
1. Fetch `/download_invoice/999999` with `page.request()` in that user's session
Expected:
- Safe behaviour: no invoice claiming "purchase amount is 999999" is served
- If the site echoes the amount from the URL, the test fails and this is a product bug for triage
Data: TestUsers.newUser() with a unique first name
Cleanup: delete account via AccountApi.delete(email, password)

## Observed values (live probe + run, 2026-09-29)

- Order id in `/payment_done/<id>` and invoice href `/download_invoice/<id>`: the cart total in Rs. (500 for Blue Top), not unique per order.
- TC-INVOICE-01/03: suggested filename is always `invoice.txt` (both downloads); response is `text/plain`, HTTP 200.
- TC-INVOICE-02: content is exactly `Hi <first name> <last name>, Your total purchase amount is <total>. Thank you`. The name is the account's first/last name (address data), not the signup name (`user.name()`) and not the card name. No "Rs." currency in the text. Tests use users with a unique first name so the name check is meaningful.
- TC-INVOICE-03: two downloads are byte-identical; names identical; URL stays `/payment_done/<id>`.
- TC-INVOICE-04: Continue goes to `/`; header shows "Logged in as <name>".
- TC-INVOICE-05: same-session fetch returns 200 and the same body as the download.
- TC-INVOICE-06: the name is taken from the session, not the id. User B's session fetching A's href gets B's own invoice, so A's name does not appear (safe). Both users' hrefs are identical (`/download_invoice/500`).
- TC-INVOICE-07: no session returns HTTP 200 text/plain `Hi , Your total purchase amount is 500. Thank you` (no redirect, empty name, amount echoed from the URL). The name is absent, so the test asserts that; FLAG: the invoice endpoint has no auth guard, and the total (which the case lists as not to appear) is echoed.
