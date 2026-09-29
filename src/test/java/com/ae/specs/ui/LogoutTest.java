package com.ae.specs.ui;

import com.ae.config.Config;
import com.ae.fixtures.AccountApi;
import com.ae.fixtures.BaseUiTest;
import com.ae.fixtures.TestUser;
import com.ae.fixtures.TestUsers;
import com.ae.pages.CartPage;
import com.ae.pages.LoginPage;
import com.ae.pages.ProductsPage;
import com.ae.pages.components.Header;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

class LogoutTest extends BaseUiTest {

    private LoginPage loginPage;
    private Header header;

    @BeforeEach
    void initPages() {
        loginPage = new LoginPage(page);
        header = new Header(page);
    }

    /** Creates a user via API and logs in through the UI from home (site TC4 steps 1-8). */
    private TestUser loggedInUser() {
        TestUser user = TestUsers.newUser();
        registerForCleanup(user);
        JsonObject res = AccountApi.create(user);
        assertThat(res.get("responseCode").getAsInt()).isEqualTo(201);
        assertThat(res.get("message").getAsString()).isEqualTo("User created!");
        page.navigate("/");
        header.signupLoginLink.click();
        assertThat(loginPage.heading()).isVisible();
        loginPage.loginAs(user);
        assertThat(header.loggedInAs).containsText("Logged in as " + user.name());
        return user;
    }

    private void assertLoggedOutHeader() {
        assertThat(header.signupLoginLink).isVisible();
        assertThat(header.loggedInAs).not().isVisible();
        assertThat(header.logoutLink).not().isVisible();
        assertThat(header.deleteAccountLink).not().isVisible();
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-LOGOUT-01 logout from home ends on the login page")
    void tcLogout01_logoutFromHome() {
        loggedInUser();
        header.logout();
        assertThat(page).hasURL(Config.baseUrl() + "/login");
        assertThat(loginPage.heading()).isVisible();
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-LOGOUT-02 header returns to logged-out state after logout")
    void tcLogout02_headerLoggedOut() {
        loggedInUser();
        header.logout();
        assertLoggedOutHeader();
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-LOGOUT-03 checkout is not accessible after logout")
    void tcLogout03_checkoutAfterLogout() {
        loggedInUser();
        header.logout();
        page.navigate("/checkout");
        // Observed on live site: no redirect; /checkout renders the (empty) checkout page to a logged-out visitor.
        assertThat(page).hasURL(Config.baseUrl() + "/checkout");
        assertThat(page.getByText("Address Details")).isVisible();
        assertLoggedOutHeader();
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-LOGOUT-04 browser Back after logout does not restore the session")
    void tcLogout04_backAfterLogout() {
        loggedInUser();
        header.logout();
        assertThat(page).hasURL(Config.baseUrl() + "/login");
        page.goBack();
        // Observed on live site: Back lands on the home page with a logged-out header.
        assertThat(page).hasURL(Config.baseUrl() + "/");
        assertLoggedOutHeader();
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-LOGOUT-05 cart is empty after logout")
    void tcLogout05_cartAfterLogout() {
        loggedInUser();
        ProductsPage products = new ProductsPage(page).open();
        products.addFirstProductToCart();
        products.continueShopping();
        header.logout();
        CartPage cart = new CartPage(page).open();
        // Observed on live site: the cart is not kept for the logged-out visitor.
        assertThat(cart.emptyMessage()).isVisible();
        assertThat(header.signupLoginLink).isVisible();
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-LOGOUT-06 log in again with the same credentials after logout")
    void tcLogout06_loginAgain() {
        TestUser user = loggedInUser();
        header.logout();
        assertThat(page).hasURL(Config.baseUrl() + "/login");
        loginPage.loginAs(user);
        assertThat(header.loggedInAs).containsText("Logged in as " + user.name());
        assertThat(header.logoutLink).isVisible();
        assertThat(header.deleteAccountLink).isVisible();
        assertThat(header.signupLoginLink).not().isVisible();
        assertThat(loginPage.heading()).not().isVisible();
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-LOGOUT-07 logout from a non-home page, then reload")
    void tcLogout07_logoutFromProducts() {
        loggedInUser();
        new ProductsPage(page).open();
        header.logout();
        // Observed on live site: logging out from /products also lands on /login.
        assertThat(page).hasURL(Config.baseUrl() + "/login");
        page.reload();
        assertThat(header.signupLoginLink).isVisible();
        assertThat(header.loggedInAs).not().isVisible();
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-LOGOUT-08 direct GET of /logout while logged out")
    void tcLogout08_logoutWhileLoggedOut() {
        page.navigate("/logout");
        assertThat(page.title()).doesNotContain("KeyError");
        assertThat(header.signupLoginLink).isVisible();
    }
}
