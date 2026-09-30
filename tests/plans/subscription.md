# Test plan: subscription

Source: site TC10 (Subscription in home page) and TC11 (Subscription in Cart page).

## Scope
In: the footer subscription box on `/` and on `/view_cart`; success text; input validation; repeat email.
Out: newsletter emails actually sent; the signup-form newsletter checkbox (see register plan).

## Preconditions & data
- Guest is enough. No account, no cart items needed (`/view_cart` renders with an empty cart).
- Email generated per test (`ae.test+<epoch>@example.com` style), never hardcoded.
- Footer text "SUBSCRIPTION" is the heading of the box.

## Scenarios
| # | Scenario | Expected | Priority | Type |
|---|---|---|---|---|
| 1 | TC10: on `/` scroll to footer, "SUBSCRIPTION" visible, enter a valid email, click the arrow button | Success text visible: "You have been successfully subscribed!" | P1 | ui |
| 2 | TC11: open `/view_cart` via the header "Cart" link, footer "SUBSCRIPTION" visible, enter a valid email, click the arrow | Success text visible: "You have been successfully subscribed!" | P1 | ui |
| 3 | Empty email, click the arrow | Probe live and record: expect HTML5 required blocks submit and no success text | P2 | ui |
| 4 | Invalid email (e.g. "not-an-email") | Probe live and record: expect email-type validation blocks submit and no success text | P3 | ui |
| 5 | Same valid email subscribed twice in a row (same page, then again) | Probe live and record: success text both times, or a different message | P3 | ui |
| 6 | After success, is the text shown next to the box and does the input clear | Probe live and record | P3 | ui |

Realistic break (covered by 1 and 2): the arrow button is icon-only and the success message is shown by an ajax call, so asserting immediately after the click races the response. Wait for the visible text.

## Risks & quirks
- The submit is an icon-only arrow button with no accessible name; inspect live, do not guess.
- Success text is injected by ajax, so use an auto-waiting visibility assertion. It may disappear after a few seconds; assert promptly.
- The footer is far down the page. Scroll it into view, but do not assert on pixel offsets.
- Ads or interstitials can overlap the footer.
- The same box exists on other pages; only `/` and `/view_cart` are in scope.

## Out of scope / open questions
- Is the success text a "alert-success" element that hides itself: probe live and record.
- Does the server dedupe emails or validate format server-side: probe live and record.
- Whether the box behaves the same for a logged-in user: probe live and record.
