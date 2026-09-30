# Test plan: scroll

Source: site TC25 (Scroll Up using 'Arrow' button) and TC26 (Scroll Up without 'Arrow' button). Route `/`.

## Scope
In: scroll to page bottom, scroll-to-top arrow (`#scrollUp`), top-of-page text visibility.
Out: scroll behaviour on other pages; smooth-scroll animation timing.

## Preconditions & data
- Guest, home page `/`. No data needed.
- Top text to check: "Full-Fledged practice website for Automation Engineers".
- Bottom marker: "SUBSCRIPTION" (footer heading).

## Scenarios
| # | Scenario | Expected | Priority | Type |
|---|---|---|---|---|
| 1 | TC25: open `/`, scroll to bottom, "SUBSCRIPTION" visible, click the arrow | "Full-Fledged practice website for Automation Engineers" is visible in the viewport | P1 | ui |
| 2 | TC26: open `/`, scroll to bottom, "SUBSCRIPTION" visible, scroll back to top via script (no arrow) | "Full-Fledged practice website for Automation Engineers" is visible in the viewport | P1 | ui |
| 3 | At the top of a freshly loaded `/` the arrow is hidden | Probe live and record: expect arrow not visible at top, visible after scrolling down | P2 | ui |
| 4 | After scrolling down the arrow becomes visible, and after clicking it the arrow hides again | Probe live and record | P3 | ui |

Realistic break (covered by 1): the arrow appears only after scroll events fire, and the scroll-up animates. Asserting instantly after the click fails; use auto-waiting viewport/visibility assertions.

## Risks & quirks
- Never assert fixed pixel offsets or a specific `scrollY` number. Assert only visibility or in-viewport state (the top text in viewport, "SUBSCRIPTION" in viewport at the bottom, and the arrow visible or hidden).
- "In viewport" needs a way to check intersection; a visibility assertion alone passes for off-screen elements. Choose a viewport-aware check and record it.
- Scrolling to the bottom must be triggered so the arrow appears (scroll event). Scrolling the footer into view counts.
- The arrow `#scrollUp` is tier-5 (no role/name); justify in a comment.
- The carousel and ads change page height, so no absolute positions.
- The top text is inside the carousel and may rotate; probe live and record whether it stays on every slide.

## Out of scope / open questions
- Is the arrow hidden or absent from the DOM at the top: probe live and record.
- Does the top text stay in the viewport on every carousel slide: probe live and record.
- Does the arrow appear on other pages (`/products`, `/view_cart`): probe live and record.
