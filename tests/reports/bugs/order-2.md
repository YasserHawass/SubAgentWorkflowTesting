# BUG: Card with expired expiry date accepted

Feature: order   Test: TC-ORDER-07   Severity: high
Environment: chromium, 2026-09-29

## Steps
1. Log in with a valid account
2. Add product 1 to the cart, proceed to checkout
3. On /payment, enter name "Test User", card number 4111111111111111 (valid fake card), CVC 123, expiry month 01, expiry year 2000 (past)
4. Click "Pay and Confirm Order"

## Expected
(from tests/cases/order.md, TC-ORDER-07)

Probe: record whether the past expiry is rejected (URL stays /payment) or accepted (URL matches /payment_done/<id>). The test asserts that single recorded outcome. If accepted: log as a product bug candidate (no expiry validation).

Expected: the past expiry date (01/2000) should be rejected and the URL should remain /payment.

## Actual
The card with expiry date 01/2000 (over 26 years in the past) was accepted and the order was created successfully. The URL changed to /payment_done/500 and the page displays "ORDER PLACED!" with "Congratulations! Your order has been confirmed!"

The site has no validation to reject expired credit card expiry dates, allowing payments with past expiry dates to be processed.

## Evidence
- target/screenshots/OrderTest-tcOrder07_pastExpiry.png (shows payment_done page with order confirmation)
- target/traces/OrderTest-tcOrder07_pastExpiry.zip
