# Test run 🔴 red

Commit `c0f2900` · 2026-10-30T09:18:39+00:00 · [workflow run](https://github.com/YasserHawass/SubAgentWorkflowTesting/actions/runs/41031041521)

**180 passed · 14 failed · 0 skipped** (2 new, 12 known product bugs)

| Class | Passed | Failed | Skipped |
|---|---|---|---|
| AccountApiTest | 12 | 0 | 0 |
| AuthApiTest | 11 | 0 | 0 |
| BrandsApiTest | 6 | 0 | 0 |
| BrandsTest | 9 | 0 | 0 |
| CartTest | 13 | 2 | 0 |
| CategoriesTest | 8 | 2 | 0 |
| CheckoutTest | 10 | 0 | 0 |
| ContactUsTest | 11 | 0 | 0 |
| InvoiceTest | 5 | 3 | 0 |
| LoginTest | 11 | 1 | 0 |
| LogoutTest | 7 | 1 | 0 |
| OrderTest | 7 | 3 | 0 |
| ProductsApiTest | 6 | 0 | 0 |
| ProductsTest | 10 | 1 | 0 |
| RegisterTest | 12 | 0 | 0 |
| ReviewsTest | 12 | 0 | 0 |
| ScrollTest | 6 | 0 | 0 |
| SearchApiTest | 7 | 0 | 0 |
| SearchTest | 10 | 1 | 0 |
| SmokeSanityTest | 1 | 0 | 0 |
| SubscriptionTest | 6 | 0 | 0 |

## Failures

| TC id | Test | Known bug | Message |
|---|---|---|---|
| TC-CART-09 | CartTest.tcCart09c_quantityNonNumeric | tests/reports/bugs/cart-2.md | Expecting actual: 500 to be less than: 500 |
| TC-CART-09 | CartTest.tcCart09b_quantityNegative | tests/reports/bugs/cart-1.md | Locator expected to have count: 0 Received: 1 Call log: - Locator.expect with timeout 5000ms - waiting for locator("#cart_info_table tbody tr").locator(".cart_q |
| TC-CATEGORIES-07 | CategoriesTest.tcCategories07a_malformedIdAbc | tests/reports/bugs/categories-1.md | Expecting actual: "<!DOCTYPE html><html lang="en"><head> <meta http-equiv="content-type" content="text/html; charset=utf-8"> <title>Page not found at /category_ |
| TC-CATEGORIES-07 | CategoriesTest.tcCategories07c_malformedIdMinusOne | tests/reports/bugs/categories-1.md | Expecting actual: "<!DOCTYPE html><html lang="en"><head> <meta http-equiv="content-type" content="text/html; charset=utf-8"> <title>Page not found at /category_ |
| TC-INVOICE-02 | InvoiceTest.tcInvoice02_contentHasNameAndTotal | **NEW** | Timeout 30000ms exceeded |
| TC-INVOICE-08 | InvoiceTest.tcInvoice08_forgedAmount | tests/reports/bugs/invoice-2.md | Expecting actual: "Hi Inv152697783272x7 Payer, Your total purchase amount is 999999. Thank you" not to contain: "purchase amount is 999999" |
| TC-INVOICE-07 | InvoiceTest.tcInvoice07_noSession | tests/reports/bugs/invoice-1.md | Expecting actual: "Hi , Your total purchase amount is 500. Thank you" not to contain: "purchase amount is 500" |
| TC-LOGIN-07 | LoginTest.tcLogin07_cartKeptAfterLogin | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for locator("#cart_info_table").getByRole(AriaRole.LINK, new Locator.Get |
| TC-LOGOUT-08 | LogoutTest.tcLogout08_logoutWhileLoggedOut | tests/reports/bugs/logout-1.md | Expecting actual: "KeyError at /logout" not to contain: "KeyError" |
| TC-ORDER-09 | OrderTest.tcOrder09_paymentEmptyCart | tests/reports/bugs/order-3.md | Page URL expected not to match regex: .*/payment_done/\d+$ Received: https://www.automationexercise.com/payment_done/0 Call log: - Locator.expect with timeout 5 |
| TC-ORDER-07 | OrderTest.tcOrder07_pastExpiry | tests/reports/bugs/order-2.md | Page URL expected to be: https://www.automationexercise.com/payment Received: https://www.automationexercise.com/payment_done/500 Call log: - Locator.expect wit |
| TC-ORDER-06 | OrderTest.tcOrder06_nonNumericCard | tests/reports/bugs/order-1.md | Page URL expected to be: https://www.automationexercise.com/payment Received: https://www.automationexercise.com/payment_done/500 Call log: - Locator.expect wit |
| TC-PRODUCTS-10 | ProductsTest.tcProducts10_nonNumericId | tests/reports/bugs/products-1.md | Expecting actual: "<!DOCTYPE html><html lang="en"><head> <meta http-equiv="content-type" content="text/html; charset=utf-8"> <title>Page not found at /product_d |
| TC-SEARCH-07 | SearchTest.tcSearch07_ampersand | tests/reports/bugs/search-1.md | Page URL expected to match regex: ^.*/products\?search=%26$ Received: https://www.automationexercise.com/products?search=& Call log: - Locator.expect with timeo |
