# BUG: Invoice served without session, with amount from URL

Feature: invoice   Test: TC-INVOICE-07   Severity: high
Environment: chromium, 2026-09-29

## Steps
1. Log in with a valid account and create an order with product total 500 Rs
2. Record the invoice URL /download_invoice/500 from the /payment_done/<id> page
3. Create a fresh APIRequestContext with no cookies (no session)
4. Fetch /download_invoice/500 using the cookie-less context

## Expected
(from tests/cases/invoice.md, TC-INVOICE-07)

Safe behaviour: the user's name and order total do NOT appear in the response body. Record the observed outcome (redirect to /login, error status, or other) and assert that single outcome. If invoice content is served without a session, the test fails and this is a product bug for triage.

Expected: the invoice endpoint should not serve invoice content without a valid session. The response should either redirect to /login or return an error status, preventing unauthorized access to invoice data.

## Actual
The /download_invoice/500 endpoint served invoice content without authentication. The response was HTTP 200 text/plain with content: "Hi , Your total purchase amount is 500. Thank you"

The endpoint lacks session validation and serves invoice content to any request. Additionally, the total amount "500" is echoed from the URL parameter, allowing it to be manipulated. While the user name is absent (because there is no session), the amount appears in the response, defeating the purpose of auth-gated content.

## Evidence
- target/screenshots/InvoiceTest-tcInvoice07_noSession.png
- target/traces/InvoiceTest-tcInvoice07_noSession.zip
- Test failure message shows response body: "Hi , Your total purchase amount is 500. Thank you"
