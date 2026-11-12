# Test run 🔴 red

Commit `c0f2900` · 2026-11-12T10:57:23+00:00 · [workflow run](https://github.com/YasserHawass/SubAgentWorkflowTesting/actions/runs/43199999200)

**147 passed · 47 failed · 0 skipped** (35 new, 12 known product bugs)

| Class | Passed | Failed | Skipped |
|---|---|---|---|
| AccountApiTest | 11 | 1 | 0 |
| AuthApiTest | 11 | 0 | 0 |
| BrandsApiTest | 5 | 1 | 0 |
| BrandsTest | 8 | 1 | 0 |
| CartTest | 9 | 6 | 0 |
| CategoriesTest | 7 | 3 | 0 |
| CheckoutTest | 6 | 4 | 0 |
| ContactUsTest | 11 | 0 | 0 |
| InvoiceTest | 2 | 6 | 0 |
| LoginTest | 12 | 0 | 0 |
| LogoutTest | 6 | 2 | 0 |
| OrderTest | 3 | 7 | 0 |
| ProductsApiTest | 6 | 0 | 0 |
| ProductsTest | 9 | 2 | 0 |
| RegisterTest | 5 | 7 | 0 |
| ReviewsTest | 10 | 2 | 0 |
| ScrollTest | 5 | 1 | 0 |
| SearchApiTest | 6 | 1 | 0 |
| SearchTest | 8 | 3 | 0 |
| SmokeSanityTest | 1 | 0 | 0 |
| SubscriptionTest | 6 | 0 | 0 |

## Failures

| TC id | Test | Known bug | Message |
|---|---|---|---|
| TC-APIACCOUNT-07 | AccountApiTest.tcApiAccount07_createWithMissingField | **NEW** | com.google.gson.stream.MalformedJsonException: Use JsonReader.setStrictness(Strictness.LENIENT) to accept malformed JSON at line 1 column 20 path $ See https:// |
| TC-APIBRANDS-05 | BrandsApiTest.tcApiBrands05_postAndDeleteRejected | **NEW** | com.google.gson.stream.MalformedJsonException: Use JsonReader.setStrictness(Strictness.LENIENT) to accept malformed JSON at line 1 column 20 path $ See https:// |
| TC-APISEARCH-06 | SearchApiTest.tcApiSearch06_wrongMethods | **NEW** | com.google.gson.stream.MalformedJsonException: Use JsonReader.setStrictness(Strictness.LENIENT) to accept malformed JSON at line 1 column 20 path $ See https:// |
| TC-BRANDS-08 | BrandsTest.tcBrands08_everyApiBrandResolvesAndListsProducts | **NEW** | expected: 200 but was: 503 |
| TC-CART-09 | CartTest.tcCart09c_quantityNonNumeric | tests/reports/bugs/cart-2.md | Expecting actual: 500 to be less than: 500 |
| TC-CART-12 | CartTest.tcCart12_differentQuantities | **NEW** | Locator expected to have text: Men Tshirt Received: <element(s) not found> Call log: - Locator.expect with timeout 5000ms - waiting for locator(".product-inform |
| TC-CART-06 | CartTest.tcCart06_removeProducts | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 15000ms - waiting for getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Ad |
| TC-CART-10 | CartTest.tcCart10_guestCartSurvivesLogin | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Sea |
| TC-CART-09 | CartTest.tcCart09b_quantityNegative | tests/reports/bugs/cart-1.md | Locator expected to have count: 0 Received: 1 Call log: - Locator.expect with timeout 5000ms - waiting for locator("#cart_info_table tbody tr").locator(".cart_q |
| TC-CART-05 | CartTest.tcCart05_sameProductTwice | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 15000ms - waiting for getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Ad |
| TC-CATEGORIES-05 | CategoriesTest.tcCategories05_accordionExpandsAndCollapses | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-772 |
| TC-CATEGORIES-07 | CategoriesTest.tcCategories07a_malformedIdAbc | tests/reports/bugs/categories-1.md | Expecting actual: "<!DOCTYPE html><html lang="en"><head> <meta http-equiv="content-type" content="text/html; charset=utf-8"> <title>Page not found at /category_ |
| TC-CATEGORIES-07 | CategoriesTest.tcCategories07c_malformedIdMinusOne | tests/reports/bugs/categories-1.md | Expecting actual: "<!DOCTYPE html><html lang="en"><head> <meta http-equiv="content-type" content="text/html; charset=utf-8"> <title>Page not found at /category_ |
| TC-CHECKOUT-04 | CheckoutTest.tcCheckout04_deliveryAddress | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for locator(".features_items .product-image-wrapper").first().locator(". |
| TC-CHECKOUT-02 | CheckoutTest.tcCheckout02_promptLeadsToLoginCartKept | **NEW** | Locator expected to contain text: Logged in as AE Tester 1794480185236-42 Received: <element(s) not found> Call log: - Locator.expect with timeout 5000ms - wait |
| TC-CHECKOUT-05 | CheckoutTest.tcCheckout05_billingAddress | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Log |
| TC-CHECKOUT-01 | CheckoutTest.tcCheckout01_guestPrompt | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for locator(".features_items .product-image-wrapper").first().locator(". |
| TC-INVOICE-06 | InvoiceTest.tcInvoice06_otherUsersInvoice | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Log |
| TC-INVOICE-04 | InvoiceTest.tcInvoice04_continueAfterDownload | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for locator("[data-qa='pay-button']") |
| TC-INVOICE-01 | InvoiceTest.tcInvoice01_downloadIsTxt | **NEW** | Timeout 30000ms exceeded |
| TC-INVOICE-02 | InvoiceTest.tcInvoice02_contentHasNameAndTotal | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for locator("#cart_info_table").getByRole(AriaRole.LINK, new Locator.Get |
| TC-INVOICE-08 | InvoiceTest.tcInvoice08_forgedAmount | tests/reports/bugs/invoice-2.md | Expecting actual: "Hi Inv1715844266835x7 Payer, Your total purchase amount is 999999. Thank you" not to contain: "purchase amount is 999999" |
| TC-INVOICE-07 | InvoiceTest.tcInvoice07_noSession | tests/reports/bugs/invoice-1.md | Expecting actual: "Hi , Your total purchase amount is 500. Thank you" not to contain: "purchase amount is 500" |
| TC-LOGOUT-08 | LogoutTest.tcLogout08_logoutWhileLoggedOut | tests/reports/bugs/logout-1.md | Expecting actual: "KeyError at /logout" not to contain: "KeyError" |
| TC-LOGOUT-04 | LogoutTest.tcLogout04_backAfterLogout | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for getByRole(AriaRole.BANNER).getByRole(AriaRole.LINK, new Locator.GetB |
| TC-ORDER-09 | OrderTest.tcOrder09_paymentEmptyCart | tests/reports/bugs/order-3.md | Page URL expected not to match regex: .*/payment_done/\d+$ Received: https://www.automationexercise.com/payment_done/0 Call log: - Locator.expect with timeout 5 |
| TC-ORDER-04 | OrderTest.tcOrder04_continueAfterOrder | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for locator("[data-qa='pay-button']") |
| TC-ORDER-07 | OrderTest.tcOrder07_pastExpiry | tests/reports/bugs/order-2.md | Page URL expected to be: https://www.automationexercise.com/payment Received: https://www.automationexercise.com/payment_done/500 Call log: - Locator.expect wit |
| TC-ORDER-02 | OrderTest.tcOrder02_loginOrderDelete | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Log |
| TC-ORDER-03 | OrderTest.tcOrder03_registerDuringCheckout | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for locator("[data-qa='pay-button']") |
| TC-ORDER-06 | OrderTest.tcOrder06_nonNumericCard | tests/reports/bugs/order-1.md | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-772 |
| TC-ORDER-01 | OrderTest.tcOrder01_registerThenOrder | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Add |
| TC-PRODUCTS-08 | ProductsTest.tcProducts08_everyIdOpens | **NEW** | [status for id 7] expected: 200 but was: 503 |
| TC-PRODUCTS-10 | ProductsTest.tcProducts10_nonNumericId | tests/reports/bugs/products-1.md | expected: 404 but was: 503 |
| TC-REGISTER-07 | RegisterTest.tcRegister07_specialNames | **NEW** | Page URL expected to be: https://www.automationexercise.com/account_created Received: https://www.automationexercise.com/signup Call log: - Locator.expect with  |
| TC-REGISTER-10 | RegisterTest.tcRegister10_cartKeptAfterRegistering | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-772 |
| TC-REGISTER-06 | RegisterTest.tcRegister06_requiredFieldsOnSignup | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-772 |
| TC-REGISTER-08 | RegisterTest.tcRegister08_prefillAndDobBoundaries | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-772 |
| TC-REGISTER-02 | RegisterTest.tcRegister02_deleteAccountThenLoginFails | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-772 |
| TC-REGISTER-09 | RegisterTest.tcRegister09_sessionPersists | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for getByText("Enter Account Information") |
| TC-REGISTER-01 | RegisterTest.tcRegister01_fullRegistrationFlow | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for getByRole(AriaRole.BANNER).getByRole(AriaRole.LINK, new Locator.GetB |
| TC-REVIEWS-05 | ReviewsTest.tcReviews05_markupNotRendered | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-772 |
| TC-REVIEWS-06 | ReviewsTest.tcReviews06_notDisplayedAfterReload | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Write  |
| TC-SCROLL-01 | ScrollTest.tcScroll01_withArrow | **NEW** | Locator expected to be in viewport Call log: - Locator.expect with timeout 5000ms - waiting for locator("#slider-carousel .item.active").getByRole(AriaRole.HEAD |
| TC-SEARCH-10 | SearchTest.tcSearch10_apiMissingParam | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-772 |
| TC-SEARCH-07 | SearchTest.tcSearch07_ampersand | tests/reports/bugs/search-1.md | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("All |
| TC-SEARCH-04 | SearchTest.tcSearch04_partialTermSuperset | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Sea |
