# BUG: Negative quantity creates negative cart total

Feature: cart   Test: TC-CART-09b   Severity: high
Environment: chromium, 2026-09-29

## Steps
1. Open a product detail page (product 1: Blue Top, Rs. 500)
2. Enter quantity -1 in the quantity input
3. Click "Add to cart"
4. Click "View Cart" in the modal

## Expected
Negative quantity is rejected; no item is added to the cart or the cart shows a validation error. No negative cart totals are permitted.

## Actual
The negative quantity is accepted and added to the cart, resulting in a cart row with quantity -1 and a negative total (e.g., -Rs. 500), which is incorrect and breaks the cart logic.

## Evidence
- target/screenshots/CartTest-tcCart09b_quantityNegative.png
- target/traces/CartTest-tcCart09b_quantityNegative.zip
