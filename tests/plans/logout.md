# Test plan: logout

## Scope
In: "Logout" header link, redirect to `/login`, header state after logout, session end (protected pages, Back button), cart after logout; site TC4.
Out: Login form validation (plan: `login`), account deletion (plan: `register`), checkout flows (plan: `order`).

## Preconditions & data
- Each test creates its own account via `AccountApi` (`TestUsers.newUser()`, expect responseCode 201 "User created!") and calls `registerForCleanup(user)`. Never hardcode an email.
- Tests log in through the UI first (steps 1-8 of TC4), then act. Start logged out on home.

## Scenarios
| # | Scenario | Expected | Priority | Type |
|---|---|---|---|---|
| 1 | TC4: home visible, click "Signup / Login", "Login to your account" visible, log in with valid creds, "Logged in as <name>" visible, click "Logout" | User lands on `/login`; "Login to your account" visible | P1 | ui |
| 2 | After logout, inspect header | "Signup / Login" visible; "Logout", "Delete Account" and "Logged in as <name>" gone | P1 | ui |
| 3 | After logout, open `/checkout` directly | Probe live and record (redirect to `/login`, or other); user is not treated as logged in | P2 | ui |
| 4 | After logout, press browser Back | Probe live and record what page shows and header state; assert header does not show "Logged in as <name>" if the site agrees | P2 | ui |
| 5 | Logged-in user with item in cart clicks "Logout", then opens `/view_cart` | Probe live and record whether cart keeps the item (server-side vs session cart) | P2 | ui |
| 6 | Log out, then log in again with same creds | Login succeeds; header shows "Logged in as <name>" (session fully reset, no stale state) | P2 | ui |
| 7 | What could break: log out on one page (e.g. `/products`), then reload | Probe live and record landing URL after logout from a non-home page; after reload still logged out | P2 | ui |
| 8 | Direct GET of `/logout` while logged out | Probe live and record (redirect to `/login` without error) | P3 | ui |

## Risks & quirks
- Ads: iframes and interstitials can cover header links; rely on `BaseUiTest` blocking and `Ads.dismissIfPresent` before clicking "Logout".
- Site-map says `/logout` redirects to `/login`; assert the URL, not just the heading.
- Logout link only exists while logged in; scenario setup must confirm login succeeded before clicking.
- A logged-in user opening `/login` is redirected to `/` (login plan), so do not use that to check logout; check the header instead.
- Public demo site: slow redirects after login/logout; use auto-waiting assertions only.
- Cleanup deletes the account via API after the test; logout does not affect it.

## Out of scope / open questions
- Session timeout / expiry: not testable in reasonable time.
- Multi-tab logout propagation: not covered; note as possible extension.
- Cart persistence after logout (scenario 5) and Back-button behaviour (scenario 4): probe live and record, then fix expectations.
