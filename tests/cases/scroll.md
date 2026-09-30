# Test cases: scroll

Plan: tests/plans/scroll.md

Common setup for every case unless stated: fresh browser context, logged out, start on the home page (/). Ads blocked via `BaseUiTest`. Expectations are about visibility and viewport state only, never pixel offsets or a `scrollY` number. "In the viewport" is checked with Playwright's viewport-aware assertion (`assertThat(locator).isInViewport()`), since plain visibility also passes for off-screen elements. All checks auto-wait because the scroll animates and the arrow appears only after scroll events fire. The arrow is `#scrollUp` (tier-5, justified in a comment).

Items marked "probe" are unknown until the live site is observed. The automation-writer records the observed behaviour and the test asserts that single outcome. Exact texts appear in Expected only where site TC25/TC26 or docs/site-map.md states them.

## TC-SCROLL-01 Scroll down, then scroll up with the arrow (site TC25)  [P1] [ui] [smoke]
Preconditions: none
Steps:
1. Open the home page and verify it is visible
2. Scroll down to the bottom of the page
3. Verify "SUBSCRIPTION" is in the viewport
4. Click the scroll-up arrow
Expected:
- "SUBSCRIPTION" is in the viewport in step 3
- "Full-Fledged practice website for Automation Engineers" is in the viewport
Data: none
Cleanup: none
Observed (live, 2026-09-30): #scrollUp is <a href="#top"> with an angle-up icon; visible at the bottom; click brings the hero h2 into the viewport.

## TC-SCROLL-02 Scroll down, then scroll up without the arrow (site TC26)  [P1] [ui] [smoke]
Preconditions: none
Steps:
1. Open the home page and verify it is visible
2. Scroll down to the bottom of the page
3. Verify "SUBSCRIPTION" is in the viewport
4. Scroll back to the top by script (no arrow click)
Expected:
- "SUBSCRIPTION" is in the viewport in step 3
- "Full-Fledged practice website for Automation Engineers" is in the viewport
Data: none
Cleanup: none

## TC-SCROLL-03 Arrow is not shown at the top and appears after scrolling down  [P2] [ui] [regression]
Preconditions: none
Steps:
1. Open the home page freshly and stay at the top
2. Scroll the footer into view
Expected:
- Probe: record whether the arrow is hidden or absent from the DOM at the top; test asserts that single outcome.
- After step 2 the arrow is visible (recorded on the live site; test asserts it).
Data: none
Cleanup: none
Observed (live, 2026-09-30): arrow is in the DOM (count 1) but hidden at the top; visible after the footer is scrolled into view.

## TC-SCROLL-04 Arrow hides again after it is used  [P3] [ui] [regression]
Preconditions: none
Steps:
1. Open the home page; scroll to the bottom until the arrow is visible
2. Click the arrow and wait for the top text to be in the viewport
Expected:
- Probe: record whether the arrow is hidden afterwards; test asserts that single outcome.
- "Full-Fledged practice website for Automation Engineers" is in the viewport
Data: none
Cleanup: none
Observed (live, 2026-09-30): arrow is hidden again after the click (back at the top).

## TC-SCROLL-05 Top text stays in the viewport on each carousel slide  [P3] [ui] [regression]
Preconditions: none
Steps:
1. Open the home page at the top
2. Wait for the carousel to advance to the next slide (auto-advance, no sleep; wait on the active slide change)
Expected:
- Probe: record whether "Full-Fledged practice website for Automation Engineers" stays in the viewport on every slide; test asserts that single observed outcome.
Data: none
Cleanup: none
Observed (live, 2026-09-30): 3 slides, identical h2 text; the first slide loses .active about every 5-6 s. Asserted: hero h2 in the viewport on the next slide.

## TC-SCROLL-06 Arrow on /products after scrolling down  [P3] [ui] [regression]
Preconditions: none
Steps:
1. Open /products
2. Scroll to the bottom of the page
Expected:
- Probe: record whether the arrow becomes visible on /products (out-of-scope page check); test asserts that single observed outcome.
Data: none
Cleanup: none
Observed (live, 2026-09-30): arrow becomes visible on /products after scrolling to the footer too.
