# BUG: Empty cart payment creates order with zero total

Feature: order   Test: TC-ORDER-09   Severity: high
Environment: chromium, 2026-09-29

## Steps
1. Log in with a valid account
2. Ensure the cart is empty (no products added)
3. Navigate directly to /payment
4. Enter card data: name "Test User", card 4111111111111111, CVC 123, expiry 12/2028
5. Click "Pay and Confirm Order"

## Expected
(from tests/cases/order.md, TC-ORDER-09)

Probe: record whether an order is created with nothing in it (URL matches /payment_done/<id>) or the request is rejected. The test asserts that single recorded outcome. If reached, record the /payment_done text; an order with an empty cart is a product bug candidate.

Expected: payment with an empty cart should be rejected and the URL should not match /payment_done/<id>.

## Actual
The empty cart payment was processed and an order was successfully created with order ID 0. The URL changed to /payment_done/0 and the page displays "ORDER PLACED!" with "Congratulations! Your order has been confirmed!"

The site allows payments to be processed with a zero-value empty cart, creating orders with ID 0. This represents an incomplete transaction with no actual products purchased.

## Evidence
- target/screenshots/OrderTest-tcOrder09_paymentEmptyCart.png (shows payment_done/0 page with order confirmation)
- target/traces/OrderTest-tcOrder09_paymentEmptyCart.zip
