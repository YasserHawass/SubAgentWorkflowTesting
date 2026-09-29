package com.ae.specs.ui;

import com.ae.config.Config;
import com.ae.fixtures.AccountApi;
import com.ae.fixtures.BaseUiTest;
import com.ae.fixtures.TestUser;
import com.ae.fixtures.TestUsers;
import com.ae.pages.CartPage;
import com.ae.pages.HomePage;
import com.ae.pages.LoginPage;
import com.ae.pages.ProductDetailsPage;
import com.ae.pages.ProductsPage;
import com.ae.pages.components.CartModal;
import com.ae.pages.components.Header;
import com.google.gson.JsonObject;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

class CartTest extends BaseUiTest {

    private ProductsPage productsPage;
    private CartPage cartPage;
    private HomePage homePage;
    private Header header;

    @BeforeEach
    void initPages() {
        productsPage = new ProductsPage(page);
        cartPage = new CartPage(page);
        homePage = new HomePage(page);
        header = new Header(page);
    }

    /** "Rs. 500" -> 500. */
    private static int money(String text) {
        return Integer.parseInt(text.replaceAll("\\D", ""));
    }

    private static String rs(int amount) {
        return "Rs. " + amount;
    }

    /** Opens /products, waits for the grid and returns the card names in DOM order. */
    private List<String> openProductsAndListNames() {
        productsPage.open();
        assertThat(productsPage.firstCard().name()).isVisible();
        return productsPage.cardNames();
    }

    /** Adds the named product from the grid and waits for "Added!". Returns the modal. */
    private CartModal addFromGrid(String name) {
        CartModal modal = productsPage.card(name).addToCart();
        assertThat(modal.heading()).isVisible();
        return modal;
    }

    private void addFromGridAndContinue(String name) {
        CartModal modal = addFromGrid(name);
        modal.continueShopping();
        assertThat(modal.heading()).isHidden();
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-CART-01 add two products and view both in the cart")
    void tcCart01_addTwoProducts() {
        page.navigate("/");
        assertThat(page).hasURL(Config.baseUrl() + "/");
        header.openProducts();
        assertThat(page).hasURL(Config.baseUrl() + "/products");
        assertThat(productsPage.firstCard().name()).isVisible();
        List<String> names = productsPage.cardNames();
        String first = names.get(0);
        String second = names.get(1);
        String firstPrice = productsPage.card(first).price().innerText().trim();
        String secondPrice = productsPage.card(second).price().innerText().trim();

        addFromGridAndContinue(first);
        CartModal modal = addFromGrid(second);
        modal.viewCart();

        assertThat(page).hasURL(Config.baseUrl() + "/view_cart");
        assertThat(cartPage.productLink(first)).isVisible();
        assertThat(cartPage.productLink(second)).isVisible();
        assertThat(cartPage.price(first)).hasText(firstPrice);
        assertThat(cartPage.quantity(first)).hasText("1");
        assertThat(cartPage.total(first)).hasText(firstPrice);
        assertThat(cartPage.price(second)).hasText(secondPrice);
        assertThat(cartPage.quantity(second)).hasText("1");
        assertThat(cartPage.total(second)).hasText(secondPrice);
        assertThat(cartPage.checkoutButton()).isVisible();
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-CART-02 View Cart in the modal opens the cart with quantity 1")
    void tcCart02_viewCartFromModal() {
        String name = openProductsAndListNames().get(0);
        addFromGrid(name).viewCart();

        assertThat(page).hasURL(Config.baseUrl() + "/view_cart");
        assertThat(cartPage.productLink(name)).isVisible();
        assertThat(cartPage.quantity(name)).hasText("1");
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-CART-03 Continue Shopping closes the modal and stays on the page")
    void tcCart03_continueShopping() {
        String name = openProductsAndListNames().get(0);
        CartModal modal = addFromGrid(name);
        modal.continueShopping();

        assertThat(modal.heading()).isHidden();
        assertThat(page).hasURL(Config.baseUrl() + "/products");
        // Probe: the header shows no cart count; the Cart link text stays exactly "Cart".
        assertThat(header.cartLink).hasText("Cart");
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-CART-04 quantity 4 on the detail page shows in the cart")
    void tcCart04_quantityFourFromDetailPage() {
        page.navigate("/");
        assertThat(page).hasURL(Config.baseUrl() + "/");
        assertThat(homePage.firstFeaturedCard().name()).isVisible();
        ProductDetailsPage details = homePage.firstFeaturedCard().viewProduct();
        assertThat(page).hasURL(Pattern.compile(".*/product_details/\\d+$"));
        assertThat(details.name()).isVisible();
        String name = details.name().innerText().trim();
        int price = money(details.price().innerText());

        details.setQuantity(4);
        details.addToCart().viewCart();

        assertThat(page).hasURL(Config.baseUrl() + "/view_cart");
        assertThat(cartPage.quantity(name)).hasText("4");
        assertThat(cartPage.total(name)).hasText(rs(price * 4));
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-CART-05 adding the same product twice sums into one row")
    void tcCart05_sameProductTwice() {
        String name = openProductsAndListNames().get(0);
        int price = money(productsPage.card(name).price().innerText());

        addFromGridAndContinue(name);
        addFromGrid(name).viewCart();

        // Observed: one row with quantity 2, not two rows.
        assertThat(cartPage.rows()).hasCount(1);
        assertThat(cartPage.quantity(name)).hasText("2");
        assertThat(cartPage.total(name)).hasText(rs(price * 2));
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-CART-06 remove products from the cart until it is empty")
    void tcCart06_removeProducts() {
        List<String> names = openProductsAndListNames();
        String first = names.get(0);
        String second = names.get(1);
        addFromGridAndContinue(first);
        addFromGridAndContinue(second);

        cartPage.open();
        assertThat(page).hasURL(Config.baseUrl() + "/view_cart");
        assertThat(cartPage.productLink(first)).isVisible();
        assertThat(cartPage.productLink(second)).isVisible();

        cartPage.remove(first);
        assertThat(cartPage.row(first)).hasCount(0);
        assertThat(cartPage.productLink(second)).isVisible();

        cartPage.remove(second);
        assertThat(cartPage.emptyMessage()).isVisible();
        assertThat(cartPage.rows()).hasCount(0);
        // Observed: after removing the last row the button stays in the DOM but is hidden.
        assertThat(cartPage.checkoutButton()).hasCount(1);
        assertThat(cartPage.checkoutButton()).isHidden();
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-CART-07 cart page with nothing added shows the empty state")
    void tcCart07_emptyState() {
        cartPage.open();

        assertThat(cartPage.emptyMessage()).isVisible();
        assertThat(cartPage.emptyState()).hasText("Cart is empty! Click here to buy products.");
        assertThat(cartPage.emptyStateLink()).hasAttribute("href", "/products");
        // Observed: on a fresh empty cart the button is absent from the DOM (not just hidden).
        assertThat(cartPage.checkoutButton()).hasCount(0);
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-CART-08 add a recommended item to the cart")
    void tcCart08_recommendedItem() {
        homePage.open();
        homePage.recommendedHeading().scrollIntoViewIfNeeded();
        assertThat(homePage.recommendedHeading()).isVisible();
        // Observed: the first slide shows 3 cards on load; the carousel then auto-advances (every ~5 s) to the next slide.
        assertThat(homePage.visibleRecommendedCards()).hasCount(3);
        String name = homePage.visibleRecommended().get(0).name().innerText().trim();

        CartModal modal = homePage.recommendedCard(name).addToCart();
        assertThat(modal.heading()).isVisible();
        modal.viewCart();

        assertThat(page).hasURL(Config.baseUrl() + "/view_cart");
        assertThat(cartPage.productLink(name)).isVisible();
    }

    /** Opens product 1's detail page (name and price are read live). */
    private ProductDetailsPage openProductOne() {
        ProductDetailsPage details = new ProductDetailsPage(page).open(1);
        assertThat(details.name()).isVisible();
        return details;
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-CART-09a quantity 0 is accepted and added with quantity 0")
    void tcCart09a_quantityZero() {
        ProductDetailsPage details = openProductOne();
        String name = details.name().innerText().trim();
        details.typeQuantity("0");
        CartModal modal = details.addToCart();
        assertThat(modal.heading()).isVisible();
        modal.viewCart();

        // Observed (product-quality concern): quantity 0 is not rejected; the row shows 0 and Rs. 0.
        assertThat(cartPage.quantity(name)).hasText("0");
        assertThat(cartPage.total(name)).hasText("Rs. 0");
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-CART-09b quantity -1 is rejected; no negative cart totals")
    void tcCart09b_quantityNegative() {
        ProductDetailsPage details = openProductOne();
        details.typeQuantity("-1");
        page.waitForResponse(r -> r.url().contains("/add_to_cart/"), () -> details.addToCartButton().click());
        cartPage.open();

        // Correct behaviour: no cart row has a negative quantity or total (currently fails, see bugs).
        Pattern negative = Pattern.compile("-");
        assertThat(cartPage.rows().locator(".cart_quantity").filter(new Locator.FilterOptions().setHasText(negative))).hasCount(0);
        assertThat(cartPage.rows().locator(".cart_total_price").filter(new Locator.FilterOptions().setHasText(negative))).hasCount(0);
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-CART-09c non-numeric quantity is not added")
    void tcCart09c_quantityNonNumeric() {
        ProductDetailsPage details = openProductOne();
        details.typeQuantity("abc");
        // Observed: the number input keeps no letters, so it is empty.
        assertThat(details.quantityInput()).hasValue("");

        Response response = page.waitForResponse(r -> r.url().contains("/add_to_cart/"),
                () -> details.addToCartButton().click());

        // Observed: the site sends /add_to_cart/<id>?quantity= and answers HTTP 500. A server error is never
        // the expected outcome; the sane behaviour is a client-side rejection or a 4xx. See the report.
        assertThat(response.status()).isLessThan(500);
        assertThat(new CartModal(page).heading()).isHidden();
        cartPage.open();
        assertThat(cartPage.emptyMessage()).isVisible();
        assertThat(cartPage.rows()).hasCount(0);
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-CART-09d quantity 999 is accepted with the full total")
    void tcCart09d_quantity999() {
        ProductDetailsPage details = openProductOne();
        String name = details.name().innerText().trim();
        int price = money(details.price().innerText());
        details.typeQuantity("999");
        CartModal modal = details.addToCart();
        assertThat(modal.heading()).isVisible();
        modal.viewCart();

        assertThat(cartPage.quantity(name)).hasText("999");
        assertThat(cartPage.total(name)).hasText(rs(price * 999));
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-CART-10 guest cart survives login")
    void tcCart10_guestCartSurvivesLogin() {
        TestUser user = TestUsers.newUser();
        registerForCleanup(user);
        JsonObject created = AccountApi.create(user);
        assertThat(created.get("responseCode").getAsInt()).isEqualTo(201);
        assertThat(created.get("message").getAsString()).isEqualTo("User created!");

        String term = openProductsAndListNames().get(0);
        assertThat(page).hasURL(Config.baseUrl() + "/products");
        assertThat(productsPage.heading()).isVisible();
        productsPage.search(term);
        assertThat(page).hasURL(Pattern.compile(".*/products\\?search=.+"));
        assertThat(productsPage.searchedHeading()).isVisible();
        assertThat(productsPage.firstCard().name()).isVisible();
        List<String> results = productsPage.cardNames();
        assertThat(results).contains(term);

        for (String name : results) {
            addFromGridAndContinue(name);
        }

        header.openCart();
        assertThat(page).hasURL(Config.baseUrl() + "/view_cart");
        for (String name : results) {
            assertThat(cartPage.productLink(name)).isVisible();
        }

        LoginPage login = header.openLogin();
        assertThat(login.heading()).isVisible();
        login.loginAs(user);
        assertThat(header.loggedInAs).containsText("Logged in as " + user.name());

        cartPage.open();
        assertThat(page).hasURL(Config.baseUrl() + "/view_cart");
        for (String name : results) {
            assertThat(cartPage.productLink(name)).isVisible();
        }
        assertThat(cartPage.rows()).hasCount(results.size());
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-CART-11 cart contents are stable across reload and Back/Forward")
    void tcCart11_stableAcrossNavigation() {
        String name = openProductsAndListNames().get(0);
        String price = productsPage.card(name).price().innerText().trim();
        addFromGrid(name).viewCart();
        assertThat(page).hasURL(Config.baseUrl() + "/view_cart");
        assertSingleRow(name, price);

        page.reload();
        assertSingleRow(name, price);

        page.navigate("/products");
        assertThat(page).hasURL(Config.baseUrl() + "/products");
        page.goBack();
        assertThat(page).hasURL(Config.baseUrl() + "/view_cart");
        assertSingleRow(name, price);

        page.goForward();
        assertThat(page).hasURL(Config.baseUrl() + "/products");
        header.openCart();
        assertThat(page).hasURL(Config.baseUrl() + "/view_cart");
        assertSingleRow(name, price);
    }

    private void assertSingleRow(String name, String price) {
        assertThat(cartPage.rows()).hasCount(1);
        assertThat(cartPage.productLink(name)).isVisible();
        assertThat(cartPage.quantity(name)).hasText("1");
        assertThat(cartPage.total(name)).hasText(price);
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-CART-12 two products with different quantities have correct row totals")
    void tcCart12_differentQuantities() {
        List<String> names = openProductsAndListNames();
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
        assertThat(cartPage.quantity(a)).hasText("1");
        assertThat(cartPage.total(a)).hasText(rs(priceA));
        assertThat(cartPage.quantity(b)).hasText("3");
        assertThat(cartPage.total(b)).hasText(rs(priceB * 3));
        // Observed: no grand total is shown, only the per-row Total column.
        assertThat(page.getByText(Pattern.compile("grand total|cart total|sub ?total|total amount", Pattern.CASE_INSENSITIVE)))
                .hasCount(0);
    }
}
