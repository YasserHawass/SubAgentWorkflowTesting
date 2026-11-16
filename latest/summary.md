# Test run 🔴 red

Commit `c0f2900` · 2026-11-16T10:42:32+00:00 · [workflow run](https://github.com/YasserHawass/SubAgentWorkflowTesting/actions/runs/43846936112)

**161 passed · 33 failed · 0 skipped** (21 new, 12 known product bugs)

| Class | Passed | Failed | Skipped |
|---|---|---|---|
| AccountApiTest | 12 | 0 | 0 |
| AuthApiTest | 11 | 0 | 0 |
| BrandsApiTest | 6 | 0 | 0 |
| BrandsTest | 7 | 2 | 0 |
| CartTest | 10 | 5 | 0 |
| CategoriesTest | 7 | 3 | 0 |
| CheckoutTest | 9 | 1 | 0 |
| ContactUsTest | 11 | 0 | 0 |
| InvoiceTest | 3 | 5 | 0 |
| LoginTest | 10 | 2 | 0 |
| LogoutTest | 6 | 2 | 0 |
| OrderTest | 2 | 8 | 0 |
| ProductsApiTest | 6 | 0 | 0 |
| ProductsTest | 10 | 1 | 0 |
| RegisterTest | 12 | 0 | 0 |
| ReviewsTest | 10 | 2 | 0 |
| ScrollTest | 6 | 0 | 0 |
| SearchApiTest | 6 | 1 | 0 |
| SearchTest | 10 | 1 | 0 |
| SmokeSanityTest | 1 | 0 | 0 |
| SubscriptionTest | 6 | 0 | 0 |

## Failures

| TC id | Test | Known bug | Message |
|---|---|---|---|
| TC-APISEARCH-06 | SearchApiTest.tcApiSearch06_wrongMethods | **NEW** | com.google.gson.stream.MalformedJsonException: Use JsonReader.setStrictness(Strictness.LENIENT) to accept malformed JSON at line 1 column 20 path $ See https:// |
| TC-BRANDS-03 | BrandsTest.tcBrands03_switchToDifferentBrand | **NEW** | Locator expected to have text matching regex: ^\s*Brand\s+\-\s+H&M\s+Products\s*$ Received: <element(s) not found> Call log: - Locator.expect with timeout 5000m |
| TC-BRANDS-02 | BrandsTest.tcBrands02_clickBrandShowsItsPage | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Bra |
| TC-CART-09 | CartTest.tcCart09c_quantityNonNumeric | tests/reports/bugs/cart-2.md | Expecting actual: 500 to be less than: 500 |
| TC-CART-12 | CartTest.tcCart12_differentQuantities | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Add |
| TC-CART-02 | CartTest.tcCart02_viewCartFromModal | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 15000ms - waiting for getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Ad |
| TC-CART-09 | CartTest.tcCart09b_quantityNegative | tests/reports/bugs/cart-1.md | Locator expected to have count: 0 Received: 1 Call log: - Locator.expect with timeout 5000ms - waiting for locator("#cart_info_table tbody tr").locator(".cart_q |
| TC-CART-05 | CartTest.tcCart05_sameProductTwice | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 15000ms - waiting for getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Ad |
| TC-CATEGORIES-01 | CategoriesTest.tcCategories01_womenSubcategoryPageFromHome | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Cat |
| TC-CATEGORIES-07 | CategoriesTest.tcCategories07a_malformedIdAbc | tests/reports/bugs/categories-1.md | Expecting actual: "<!DOCTYPE html><html lang="en"><head> <meta http-equiv="content-type" content="text/html; charset=utf-8"> <title>Page not found at /category_ |
| TC-CATEGORIES-07 | CategoriesTest.tcCategories07c_malformedIdMinusOne | tests/reports/bugs/categories-1.md | Expecting actual: "<!DOCTYPE html><html lang="en"><head> <meta http-equiv="content-type" content="text/html; charset=utf-8"> <title>Page not found at /category_ |
| TC-CHECKOUT-10 | CheckoutTest.tcCheckout10_reloadAndBack | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for locator("#cart_info_table").getByRole(AriaRole.LINK, new Locator.Get |
| TC-INVOICE-06 | InvoiceTest.tcInvoice06_otherUsersInvoice | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for locator("#cart_info_table").getByRole(AriaRole.LINK, new Locator.Get |
| TC-INVOICE-01 | InvoiceTest.tcInvoice01_downloadIsTxt | **NEW** | com.google.gson.stream.MalformedJsonException: Use JsonReader.setStrictness(Strictness.LENIENT) to accept malformed JSON at line 1 column 20 path $ See https:// |
| TC-INVOICE-02 | InvoiceTest.tcInvoice02_contentHasNameAndTotal | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Log |
| TC-INVOICE-08 | InvoiceTest.tcInvoice08_forgedAmount | tests/reports/bugs/invoice-2.md | Expecting actual: "Hi Inv1456900833990x7 Payer, Your total purchase amount is 999999. Thank you" not to contain: "purchase amount is 999999" |
| TC-INVOICE-07 | InvoiceTest.tcInvoice07_noSession | tests/reports/bugs/invoice-1.md | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-171 |
| TC-LOGIN-03 | LoginTest.tcLogin03_wrongPasswordRejected | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-171 |
| TC-LOGIN-12 | LoginTest.tcLogin12_apiAgreesWithUi | **NEW** | com.google.gson.stream.MalformedJsonException: Use JsonReader.setStrictness(Strictness.LENIENT) to accept malformed JSON at line 1 column 20 path $ See https:// |
| TC-LOGOUT-07 | LogoutTest.tcLogout07_logoutFromProducts | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-171 |
| TC-LOGOUT-08 | LogoutTest.tcLogout08_logoutWhileLoggedOut | tests/reports/bugs/logout-1.md | Expecting actual: "KeyError at /logout" not to contain: "KeyError" |
| TC-ORDER-09 | OrderTest.tcOrder09_paymentEmptyCart | tests/reports/bugs/order-3.md | Page URL expected not to match regex: .*/payment_done/\d+$ Received: https://www.automationexercise.com/payment_done/0 Call log: - Locator.expect with timeout 5 |
| TC-ORDER-04 | OrderTest.tcOrder04_continueAfterOrder | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for locator("#cart_info_table").getByRole(AriaRole.LINK, new Locator.Get |
| TC-ORDER-07 | OrderTest.tcOrder07_pastExpiry | tests/reports/bugs/order-2.md | Page URL expected to be: https://www.automationexercise.com/payment Received: https://www.automationexercise.com/payment_done/500 Call log: - Locator.expect wit |
| TC-ORDER-02 | OrderTest.tcOrder02_loginOrderDelete | **NEW** | Locator expected to contain text: Logged in as AE Tester 1794824656164-24 Received: <element(s) not found> Call log: - Locator.expect with timeout 5000ms - wait |
| TC-ORDER-03 | OrderTest.tcOrder03_registerDuringCheckout | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("New |
| TC-ORDER-10 | OrderTest.tcOrder10_reloadAndBack | **NEW** | Locator expected to contain text: Logged in as AE Tester 1794824686720-26 Received: <element(s) not found> Call log: - Locator.expect with timeout 5000ms - wait |
| TC-ORDER-06 | OrderTest.tcOrder06_nonNumericCard | tests/reports/bugs/order-1.md | Locator expected to contain text: Logged in as AE Tester 1794824697813-27 Received: <element(s) not found> Call log: - Locator.expect with timeout 5000ms - wait |
| TC-ORDER-01 | OrderTest.tcOrder01_registerThenOrder | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for locator("[data-qa='pay-button']") |
| TC-PRODUCTS-10 | ProductsTest.tcProducts10_nonNumericId | tests/reports/bugs/products-1.md | expected: 404 but was: 503 |
| TC-REVIEWS-03 | ReviewsTest.tcReviews03a_nameEmpty | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-171 |
| TC-REVIEWS-01 | ReviewsTest.tcReviews01_formVisible | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-171 |
| TC-SEARCH-07 | SearchTest.tcSearch07_ampersand | tests/reports/bugs/search-1.md | Page URL expected to match regex: ^.*/products\?search=%26$ Received: https://www.automationexercise.com/products?search=& Call log: - Locator.expect with timeo |
