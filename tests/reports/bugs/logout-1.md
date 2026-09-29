# BUG: GET /logout while logged out returns Django KeyError

Feature: logout   Test: TC-LOGOUT-08   Severity: high
Environment: chromium, 2026-09-29

## Steps
1. Open a fresh browser context (not logged in)
2. Navigate directly to /logout

## Expected
no server error; logged-out header ('Signup / Login') visible

## Actual
Server responds with HTTP 500 and renders a Django error page displaying "KeyError at /logout". The page title contains "KeyError" and the logged-out header is not visible. This occurs consistently and reproducibly when accessing /logout while not authenticated.

## Evidence
- target/screenshots/LogoutTest-tcLogout08_logoutWhileLoggedOut.png
- target/traces/LogoutTest-tcLogout08_logoutWhileLoggedOut.zip
