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

import java.util.ArrayList;
import java.util.List;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

class LoginTest extends BaseUiTest {

    private LoginPage loginPage;
    private Header header;

    @BeforeEach
    void initPages() {
        loginPage = new LoginPage(page);
        header = new Header(page);
    }

    private TestUser registeredUser() {
        TestUser user = TestUsers.newUser();
        registerForCleanup(user);
        JsonObject res = AccountApi.create(user);
        assertThat(res.get("responseCode").getAsInt()).isEqualTo(201);
        assertThat(res.get("message").getAsString()).isEqualTo("User created!");
        return user;
    }

    private void assertNotLoggedIn() {
        assertThat(page).hasURL(Config.baseUrl() + "/login");
        assertThat(header.signupLoginLink).isVisible();
    }

    private void assertLoggedIn(TestUser user) {
        assertThat(header.loggedInAs).containsText("Logged in as " + user.name());
        assertThat(header.signupLoginLink).not().isVisible();
    }

    private static String swapCase(String s) {
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            sb.append(Character.isUpperCase(c) ? Character.toLowerCase(c)
                    : Character.isLowerCase(c) ? Character.toUpperCase(c) : c);
        }
        return sb.toString();
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-LOGIN-01 login with valid credentials")
    void tcLogin01_validCredentials() {
        TestUser user = registeredUser();
        page.navigate("/");
        header.signupLoginLink.click();
        assertThat(loginPage.heading()).isVisible();
        loginPage.loginAs(user);
        assertThat(page).hasURL(Config.baseUrl() + "/");
        assertThat(header.loggedInAs).containsText("Logged in as " + user.name());
        assertThat(header.logoutLink).isVisible();
        assertThat(header.deleteAccountLink).isVisible();
        assertThat(header.signupLoginLink).not().isVisible();
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-LOGIN-02 login with unregistered email shows error")
    void tcLogin02_unregisteredEmail() {
        TestUser user = TestUsers.newUser();
        loginPage.open().submit(user.email(), "AnyPass#1");
        assertThat(loginPage.errorMessage()).isVisible();
        assertNotLoggedIn();
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-LOGIN-03 wrong password is rejected, including different-case password")
    void tcLogin03_wrongPasswordRejected() {
        TestUser user = registeredUser();
        loginPage.open().submit(user.email(), "Wrong#Pass1");
        assertThat(loginPage.errorMessage()).isVisible();
        assertNotLoggedIn();

        String swapped = swapCase(user.password());
        assertThat(swapped).isNotEqualTo(user.password());
        loginPage.open().submit(user.email(), swapped);
        assertThat(loginPage.errorMessage()).isVisible();
        assertNotLoggedIn();
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-LOGIN-04 required and format validation blocks submit")
    void tcLogin04_validationBlocksSubmit() {
        String validEmail = TestUsers.newUser().email();
        String[][] variants = {
                {"", ""},
                {validEmail, ""},
                {"", "SomePass1"},
                {"notanemail", "SomePass1"},
        };
        for (String[] v : variants) {
            loginPage.open().submit(v[0], v[1]);
            assertNotLoggedIn();
            assertThat(loginPage.errorMessage()).not().isVisible();
        }
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-LOGIN-05 hostile and oversized input does not log in or break the page")
    void tcLogin05_hostileInput() {
        List<String> dialogs = new ArrayList<>();
        page.onDialog(d -> { dialogs.add(d.message()); d.dismiss(); });
        String longPad = "a".repeat(300);
        String[][] attempts = {
                {"' OR '1'='1", "' OR '1'='1"},
                {"<script>alert(1)</script>@example.com", "<script>alert(1)</script>"},
                {longPad + "@example.com", longPad},
        };
        for (String[] a : attempts) {
            loginPage.open().submit(a[0], a[1]);
            assertThat(header.signupLoginLink).isVisible();
            assertThat(header.loggedInAs).not().isVisible();
            assertThat(loginPage.emailInput()).isVisible();
            assertThat(loginPage.loginButton()).isVisible();
        }
        assertThat(dialogs).isEmpty();
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-LOGIN-06 session persists across pages and reload")
    void tcLogin06_sessionPersists() {
        TestUser user = registeredUser();
        loginPage.open().loginAs(user);
        assertLoggedIn(user);
        page.navigate("/products");
        assertLoggedIn(user);
        page.reload();
        assertLoggedIn(user);
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-LOGIN-07 cart item added while logged out is kept after login")
    void tcLogin07_cartKeptAfterLogin() {
        TestUser user = registeredUser();
        ProductsPage products = new ProductsPage(page).open();
        String name = products.firstProductName();
        products.addFirstProductToCart();
        products.continueShopping();
        loginPage.open().loginAs(user);
        assertThat(header.loggedInAs).containsText("Logged in as " + user.name());
        CartPage cart = new CartPage(page).open();
        assertThat(cart.productLink(name)).isVisible();
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-LOGIN-08 correct login succeeds after a failed attempt on the same page")
    void tcLogin08_recoverAfterFailure() {
        TestUser user = registeredUser();
        loginPage.open().submit(user.email(), "Wrong#Pass1");
        assertThat(loginPage.errorMessage()).isVisible();
        loginPage.loginAs(user);
        assertThat(page).hasURL(Config.baseUrl() + "/");
        assertThat(header.loggedInAs).containsText("Logged in as " + user.name());
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-LOGIN-09 deleted account cannot log in")
    void tcLogin09_deletedAccount() {
        TestUser user = registeredUser();
        JsonObject res = AccountApi.delete(user);
        assertThat(res.get("responseCode").getAsInt()).isEqualTo(200);
        assertThat(res.get("message").getAsString()).isEqualTo("Account deleted!");
        loginPage.open().loginAs(user);
        assertThat(loginPage.errorMessage()).isVisible();
        assertNotLoggedIn();
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-LOGIN-10 email letter case and surrounding spaces")
    void tcLogin10_emailCaseAndSpaces() {
        TestUser user = registeredUser();
        // Observed on live site: upper-cased email is rejected (case-sensitive match).
        loginPage.open().submit(user.email().toUpperCase(), user.password());
        assertThat(loginPage.errorMessage()).isVisible();
        assertNotLoggedIn();

        // Observed on live site: leading/trailing spaces are trimmed and login succeeds.
        context.clearCookies();
        loginPage.open().submit("  " + user.email() + "  ", user.password());
        assertLoggedIn(user);
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-LOGIN-11 logged-in user opens /login directly")
    void tcLogin11_loggedInOpensLogin() {
        TestUser user = registeredUser();
        loginPage.open().loginAs(user);
        assertThat(header.loggedInAs).containsText("Logged in as " + user.name());
        loginPage.open();
        // Observed: the site redirects a logged-in user from /login to the home page.
        assertThat(page).hasURL(Config.baseUrl() + "/");
        assertThat(header.loggedInAs).containsText("Logged in as " + user.name());
        assertThat(loginPage.heading()).not().isVisible();
    }

    @Test
    @Tag("api")
    @Tag("regression")
    @DisplayName("TC-LOGIN-12 API verifyLogin agrees with UI login for the same credentials")
    void tcLogin12_apiAgreesWithUi() {
        TestUser user = registeredUser();
        JsonObject res = AccountApi.verifyLogin(user.email(), user.password());
        assertThat(res.get("responseCode").getAsInt()).isEqualTo(200);
        assertThat(res.get("message").getAsString()).isEqualTo("User exists!");
        loginPage.open().loginAs(user);
        assertThat(header.loggedInAs).containsText("Logged in as " + user.name());
    }
}
