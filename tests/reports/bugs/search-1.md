# BUG: Search form sends ampersand unencoded in query string

Feature: search   Test: TC-SEARCH-07   Severity: medium
Environment: chromium, 2026-09-29

## Steps
1. On /products, enter "&" in the search box and click the search button

## Expected
The & character is URL-encoded; the search form sends `GET /products?search=%26` (percent-encoded ampersand).

## Actual
The search form sends `GET /products?search=&` with the ampersand unencoded, which can break downstream query string parsing and lead to incorrect or missing search results for the ampersand character.

## Evidence
- target/screenshots/SearchTest-tcSearch07_ampersand.png
- target/traces/SearchTest-tcSearch07_ampersand.zip
