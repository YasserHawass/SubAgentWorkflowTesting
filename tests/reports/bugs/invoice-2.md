# BUG: Arbitrary invoice amount can be forged via URL parameter

Feature: invoice   Test: TC-INVOICE-08   Severity: high
Environment: chromium, 2026-09-29

## Steps
1. Log in with a valid account (no order needed for amount 999999)
2. Fetch /download_invoice/999999 using the logged-in session

## Expected
(from tests/cases/invoice.md, TC-INVOICE-08)

Safe behaviour: no invoice claiming "purchase amount is 999999" is served. The amount must match an actual order for that user, not be taken from the URL. If the site echoes the amount from the URL, the test fails and this is a product bug for triage.

Expected: the invoice endpoint should validate that the requested order id belongs to the logged-in user and serve the actual invoice data, not echo URL parameters.

## Actual
The /download_invoice/999999 endpoint served forged invoice content. The response was HTTP 200 text/plain with content: "Hi Inv296450739530700x7 Payer, Your total purchase amount is 999999. Thank you"

The endpoint accepts arbitrary amounts from the URL and includes them in the invoice, even though the user has no order for that amount. This allows attackers to forge invoices claiming any amount.

## Evidence
- target/screenshots/InvoiceTest-tcInvoice08_forgedAmount.png
- target/traces/InvoiceTest-tcInvoice08_forgedAmount.zip
- Test failure message shows response body contains "purchase amount is 999999"

## Cross-reference
Same root cause as bugs/invoice-1.md: the /download_invoice endpoint lacks proper validation and echoes URL parameters into the response. A complete fix requires server-side validation to ensure the amount matches the actual order.
