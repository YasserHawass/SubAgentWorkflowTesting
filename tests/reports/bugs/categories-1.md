# BUG: GET /category_products/<malformed_id> returns Django DEBUG page with URLconf listing

Feature: categories   Test: TC-CATEGORIES-07a, TC-CATEGORIES-07c   Severity: high
Environment: chromium, 2026-09-30

## Steps
1. Navigate directly to /category_products/abc
2. Navigate directly to /category_products/-1

## Expected
No server error; HTTP status less than 500; clean 404 error page with site header visible; no Django debug information

## Actual
Server responds with HTTP 404 and renders a Django error page displaying "Page not found" with "DEBUG = True" in the settings explanation and a complete URLconf listing. The page title contains "Page not found at /category_products/abc" and "Page not found at /category_products/-1" respectively. This occurs consistently and reproducibly when accessing /category_products with malformed IDs. The site header is not rendered, causing links to be absent.

For /category_products/0, the server correctly returns HTTP 200 with an empty product listing (TC-07b passes).

## Evidence
- target/traces/CategoriesTest-tcCategories07a_malformedIdAbc.zip
- target/traces/CategoriesTest-tcCategories07c_malformedIdMinusOne.zip

## Related
Same root cause as tests/reports/bugs/products-1.md: DEBUG = True in production Django settings allows exposure of sensitive configuration and URLconf information. The site should serve a standard 404 error page (either Django debug disabled or a custom 404 template) that preserves the site header and does not expose framework internals. This affects both /product_details/ and /category_products/ endpoints with malformed input.
