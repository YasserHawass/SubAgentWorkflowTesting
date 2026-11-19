# Test run 🔴 red

Commit `c0f2900` · 2026-11-19T11:25:55+00:00 · [workflow run](https://github.com/YasserHawass/SubAgentWorkflowTesting/actions/runs/44509086631)

**128 passed · 66 failed · 0 skipped** (54 new, 12 known product bugs)

| Class | Passed | Failed | Skipped |
|---|---|---|---|
| AccountApiTest | 10 | 2 | 0 |
| AuthApiTest | 8 | 3 | 0 |
| BrandsApiTest | 4 | 2 | 0 |
| BrandsTest | 7 | 2 | 0 |
| CartTest | 4 | 11 | 0 |
| CategoriesTest | 6 | 4 | 0 |
| CheckoutTest | 5 | 5 | 0 |
| ContactUsTest | 10 | 1 | 0 |
| InvoiceTest | 0 | 8 | 0 |
| LoginTest | 9 | 3 | 0 |
| LogoutTest | 4 | 4 | 0 |
| OrderTest | 2 | 8 | 0 |
| ProductsApiTest | 3 | 3 | 0 |
| ProductsTest | 10 | 1 | 0 |
| RegisterTest | 10 | 2 | 0 |
| ReviewsTest | 9 | 3 | 0 |
| ScrollTest | 6 | 0 | 0 |
| SearchApiTest | 7 | 0 | 0 |
| SearchTest | 7 | 4 | 0 |
| SmokeSanityTest | 1 | 0 | 0 |
| SubscriptionTest | 6 | 0 | 0 |

## Failures

| TC id | Test | Known bug | Message |
|---|---|---|---|
| TC-APIACCOUNT-05 | AccountApiTest.tcApiAccount05_deleteWithCorrectCredentials | **NEW** | com.google.gson.stream.MalformedJsonException: Use JsonReader.setStrictness(Strictness.LENIENT) to accept malformed JSON at line 1 column 20 path $ See https:// |
| TC-APIACCOUNT-11 | AccountApiTest.tcApiAccount11_updateUnknownEmailAndWrongPassword | **NEW** | com.google.gson.stream.MalformedJsonException: Use JsonReader.setStrictness(Strictness.LENIENT) to accept malformed JSON at line 1 column 20 path $ See https:// |
| TC-APIAUTH-11 | AuthApiTest.tcApiAuth11_deletedAccountCannotVerify | **NEW** | com.google.gson.stream.MalformedJsonException: Use JsonReader.setStrictness(Strictness.LENIENT) to accept malformed JSON at line 1 column 20 path $ See https:// |
| TC-APIAUTH-10 | AuthApiTest.tcApiAuth10_upperCasedEmailReturnsUserNotFound | **NEW** | com.google.gson.stream.MalformedJsonException: Use JsonReader.setStrictness(Strictness.LENIENT) to accept malformed JSON at line 1 column 20 path $ See https:// |
| TC-APIAUTH-02 | AuthApiTest.tcApiAuth02_wrongPasswordReturnsUserNotFound | **NEW** | com.google.gson.stream.MalformedJsonException: Use JsonReader.setStrictness(Strictness.LENIENT) to accept malformed JSON at line 1 column 20 path $ See https:// |
| TC-APIBRANDS-03 | BrandsApiTest.tcApiBrands03_productBrandsAreListed | **NEW** | com.google.gson.stream.MalformedJsonException: Use JsonReader.setStrictness(Strictness.LENIENT) to accept malformed JSON at line 1 column 20 path $ See https:// |
| TC-APIBRANDS-04 | BrandsApiTest.tcApiBrands04_putRejected | **NEW** | com.google.gson.stream.MalformedJsonException: Use JsonReader.setStrictness(Strictness.LENIENT) to accept malformed JSON at line 1 column 20 path $ See https:// |
| TC-APIPRODUCTS-03 | ProductsApiTest.tcApiProducts03_catalogueHas34UniqueProducts | **NEW** | com.google.gson.stream.MalformedJsonException: Use JsonReader.setStrictness(Strictness.LENIENT) to accept malformed JSON at line 1 column 20 path $ See https:// |
| TC-APIPRODUCTS-05 | ProductsApiTest.tcApiProducts05_putAndDeleteRejected | **NEW** | com.google.gson.stream.MalformedJsonException: Use JsonReader.setStrictness(Strictness.LENIENT) to accept malformed JSON at line 1 column 20 path $ See https:// |
| TC-APIPRODUCTS-01 | ProductsApiTest.tcApiProducts01_getReturnsProductsArray | **NEW** | com.google.gson.stream.MalformedJsonException: Use JsonReader.setStrictness(Strictness.LENIENT) to accept malformed JSON at line 1 column 20 path $ See https:// |
| TC-BRANDS-05 | BrandsTest.tcBrands05_sidebarCountMatchesListedAndApiCount | **NEW** | Locator expected to have text matching regex: ^\s*Brand\s+\-\s+Kookie\s+Kids\s+Products\s*$ Received: <element(s) not found> Call log: - Locator.expect with tim |
| TC-BRANDS-08 | BrandsTest.tcBrands08_everyApiBrandResolvesAndListsProducts | **NEW** | expected: 200 but was: 503 |
| TC-CART-09 | CartTest.tcCart09c_quantityNonNumeric | tests/reports/bugs/cart-2.md | Expecting actual: 500 to be less than: 500 |
| TC-CART-12 | CartTest.tcCart12_differentQuantities | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for locator(".features_items .product-image-wrapper").first().locator(". |
| TC-CART-09 | CartTest.tcCart09d_quantity999 | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Add |
| TC-CART-06 | CartTest.tcCart06_removeProducts | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 15000ms - waiting for getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Ad |
| TC-CART-11 | CartTest.tcCart11_stableAcrossNavigation | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for locator(".features_items .product-image-wrapper").first().locator(". |
| TC-CART-04 | CartTest.tcCart04_quantityFourFromDetailPage | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for locator(".product-information").getByRole(AriaRole.HEADING) |
| TC-CART-10 | CartTest.tcCart10_guestCartSurvivesLogin | **NEW** | com.google.gson.stream.MalformedJsonException: Use JsonReader.setStrictness(Strictness.LENIENT) to accept malformed JSON at line 1 column 20 path $ See https:// |
| TC-CART-08 | CartTest.tcCart08_recommendedItem | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-765 |
| TC-CART-02 | CartTest.tcCart02_viewCartFromModal | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for locator("#cart_info_table").getByRole(AriaRole.LINK, new Locator.Get |
| TC-CART-01 | CartTest.tcCart01_addTwoProducts | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 15000ms - waiting for getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Ad |
| TC-CART-09 | CartTest.tcCart09b_quantityNegative | tests/reports/bugs/cart-1.md | Locator expected to have count: 0 Received: 1 Call log: - Locator.expect with timeout 5000ms - waiting for locator("#cart_info_table tbody tr").locator(".cart_q |
| TC-CATEGORIES-01 | CategoriesTest.tcCategories01_womenSubcategoryPageFromHome | **NEW** | Locator expected to have text matching regex: ^\s*Women\s+\-\s+Dress\s+Products\s*$ Received: <element(s) not found> Call log: - Locator.expect with timeout 500 |
| TC-CATEGORIES-07 | CategoriesTest.tcCategories07a_malformedIdAbc | tests/reports/bugs/categories-1.md | Expecting actual: 503 to be less than: 500 |
| TC-CATEGORIES-07 | CategoriesTest.tcCategories07c_malformedIdMinusOne | tests/reports/bugs/categories-1.md | Expecting actual: "<!DOCTYPE html><html lang="en"><head> <meta http-equiv="content-type" content="text/html; charset=utf-8"> <title>Page not found at /category_ |
| TC-CATEGORIES-06 | CategoriesTest.tcCategories06_nonexistentCategoryId | **NEW** | Expecting actual: 503 to be less than: 500 |
| TC-CHECKOUT-08 | CheckoutTest.tcCheckout08_emptyCartLoggedIn | **NEW** | Locator expected to contain text: Logged in as AE Tester 1795086682138-46 Received: <element(s) not found> Call log: - Locator.expect with timeout 5000ms - wait |
| TC-CHECKOUT-07 | CheckoutTest.tcCheckout07_comment | **NEW** | com.google.gson.stream.MalformedJsonException: Use JsonReader.setStrictness(Strictness.LENIENT) to accept malformed JSON at line 1 column 20 path $ See https:// |
| TC-CHECKOUT-05 | CheckoutTest.tcCheckout05_billingAddress | **NEW** | com.google.gson.stream.MalformedJsonException: Use JsonReader.setStrictness(Strictness.LENIENT) to accept malformed JSON at line 1 column 20 path $ See https:// |
| TC-CHECKOUT-10 | CheckoutTest.tcCheckout10_reloadAndBack | **NEW** | Locator expected to have text: [Your delivery address, Mr. Auto Tester, AE QA, 1 Test Street, Suite 2, San Jose California 95101, United States, +10000000000] R |
| TC-CHECKOUT-06 | CheckoutTest.tcCheckout06_reviewRows | **NEW** | Locator expected to have text: Men Tshirt Received: <element(s) not found> Call log: - Locator.expect with timeout 5000ms - waiting for locator(".product-inform |
| TC-CONTACTUS-02 | ContactUsTest.tcContactUs02_homeButtonReturnsHome | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for getByRole(AriaRole.BANNER).getByRole(AriaRole.LINK, new Locator.GetB |
| TC-INVOICE-06 | InvoiceTest.tcInvoice06_otherUsersInvoice | **NEW** | Locator expected to contain text: Logged in as AE Tester 1795087061032-52 Received: <element(s) not found> Call log: - Locator.expect with timeout 5000ms - wait |
| TC-INVOICE-05 | InvoiceTest.tcInvoice05_directFetchSameSession | **NEW** | Timeout 30000ms exceeded |
| TC-INVOICE-04 | InvoiceTest.tcInvoice04_continueAfterDownload | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-765 |
| TC-INVOICE-01 | InvoiceTest.tcInvoice01_downloadIsTxt | **NEW** | Locator expected to contain text: Logged in as AE Tester 1795087177703-56 Received: <element(s) not found> Call log: - Locator.expect with timeout 5000ms - wait |
| TC-INVOICE-02 | InvoiceTest.tcInvoice02_contentHasNameAndTotal | **NEW** | com.google.gson.stream.MalformedJsonException: Use JsonReader.setStrictness(Strictness.LENIENT) to accept malformed JSON at line 1 column 20 path $ See https:// |
| TC-INVOICE-08 | InvoiceTest.tcInvoice08_forgedAmount | tests/reports/bugs/invoice-2.md | Expecting actual: "Hi Inv1899138414204x7 Payer, Your total purchase amount is 999999. Thank you" not to contain: "purchase amount is 999999" |
| TC-INVOICE-07 | InvoiceTest.tcInvoice07_noSession | tests/reports/bugs/invoice-1.md | Locator expected to contain text: Logged in as AE Tester 1795087210184-59 Received: <element(s) not found> Call log: - Locator.expect with timeout 5000ms - wait |
| TC-INVOICE-03 | InvoiceTest.tcInvoice03_twiceIdentical | **NEW** | Locator expected to contain text: Logged in as AE Tester 1795087222505-60 Received: <element(s) not found> Call log: - Locator.expect with timeout 5000ms - wait |
| TC-LOGIN-11 | LoginTest.tcLogin11_loggedInOpensLogin | **NEW** | com.google.gson.stream.MalformedJsonException: Use JsonReader.setStrictness(Strictness.LENIENT) to accept malformed JSON at line 1 column 20 path $ See https:// |
| TC-LOGIN-07 | LoginTest.tcLogin07_cartKeptAfterLogin | **NEW** | Locator expected to contain text: Logged in as AE Tester 1795085650045-5 Received: <element(s) not found> Call log: - Locator.expect with timeout 5000ms - waiti |
| TC-LOGIN-03 | LoginTest.tcLogin03_wrongPasswordRejected | **NEW** | com.google.gson.stream.MalformedJsonException: Use JsonReader.setStrictness(Strictness.LENIENT) to accept malformed JSON at line 1 column 20 path $ See https:// |
| TC-LOGOUT-07 | LogoutTest.tcLogout07_logoutFromProducts | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-765 |
| TC-LOGOUT-08 | LogoutTest.tcLogout08_logoutWhileLoggedOut | tests/reports/bugs/logout-1.md | Expecting actual: "KeyError at /logout" not to contain: "KeyError" |
| TC-LOGOUT-04 | LogoutTest.tcLogout04_backAfterLogout | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for getByRole(AriaRole.BANNER).getByRole(AriaRole.LINK, new Locator.GetB |
| TC-LOGOUT-02 | LogoutTest.tcLogout02_headerLoggedOut | **NEW** | Locator expected to contain text: Logged in as AE Tester 1795086046653-18 Received: <element(s) not found> Call log: - Locator.expect with timeout 5000ms - wait |
| TC-ORDER-09 | OrderTest.tcOrder09_paymentEmptyCart | tests/reports/bugs/order-3.md | Page URL expected not to match regex: .*/payment_done/\d+$ Received: https://www.automationexercise.com/payment_done/0 Call log: - Locator.expect with timeout 5 |
| TC-ORDER-04 | OrderTest.tcOrder04_continueAfterOrder | **NEW** | Locator expected to contain text: Logged in as AE Tester 1795086116403-22 Received: <element(s) not found> Call log: - Locator.expect with timeout 5000ms - wait |
| TC-ORDER-07 | OrderTest.tcOrder07_pastExpiry | tests/reports/bugs/order-2.md | com.google.gson.stream.MalformedJsonException: Use JsonReader.setStrictness(Strictness.LENIENT) to accept malformed JSON at line 1 column 20 path $ See https:// |
| TC-ORDER-02 | OrderTest.tcOrder02_loginOrderDelete | **NEW** | Locator expected to contain text: Logged in as AE Tester 1795086129544-24 Received: <element(s) not found> Call log: - Locator.expect with timeout 5000ms - wait |
| TC-ORDER-03 | OrderTest.tcOrder03_registerDuringCheckout | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-765 |
| TC-ORDER-10 | OrderTest.tcOrder10_reloadAndBack | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Log |
| TC-ORDER-06 | OrderTest.tcOrder06_nonNumericCard | tests/reports/bugs/order-1.md | Page URL expected to be: https://www.automationexercise.com/payment Received: https://www.automationexercise.com/payment_done/500 Call log: - Locator.expect wit |
| TC-ORDER-01 | OrderTest.tcOrder01_registerThenOrder | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for locator("#cart_info_table").getByRole(AriaRole.LINK, new Locator.Get |
| TC-PRODUCTS-10 | ProductsTest.tcProducts10_nonNumericId | tests/reports/bugs/products-1.md | Expecting actual: "<!DOCTYPE html><html lang="en"><head> <meta http-equiv="content-type" content="text/html; charset=utf-8"> <title>Page not found at /product_d |
| TC-REGISTER-06 | RegisterTest.tcRegister06_requiredFieldsOnSignup | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Acc |
| TC-REGISTER-03 | RegisterTest.tcRegister03_existingEmailShowsError | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-765 |
| TC-REVIEWS-05 | ReviewsTest.tcReviews05_markupNotRendered | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-765 |
| TC-REVIEWS-01 | ReviewsTest.tcReviews01_formVisible | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-765 |
| TC-REVIEWS-07 | ReviewsTest.tcReviews07_loggedIn | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-765 |
| TC-SEARCH-10 | SearchTest.tcSearch10_apiMissingParam | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-765 |
| TC-SEARCH-09 | SearchTest.tcSearch09_apiMatchesUi | **NEW** | com.google.gson.stream.MalformedJsonException: Use JsonReader.setStrictness(Strictness.LENIENT) to accept malformed JSON at line 1 column 20 path $ See https:// |
| TC-SEARCH-01 | SearchTest.tcSearch01_searchTshirt | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Sea |
| TC-SEARCH-07 | SearchTest.tcSearch07_ampersand | tests/reports/bugs/search-1.md | Page URL expected to match regex: ^.*/products\?search=%26$ Received: https://www.automationexercise.com/products?search=& Call log: - Locator.expect with timeo |
