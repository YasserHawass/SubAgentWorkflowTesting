# Test run 🔴 red

Commit `c0f2900` · 2026-11-18T11:25:11+00:00 · [workflow run](https://github.com/YasserHawass/SubAgentWorkflowTesting/actions/runs/44288717545)

**0 passed · 194 failed · 0 skipped** (182 new, 12 known product bugs)

| Class | Passed | Failed | Skipped |
|---|---|---|---|
| AccountApiTest | 0 | 12 | 0 |
| AuthApiTest | 0 | 11 | 0 |
| BrandsApiTest | 0 | 6 | 0 |
| BrandsTest | 0 | 9 | 0 |
| CartTest | 0 | 15 | 0 |
| CategoriesTest | 0 | 10 | 0 |
| CheckoutTest | 0 | 10 | 0 |
| ContactUsTest | 0 | 11 | 0 |
| InvoiceTest | 0 | 8 | 0 |
| LoginTest | 0 | 12 | 0 |
| LogoutTest | 0 | 8 | 0 |
| OrderTest | 0 | 10 | 0 |
| ProductsApiTest | 0 | 6 | 0 |
| ProductsTest | 0 | 11 | 0 |
| RegisterTest | 0 | 12 | 0 |
| ReviewsTest | 0 | 12 | 0 |
| ScrollTest | 0 | 6 | 0 |
| SearchApiTest | 0 | 7 | 0 |
| SearchTest | 0 | 11 | 0 |
| SmokeSanityTest | 0 | 1 | 0 |
| SubscriptionTest | 0 | 6 | 0 |

## Failures

| TC id | Test | Known bug | Message |
|---|---|---|---|
|  | SmokeSanityTest.sanity01_homeLoads | **NEW** | Page title expected to be: Automation Exercise Received: Call log: - Locator.expect with timeout 5000ms - waiting for locator(":root") 9 × locator resolved to < |
| TC-APIACCOUNT-10 | AccountApiTest.tcApiAccount10_wrongHttpMethods | **NEW** | [GET createAccount] expected: 405 but was: 403 |
| TC-APIACCOUNT-07 | AccountApiTest.tcApiAccount07_createWithMissingField | **NEW** | Not a JSON Object: null |
| TC-APIACCOUNT-12 | AccountApiTest.tcApiAccount12_passwordChangeViaUpdate | **NEW** | Not a JSON Object: null |
| TC-APIACCOUNT-05 | AccountApiTest.tcApiAccount05_deleteWithCorrectCredentials | **NEW** | Not a JSON Object: null |
| TC-APIACCOUNT-09 | AccountApiTest.tcApiAccount09_getDetailUnknownAndMissingEmail | **NEW** | Not a JSON Object: null |
| TC-APIACCOUNT-01 | AccountApiTest.tcApiAccount01_createAccountWithAllFields | **NEW** | Not a JSON Object: null |
| TC-APIACCOUNT-02 | AccountApiTest.tcApiAccount02_getUserDetailReturnsSentFields | **NEW** | Not a JSON Object: null |
| TC-APIACCOUNT-11 | AccountApiTest.tcApiAccount11_updateUnknownEmailAndWrongPassword | **NEW** | Not a JSON Object: null |
| TC-APIACCOUNT-03 | AccountApiTest.tcApiAccount03_updateChangesNameCityAddress | **NEW** | Not a JSON Object: null |
| TC-APIACCOUNT-04 | AccountApiTest.tcApiAccount04_lifecycle | **NEW** | Not a JSON Object: null |
| TC-APIACCOUNT-08 | AccountApiTest.tcApiAccount08_deleteWithWrongPassword | **NEW** | Not a JSON Object: null |
| TC-APIACCOUNT-06 | AccountApiTest.tcApiAccount06_duplicateCreateRejected | **NEW** | Not a JSON Object: null |
| TC-APIAUTH-11 | AuthApiTest.tcApiAuth11_deletedAccountCannotVerify | **NEW** | Not a JSON Object: null |
| TC-APIAUTH-06 | AuthApiTest.tcApiAuth06_deleteNotSupported | **NEW** | Not a JSON Object: null |
| TC-APIAUTH-05 | AuthApiTest.tcApiAuth05_missingEmailReturns400 | **NEW** | Not a JSON Object: null |
| TC-APIAUTH-09 | AuthApiTest.tcApiAuth09_emptyStringsReturnUserNotFound | **NEW** | Not a JSON Object: null |
| TC-APIAUTH-07 | AuthApiTest.tcApiAuth07_getNotSupported | **NEW** | Not a JSON Object: null |
| TC-APIAUTH-10 | AuthApiTest.tcApiAuth10_upperCasedEmailReturnsUserNotFound | **NEW** | Not a JSON Object: null |
| TC-APIAUTH-03 | AuthApiTest.tcApiAuth03_unregisteredEmailReturnsUserNotFound | **NEW** | Not a JSON Object: null |
| TC-APIAUTH-02 | AuthApiTest.tcApiAuth02_wrongPasswordReturnsUserNotFound | **NEW** | Not a JSON Object: null |
| TC-APIAUTH-01 | AuthApiTest.tcApiAuth01_validCredentialsReturnUserExists | **NEW** | Not a JSON Object: null |
| TC-APIAUTH-08 | AuthApiTest.tcApiAuth08_putNotSupported | **NEW** | Not a JSON Object: null |
| TC-APIAUTH-04 | AuthApiTest.tcApiAuth04_missingPasswordReturns400 | **NEW** | Not a JSON Object: null |
| TC-APIBRANDS-03 | BrandsApiTest.tcApiBrands03_productBrandsAreListed | **NEW** | Not a JSON Object: null |
| TC-APIBRANDS-04 | BrandsApiTest.tcApiBrands04_putRejected | **NEW** | Not a JSON Object: null |
| TC-APIBRANDS-06 | BrandsApiTest.tcApiBrands06_noOrphanBrands | **NEW** | Not a JSON Object: null |
| TC-APIBRANDS-01 | BrandsApiTest.tcApiBrands01_getReturnsBrandsArray | **NEW** | Not a JSON Object: null |
| TC-APIBRANDS-02 | BrandsApiTest.tcApiBrands02_brandShapeAndUniqueIds | **NEW** | Not a JSON Object: null |
| TC-APIBRANDS-05 | BrandsApiTest.tcApiBrands05_postAndDeleteRejected | **NEW** | Not a JSON Object: null |
| TC-APIPRODUCTS-02 | ProductsApiTest.tcApiProducts02_everyProductHasShape | **NEW** | Not a JSON Object: null |
| TC-APIPRODUCTS-04 | ProductsApiTest.tcApiProducts04_postRejected | **NEW** | Not a JSON Object: null |
| TC-APIPRODUCTS-03 | ProductsApiTest.tcApiProducts03_catalogueHas34UniqueProducts | **NEW** | Not a JSON Object: null |
| TC-APIPRODUCTS-05 | ProductsApiTest.tcApiProducts05_putAndDeleteRejected | **NEW** | Not a JSON Object: null |
| TC-APIPRODUCTS-01 | ProductsApiTest.tcApiProducts01_getReturnsProductsArray | **NEW** | Not a JSON Object: null |
| TC-APIPRODUCTS-06 | ProductsApiTest.tcApiProducts06_categoryAndBrandWellFormed | **NEW** | Not a JSON Object: null |
| TC-APISEARCH-05 | SearchApiTest.tcApiSearch05_emptySearchTerm | **NEW** | Not a JSON Object: null |
| TC-APISEARCH-07 | SearchApiTest.tcApiSearch07_jsonBodyIgnored | **NEW** | Not a JSON Object: null |
| TC-APISEARCH-01 | SearchApiTest.tcApiSearch01_topMatchesNameOrCategory | **NEW** | Not a JSON Object: null |
| TC-APISEARCH-03 | SearchApiTest.tcApiSearch03_resultsEqualCatalogueEntries | **NEW** | Not a JSON Object: null |
| TC-APISEARCH-02 | SearchApiTest.tcApiSearch02_caseInsensitiveAndCategory | **NEW** | Not a JSON Object: null |
| TC-APISEARCH-04 | SearchApiTest.tcApiSearch04_noMatchAndSpecialCharacters | **NEW** | Not a JSON Object: null |
| TC-APISEARCH-06 | SearchApiTest.tcApiSearch06_wrongMethods | **NEW** | Not a JSON Object: null |
| TC-BRANDS-03 | BrandsTest.tcBrands03_switchToDifferentBrand | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Bra |
| TC-BRANDS-06 | BrandsTest.tcBrands06b_brandUrlIsCaseSensitive | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Bra |
| TC-BRANDS-05 | BrandsTest.tcBrands05_sidebarCountMatchesListedAndApiCount | **NEW** | Not a JSON Object: null |
| TC-BRANDS-06 | BrandsTest.tcBrands06a_unknownBrandShowsCleanPage | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for getByRole(AriaRole.BANNER).getByRole(AriaRole.LINK, new Locator.GetB |
| TC-BRANDS-08 | BrandsTest.tcBrands08_everyApiBrandResolvesAndListsProducts | **NEW** | Not a JSON Object: null |
| TC-BRANDS-04 | BrandsTest.tcBrands04_onlyChosenBrandProductsListed | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Bra |
| TC-BRANDS-02 | BrandsTest.tcBrands02_clickBrandShowsItsPage | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Bra |
| TC-BRANDS-01 | BrandsTest.tcBrands01_brandsSidebarOnProducts | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-546 |
| TC-BRANDS-07 | BrandsTest.tcBrands07_addBrandProductToCart | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Bra |
| TC-CART-07 | CartTest.tcCart07_emptyState | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for getByText("Cart is empty!") |
| TC-CART-09 | CartTest.tcCart09c_quantityNonNumeric | tests/reports/bugs/cart-2.md | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for locator(".product-information").getByRole(AriaRole.HEADING) |
| TC-CART-12 | CartTest.tcCart12_differentQuantities | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for locator(".features_items .product-image-wrapper").first().locator(". |
| TC-CART-09 | CartTest.tcCart09d_quantity999 | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for locator(".product-information").getByRole(AriaRole.HEADING) |
| TC-CART-06 | CartTest.tcCart06_removeProducts | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for locator(".features_items .product-image-wrapper").first().locator(". |
| TC-CART-11 | CartTest.tcCart11_stableAcrossNavigation | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for locator(".features_items .product-image-wrapper").first().locator(". |
| TC-CART-04 | CartTest.tcCart04_quantityFourFromDetailPage | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for locator(".features_items .product-image-wrapper").first().locator(". |
| TC-CART-10 | CartTest.tcCart10_guestCartSurvivesLogin | **NEW** | Not a JSON Object: null |
| TC-CART-08 | CartTest.tcCart08_recommendedItem | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-546 |
| TC-CART-09 | CartTest.tcCart09a_quantityZero | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for locator(".product-information").getByRole(AriaRole.HEADING) |
| TC-CART-02 | CartTest.tcCart02_viewCartFromModal | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for locator(".features_items .product-image-wrapper").first().locator(". |
| TC-CART-01 | CartTest.tcCart01_addTwoProducts | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-546 |
| TC-CART-09 | CartTest.tcCart09b_quantityNegative | tests/reports/bugs/cart-1.md | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for locator(".product-information").getByRole(AriaRole.HEADING) |
| TC-CART-05 | CartTest.tcCart05_sameProductTwice | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for locator(".features_items .product-image-wrapper").first().locator(". |
| TC-CART-03 | CartTest.tcCart03_continueShopping | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for locator(".features_items .product-image-wrapper").first().locator(". |
| TC-CATEGORIES-01 | CategoriesTest.tcCategories01_womenSubcategoryPageFromHome | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Cat |
| TC-CATEGORIES-08 | CategoriesTest.tcCategories08_everySubcategoryLinkResolves | **NEW** | Not a JSON Object: null |
| TC-CATEGORIES-04 | CategoriesTest.tcCategories04_categorySidebarOnProducts | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Cat |
| TC-CATEGORIES-07 | CategoriesTest.tcCategories07b_malformedIdZero | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for getByRole(AriaRole.BANNER).getByRole(AriaRole.LINK, new Locator.GetB |
| TC-CATEGORIES-05 | CategoriesTest.tcCategories05_accordionExpandsAndCollapses | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-546 |
| TC-CATEGORIES-03 | CategoriesTest.tcCategories03_listedProductsBelongToCategory | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-546 |
| TC-CATEGORIES-07 | CategoriesTest.tcCategories07a_malformedIdAbc | tests/reports/bugs/categories-1.md | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for getByRole(AriaRole.BANNER).getByRole(AriaRole.LINK, new Locator.GetB |
| TC-CATEGORIES-07 | CategoriesTest.tcCategories07c_malformedIdMinusOne | tests/reports/bugs/categories-1.md | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for getByRole(AriaRole.BANNER).getByRole(AriaRole.LINK, new Locator.GetB |
| TC-CATEGORIES-06 | CategoriesTest.tcCategories06_nonexistentCategoryId | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for getByRole(AriaRole.BANNER).getByRole(AriaRole.LINK, new Locator.GetB |
| TC-CATEGORIES-02 | CategoriesTest.tcCategories02_switchFromWomenToMen | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Cat |
| TC-CHECKOUT-04 | CheckoutTest.tcCheckout04_deliveryAddress | **NEW** | Not a JSON Object: null |
| TC-CHECKOUT-09 | CheckoutTest.tcCheckout09_guestDirect | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Add |
| TC-CHECKOUT-02 | CheckoutTest.tcCheckout02_promptLeadsToLoginCartKept | **NEW** | Not a JSON Object: null |
| TC-CHECKOUT-08 | CheckoutTest.tcCheckout08_emptyCartLoggedIn | **NEW** | Not a JSON Object: null |
| TC-CHECKOUT-07 | CheckoutTest.tcCheckout07_comment | **NEW** | Not a JSON Object: null |
| TC-CHECKOUT-05 | CheckoutTest.tcCheckout05_billingAddress | **NEW** | Not a JSON Object: null |
| TC-CHECKOUT-10 | CheckoutTest.tcCheckout10_reloadAndBack | **NEW** | Not a JSON Object: null |
| TC-CHECKOUT-01 | CheckoutTest.tcCheckout01_guestPrompt | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for locator(".features_items .product-image-wrapper").first().locator(". |
| TC-CHECKOUT-06 | CheckoutTest.tcCheckout06_reviewRows | **NEW** | Not a JSON Object: null |
| TC-CHECKOUT-03 | CheckoutTest.tcCheckout03_continueOnCart | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for locator(".features_items .product-image-wrapper").first().locator(". |
| TC-CONTACTUS-03 | ContactUsTest.tcContactUs03_dismissDoesNotSubmit | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-546 |
| TC-CONTACTUS-06 | ContactUsTest.tcContactUs06b_fileWithoutExtension | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-546 |
| TC-CONTACTUS-04 | ContactUsTest.tcContactUs04d_emptyMessage | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-546 |
| TC-CONTACTUS-04 | ContactUsTest.tcContactUs04b_emptyEmail | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-546 |
| TC-CONTACTUS-06 | ContactUsTest.tcContactUs06a_largeFile | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-546 |
| TC-CONTACTUS-05 | ContactUsTest.tcContactUs05_invalidEmail | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-546 |
| TC-CONTACTUS-02 | ContactUsTest.tcContactUs02_homeButtonReturnsHome | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-546 |
| TC-CONTACTUS-01 | ContactUsTest.tcContactUs01_submitShowsSuccess | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for getByRole(AriaRole.BANNER).getByRole(AriaRole.LINK, new Locator.GetB |
| TC-CONTACTUS-04 | ContactUsTest.tcContactUs04c_emptySubject | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-546 |
| TC-CONTACTUS-04 | ContactUsTest.tcContactUs04e_noFile | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-546 |
| TC-CONTACTUS-04 | ContactUsTest.tcContactUs04a_emptyName | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-546 |
| TC-INVOICE-06 | InvoiceTest.tcInvoice06_otherUsersInvoice | **NEW** | Not a JSON Object: null |
| TC-INVOICE-05 | InvoiceTest.tcInvoice05_directFetchSameSession | **NEW** | Not a JSON Object: null |
| TC-INVOICE-04 | InvoiceTest.tcInvoice04_continueAfterDownload | **NEW** | Not a JSON Object: null |
| TC-INVOICE-01 | InvoiceTest.tcInvoice01_downloadIsTxt | **NEW** | Not a JSON Object: null |
| TC-INVOICE-02 | InvoiceTest.tcInvoice02_contentHasNameAndTotal | **NEW** | Not a JSON Object: null |
| TC-INVOICE-08 | InvoiceTest.tcInvoice08_forgedAmount | tests/reports/bugs/invoice-2.md | Not a JSON Object: null |
| TC-INVOICE-07 | InvoiceTest.tcInvoice07_noSession | tests/reports/bugs/invoice-1.md | Not a JSON Object: null |
| TC-INVOICE-03 | InvoiceTest.tcInvoice03_twiceIdentical | **NEW** | Not a JSON Object: null |
| TC-LOGIN-10 | LoginTest.tcLogin10_emailCaseAndSpaces | **NEW** | Not a JSON Object: null |
| TC-LOGIN-06 | LoginTest.tcLogin06_sessionPersists | **NEW** | Not a JSON Object: null |
| TC-LOGIN-08 | LoginTest.tcLogin08_recoverAfterFailure | **NEW** | Not a JSON Object: null |
| TC-LOGIN-11 | LoginTest.tcLogin11_loggedInOpensLogin | **NEW** | Not a JSON Object: null |
| TC-LOGIN-07 | LoginTest.tcLogin07_cartKeptAfterLogin | **NEW** | Not a JSON Object: null |
| TC-LOGIN-03 | LoginTest.tcLogin03_wrongPasswordRejected | **NEW** | Not a JSON Object: null |
| TC-LOGIN-02 | LoginTest.tcLogin02_unregisteredEmail | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-546 |
| TC-LOGIN-04 | LoginTest.tcLogin04_validationBlocksSubmit | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-546 |
| TC-LOGIN-09 | LoginTest.tcLogin09_deletedAccount | **NEW** | Not a JSON Object: null |
| TC-LOGIN-05 | LoginTest.tcLogin05_hostileInput | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-546 |
| TC-LOGIN-12 | LoginTest.tcLogin12_apiAgreesWithUi | **NEW** | Not a JSON Object: null |
| TC-LOGIN-01 | LoginTest.tcLogin01_validCredentials | **NEW** | Not a JSON Object: null |
| TC-LOGOUT-07 | LogoutTest.tcLogout07_logoutFromProducts | **NEW** | Not a JSON Object: null |
| TC-LOGOUT-08 | LogoutTest.tcLogout08_logoutWhileLoggedOut | tests/reports/bugs/logout-1.md | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for getByRole(AriaRole.BANNER).getByRole(AriaRole.LINK, new Locator.GetB |
| TC-LOGOUT-01 | LogoutTest.tcLogout01_logoutFromHome | **NEW** | Not a JSON Object: null |
| TC-LOGOUT-03 | LogoutTest.tcLogout03_checkoutAfterLogout | **NEW** | Not a JSON Object: null |
| TC-LOGOUT-05 | LogoutTest.tcLogout05_cartAfterLogout | **NEW** | Not a JSON Object: null |
| TC-LOGOUT-04 | LogoutTest.tcLogout04_backAfterLogout | **NEW** | Not a JSON Object: null |
| TC-LOGOUT-06 | LogoutTest.tcLogout06_loginAgain | **NEW** | Not a JSON Object: null |
| TC-LOGOUT-02 | LogoutTest.tcLogout02_headerLoggedOut | **NEW** | Not a JSON Object: null |
| TC-ORDER-05 | OrderTest.tcOrder05_emptyFields | **NEW** | Not a JSON Object: null |
| TC-ORDER-09 | OrderTest.tcOrder09_paymentEmptyCart | tests/reports/bugs/order-3.md | Not a JSON Object: null |
| TC-ORDER-08 | OrderTest.tcOrder08_paymentLoggedOut | **NEW** | Not a JSON Object: null |
| TC-ORDER-04 | OrderTest.tcOrder04_continueAfterOrder | **NEW** | Not a JSON Object: null |
| TC-ORDER-07 | OrderTest.tcOrder07_pastExpiry | tests/reports/bugs/order-2.md | Not a JSON Object: null |
| TC-ORDER-02 | OrderTest.tcOrder02_loginOrderDelete | **NEW** | Not a JSON Object: null |
| TC-ORDER-03 | OrderTest.tcOrder03_registerDuringCheckout | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-546 |
| TC-ORDER-10 | OrderTest.tcOrder10_reloadAndBack | **NEW** | Not a JSON Object: null |
| TC-ORDER-06 | OrderTest.tcOrder06_nonNumericCard | tests/reports/bugs/order-1.md | Not a JSON Object: null |
| TC-ORDER-01 | OrderTest.tcOrder01_registerThenOrder | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("rec |
| TC-PRODUCTS-10 | ProductsTest.tcProducts10_zeroId | **NEW** | expected: 200 but was: 403 |
| TC-PRODUCTS-01 | ProductsTest.tcProducts01_openFromHeader | **NEW** | Page title expected to be: Automation Exercise Received: Call log: - Locator.expect with timeout 5000ms - waiting for locator(":root") 9 × locator resolved to < |
| TC-PRODUCTS-09 | ProductsTest.tcProducts09_nonexistentId | **NEW** | expected: 200 but was: 403 |
| TC-PRODUCTS-02 | ProductsTest.tcProducts02_viewFirstProduct | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-546 |
| TC-PRODUCTS-05 | ProductsTest.tcProducts05_detailMatchesCard | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-546 |
| TC-PRODUCTS-07 | ProductsTest.tcProducts07_quantityInput | **NEW** | Not a JSON Object: null |
| TC-PRODUCTS-08 | ProductsTest.tcProducts08_everyIdOpens | **NEW** | Not a JSON Object: null |
| TC-PRODUCTS-10 | ProductsTest.tcProducts10_nonNumericId | tests/reports/bugs/products-1.md | expected: 404 but was: 403 |
| TC-PRODUCTS-06 | ProductsTest.tcProducts06_detailLabels | **NEW** | Not a JSON Object: null |
| TC-PRODUCTS-03 | ProductsTest.tcProducts03_gridMatchesApi | **NEW** | Not a JSON Object: null |
| TC-PRODUCTS-04 | ProductsTest.tcProducts04_cardContents | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for locator(".features_items .product-image-wrapper").first().locator(". |
| TC-REGISTER-04 | RegisterTest.tcRegister04_upperCasedExistingEmail | **NEW** | Not a JSON Object: null |
| TC-REGISTER-12 | RegisterTest.tcRegister12_uiSignupDetailsStored | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-546 |
| TC-REGISTER-07 | RegisterTest.tcRegister07_specialNames | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-546 |
| TC-REGISTER-10 | RegisterTest.tcRegister10_cartKeptAfterRegistering | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-546 |
| TC-REGISTER-11 | RegisterTest.tcRegister11_backAfterAccountCreated | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-546 |
| TC-REGISTER-06 | RegisterTest.tcRegister06_requiredFieldsOnSignup | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-546 |
| TC-REGISTER-05 | RegisterTest.tcRegister05_signupFormBlocksInvalidInput | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-546 |
| TC-REGISTER-03 | RegisterTest.tcRegister03_existingEmailShowsError | **NEW** | Not a JSON Object: null |
| TC-REGISTER-08 | RegisterTest.tcRegister08_prefillAndDobBoundaries | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-546 |
| TC-REGISTER-02 | RegisterTest.tcRegister02_deleteAccountThenLoginFails | **NEW** | Not a JSON Object: null |
| TC-REGISTER-09 | RegisterTest.tcRegister09_sessionPersists | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-546 |
| TC-REGISTER-01 | RegisterTest.tcRegister01_fullRegistrationFlow | **NEW** | Locator expected to be visible Call log: - Locator.expect with timeout 5000ms - waiting for getByRole(AriaRole.BANNER).getByRole(AriaRole.LINK, new Locator.GetB |
| TC-REVIEWS-05 | ReviewsTest.tcReviews05_markupNotRendered | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-546 |
| TC-REVIEWS-03 | ReviewsTest.tcReviews03a_nameEmpty | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-546 |
| TC-REVIEWS-03 | ReviewsTest.tcReviews03c_reviewEmpty | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-546 |
| TC-REVIEWS-06 | ReviewsTest.tcReviews06_notDisplayedAfterReload | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-546 |
| TC-REVIEWS-04 | ReviewsTest.tcReviews04c_emailSpace | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-546 |
| TC-REVIEWS-03 | ReviewsTest.tcReviews03b_emailEmpty | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-546 |
| TC-REVIEWS-03 | ReviewsTest.tcReviews03d_allEmpty | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-546 |
| TC-REVIEWS-04 | ReviewsTest.tcReviews04b_emailAt | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-546 |
| TC-REVIEWS-02 | ReviewsTest.tcReviews02_guestSubmits | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-546 |
| TC-REVIEWS-01 | ReviewsTest.tcReviews01_formVisible | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-546 |
| TC-REVIEWS-07 | ReviewsTest.tcReviews07_loggedIn | **NEW** | Not a JSON Object: null |
| TC-REVIEWS-04 | ReviewsTest.tcReviews04a_emailAbc | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-546 |
| TC-SCROLL-04 | ScrollTest.tcScroll04_arrowHidesAfterUse | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-546 |
| TC-SCROLL-01 | ScrollTest.tcScroll01_withArrow | **NEW** | Locator expected to be in viewport Call log: - Locator.expect with timeout 5000ms - waiting for locator("#slider-carousel .item.active").getByRole(AriaRole.HEAD |
| TC-SCROLL-06 | ScrollTest.tcScroll06_arrowOnProducts | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-546 |
| TC-SCROLL-05 | ScrollTest.tcScroll05_carouselSlides | **NEW** | Locator expected to be in viewport Call log: - Locator.expect with timeout 5000ms - waiting for locator("#slider-carousel .item.active").getByRole(AriaRole.HEAD |
| TC-SCROLL-02 | ScrollTest.tcScroll02_withoutArrow | **NEW** | Locator expected to be in viewport Call log: - Locator.expect with timeout 5000ms - waiting for locator("#slider-carousel .item.active").getByRole(AriaRole.HEAD |
| TC-SCROLL-03 | ScrollTest.tcScroll03_arrowAppearsAfterScroll | **NEW** | Locator expected to have count: 1 Received: 0 Call log: - Locator.expect with timeout 5000ms - waiting for locator("#scrollUp") 9 × locator resolved to 0 elemen |
| TC-SEARCH-10 | SearchTest.tcSearch10_apiMissingParam | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-546 |
| TC-SEARCH-09 | SearchTest.tcSearch09_apiMatchesUi | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-546 |
| TC-SEARCH-02 | SearchTest.tcSearch02_resultsRelateToDress | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-546 |
| TC-SEARCH-08 | SearchTest.tcSearch08_reloadKeepsResults | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-546 |
| TC-SEARCH-06 | SearchTest.tcSearch06_emptySearch | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-546 |
| TC-SEARCH-05 | SearchTest.tcSearch05_noMatches | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-546 |
| TC-SEARCH-01 | SearchTest.tcSearch01_searchTshirt | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-546 |
| TC-SEARCH-07 | SearchTest.tcSearch07_ampersand | tests/reports/bugs/search-1.md | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-546 |
| TC-SEARCH-07 | SearchTest.tcSearch07_specialCharacters | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-546 |
| TC-SEARCH-03 | SearchTest.tcSearch03_caseInsensitive | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-546 |
| TC-SEARCH-04 | SearchTest.tcSearch04_partialTermSuperset | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-546 |
| TC-SUBSCRIPTION-01 | SubscriptionTest.tcSubscription01_homeFooter | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-546 |
| TC-SUBSCRIPTION-02 | SubscriptionTest.tcSubscription02_cartFooter | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-546 |
| TC-SUBSCRIPTION-05 | SubscriptionTest.tcSubscription05_twice | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-546 |
| TC-SUBSCRIPTION-06 | SubscriptionTest.tcSubscription06_placementAndInput | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-546 |
| TC-SUBSCRIPTION-04 | SubscriptionTest.tcSubscription04_invalidEmail | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-546 |
| TC-SUBSCRIPTION-03 | SubscriptionTest.tcSubscription03_emptyEmail | **NEW** | Error { message='Timeout 30000ms exceeded. name='TimeoutError stack='TimeoutError: Timeout 30000ms exceeded. at ProgressController.run (/tmp/playwright-java-546 |
