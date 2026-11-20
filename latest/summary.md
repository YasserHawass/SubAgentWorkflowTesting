# Test run 🔴 red

Commit `c0f2900` · 2026-11-20T11:07:05+00:00 · [workflow run](https://github.com/YasserHawass/SubAgentWorkflowTesting/actions/runs/44720917331)

**162 passed · 32 failed · 0 skipped** (20 new, 12 known product bugs)

| Class | Passed | Failed | Skipped |
|---|---|---|---|
| AccountApiTest | 10 | 2 | 0 |
| AuthApiTest | 10 | 1 | 0 |
| BrandsApiTest | 5 | 1 | 0 |
| BrandsTest | 9 | 0 | 0 |
| CartTest | 9 | 6 | 0 |
| CategoriesTest | 7 | 3 | 0 |
| CheckoutTest | 9 | 1 | 0 |
| ContactUsTest | 10 | 1 | 0 |
| InvoiceTest | 3 | 5 | 0 |
| LoginTest | 12 | 0 | 0 |
| LogoutTest | 5 | 3 | 0 |
| OrderTest | 7 | 3 | 0 |
| ProductsApiTest | 6 | 0 | 0 |
| ProductsTest | 10 | 1 | 0 |
| RegisterTest | 11 | 1 | 0 |
| ReviewsTest | 11 | 1 | 0 |
| ScrollTest | 5 | 1 | 0 |
| SearchApiTest | 7 | 0 | 0 |
| SearchTest | 10 | 1 | 0 |
| SmokeSanityTest | 1 | 0 | 0 |
| SubscriptionTest | 5 | 1 | 0 |

## Failures

| TC id | Test | Known bug | Message |
|---|---|---|---|
| TC-APIACCOUNT-12 | AccountApiTest.tcApiAccount12_passwordChangeViaUpdate | **NEW** | com.google.gson.stream.MalformedJsonException: Use JsonReader.setStrictness(Strictness.LENIENT) to accept malformed JSON at line 1 column 20 path $ See https:// |
| TC-APIACCOUNT-04 | AccountApiTest.tcApiAccount04_lifecycle | **NEW** | com.google.gson.stream.MalformedJsonException: Use JsonReader.setStrictness(Strictness.LENIENT) to accept malformed JSON at line 1 column 20 path $ See https:// |
| TC-APIAUTH-09 | AuthApiTest.tcApiAuth09_emptyStringsReturnUserNotFound | **NEW** | com.google.gson.stream.MalformedJsonException: Use JsonReader.setStrictness(Strictness.LENIENT) to accept malformed JSON at line 1 column 20 path $ See https:// |
| TC-APIBRANDS-03 | BrandsApiTest.tcApiBrands03_productBrandsAreListed | **NEW** | com.google.gson.stream.MalformedJsonException: Use JsonReader.setStrictness(Strictness.LENIENT) to accept malformed JSON at line 1 column 20 path $ See https:// |
| TC-CART-09 | CartTest.tcCart09c_quantityNonNumeric | tests/reports/bugs/cart-2.md | Expecting actual: 500 to be less than: 500 |
| TC-CART-06 | CartTest.tcCart06_removeProducts | **NEW** | Locator expected to have count: 0 Received: 1 Call log: - Locator.expect with timeout 5000ms - waiting for locator("#cart_info_table tbody tr").filter(new Locat |
| TC-CART-04 | CartTest.tcCart04_quantityFourFromDetailPage | **NEW** | Locator expected to have text: 4 Received: <element(s) not found> Call log: - Locator.expect with timeout 5000ms - waiting for locator("#cart_info_table tbody t |
| TC-CART-08 | CartTest.tcCart08_recommendedItem | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-381 |
| TC-CART-01 | CartTest.tcCart01_addTwoProducts | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-381 |
| TC-CART-09 | CartTest.tcCart09b_quantityNegative | tests/reports/bugs/cart-1.md | Locator expected to have count: 0 Received: 1 Call log: - Locator.expect with timeout 5000ms - waiting for locator("#cart_info_table tbody tr").locator(".cart_q |
| TC-CATEGORIES-08 | CategoriesTest.tcCategories08_everySubcategoryLinkResolves | **NEW** | expected: 200 but was: 503 |
| TC-CATEGORIES-07 | CategoriesTest.tcCategories07a_malformedIdAbc | tests/reports/bugs/categories-1.md | Expecting actual: "<!DOCTYPE html><html lang="en"><head> <meta http-equiv="content-type" content="text/html; charset=utf-8"> <title>Page not found at /category_ |
| TC-CATEGORIES-07 | CategoriesTest.tcCategories07c_malformedIdMinusOne | tests/reports/bugs/categories-1.md | Expecting actual: "<!DOCTYPE html><html lang="en"><head> <meta http-equiv="content-type" content="text/html; charset=utf-8"> <title>Page not found at /category_ |
| TC-CHECKOUT-10 | CheckoutTest.tcCheckout10_reloadAndBack | **NEW** | com.google.gson.stream.MalformedJsonException: Use JsonReader.setStrictness(Strictness.LENIENT) to accept malformed JSON at line 1 column 20 path $ See https:// |
| TC-CONTACTUS-02 | ContactUsTest.tcContactUs02_homeButtonReturnsHome | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-381 |
| TC-INVOICE-06 | InvoiceTest.tcInvoice06_otherUsersInvoice | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-381 |
| TC-INVOICE-04 | InvoiceTest.tcInvoice04_continueAfterDownload | **NEW** | Timeout 30000ms exceeded |
| TC-INVOICE-08 | InvoiceTest.tcInvoice08_forgedAmount | tests/reports/bugs/invoice-2.md | Expecting actual: "Hi Inv1387208415470x7 Payer, Your total purchase amount is 999999. Thank you" not to contain: "purchase amount is 999999" |
| TC-INVOICE-07 | InvoiceTest.tcInvoice07_noSession | tests/reports/bugs/invoice-1.md | com.google.gson.stream.MalformedJsonException: Use JsonReader.setStrictness(Strictness.LENIENT) to accept malformed JSON at line 1 column 20 path $ See https:// |
| TC-INVOICE-03 | InvoiceTest.tcInvoice03_twiceIdentical | **NEW** | Timeout 30000ms exceeded |
| TC-LOGOUT-08 | LogoutTest.tcLogout08_logoutWhileLoggedOut | tests/reports/bugs/logout-1.md | Expecting actual: "KeyError at /logout" not to contain: "KeyError" |
| TC-LOGOUT-01 | LogoutTest.tcLogout01_logoutFromHome | **NEW** | Locator expected to contain text: Logged in as AE Tester 1795171456150-13 Received: <element(s) not found> Call log: - Locator.expect with timeout 5000ms - wait |
| TC-LOGOUT-06 | LogoutTest.tcLogout06_loginAgain | **NEW** | com.google.gson.stream.MalformedJsonException: Use JsonReader.setStrictness(Strictness.LENIENT) to accept malformed JSON at line 1 column 20 path $ See https:// |
| TC-ORDER-09 | OrderTest.tcOrder09_paymentEmptyCart | tests/reports/bugs/order-3.md | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Log |
| TC-ORDER-07 | OrderTest.tcOrder07_pastExpiry | tests/reports/bugs/order-2.md | Page URL expected to be: https://www.automationexercise.com/payment Received: https://www.automationexercise.com/payment_done/500 Call log: - Locator.expect wit |
| TC-ORDER-06 | OrderTest.tcOrder06_nonNumericCard | tests/reports/bugs/order-1.md | com.google.gson.stream.MalformedJsonException: Use JsonReader.setStrictness(Strictness.LENIENT) to accept malformed JSON at line 1 column 20 path $ See https:// |
| TC-PRODUCTS-10 | ProductsTest.tcProducts10_nonNumericId | tests/reports/bugs/products-1.md | Expecting actual: "<!DOCTYPE html><html lang="en"><head> <meta http-equiv="content-type" content="text/html; charset=utf-8"> <title>Page not found at /product_d |
| TC-REGISTER-06 | RegisterTest.tcRegister06_requiredFieldsOnSignup | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-381 |
| TC-REVIEWS-07 | ReviewsTest.tcReviews07_loggedIn | **NEW** | com.google.gson.stream.MalformedJsonException: Use JsonReader.setStrictness(Strictness.LENIENT) to accept malformed JSON at line 1 column 20 path $ See https:// |
| TC-SCROLL-03 | ScrollTest.tcScroll03_arrowAppearsAfterScroll | **NEW** | Locator expected to have count: 1 Received: 0 Call log: - Locator.expect with timeout 5000ms - waiting for locator("#scrollUp") 9 × locator resolved to 0 elemen |
| TC-SEARCH-07 | SearchTest.tcSearch07_ampersand | tests/reports/bugs/search-1.md | Page URL expected to match regex: ^.*/products\?search=%26$ Received: https://www.automationexercise.com/products?search=& Call log: - Locator.expect with timeo |
| TC-SUBSCRIPTION-06 | SubscriptionTest.tcSubscription06_placementAndInput | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-381 |
