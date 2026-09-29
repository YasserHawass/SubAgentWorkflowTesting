# BUG: Empty quantity input sends malformed request, server returns HTTP 500

Feature: cart   Test: TC-CART-09c   Severity: high
Environment: chromium, 2026-09-29

## Steps
1. Open a product detail page (product 1: Blue Top, Rs. 500)
2. Leave the quantity input empty (or enter a non-numeric value like "abc")
3. Click "Add to cart"

## Expected
The server handles the invalid input gracefully: either the form validation prevents the submission, or the server returns a valid error response (HTTP status < 500) with an appropriate error message. No modal is shown; the cart remains unchanged.

## Actual
The number input remains empty when a non-numeric value like "abc" is entered. Clicking "Add to cart" sends a malformed GET request `/add_to_cart/1?quantity=` with an empty quantity parameter. The server responds with HTTP 500 (Internal Server Error) instead of handling the invalid input gracefully.

## Evidence
- target/screenshots/CartTest-tcCart09c_quantityNonNumeric.png
- target/traces/CartTest-tcCart09c_quantityNonNumeric.zip
