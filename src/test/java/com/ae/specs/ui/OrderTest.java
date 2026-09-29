package com.ae.specs.ui;

import com.ae.config.Config;
import com.ae.fixtures.AccountApi;
import com.ae.fixtures.BaseUiTest;
import com.ae.fixtures.TestUser;
import com.ae.fixtures.TestUsers;
import com.ae.pages.CartPage;
import com.ae.pages.CheckoutPage;
import com.ae.pages.HomePage;
import com.ae.pages.LoginPage;
import com.ae.pages.PaymentDonePage;
import com.ae.pages.PaymentPage;
import com.ae.pages.ProductsPage;
import com.ae.pages.SignupPage;
import com.ae.pages.components.CartModal;
import com.ae.pages.components.GuestCheckoutModal;
import com.ae.pages.components.Header;
import com.ae.utils.Ads;
import com.google.gson.JsonObject;
import com.microsoft.playwright.options.LoadState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.Year;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Order flow: /view_cart -> /checkout -> /payment -> /payment_done/&lt;id&gt;.
 * Observed live: the id in /payment_done/&lt;id&gt; is the cart total in Rs. (500 for Blue Top, 0 for an empty cart), so it is matched by pattern.
 */
class OrderTest extends BaseUiTest {

    /** Product 1 on the site. */
    private static final String PRODUCT = "Blue Top";
    private static final Pattern PAYMENT_DONE = Pattern.compile(".*/payment_done/\\d+$");
    private static final String CONFIRMATION = "Congratulations! Your order has been confirmed!";
    private static final String CARD_NAME = "Auto Tester";
    private static final String CARD_NUMBER = "4111111111111111";
    private static final String CARD_CVC = "123";
    private static final String CARD_MONTH = "12";

    private ProductsPage productsPage;
    private CartPage cartPage;
    private CheckoutPage checkoutPage;
    private PaymentPage paymentPage;
    private PaymentDonePage donePage;
    private Header header;

    @BeforeEach
    void initPages() {
        productsPage = new ProductsPage(page);
        cartPage = new CartPage(page);
        checkoutPage = new CheckoutPage(page);
        paymentPage = new PaymentPage(page);
        donePage = new PaymentDonePage(page);
        header = new Header(page);
    }

    private static String futureYear() {
        return String.valueOf(Year.now().getValue() + 2);
    }

    /** Creates the account through the API (asserting the response) and registers it for cleanup (a 404 at cleanup is tolerated by BaseUiTest). */
    private TestUser createUser() {
        TestUser user = TestUsers.newUser();
        registerForCleanup(user);
        JsonObject created = AccountApi.create(user);
        assertThat(created.get("responseCode").getAsInt()).isEqualTo(201);
        assertThat(created.get("message").getAsString()).isEqualTo("User created!");
        return user;
    }

    private void loginByUi(TestUser user) {
        LoginPage login = new LoginPage(page).open();
        assertThat(login.heading()).isVisible();
        login.loginAs(user);
        assertThat(header.loggedInAs).containsText("Logged in as " + user.name());
    }

    /** Opens /products, adds product 1 and opens the cart from the modal. */
    private void addProductAndOpenCart() {
        productsPage.open();
        CartModal modal = productsPage.card(PRODUCT).addToCart();
        assertThat(modal.heading()).isVisible();
        modal.viewCart();
        assertThat(page).hasURL(Config.baseUrl() + "/view_cart");
        assertThat(cartPage.productLink(PRODUCT)).isVisible();
    }

    /** From /view_cart: Proceed To Checkout, comment, Place Order; ends on /payment. */
    private void checkoutToPayment() {
        cartPage.proceedToCheckout();
        assertThat(page).hasURL(Config.baseUrl() + "/checkout");
        checkoutPage.fillComment("Please leave at the door.");
        Ads.dismissIfPresent(page);
        checkoutPage.placeOrderLink().click();
        assertThat(page).hasURL(Config.baseUrl() + "/payment");
        assertThat(paymentPage.payButton()).isVisible();
    }

    /** Logged-in user (API account) with product 1 in the cart, on /payment. */
    private TestUser loggedInUserOnPayment() {
        TestUser user = createUser();
        loginByUi(user);
        addProductAndOpenCart();
        checkoutToPayment();
        return user;
    }

    private void fillValidCard() {
        paymentPage.fillCard(CARD_NAME, CARD_NUMBER, CARD_CVC, CARD_MONTH, futureYear());
    }

    /**
     * Clicks pay and waits for the first navigation response the site returns (200 on /payment or /payment_done/N,
     * after the POST's redirect), so a following URL check is not made before the site has answered.
     */
    private void payAndSettle() {
        page.waitForResponse(r -> r.request().isNavigationRequest() && r.status() == 200
                        && r.url().matches(".*/payment(_done/\\d+)?$"),
                () -> paymentPage.pay());
        page.waitForLoadState(LoadState.LOAD);
    }

    private void assertOrderConfirmed() {
        assertThat(page).hasURL(PAYMENT_DONE);
        assertThat(donePage.heading()).hasText("Order Placed!");
        assertThat(donePage.message()).hasText(CONFIRMATION);
        assertThat(donePage.downloadInvoiceLink()).isVisible();
        assertThat(donePage.continueButton()).isVisible();
    }

    /** Submits the Signup form on /login, fills the account form, presses Continue. Ends logged in on the home page. */
    private void signUpAndContinue(LoginPage login, TestUser user) {
        assertThat(login.signupHeading()).isVisible();
        login.submitSignup(user.name(), user.email());
        SignupPage signup = new SignupPage(page);
        assertThat(signup.heading()).isVisible();
        signup.fillAll(user).submit();
        assertThat(signup.accountCreated()).isVisible();
        signup.continueAfter();
        assertThat(header.loggedInAs).containsText("Logged in as " + user.name());
    }

    private void assertCartEmpty() {
        cartPage.open();
        assertThat(cartPage.emptyState()).containsText("Cart is empty! Click here to buy products.");
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-ORDER-01 register before checkout, then place an order")
    void tcOrder01_registerThenOrder() {
        TestUser user = TestUsers.newUser();
        registerForCleanup(user);

        HomePage home = new HomePage(page).open();
        assertThat(page).hasURL(Config.baseUrl() + "/");
        assertThat(home.recommendedHeading()).isVisible();
        signUpAndContinue(header.openLogin(), user);

        addProductAndOpenCart();
        checkoutToPayment();
        fillValidCard();
        payAndSettle();

        assertOrderConfirmed();
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-ORDER-02 log in before checkout, place an order, then delete the account")
    void tcOrder02_loginOrderDelete() {
        TestUser user = createUser();
        new HomePage(page).open();
        loginByUi(user);
        addProductAndOpenCart();
        checkoutToPayment();
        fillValidCard();
        payAndSettle();
        assertOrderConfirmed();

        Ads.dismissIfPresent(page);
        header.deleteAccountLink.click();
        SignupPage deleted = new SignupPage(page);
        assertThat(page).hasURL(Config.baseUrl() + "/delete_account");
        assertThat(deleted.accountDeleted()).isVisible();
        // Observed: data-qa account-deleted holds "Account Deleted!" (rendered upper-case by CSS).
        assertThat(deleted.accountDeleted()).hasText("Account Deleted!");

        deleted.continueAfter();
        // Observed: Continue leads to the home page, logged out.
        assertThat(page).hasURL(Config.baseUrl() + "/");
        assertThat(header.signupLoginLink).isVisible();
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-ORDER-03 register during checkout, then place an order and delete the account")
    void tcOrder03_registerDuringCheckout() {
        TestUser user = TestUsers.newUser();
        registerForCleanup(user);

        new HomePage(page).open();
        addProductAndOpenCart();
        // Observed: a guest gets the in-page "Register / Login" modal (see TC-CHECKOUT-01).
        GuestCheckoutModal modal = cartPage.proceedToCheckoutAsGuest();
        assertThat(modal.registerLoginLink()).isVisible();
        signUpAndContinue(modal.registerLogin(), user);

        // The cart survives registration.
        header.openCart();
        assertThat(page).hasURL(Config.baseUrl() + "/view_cart");
        assertThat(cartPage.productLink(PRODUCT)).isVisible();
        checkoutToPayment();
        fillValidCard();
        payAndSettle();
        assertOrderConfirmed();

        Ads.dismissIfPresent(page);
        header.deleteAccountLink.click();
        assertThat(page).hasURL(Config.baseUrl() + "/delete_account");
        assertThat(new SignupPage(page).accountDeleted()).hasText("Account Deleted!");
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-ORDER-04 Continue after an order returns to the site and the cart is empty")
    void tcOrder04_continueAfterOrder() {
        TestUser user = loggedInUserOnPayment();
        fillValidCard();
        payAndSettle();
        assertOrderConfirmed();

        donePage.continueToHome();
        // Observed: Continue links to "/".
        assertThat(page).hasURL(Config.baseUrl() + "/");
        assertCartEmpty();
        assertThat(header.loggedInAs).containsText("Logged in as " + user.name());
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-ORDER-05 submit /payment with all fields empty")
    void tcOrder05_emptyFields() {
        loggedInUserOnPayment();
        paymentPage.pay();

        // Observed: HTML5 "required" blocks the submit; the page stays on /payment and no request is made.
        assertThat(page).hasURL(Config.baseUrl() + "/payment");
        assertThat(paymentPage.nameOnCard()).isVisible();
        assertThat((Boolean) paymentPage.nameOnCard().evaluate("el => el.validity.valueMissing")).isTrue();
        assertThat(page).not().hasURL(PAYMENT_DONE);
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-ORDER-06 non-numeric card number and CVC")
    void tcOrder06_nonNumericCard() {
        loggedInUserOnPayment();
        paymentPage.fillCard(CARD_NAME, "abcd", "xyz", CARD_MONTH, futureYear());
        payAndSettle();

        // Sane behaviour: rejected. PRODUCT BUG CANDIDATE: the live site accepts it and goes to /payment_done/<id>.
        assertThat(page).hasURL(Config.baseUrl() + "/payment");
        assertThat(page).not().hasURL(PAYMENT_DONE);
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-ORDER-07 expiry date in the past")
    void tcOrder07_pastExpiry() {
        loggedInUserOnPayment();
        paymentPage.fillCard(CARD_NAME, CARD_NUMBER, CARD_CVC, "01", "2000");
        payAndSettle();

        // Sane behaviour: rejected. PRODUCT BUG CANDIDATE: the live site accepts it and goes to /payment_done/<id>.
        assertThat(page).hasURL(Config.baseUrl() + "/payment");
        assertThat(page).not().hasURL(PAYMENT_DONE);
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-ORDER-08 open /payment directly while logged out with an empty cart")
    void tcOrder08_paymentLoggedOut() {
        // The user only keeps the "every case has an account" rule; the browser stays logged out.
        createUser();
        paymentPage.open();

        // Observed: no redirect and no guard; the empty card form renders with a logged-out header (like /checkout).
        // FLAG for triage: a payment form is offered to a logged-out user with an empty cart.
        assertThat(page).hasURL(Config.baseUrl() + "/payment");
        assertThat(header.signupLoginLink).isVisible();
        assertThat(paymentPage.payButton()).isVisible();
        assertThat(page).not().hasURL(PAYMENT_DONE);
        assertThat(donePage.message()).hasCount(0);
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-ORDER-09 open /payment directly while logged in with an empty cart, then pay")
    void tcOrder09_paymentEmptyCart() {
        TestUser user = createUser();
        loginByUi(user);
        paymentPage.open();
        assertThat(paymentPage.payButton()).isVisible();
        fillValidCard();
        payAndSettle();

        // Sane behaviour: rejected. PRODUCT BUG CANDIDATE: the live site creates an order with nothing in it (/payment_done/0).
        assertThat(page).not().hasURL(PAYMENT_DONE);
        assertThat(donePage.message()).hasCount(0);
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-ORDER-10 reload /payment_done and go Back after ordering")
    void tcOrder10_reloadAndBack() {
        loggedInUserOnPayment();
        fillValidCard();
        payAndSettle();
        assertOrderConfirmed();
        String doneUrl = page.url();

        page.reload();
        // Observed: the confirmation is shown again at the same URL.
        assertThat(page).hasURL(doneUrl);
        assertThat(donePage.message()).hasText(CONFIRMATION);

        page.goBack();
        // Observed: Back shows the empty /payment form again (no server error).
        assertThat(page).hasURL(Config.baseUrl() + "/payment");
        assertThat(paymentPage.payButton()).isVisible();

        page.goForward();
        assertThat(page).hasURL(doneUrl);
        assertThat(donePage.message()).hasText(CONFIRMATION);

        assertCartEmpty();
    }
}
