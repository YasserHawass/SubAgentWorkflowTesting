# Test cases: subscription

Plan: tests/plans/subscription.md

Common setup for every case unless stated: fresh browser context (logged out, empty cart). Ads blocked via `BaseUiTest`; call `Ads.dismissIfPresent` before clicking. The email is generated per test (`ae.test+<epoch>@example.com` style). The submit is the icon-only arrow button in the footer box; the success text is injected by ajax, so assert with an auto-waiting visibility check. No pixel offsets are asserted.

Items marked "probe" are unknown until the live site is observed. The automation-writer records the observed behaviour and the test asserts that single outcome. Exact texts appear in Expected only where site TC10/TC11 or docs/site-map.md states them.

## TC-SUBSCRIPTION-01 Subscribe from the home page footer (site TC10)  [P1] [ui] [smoke]
Preconditions: none
Steps:
1. Open the home page and verify it is visible
2. Scroll down to the footer
3. Verify "SUBSCRIPTION" is visible
4. Enter a valid email in the box and click the arrow button
Expected:
- "SUBSCRIPTION" is visible in step 3
- "You have been successfully subscribed!" is visible
Data: generated email
Cleanup: none (no account created)
Observed (live, 2026-09-30): success box #success-subscribe (class hide removed) sits at the top of the footer; text "You have been successfully subscribed!".

## TC-SUBSCRIPTION-02 Subscribe from the cart page footer (site TC11)  [P1] [ui] [smoke]
Preconditions: none
Steps:
1. Click "Cart" in the header
2. Verify the cart page is open (URL is /view_cart)
3. Scroll down to the footer and verify "SUBSCRIPTION" is visible
4. Enter a valid email and click the arrow button
Expected:
- URL is /view_cart in step 2
- "SUBSCRIPTION" is visible in step 3
- "You have been successfully subscribed!" is visible
Data: generated email
Cleanup: none
Observed (live, 2026-09-30): same behaviour on /view_cart.

## TC-SUBSCRIPTION-03 Empty email is not subscribed  [P2] [ui] [regression]
Preconditions: none
Steps:
1. Open the home page; scroll to the footer
2. Leave the email empty and click the arrow button
Expected:
- Probe: record observed behaviour; test asserts that single outcome. Recorded aspect: whether "You have been successfully subscribed!" appears (expected outcome to confirm is that it does not, via browser validation).
Data: empty email
Cleanup: none
Observed (live, 2026-09-30): required type=email blocks in the browser (valueMissing); success text not shown.

## TC-SUBSCRIPTION-04 Invalid email format is not subscribed  [P3] [ui] [regression]
Preconditions: none
Steps:
1. Open the home page; scroll to the footer
2. Enter "not-an-email" and click the arrow button
Expected:
- Probe: record observed behaviour; test asserts that single outcome. Recorded aspects: whether "You have been successfully subscribed!" appears and any message shown.
Data: email "not-an-email"
Cleanup: none
Observed (live, 2026-09-30): type=email blocks in the browser (typeMismatch); success not shown; no site message.

## TC-SUBSCRIPTION-05 Same email subscribed twice in a row  [P3] [ui] [regression]
Preconditions: none
Steps:
1. Open the home page; scroll to the footer
2. Subscribe with a generated email and wait for the success text
3. Subscribe again with the same email on the same page
Expected:
- After step 2: "You have been successfully subscribed!" is visible
- Probe: record the observed result of step 3 (same success text or a different message); test asserts that single outcome.
Data: one generated email used twice
Cleanup: none
Observed (live, 2026-09-30): second subscribe with the same email shows the same success text again (after the first text hid itself).

## TC-SUBSCRIPTION-06 Where the success text appears and whether the input clears  [P3] [ui] [regression]
Preconditions: none
Steps:
1. Open the home page; scroll to the footer
2. Subscribe with a generated email
Expected:
- "You have been successfully subscribed!" is visible and inside the footer subscription box area
- Probe: record whether the email input is cleared afterwards and whether the success text hides itself after a few seconds; test asserts that single observed outcome for each.
Data: generated email
Cleanup: none
Observed (live, 2026-09-30): success text is inside the footer; the email input is NOT cleared; the text hides itself after a few seconds (seen hidden by 6.8 s).
