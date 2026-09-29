package com.ae.fixtures;

import com.ae.config.Config;
import com.ae.pages.CartPage;
import com.ae.pages.CheckoutPage;
import com.ae.pages.LoginPage;
import com.ae.pages.PaymentDonePage;
import com.ae.pages.PaymentPage;
import com.ae.pages.ProductsPage;
import com.ae.pages.components.CartModal;
import com.ae.pages.components.Header;
import com.ae.utils.Ads;
import com.google.gson.JsonObject;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;

import java.time.Year;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Setup helper: creates an account via the API, logs in through the UI, buys one product with fake card data
 * and ends on /payment_done/&lt;id&gt;. Used by specs whose subject is what happens after the order.
 */
public final class OrderFlow {
    private OrderFlow() {}

    public static final String PRODUCT = "Blue Top";
    private static final Pattern PAYMENT_DONE = Pattern.compile(".*/payment_done/[0-9]+$");

    /** Creates the account through the API and fails fast unless the site answers 201 "User created!". */
    public static void createAccount(TestUser user) {
        JsonObject created = AccountApi.create(user);
        if (created.get("responseCode").getAsInt() != 201 || !"User created!".equals(created.get("message").getAsString())) {
            throw new AssertionError("Account creation failed: " + created);
        }
    }

    /** Logs in through the UI, buys {@link #PRODUCT} and pays; ends on /payment_done/&lt;id&gt;. The account must already exist. */
    public static PaymentDonePage placeOrder(Page page, TestUser user) {
        Header header = new Header(page);
        LoginPage login = new LoginPage(page).open();
        assertThat(login.heading()).isVisible();
        login.loginAs(user);
        assertThat(header.loggedInAs).containsText("Logged in as " + user.name());

        ProductsPage products = new ProductsPage(page).open();
        CartModal modal = products.card(PRODUCT).addToCart();
        assertThat(modal.heading()).isVisible();
        modal.viewCart();
        CartPage cart = new CartPage(page);
        assertThat(page).hasURL(Config.baseUrl() + "/view_cart");
        assertThat(cart.productLink(PRODUCT)).isVisible();

        cart.proceedToCheckout();
        assertThat(page).hasURL(Config.baseUrl() + "/checkout");
        CheckoutPage checkout = new CheckoutPage(page);
        checkout.fillComment("Please leave at the door.");
        Ads.dismissIfPresent(page);
        checkout.placeOrderLink().click();
        assertThat(page).hasURL(Config.baseUrl() + "/payment");

        PaymentPage payment = new PaymentPage(page);
        assertThat(payment.payButton()).isVisible();
        payment.fillCard("Auto Tester", "4111111111111111", "123", "12", String.valueOf(Year.now().getValue() + 2));
        page.waitForResponse(r -> r.request().isNavigationRequest() && r.status() == 200
                        && r.url().matches(".*/payment(_done/[0-9]+)?$"),
                () -> payment.pay());
        page.waitForLoadState(LoadState.LOAD);
        assertThat(page).hasURL(PAYMENT_DONE);
        return new PaymentDonePage(page);
    }
}
