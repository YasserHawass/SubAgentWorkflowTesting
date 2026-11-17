# Test run 🔴 red

Commit `c0f2900` · 2026-11-17T11:47:34+00:00 · [workflow run](https://github.com/YasserHawass/SubAgentWorkflowTesting/actions/runs/44072352399)

**139 passed · 55 failed · 0 skipped** (43 new, 12 known product bugs)

| Class | Passed | Failed | Skipped |
|---|---|---|---|
| AccountApiTest | 9 | 3 | 0 |
| AuthApiTest | 10 | 1 | 0 |
| BrandsApiTest | 6 | 0 | 0 |
| BrandsTest | 7 | 2 | 0 |
| CartTest | 8 | 7 | 0 |
| CategoriesTest | 8 | 2 | 0 |
| CheckoutTest | 6 | 4 | 0 |
| ContactUsTest | 9 | 2 | 0 |
| InvoiceTest | 2 | 6 | 0 |
| LoginTest | 11 | 1 | 0 |
| LogoutTest | 5 | 3 | 0 |
| OrderTest | 4 | 6 | 0 |
| ProductsApiTest | 6 | 0 | 0 |
| ProductsTest | 8 | 3 | 0 |
| RegisterTest | 7 | 5 | 0 |
| ReviewsTest | 10 | 2 | 0 |
| ScrollTest | 5 | 1 | 0 |
| SearchApiTest | 5 | 2 | 0 |
| SearchTest | 6 | 5 | 0 |
| SmokeSanityTest | 1 | 0 | 0 |
| SubscriptionTest | 6 | 0 | 0 |

## Failures

| TC id | Test | Known bug | Message |
|---|---|---|---|
| TC-APIACCOUNT-12 | AccountApiTest.tcApiAccount12_passwordChangeViaUpdate | **NEW** | com.google.gson.stream.MalformedJsonException: Use JsonReader.setStrictness(Strictness.LENIENT) to accept malformed JSON at line 1 column 20 path $ See https:// |
| TC-APIACCOUNT-05 | AccountApiTest.tcApiAccount05_deleteWithCorrectCredentials | **NEW** | com.google.gson.stream.MalformedJsonException: Use JsonReader.setStrictness(Strictness.LENIENT) to accept malformed JSON at line 1 column 20 path $ See https:// |
| TC-APIACCOUNT-11 | AccountApiTest.tcApiAccount11_updateUnknownEmailAndWrongPassword | **NEW** | com.google.gson.stream.MalformedJsonException: Use JsonReader.setStrictness(Strictness.LENIENT) to accept malformed JSON at line 1 column 20 path $ See https:// |
| TC-APIAUTH-11 | AuthApiTest.tcApiAuth11_deletedAccountCannotVerify | **NEW** | com.google.gson.stream.MalformedJsonException: Use JsonReader.setStrictness(Strictness.LENIENT) to accept malformed JSON at line 1 column 20 path $ See https:// |
| TC-APISEARCH-01 | SearchApiTest.tcApiSearch01_topMatchesNameOrCategory | **NEW** | com.google.gson.stream.MalformedJsonException: Use JsonReader.setStrictness(Strictness.LENIENT) to accept malformed JSON at line 1 column 20 path $ See https:// |
| TC-APISEARCH-02 | SearchApiTest.tcApiSearch02_caseInsensitiveAndCategory | **NEW** | com.google.gson.stream.MalformedJsonException: Use JsonReader.setStrictness(Strictness.LENIENT) to accept malformed JSON at line 1 column 20 path $ See https:// |
| TC-BRANDS-08 | BrandsTest.tcBrands08_everyApiBrandResolvesAndListsProducts | **NEW** | expected: 200 but was: 503 |
| TC-BRANDS-04 | BrandsTest.tcBrands04_onlyChosenBrandProductsListed | **NEW** | com.google.gson.stream.MalformedJsonException: Use JsonReader.setStrictness(Strictness.LENIENT) to accept malformed JSON at line 1 column 20 path $ See https:// |
| TC-CART-09 | CartTest.tcCart09c_quantityNonNumeric | tests/reports/bugs/cart-2.md | Expecting actual: 500 to be less than: 500 |
| TC-CART-12 | CartTest.tcCart12_differentQuantities | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Add |
| TC-CART-06 | CartTest.tcCart06_removeProducts | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for locator("#cart_info_table").getByRole(AriaRole.LINK, new Locator.Get |
| TC-CART-11 | CartTest.tcCart11_stableAcrossNavigation | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for locator(".features_items .product-image-wrapper").first().locator(". |
| TC-CART-04 | CartTest.tcCart04_quantityFourFromDetailPage | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for locator(".features_items .product-image-wrapper").first().locator(". |
| TC-CART-09 | CartTest.tcCart09b_quantityNegative | tests/reports/bugs/cart-1.md | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for locator(".product-information").getByRole(AriaRole.HEADING) |
| TC-CART-05 | CartTest.tcCart05_sameProductTwice | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 15000ms - waiting for getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Ad |
| TC-CATEGORIES-07 | CategoriesTest.tcCategories07a_malformedIdAbc | tests/reports/bugs/categories-1.md | Expecting actual: "<!DOCTYPE html><html lang="en"><head> <meta http-equiv="content-type" content="text/html; charset=utf-8"> <title>Page not found at /category_ |
| TC-CATEGORIES-07 | CategoriesTest.tcCategories07c_malformedIdMinusOne | tests/reports/bugs/categories-1.md | Expecting actual: "<!DOCTYPE html><html lang="en"><head> <meta http-equiv="content-type" content="text/html; charset=utf-8"> <title>Page not found at /category_ |
| TC-CHECKOUT-04 | CheckoutTest.tcCheckout04_deliveryAddress | **NEW** | Locator expected to contain text: Logged in as AE Tester 1794915098171-43 Received: <element(s) not found> Call log: - Locator.expect with timeout 5000ms - wait |
| TC-CHECKOUT-02 | CheckoutTest.tcCheckout02_promptLeadsToLoginCartKept | **NEW** | Locator expected to contain text: Logged in as AE Tester 1794915112427-44 Received: <element(s) not found> Call log: - Locator.expect with timeout 5000ms - wait |
| TC-CHECKOUT-07 | CheckoutTest.tcCheckout07_comment | **NEW** | com.google.gson.stream.MalformedJsonException: Use JsonReader.setStrictness(Strictness.LENIENT) to accept malformed JSON at line 1 column 20 path $ See https:// |
| TC-CHECKOUT-05 | CheckoutTest.tcCheckout05_billingAddress | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for locator(".features_items .product-image-wrapper").first().locator(". |
| TC-CONTACTUS-06 | ContactUsTest.tcContactUs06b_fileWithoutExtension | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-873 |
| TC-CONTACTUS-02 | ContactUsTest.tcContactUs02_homeButtonReturnsHome | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for getByRole(AriaRole.BANNER).getByRole(AriaRole.LINK, new Locator.GetB |
| TC-INVOICE-05 | InvoiceTest.tcInvoice05_directFetchSameSession | **NEW** | Locator expected to contain text: Logged in as AE Tester 1794915622774-53 Received: <element(s) not found> Call log: - Locator.expect with timeout 5000ms - wait |
| TC-INVOICE-04 | InvoiceTest.tcInvoice04_continueAfterDownload | **NEW** | Timeout 30000ms exceeded |
| TC-INVOICE-01 | InvoiceTest.tcInvoice01_downloadIsTxt | **NEW** | com.google.gson.stream.MalformedJsonException: Use JsonReader.setStrictness(Strictness.LENIENT) to accept malformed JSON at line 1 column 20 path $ See https:// |
| TC-INVOICE-02 | InvoiceTest.tcInvoice02_contentHasNameAndTotal | **NEW** | Locator expected to contain text: Logged in as AE Tester 1794915692610-56 Received: <element(s) not found> Call log: - Locator.expect with timeout 5000ms - wait |
| TC-INVOICE-08 | InvoiceTest.tcInvoice08_forgedAmount | tests/reports/bugs/invoice-2.md | Expecting actual: "Hi Inv1869011936186x7 Payer, Your total purchase amount is 999999. Thank you" not to contain: "purchase amount is 999999" |
| TC-INVOICE-07 | InvoiceTest.tcInvoice07_noSession | tests/reports/bugs/invoice-1.md | Expecting actual: "Hi , Your total purchase amount is 500. Thank you" not to contain: "purchase amount is 500" |
| TC-LOGIN-03 | LoginTest.tcLogin03_wrongPasswordRejected | **NEW** | com.google.gson.stream.MalformedJsonException: Use JsonReader.setStrictness(Strictness.LENIENT) to accept malformed JSON at line 1 column 20 path $ See https:// |
| TC-LOGOUT-08 | LogoutTest.tcLogout08_logoutWhileLoggedOut | tests/reports/bugs/logout-1.md | Expecting actual: "KeyError at /logout" not to contain: "KeyError" |
| TC-LOGOUT-03 | LogoutTest.tcLogout03_checkoutAfterLogout | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for getByText("Address Details") |
| TC-LOGOUT-05 | LogoutTest.tcLogout05_cartAfterLogout | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for getByText("Cart is empty!") |
| TC-ORDER-09 | OrderTest.tcOrder09_paymentEmptyCart | tests/reports/bugs/order-3.md | Page URL expected not to match regex: .*/payment_done/\d+$ Received: https://www.automationexercise.com/payment_done/0 Call log: - Locator.expect with timeout 5 |
| TC-ORDER-04 | OrderTest.tcOrder04_continueAfterOrder | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Add |
| TC-ORDER-07 | OrderTest.tcOrder07_pastExpiry | tests/reports/bugs/order-2.md | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Log |
| TC-ORDER-03 | OrderTest.tcOrder03_registerDuringCheckout | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for locator("#cart_info_table").getByRole(AriaRole.LINK, new Locator.Get |
| TC-ORDER-06 | OrderTest.tcOrder06_nonNumericCard | tests/reports/bugs/order-1.md | Locator expected to contain text: Logged in as AE Tester 1794914761314-27 Received: <element(s) not found> Call log: - Locator.expect with timeout 5000ms - wait |
| TC-ORDER-01 | OrderTest.tcOrder01_registerThenOrder | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for locator("[data-qa='pay-button']") |
| TC-PRODUCTS-08 | ProductsTest.tcProducts08_everyIdOpens | **NEW** | [status for id 12] expected: 200 but was: 503 |
| TC-PRODUCTS-10 | ProductsTest.tcProducts10_nonNumericId | tests/reports/bugs/products-1.md | expected: 404 but was: 503 |
| TC-PRODUCTS-03 | ProductsTest.tcProducts03_gridMatchesApi | **NEW** | com.google.gson.stream.MalformedJsonException: Use JsonReader.setStrictness(Strictness.LENIENT) to accept malformed JSON at line 1 column 20 path $ See https:// |
| TC-REGISTER-10 | RegisterTest.tcRegister10_cartKeptAfterRegistering | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for getByText("Enter Account Information") |
| TC-REGISTER-05 | RegisterTest.tcRegister05_signupFormBlocksInvalidInput | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-873 |
| TC-REGISTER-08 | RegisterTest.tcRegister08_prefillAndDobBoundaries | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-873 |
| TC-REGISTER-09 | RegisterTest.tcRegister09_sessionPersists | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for getByText("Enter Account Information") |
| TC-REGISTER-01 | RegisterTest.tcRegister01_fullRegistrationFlow | **NEW** | Page URL expected to be: https://www.automationexercise.com/account_created Received: https://www.automationexercise.com/signup Call log: - Locator.expect with  |
| TC-REVIEWS-05 | ReviewsTest.tcReviews05_markupNotRendered | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Write  |
| TC-REVIEWS-03 | ReviewsTest.tcReviews03b_emailEmpty | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("All |
| TC-SCROLL-06 | ScrollTest.tcScroll06_arrowOnProducts | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-873 |
| TC-SEARCH-09 | SearchTest.tcSearch09_apiMatchesUi | **NEW** | com.google.gson.stream.MalformedJsonException: Use JsonReader.setStrictness(Strictness.LENIENT) to accept malformed JSON at line 1 column 20 path $ See https:// |
| TC-SEARCH-08 | SearchTest.tcSearch08_reloadKeepsResults | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-873 |
| TC-SEARCH-01 | SearchTest.tcSearch01_searchTshirt | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-873 |
| TC-SEARCH-07 | SearchTest.tcSearch07_ampersand | tests/reports/bugs/search-1.md | Page URL expected to match regex: ^.*/products\?search=%26$ Received: https://www.automationexercise.com/products?search=& Call log: - Locator.expect with timeo |
| TC-SEARCH-03 | SearchTest.tcSearch03_caseInsensitive | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Sea |
