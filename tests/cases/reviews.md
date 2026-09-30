# Test cases: reviews

Plan: tests/plans/reviews.md

Common setup for every case unless stated: fresh browser context (logged out), start on home (/). Ads blocked via `BaseUiTest`; call `Ads.dismissIfPresent` before clicking. The product is picked from the live /products grid (first product) and opened via "View Product". Reviewer name and email are generated (`example.com`), never real. Selectors for the review form are unknown; the writer inspects the live page.

Items marked "record" are unknown until the live site is observed. For each, the automation-writer records the observed behaviour and the test asserts that single outcome. Exact texts appear in Expected only where site TC21 or docs/site-map.md states them (here only "Write Your Review" and "Thank you for your review."). No review endpoint exists in /api_list, so there are no API cases.

## TC-REVIEWS-01 Review form is visible on the product detail page (site TC21 steps 1-6)  [P1] [ui] [smoke]
Preconditions: logged out
Steps:
1. Click "Products" and verify the ALL PRODUCTS page is visible
2. Click "View Product" on the first product
3. Verify the product detail page (/product_details/<id>) is open
Expected:
- "Write Your Review" is visible
- A name field, an email field, a review text field and a Submit button are visible
- Observed: no labels; placeholders "Your Name", "Email Address", "Add Review Here!"; the link/tab text is "Write Your Review"
Data: first product on /products
Cleanup: none

## TC-REVIEWS-02 Guest submits a valid review and sees the success text (site TC21 steps 7-9)  [P1] [ui] [smoke]
Preconditions: logged out; product detail page open
Steps:
1. Enter a generated name, a generated email and a short review text
2. Click Submit
Expected:
- The text "Thank you for your review." is visible after the click (assert visibility after submit, not DOM presence)
- Observed: exact text "Thank you for your review.", inline alert (no dialog), visible ~2 s, then hidden and all three fields cleared
- Observed: no review request is sent (client-side jQuery handler only; the only POST seen is Cloudflare's /cdn-cgi/rum analytics beacon)
Data: name "Review Tester", email `ae.review+<epoch>@example.com`, review "Great product, works as described."
Cleanup: none (a review list is not known to exist; TC-REVIEWS-05 checks persistence)

## TC-REVIEWS-03 Required fields block submission  [P2] [ui] [regression]
Preconditions: logged out; product detail page open
Steps:
1. Leave only the name empty, fill the others, click Submit
2. Repeat in fresh contexts with only the email empty, only the review empty, and all three empty
Expected:
- Observed: name, email and review are all `required`; the browser blocks the submit (validity.valueMissing) for each combination
- For each, "Thank you for your review." is not shown and the page stays on the same product URL
Data: generated name, email and review text
Cleanup: none

## TC-REVIEWS-04 Invalid email formats  [P2] [ui] [regression]
Preconditions: logged out; product detail page open
Steps:
1. Fill a valid name and review; enter the email `abc`; click Submit
2. Repeat in fresh contexts with `a@` and `a b@x.com`
Expected:
- Observed: the field is type=email; `abc`, `a@` and `a b@x.com` all fail validity (typeMismatch) and the browser blocks the submit; no success text
Data: emails `abc`, `a@`, `a b@x.com`
Cleanup: none

## TC-REVIEWS-05 HTML and script in name and review are not rendered or executed  [P1] [ui] [regression]
Preconditions: logged out; product detail page open; a dialog listener is registered before submit
Steps:
1. Enter name `<b>Tester</b>`, a generated email, and review `<script>alert(1)</script><b>bold</b>`
2. Click Submit
3. Reload the page
Expected:
- No dialog (alert) fires at any point
- No `<script>` or `<b>` element created from the input exists in the page DOM (the markup is escaped or absent, not rendered)
- Observed: "Thank you for your review." is shown (client-side only, the input is never rendered); no dialog, no extra <b> elements, before or after reload
- If the markup is rendered as elements or a dialog fires, that is a product bug for triage (the test asserts the safe behaviour above)
Data: name `<b>Tester</b>`, review `<script>alert(1)</script><b>bold</b>`
Cleanup: none

## TC-REVIEWS-06 Submitted review is not displayed after reload  [P2] [ui] [regression]
Preconditions: logged out; product detail page open
Steps:
1. Submit a review with a unique review text (contains `ae-review-<epoch>`) and unique name
2. Reload the product detail page
3. Search the page text for the unique review text and name
Expected:
- Observed: neither the review text nor the name appears on the page (there is no review list; nothing is stored)
Data: unique review text `ae-review-<epoch>`, name `Review Tester`
Cleanup: none (no server-side state observable; if the review is recorded as persisted, note it for the orchestrator)

## TC-REVIEWS-07 Logged-in user submits a review  [P2] [ui] [regression]
Preconditions: a registered user (create via `AccountApi` with `TestUsers.newUser()`, assert responseCode 201 "User created!", `registerForCleanup(user)`); logged in via the UI
Steps:
1. Open a product detail page from /products
2. Check the name and email fields
3. Fill any empty required fields, enter a review, click Submit
Expected:
- The header shows "Logged in as <name>"
- Observed: name and email are NOT prefilled (both empty)
- "Thank you for your review." is visible after submit, same as for a guest
Data: TestUsers.newUser(); review "Logged-in review text"
Cleanup: delete account via AccountApi.delete(email, password)
