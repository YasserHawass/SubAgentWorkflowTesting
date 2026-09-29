# BUG: Card number and CVC with non-numeric input accepted

Feature: order   Test: TC-ORDER-06   Severity: high
Environment: chromium, 2026-09-29

## Steps
1. Log in with a valid account
2. Add product 1 to the cart, proceed to checkout
3. On /payment, enter name "Test User", card number "abcd", CVC "xyz", expiry month 12, expiry year 2028 (future)
4. Click "Pay and Confirm Order"

## Expected
(from tests/cases/order.md, TC-ORDER-06)

Probe: record whether the input is rejected (URL stays /payment) or accepted (URL matches /payment_done/<id>). The test asserts that single recorded outcome. If accepted: log as a product bug candidate (no card format validation).

Expected: the non-numeric card number and CVC should be rejected and the URL should remain /payment.

## Actual
The invalid card input (number "abcd", CVC "xyz") was accepted and the order was created successfully. The URL changed to /payment_done/500 and the page displays "ORDER PLACED!" with "Congratulations! Your order has been confirmed!"

The site has no client-side or server-side validation to reject non-numeric card data, allowing invalid payment submission to complete.

## Evidence
- target/screenshots/OrderTest-tcOrder06_nonNumericCard.png (shows payment_done page with order confirmation)
- target/traces/OrderTest-tcOrder06_nonNumericCard.zip
