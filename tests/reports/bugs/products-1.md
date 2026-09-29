# BUG: GET /product_details/abc returns Django DEBUG page with URLconf listing

Feature: products   Test: TC-PRODUCTS-10   Severity: high
Environment: chromium, 2026-09-29

## Steps
1. Navigate directly to /product_details/abc

## Expected
No server error; HTTP status less than 500; clean 404 error page with site header visible (showing "Signup / Login"); no Django debug information

## Actual
Server responds with HTTP 404 and renders a Django error page displaying "Page not found" with "DEBUG = True" in the settings explanation and a complete URLconf listing. The page title contains "Page not found at /product_details/abc". This occurs consistently and reproducibly when accessing /product_details with non-numeric IDs. The site header is not rendered, causing the "Signup / Login" link to be absent.

## Evidence
- target/screenshots/ProductsTest-tcProducts10_nonNumericId.png
- target/traces/ProductsTest-tcProducts10_nonNumericId.zip

## Related
Same root cause as tests/reports/bugs/logout-1.md: DEBUG = True in production Django settings allows exposure of sensitive configuration and URLconf information. The site should serve a standard 404 error page (either Django debug disabled or a custom 404 template) that preserves the site header and does not expose framework internals.
