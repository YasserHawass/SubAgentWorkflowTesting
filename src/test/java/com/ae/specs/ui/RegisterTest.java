package com.ae.specs.ui;

import com.ae.config.Config;
import com.ae.fixtures.AccountApi;
import com.ae.fixtures.BaseUiTest;
import com.ae.fixtures.TestUser;
import com.ae.fixtures.TestUsers;
import com.ae.pages.CartPage;
import com.ae.pages.LoginPage;
import com.ae.pages.ProductsPage;
import com.ae.pages.SignupPage;
import com.ae.pages.components.Header;
import com.google.gson.JsonObject;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

class RegisterTest extends BaseUiTest {

    private LoginPage loginPage;
    private SignupPage signupPage;
    private Header header;

    @BeforeEach
    void initPages() {
        loginPage = new LoginPage(page);
        signupPage = new SignupPage(page);
        header = new Header(page);
    }

    private TestUser freshUser() {
        TestUser user = TestUsers.newUser();
        registerForCleanup(user);
        return user;
    }

    private static TestUser withEmail(TestUser b, String email) {
        return new TestUser(b.name(), email, b.password(), b.title(), b.birthDay(), b.birthMonth(), b.birthYear(),
                b.firstName(), b.lastName(), b.company(), b.address1(), b.address2(), b.country(), b.state(),
                b.city(), b.zipcode(), b.mobile());
    }

    private static TestUser withName(TestUser b, String name) {
        return new TestUser(name, b.email(), b.password(), b.title(), b.birthDay(), b.birthMonth(), b.birthYear(),
                b.firstName(), b.lastName(), b.company(), b.address1(), b.address2(), b.country(), b.state(),
                b.city(), b.zipcode(), b.mobile());
    }

    private TestUser apiUser() {
        TestUser user = freshUser();
        JsonObject res = AccountApi.create(user);
        assertThat(res.get("responseCode").getAsInt()).isEqualTo(201);
        assertThat(res.get("message").getAsString()).isEqualTo("User created!");
        return user;
    }

    /** /login -> Signup form -> /signup. */
    private void reachSignupForm(TestUser user) {
        loginPage.open().submitSignup(user.name(), user.email());
        assertThat(page).hasURL(Config.baseUrl() + "/signup");
        assertThat(signupPage.heading()).isVisible();
    }

    /** Full UI signup up to and including the /account_created page. */
    private void signUpToAccountCreated(TestUser user) {
        reachSignupForm(user);
        signupPage.fillAll(user).submit();
        assertThat(page).hasURL(Config.baseUrl() + "/account_created");
        assertThat(signupPage.accountCreated()).isVisible();
    }

    private void signUpAndContinue(TestUser user) {
        signUpToAccountCreated(user);
        signupPage.continueAfter();
    }

    private void assertLoggedIn(TestUser user) {
        assertThat(header.loggedInAs).containsText("Logged in as " + user.name());
        assertThat(header.signupLoginLink).not().isVisible();
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-REGISTER-01 full registration flow")
    void tcRegister01_fullRegistrationFlow() {
        TestUser user = freshUser();
        page.navigate("/");
        assertThat(header.signupLoginLink).isVisible();
        header.signupLoginLink.click();
        assertThat(loginPage.signupHeading()).isVisible();
        loginPage.submitSignup(user.name(), user.email());
        assertThat(page).hasURL(Config.baseUrl() + "/signup");
        assertThat(signupPage.heading()).isVisible();
        signupPage.fillAll(user).submit();
        assertThat(page).hasURL(Config.baseUrl() + "/account_created");
        assertThat(signupPage.accountCreated()).isVisible();
        signupPage.continueAfter();
        assertLoggedIn(user);
        assertThat(header.logoutLink).isVisible();
        assertThat(header.deleteAccountLink).isVisible();
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-REGISTER-02 delete account via UI then login fails")
    void tcRegister02_deleteAccountThenLoginFails() {
        TestUser user = apiUser();
        loginPage.open().loginAs(user);
        assertThat(header.deleteAccountLink).isVisible();
        header.deleteAccountLink.click();
        assertThat(page).hasURL(Config.baseUrl() + "/delete_account");
        assertThat(signupPage.accountDeleted()).isVisible();
        signupPage.continueAfter();
        assertThat(page).not().hasURL(Config.baseUrl() + "/delete_account");
        assertThat(header.signupLoginLink).isVisible();
        loginPage.open().loginAs(user);
        assertThat(loginPage.errorMessage()).isVisible();
        assertThat(page).hasURL(Config.baseUrl() + "/login");
        assertThat(header.signupLoginLink).isVisible();
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-REGISTER-03 signup with existing email shows error")
    void tcRegister03_existingEmailShowsError() {
        TestUser user = apiUser();
        loginPage.open().submitSignup("Any Name", user.email());
        assertThat(loginPage.signupError()).isVisible();
        assertThat(page).hasURL(Config.baseUrl() + "/signup");
        assertThat(signupPage.heading()).not().isVisible();
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-REGISTER-04 signup with existing email in upper case reaches account form")
    void tcRegister04_upperCasedExistingEmail() {
        TestUser user = apiUser();
        String upper = user.email().toUpperCase();
        // Clean up the upper-cased variant in case it is ever created.
        registerForCleanup(withEmail(user, upper));
        loginPage.open().submitSignup("Any Name", upper);
        // Observed live: no duplicate error; the /signup form is shown.
        assertThat(page).hasURL(Config.baseUrl() + "/signup");
        assertThat(signupPage.heading()).isVisible();
    }

    private void assertSignupBlocked() {
        assertThat(page).hasURL(Config.baseUrl() + "/login");
        assertThat(signupPage.heading()).not().isVisible();
        assertThat(loginPage.signupError()).not().isVisible();
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-REGISTER-05 signup form blocks empty name, empty email, malformed email")
    void tcRegister05_signupFormBlocksInvalidInput() {
        TestUser user = TestUsers.newUser();
        loginPage.open().submitSignup("", user.email());
        assertSignupBlocked();
        loginPage.open().submitSignup(user.name(), "");
        assertSignupBlocked();
        loginPage.open().submitSignup(user.name(), "notanemail");
        assertSignupBlocked();
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-REGISTER-06 required fields on /signup")
    void tcRegister06_requiredFieldsOnSignup() {
        TestUser user = freshUser();
        // Observed live: these are required (submit is blocked and the page stays on /signup).
        List<Function<SignupPage, Locator>> required = List.of(
                SignupPage::passwordInput, SignupPage::firstName, SignupPage::lastName, SignupPage::address1,
                SignupPage::state, SignupPage::city, SignupPage::zipcode, SignupPage::mobile);
        for (Function<SignupPage, Locator> field : required) {
            reachSignupForm(user);
            signupPage.fillAll(user);
            field.apply(signupPage).fill("");
            signupPage.submit();
            assertThat(page).hasURL(Config.baseUrl() + "/signup");
            assertThat(signupPage.accountCreated()).not().isVisible();
        }
        // Observed live: company and address2 are optional; the account is created.
        reachSignupForm(user);
        signupPage.fillAll(user);
        signupPage.company().fill("");
        signupPage.address2().fill("");
        signupPage.submit();
        assertThat(page).hasURL(Config.baseUrl() + "/account_created");
        assertThat(signupPage.accountCreated()).isVisible();
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-REGISTER-07 special characters, long and HTML names are accepted as typed")
    void tcRegister07_specialNames() {
        List<String> dialogs = new CopyOnWriteArrayList<>();
        page.onDialog(d -> { dialogs.add(d.message()); d.dismiss(); });
        String[] names = {"Zoë O'Neil", "A".repeat(120), "<b>x</b>"};
        for (String name : names) {
            context.clearCookies();
            TestUser user = withName(TestUsers.newUser(), name);
            registerForCleanup(user);
            signUpAndContinue(user);
            // Observed live: all three are accepted and shown as typed (HTML escaped, not rendered).
            assertThat(header.loggedInAs).containsText("Logged in as " + name);
        }
        assertThat(header.loggedInAs.innerHTML()).contains("&lt;b&gt;x&lt;/b&gt;");
        assertThat(dialogs).isEmpty();
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-REGISTER-08 signup prefill and date-of-birth boundaries")
    void tcRegister08_prefillAndDobBoundaries() {
        TestUser user = freshUser();
        reachSignupForm(user);
        // Observed live: name and email are prefilled; email is disabled, name is editable.
        assertThat(signupPage.nameInput()).hasValue(user.name());
        assertThat(signupPage.emailInput()).hasValue(user.email());
        assertThat(signupPage.emailInput()).isDisabled();
        assertThat(signupPage.nameInput()).isEditable();
        // Observed live: the year list runs from 2021 down to 1900.
        for (String inRange : new String[] {"2021", "1900"}) {
            assertThat(signupPage.years().getByRole(AriaRole.OPTION, new Locator.GetByRoleOptions().setName(inRange).setExact(true))).hasCount(1);
        }
        for (String outOfRange : new String[] {"2022", "1899"}) {
            assertThat(signupPage.years().getByRole(AriaRole.OPTION, new Locator.GetByRoleOptions().setName(outOfRange).setExact(true))).hasCount(0);
        }
        // Observed live: invalid dates are not blocked; the account is created.
        signupPage.fillAll(user).selectDob("29", "2", "2019");
        signupPage.submit();
        assertThat(page).hasURL(Config.baseUrl() + "/account_created");

        context.clearCookies();
        TestUser second = freshUser();
        reachSignupForm(second);
        signupPage.fillAll(second).selectDob("31", "4", "2019");
        signupPage.submit();
        assertThat(page).hasURL(Config.baseUrl() + "/account_created");
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-REGISTER-09 session persists after signup and /login redirects home")
    void tcRegister09_sessionPersists() {
        TestUser user = freshUser();
        signUpAndContinue(user);
        assertLoggedIn(user);
        new ProductsPage(page).open();
        assertLoggedIn(user);
        page.reload();
        assertLoggedIn(user);
        page.navigate("/login");
        assertThat(page).hasURL(Config.baseUrl() + "/");
        assertThat(loginPage.signupHeading()).not().isVisible();
        assertLoggedIn(user);
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-REGISTER-10 cart contents are kept after registering")
    void tcRegister10_cartKeptAfterRegistering() {
        TestUser user = freshUser();
        ProductsPage products = new ProductsPage(page).open();
        String product = products.firstProductName();
        products.addFirstProductToCart();
        products.continueShopping();
        signUpAndContinue(user);
        assertLoggedIn(user);
        CartPage cart = new CartPage(page).open();
        // Observed live: the product added while logged out is still in the cart.
        assertThat(cart.productLink(product)).isVisible();
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-REGISTER-11 back navigation after account creation does not create a second account")
    void tcRegister11_backAfterAccountCreated() {
        TestUser user = freshUser();
        signUpToAccountCreated(user);
        page.goBack();
        // Observed live: Back shows the login page with the duplicate-email error, no account form.
        assertThat(loginPage.signupError()).isVisible();
        assertThat(signupPage.heading()).not().isVisible();
        JsonObject res = AccountApi.getByEmail(user.email());
        assertThat(res.get("responseCode").getAsInt()).isEqualTo(200);
        assertThat(res.getAsJsonObject("user").get("email").getAsString()).isEqualTo(user.email());
    }

    @Test
    @Tag("api")
    @Tag("regression")
    @DisplayName("TC-REGISTER-12 details entered in UI signup are stored")
    void tcRegister12_uiSignupDetailsStored() {
        TestUser user = freshUser();
        signUpToAccountCreated(user);
        JsonObject res = AccountApi.getByEmail(user.email());
        assertThat(res.get("responseCode").getAsInt()).isEqualTo(200);
        JsonObject stored = res.getAsJsonObject("user");
        assertThat(stored.get("name").getAsString()).isEqualTo(user.name());
        assertThat(stored.get("email").getAsString()).isEqualTo(user.email());
        assertThat(stored.get("first_name").getAsString()).isEqualTo(user.firstName());
        assertThat(stored.get("last_name").getAsString()).isEqualTo(user.lastName());
        assertThat(stored.get("address1").getAsString()).isEqualTo(user.address1());
        assertThat(stored.get("city").getAsString()).isEqualTo(user.city());
        assertThat(stored.get("state").getAsString()).isEqualTo(user.state());
        assertThat(stored.get("zipcode").getAsString()).isEqualTo(user.zipcode());
        assertThat(stored.get("country").getAsString()).isEqualTo(user.country());
    }
}
