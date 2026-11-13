# Test run 🔴 red

Commit `c0f2900` · 2026-11-13T10:31:20+00:00 · [workflow run](https://github.com/YasserHawass/SubAgentWorkflowTesting/actions/runs/43406954506)

**174 passed · 20 failed · 0 skipped** (9 new, 11 known product bugs)

| Class | Passed | Failed | Skipped |
|---|---|---|---|
| AccountApiTest | 10 | 2 | 0 |
| AuthApiTest | 9 | 2 | 0 |
| BrandsApiTest | 6 | 0 | 0 |
| BrandsTest | 9 | 0 | 0 |
| CartTest | 12 | 3 | 0 |
| CategoriesTest | 8 | 2 | 0 |
| CheckoutTest | 10 | 0 | 0 |
| ContactUsTest | 11 | 0 | 0 |
| InvoiceTest | 6 | 2 | 0 |
| LoginTest | 11 | 1 | 0 |
| LogoutTest | 7 | 1 | 0 |
| OrderTest | 7 | 3 | 0 |
| ProductsApiTest | 6 | 0 | 0 |
| ProductsTest | 10 | 1 | 0 |
| RegisterTest | 11 | 1 | 0 |
| ReviewsTest | 12 | 0 | 0 |
| ScrollTest | 6 | 0 | 0 |
| SearchApiTest | 7 | 0 | 0 |
| SearchTest | 10 | 1 | 0 |
| SmokeSanityTest | 0 | 1 | 0 |
| SubscriptionTest | 6 | 0 | 0 |

## Failures

| TC id | Test | Known bug | Message |
|---|---|---|---|
|  | SmokeSanityTest.sanity01_homeLoads | **NEW** | Page title expected to be: Automation Exercise Received: Call log: - Locator.expect with timeout 5000ms - waiting for locator(":root") 9 × locator resolved to < |
| TC-APIACCOUNT-07 | AccountApiTest.tcApiAccount07_createWithMissingField | **NEW** | com.google.gson.stream.MalformedJsonException: Use JsonReader.setStrictness(Strictness.LENIENT) to accept malformed JSON at line 1 column 20 path $ See https:// |
| TC-APIACCOUNT-11 | AccountApiTest.tcApiAccount11_updateUnknownEmailAndWrongPassword | **NEW** | com.google.gson.stream.MalformedJsonException: Use JsonReader.setStrictness(Strictness.LENIENT) to accept malformed JSON at line 1 column 20 path $ See https:// |
| TC-APIAUTH-11 | AuthApiTest.tcApiAuth11_deletedAccountCannotVerify | **NEW** | com.google.gson.stream.MalformedJsonException: Use JsonReader.setStrictness(Strictness.LENIENT) to accept malformed JSON at line 1 column 20 path $ See https:// |
| TC-APIAUTH-09 | AuthApiTest.tcApiAuth09_emptyStringsReturnUserNotFound | **NEW** | com.google.gson.stream.MalformedJsonException: Use JsonReader.setStrictness(Strictness.LENIENT) to accept malformed JSON at line 1 column 20 path $ See https:// |
| TC-CART-09 | CartTest.tcCart09c_quantityNonNumeric | tests/reports/bugs/cart-2.md | Expecting actual: 500 to be less than: 500 |
| TC-CART-10 | CartTest.tcCart10_guestCartSurvivesLogin | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 15000ms - waiting for getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Ad |
| TC-CART-09 | CartTest.tcCart09b_quantityNegative | tests/reports/bugs/cart-1.md | Locator expected to have count: 0 Received: 1 Call log: - Locator.expect with timeout 5000ms - waiting for locator("#cart_info_table tbody tr").locator(".cart_q |
| TC-CATEGORIES-07 | CategoriesTest.tcCategories07a_malformedIdAbc | tests/reports/bugs/categories-1.md | Expecting actual: "<!DOCTYPE html><html lang="en"><head> <meta http-equiv="content-type" content="text/html; charset=utf-8"> <title>Page not found at /category_ |
| TC-CATEGORIES-07 | CategoriesTest.tcCategories07c_malformedIdMinusOne | tests/reports/bugs/categories-1.md | Expecting actual: "<!DOCTYPE html><html lang="en"><head> <meta http-equiv="content-type" content="text/html; charset=utf-8"> <title>Page not found at /category_ |
| TC-INVOICE-08 | InvoiceTest.tcInvoice08_forgedAmount | tests/reports/bugs/invoice-2.md | Expecting actual: "Hi Inv1220889305764x7 Payer, Your total purchase amount is 999999. Thank you" not to contain: "purchase amount is 999999" |
| TC-INVOICE-03 | InvoiceTest.tcInvoice03_twiceIdentical | **NEW** | com.google.gson.stream.MalformedJsonException: Use JsonReader.setStrictness(Strictness.LENIENT) to accept malformed JSON at line 1 column 20 path $ See https:// |
| TC-LOGIN-04 | LoginTest.tcLogin04_validationBlocksSubmit | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-786 |
| TC-LOGOUT-08 | LogoutTest.tcLogout08_logoutWhileLoggedOut | tests/reports/bugs/logout-1.md | Expecting actual: "KeyError at /logout" not to contain: "KeyError" |
| TC-ORDER-09 | OrderTest.tcOrder09_paymentEmptyCart | tests/reports/bugs/order-3.md | Page URL expected not to match regex: .*/payment_done/\d+$ Received: https://www.automationexercise.com/payment_done/0 Call log: - Locator.expect with timeout 5 |
| TC-ORDER-07 | OrderTest.tcOrder07_pastExpiry | tests/reports/bugs/order-2.md | Page URL expected to be: https://www.automationexercise.com/payment Received: https://www.automationexercise.com/payment_done/500 Call log: - Locator.expect wit |
| TC-ORDER-06 | OrderTest.tcOrder06_nonNumericCard | tests/reports/bugs/order-1.md | Timeout 30000ms exceeded |
| TC-PRODUCTS-10 | ProductsTest.tcProducts10_nonNumericId | tests/reports/bugs/products-1.md | Expecting actual: "<!DOCTYPE html><html lang="en"><head> <meta http-equiv="content-type" content="text/html; charset=utf-8"> <title>Page not found at /product_d |
| TC-REGISTER-12 | RegisterTest.tcRegister12_uiSignupDetailsStored | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for getByText("Enter Account Information") |
| TC-SEARCH-07 | SearchTest.tcSearch07_ampersand | tests/reports/bugs/search-1.md | Page URL expected to match regex: ^.*/products\?search=%26$ Received: https://www.automationexercise.com/products?search=& Call log: - Locator.expect with timeo |
