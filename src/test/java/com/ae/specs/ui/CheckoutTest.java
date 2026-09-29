package com.ae.specs.ui;

import com.ae.config.Config;
import com.ae.fixtures.AccountApi;
import com.ae.fixtures.BaseUiTest;
import com.ae.fixtures.TestUser;
import com.ae.fixtures.TestUsers;
import com.ae.pages.CartPage;
import com.ae.pages.CheckoutPage;
import com.ae.pages.LoginPage;
import com.ae.pages.ProductDetailsPage;
import com.ae.pages.ProductsPage;
import com.ae.pages.components.CartModal;
import com.ae.pages.components.GuestCheckoutModal;
import com.ae.pages.components.Header;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

class CheckoutTest extends BaseUiTest {

    private ProductsPage productsPage;
    private CartPage cartPage;
    private CheckoutPage checkoutPage;
    private Header header;

    @BeforeEach
    void initPages() {
        productsPage = new ProductsPage(page);
        cartPage = new CartPage(page);
        checkoutPage = new CheckoutPage(page);
        header = new Header(page);
    }

    private static int money(String text) {
        return Integer.parseInt(text.replaceAll("\\D", ""));
    }

    private static String rs(int amount) {
        return "Rs. " + amount;
    }

    /** Creates the account through the API (asserting the response) and registers it for cleanup. */
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

    /** Opens /products, adds the first product and opens the cart from the modal. Returns the product name. */
    private String addFirstProductAndOpenCart() {
        productsPage.open();
        assertThat(productsPage.firstCard().name()).isVisible();
        String name = productsPage.firstProductName();
        CartModal modal = productsPage.card(name).addToCart();
        assertThat(modal.heading()).isVisible();
        modal.viewCart();
        assertThat(page).hasURL(Config.baseUrl() + "/view_cart");
        assertThat(cartPage.productLink(name)).isVisible();
        return name;
    }

    /** Logged-in user with one product in the cart, on /checkout. The product name is stored in productName[0]. */
    private TestUser loggedInUserOnCheckout(String[] productName) {
        TestUser user = createUser();
        loginByUi(user);
        productName[0] = addFirstProductAndOpenCart();
        cartPage.proceedToCheckout();
        assertThat(page).hasURL(Config.baseUrl() + "/checkout");
        assertThat(checkoutPage.addressDetailsHeading()).isVisible();
        return user;
    }

    /**
     * Address block as the site renders it (verified live): title, "Mr. First Last", company, address1, address2,
     * "city state zipcode", country, mobile. The order is inherent to the block, so it is asserted as a whole list.
     */
    private static String[] addressLines(String blockTitle, TestUser u) {
        return new String[]{
                blockTitle,
                u.title() + ". " + u.firstName() + " " + u.lastName(),
                u.company(),
                u.address1(),
                u.address2(),
                u.city() + " " + u.state() + " " + u.zipcode(),
                u.country(),
                u.mobile()
        };
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-CHECKOUT-01 guest Proceed To Checkout prompts to register or login")
    void tcCheckout01_guestPrompt() {
        addFirstProductAndOpenCart();
        GuestCheckoutModal modal = cartPage.proceedToCheckoutAsGuest();

        assertThat(modal.heading()).isVisible();
        // Observed: an in-page modal titled "Checkout" with this text, a "Register / Login" link and a "Continue On Cart" button.
        assertThat(modal.message()).isVisible();
        assertThat(modal.registerLoginLink()).hasAttribute("href", "/login");
        assertThat(modal.continueOnCartButton()).isVisible();
        // Observed: the URL stays /view_cart.
        assertThat(page).hasURL(Config.baseUrl() + "/view_cart");
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-CHECKOUT-02 guest prompt Register / Login leads to /login and the cart is kept")
    void tcCheckout02_promptLeadsToLoginCartKept() {
        TestUser user = createUser();
        String name = addFirstProductAndOpenCart();
        LoginPage login = cartPage.proceedToCheckoutAsGuest().registerLogin();

        assertThat(page).hasURL(Config.baseUrl() + "/login");
        assertThat(login.signupHeading()).isVisible();
        assertThat(login.heading()).isVisible();

        login.loginAs(user);
        assertThat(header.loggedInAs).containsText("Logged in as " + user.name());

        cartPage.open();
        assertThat(cartPage.productLink(name)).isVisible();
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-CHECKOUT-03 guest prompt Continue On Cart closes it and leaves the cart unchanged")
    void tcCheckout03_continueOnCart() {
        String name = addFirstProductAndOpenCart();
        String price = cartPage.price(name).innerText().trim();
        assertThat(cartPage.quantity(name)).hasText("1");
        assertThat(cartPage.total(name)).hasText(price);

        GuestCheckoutModal modal = cartPage.proceedToCheckoutAsGuest();
        assertThat(modal.heading()).isVisible();
        modal.continueOnCart();

        assertThat(modal.heading()).isHidden();
        assertThat(page).hasURL(Config.baseUrl() + "/view_cart");
        assertThat(cartPage.rows()).hasCount(1);
        assertThat(cartPage.quantity(name)).hasText("1");
        assertThat(cartPage.total(name)).hasText(price);
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-CHECKOUT-04 delivery address matches the registered account")
    void tcCheckout04_deliveryAddress() {
        TestUser user = loggedInUserOnCheckout(new String[1]);

        assertThat(checkoutPage.reviewHeading()).isVisible();
        // Observed layout: "Mr. Auto Tester", company, address1, address2, "city state zipcode", country, mobile.
        assertThat(checkoutPage.deliveryLines()).hasText(addressLines("Your delivery address", user));
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-CHECKOUT-05 billing address matches the registered account and the delivery block")
    void tcCheckout05_billingAddress() {
        TestUser user = loggedInUserOnCheckout(new String[1]);

        // Observed: the billing block has the same title prefix, name and lines as the delivery block.
        assertThat(checkoutPage.billingLines()).hasText(addressLines("Your billing address", user));
        List<String> delivery = checkoutPage.deliveryLineTexts();
        List<String> billing = checkoutPage.billingLineTexts();
        assertThat(billing.subList(1, billing.size())).isEqualTo(delivery.subList(1, delivery.size()));
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-CHECKOUT-06 order review lists each cart row with correct totals")
    void tcCheckout06_reviewRows() {
        TestUser user = createUser();
        loginByUi(user);
        productsPage.open();
        assertThat(productsPage.firstCard().name()).isVisible();
        List<String> names = productsPage.cardNames();
        String a = names.get(0);
        String b = names.get(1);

        ProductDetailsPage detailsA = productsPage.card(a).viewProduct();
        assertThat(detailsA.name()).hasText(a);
        int priceA = money(detailsA.price().innerText());
        detailsA.setQuantity(1);
        CartModal modalA = detailsA.addToCart();
        assertThat(modalA.heading()).isVisible();
        modalA.continueShopping();
        assertThat(modalA.heading()).isHidden();

        productsPage.open();
        assertThat(productsPage.firstCard().name()).isVisible();
        ProductDetailsPage detailsB = productsPage.card(b).viewProduct();
        assertThat(detailsB.name()).hasText(b);
        int priceB = money(detailsB.price().innerText());
        detailsB.setQuantity(3);
        CartModal modalB = detailsB.addToCart();
        assertThat(modalB.heading()).isVisible();
        modalB.viewCart();

        assertThat(cartPage.rows()).hasCount(2);
        assertThat(cartPage.quantity(b)).hasText("3");
        cartPage.proceedToCheckout();
        assertThat(page).hasURL(Config.baseUrl() + "/checkout");

        assertThat(checkoutPage.reviewHeading()).isVisible();
        assertThat(checkoutPage.productRows()).hasCount(2);
        assertThat(checkoutPage.row(a)).hasCount(1);
        assertThat(checkoutPage.price(a)).hasText(rs(priceA));
        assertThat(checkoutPage.quantity(a)).hasText("1");
        assertThat(checkoutPage.total(a)).hasText(rs(priceA));
        assertThat(checkoutPage.row(b)).hasCount(1);
        assertThat(checkoutPage.price(b)).hasText(rs(priceB));
        assertThat(checkoutPage.quantity(b)).hasText("3");
        assertThat(checkoutPage.total(b)).hasText(rs(priceB * 3));
        // Observed: a "Total Amount" row exists and equals the sum of the row totals.
        assertThat(checkoutPage.totalAmountRow()).hasCount(1);
        assertThat(checkoutPage.totalAmount()).hasText(rs(priceA + priceB * 3));
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-CHECKOUT-07 comment textarea accepts text and Place Order is available")
    void tcCheckout07_comment() {
        loggedInUserOnCheckout(new String[1]);
        String comment = "Please leave at the door <b>x</b> " + "long comment ".repeat(40);
        assertThat(comment.length()).isGreaterThan(500);

        assertThat(checkoutPage.commentLabel()).isVisible();
        assertThat(checkoutPage.commentBox()).isVisible();
        // Observed: no placeholder; the only hint is the text above the box.
        assertThat(checkoutPage.commentBox().getAttribute("placeholder")).isNull();
        checkoutPage.fillComment(comment);

        assertThat(checkoutPage.commentBox()).hasValue(comment);
        assertThat(page).hasURL(Config.baseUrl() + "/checkout");
        assertThat(checkoutPage.placeOrderLink()).isVisible();
        assertThat(checkoutPage.placeOrderLink()).isEnabled();
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-CHECKOUT-08 logged-in user opens /checkout with an empty cart")
    void tcCheckout08_emptyCartLoggedIn() {
        TestUser user = createUser();
        loginByUi(user);
        checkoutPage.open();

        // Observed: no redirect; addresses shown, no product rows, Total Amount Rs. 0, Place Order still shown.
        // Product concern (flagged): an empty cart can still reach "Place Order".
        assertThat(page).hasURL(Config.baseUrl() + "/checkout");
        assertThat(checkoutPage.addressDetailsHeading()).isVisible();
        assertThat(checkoutPage.deliveryLines()).hasText(addressLines("Your delivery address", user));
        assertThat(checkoutPage.productRows()).hasCount(0);
        assertThat(checkoutPage.totalAmount()).hasText("Rs. 0");
        assertThat(checkoutPage.placeOrderLink()).isVisible();
        assertThat(header.loggedInAs).containsText("Logged in as " + user.name());
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-CHECKOUT-09 guest opens /checkout directly")
    void tcCheckout09_guestDirect() {
        checkoutPage.open();

        assertThat(page).hasURL(Config.baseUrl() + "/checkout");
        assertThat(checkoutPage.addressDetailsHeading()).isVisible();
        assertThat(checkoutPage.totalAmount()).hasText("Rs. 0");
        assertThat(checkoutPage.placeOrderLink()).isVisible();
        assertThat(header.signupLoginLink).isVisible();
        // Observed: the address blocks render but hold no data; only the name line shows a stray ".".
        assertThat(checkoutPage.deliveryLineTexts())
                .containsExactly("Your delivery address", ".", "", "", "", "", "", "");
        assertThat(checkoutPage.billingLineTexts())
                .containsExactly("Your billing address", ".", "", "", "", "", "", "");
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-CHECKOUT-10 checkout page is stable across reload and Back to the cart")
    void tcCheckout10_reloadAndBack() {
        String[] name = new String[1];
        TestUser user = loggedInUserOnCheckout(name);
        assertThat(checkoutPage.productRows()).hasCount(1);
        assertThat(checkoutPage.reviewHeading()).isVisible();
        List<String> delivery = checkoutPage.deliveryLineTexts();
        List<String> billing = checkoutPage.billingLineTexts();
        List<String> rows = checkoutPage.productRowTexts();

        page.reload();
        assertThat(checkoutPage.deliveryLines()).hasText(addressLines("Your delivery address", user));
        assertThat(checkoutPage.productRows()).hasCount(1);
        assertThat(checkoutPage.deliveryLineTexts()).isEqualTo(delivery);
        assertThat(checkoutPage.billingLineTexts()).isEqualTo(billing);
        assertThat(checkoutPage.productRowTexts()).isEqualTo(rows);

        page.goBack();
        assertThat(page).hasURL(Config.baseUrl() + "/view_cart");
        assertThat(cartPage.rows()).hasCount(1);
        assertThat(cartPage.productLink(name[0])).isVisible();
        assertThat(cartPage.quantity(name[0])).hasText("1");
    }
}
