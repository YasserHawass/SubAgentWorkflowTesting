# Test plan: order

## Scope
In: site TC14/TC15/TC16 end to end from cart to /payment_done/<id>; /payment form; cart emptied after order; direct /payment access; account deletion at the end.
In: the Continue button on /payment_done.
Out: /checkout address and review detail (plan `checkout`), invoice download content (plan `invoice`). Here /checkout is only a step; "Download Invoice" is only checked as present.
Out: cart mechanics (plan `cart`), signup/login form validation (plans `register`, `login`).

## Preconditions & data
- Fresh user per test from `TestUsers.newUser()`; register via UI (TC14, TC15) or `AccountApi` create then UI login (TC16). Cleanup with `registerForCleanup(user)`; the final "Delete Account" step is the flow itself, so cleanup must tolerate an already-deleted account (API delete returns 404 "Account not found!").
- Products: add product id 1 (via the UI or `/add_to_cart/1?quantity=1`).
- Fake card only: name "Test User", number 4111111111111111, CVC 123, expiry month 12, expiry year a future year (current year + 2). No real data.
- Site TC text: "Verify 'ACCOUNT CREATED!'", "Verify 'ACCOUNT DELETED!'", "Verify success message 'Your order has been placed successfully!'". Comment text: any short string.

## Scenarios
| # | Scenario | Expected | Priority | Type |
|---|---|---|---|---|
| 1 | TC15 Register before checkout: sign up, Continue, add product, Cart, Proceed To Checkout, comment, Place Order, fill /payment, pay-button | Lands on /payment_done/<id>. Confirmation text: site TC says 'Your order has been placed successfully!'; probe live and record the exact text (site often shows "Congratulations! Your order has been confirmed!"). "Download Invoice" and "Continue" exist. | P1 | ui |
| 2 | TC16 Login before checkout: existing account (via AccountApi), UI login, add product, same order flow as 1, then Delete Account | Order confirmation as in 1. Delete Account shows "ACCOUNT DELETED!" (probe exact case), Continue returns to home logged out. | P1 | ui |
| 3 | TC14 Register while checkout: logged out, add product, Cart, Proceed To Checkout, then Register / Login from the prompt, sign up, Continue, Cart again, Proceed To Checkout, order flow, Delete Account | Cart survives registration (site-map). Probe live and record: whether the checkout-prompt modal appears when logged out. Order confirmed; "ACCOUNT DELETED!" at the end. | P1 | ui |
| 4 | After a successful order, click Continue on /payment_done, then open /view_cart | Continue goes to probe live and record (likely home). Cart shows "Cart is empty! Click here to buy products." | P1 | ui |
| 5 | Submit /payment with all fields empty | Probe live and record: HTML5 required blocks the submit (stay on /payment) or the order goes through. Assert no /payment_done unless recorded otherwise. | P2 | ui |
| 6 | Non-numeric card number "abcd" and CVC "xyz" with other fields valid | Probe live and record: rejected or accepted. If accepted, log as a product bug candidate. | P2 | ui |
| 7 | Past expiry (month 01, year 2000) with a valid card | Probe live and record: rejected or accepted. If accepted, log as a product bug candidate (no expiry validation). | P2 | ui |
| 8 | Logged out with an empty cart, open /payment directly | Probe live and record: redirect, empty page, or error page (compare to /checkout, which renders empty when logged out). Must not show a confirmation. | P2 | ui |
| 9 | Logged in with an empty cart, open /payment directly; then fill and pay | Probe live and record: whether an order is created with nothing in it. Record the /payment_done text if reached. | P2 | ui |
| 10 | What could break: reload /payment_done/<id> (or Back from it) after ordering, then open /view_cart | Probe live and record: no duplicate order, no server error page, cart stays empty. | P3 | ui |

## Risks & quirks
- The site TC's success text may differ from the live page. Copy the exact text from the live page, keep it verbatim (punctuation, "!"), and note the difference.
- Ads and full-page interstitials on /view_cart, /checkout and /payment: use `Ads.dismissIfPresent`.
- The Place Order and pay steps navigate (POST then redirect). Use `waitForURL` on /payment and /payment_done, not sleeps.
- Order id in /payment_done/<id> is dynamic: match the URL by pattern, never a fixed id.
- DEBUG is on in production (bugs/logout-1.md): invalid input on /payment may show a Django debug page. Record it as a product bug, not a test bug.
- `/checkout` does not redirect when logged out (site-map). Do not assume guards.
- The cart is dropped on logout (site-map), so scenarios 2 and 3 must add products after login/registration where the TC says so.
- Signup on /login POSTs to /signup; email is disabled on the account form.
- Cloudflare 520 or 503 pages are transient (env or flake).
- Cart has no grand total and no header count: assert on rows or on the "Cart is empty!" text.

## Out of scope / open questions
- Address and order review content, and comment persistence: `checkout` plan.
- Invoice file content and filename: `invoice` plan.
- Multiple products or quantities >1 in an order: cart plan.
- Open: the exact live text on /payment_done; whether the /payment fields have any client or server validation; whether the site stores an order history (none known).
