# Test plan: reviews

Source: site TC21 "Add review on product". Steps: Products -> "View Product" -> verify 'Write Your Review' visible -> enter name, email and review -> click 'Submit' -> verify success message 'Thank you for your review.'

## Scope
In: review form on `/product_details/<id>` (name, email, review, Submit); success text; required and email validation; HTML/script handling; whether reviews are displayed; guest vs logged in.
Out: product info, add to cart (see `products`, `cart`); rating stars (none known); API (no review endpoint in `/api_list`).

## Preconditions & data
- Guest cases need no account. Logged-in case: `TestUsers.newUser()` + `AccountApi` create, `registerForCleanup(user)`.
- Any product id from `/products` (for example 1). Reviewer name/email are generated, never real (`example.com`).
- Reviews are probably not persisted, so no cleanup is expected (probe live and record).

## Scenarios
| # | Scenario | Expected | Priority | Type |
|---|---|---|---|---|
| 1 | Guest opens a product from `/products` via "View Product"; "Write Your Review" heading, name, email, review fields and Submit are visible (TC21 steps 1-6) | All visible. Exact field labels/placeholders: probe live and record | P1 | ui |
| 2 | Guest fills valid name, email, review; clicks Submit (TC21 steps 7-9) | Success text "Thank you for your review." shown (site text; probe live and record exact casing and whether it is inline or an alert). Form state after submit (cleared or kept): probe live and record | P1 | ui |
| 3 | Submit with each field empty in turn, then all empty | Probe live and record: expected HTML5 `required` block (no success text, no request). Record which fields are required | P2 | ui |
| 4 | Invalid email (`abc`, `a@`, `a b@x.com`) with other fields valid | Probe live and record: browser `type=email` validation blocks, or the site accepts it (then log a bug candidate) | P2 | ui |
| 5 | Review text `<script>alert(1)</script><b>bold</b>` (plus name with HTML) | No dialog fires, no markup rendered as elements anywhere on the page (escaped or absent). Success text behaviour: probe live and record | P1 | ui |
| 6 | After a successful submit, reload the page and check whether the review text or name appears anywhere on the page | Probe live and record: expected not displayed (site has no review list). If shown, assert it is escaped | P2 | ui |
| 7 | Logged-in user opens a product: is the form prefilled with name/email? Submit a review | Probe live and record: prefill or not; success text same as guest "Thank you for your review." | P2 | ui |

Realistic break (scenario 2/5): the form is ajax or a POST that reloads the page; the success banner may appear and vanish, or the submit click may do nothing before page load (see checkout inline-script gotcha).

## Risks & quirks
- Ads: iframes near the form can shift layout; use `BaseUiTest` ad blocking and `Ads.dismissIfPresent`.
- The success message may be present in the DOM but hidden until submit; assert visibility after submit, not presence.
- Long boundary values (very long review, 120-char name are accepted elsewhere on the site) are optional under scenario 5.
- `/product_details/999999` renders an empty page; do not use it for negative form tests.
- Selector hooks (`data-qa`, ids) for the form: unknown, inspect live; do not guess.

## Out of scope / open questions
- Is the review persisted server-side? Not observable through the UI if there is no list.
- Does submit send a network request? Probe live and record (URL, method).
- Duplicate submits and rate limiting: not tested.
- Live probing was not possible from this agent (fetch only, no interaction); all "probe live and record" items are unverified. Only the success text "Thank you for your review." is confirmed from the site page and TC21.
