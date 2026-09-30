package com.ae.pages;

import com.ae.config.Config;
import com.ae.fixtures.TestUser;
import com.ae.utils.Ads;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.AriaRole;

/** /login: Login and Signup forms. Only the Login form is modelled here. */
public class LoginPage {
    private final Page page;
    private final Locator heading;
    private final Locator emailInput;
    private final Locator passwordInput;
    private final Locator loginButton;
    private final Locator errorMessage;
    private final Locator signupHeading;
    private final Locator signupName;
    private final Locator signupEmail;
    private final Locator signupButton;
    private final Locator signupError;

    public LoginPage(Page page) {
        this.page = page;
        heading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Login to your account"));
        emailInput = page.locator("[data-qa='login-email']");
        passwordInput = page.locator("[data-qa='login-password']");
        loginButton = page.locator("[data-qa='login-button']");
        errorMessage = page.getByText("Your email or password is incorrect!");
        signupHeading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("New User Signup!"));
        signupName = page.locator("[data-qa='signup-name']");
        signupEmail = page.locator("[data-qa='signup-email']");
        signupButton = page.locator("[data-qa='signup-button']");
        signupError = page.getByText("Email Address already exist!");
    }

    public LoginPage open() {
        page.navigate(Config.baseUrl() + "/login");
        Ads.dismissIfPresent(page);
        return this;
    }

    public LoginPage submit(String email, String password) {
        Ads.dismissIfPresent(page);
        emailInput.fill(email);
        passwordInput.fill(password);
        loginButton.click();
        // Click handlers on the next page bind at load; wait for it (see CONVENTIONS: Late-bound handlers).
        page.waitForLoadState(LoadState.LOAD);
        return this;
    }

    public LoginPage loginAs(TestUser user) {
        return submit(user.email(), user.password());
    }

    /** Fills and submits the Signup form. */
    public LoginPage submitSignup(String name, String email) {
        Ads.dismissIfPresent(page);
        signupName.fill(name);
        signupEmail.fill(email);
        signupButton.click();
        // Click handlers on the next page bind at load; wait for it (see CONVENTIONS: Late-bound handlers).
        page.waitForLoadState(LoadState.LOAD);
        return this;
    }

    public Locator signupHeading() { return signupHeading; }
    public Locator signupError() { return signupError; }

    public Locator heading() { return heading; }
    public Locator emailInput() { return emailInput; }
    public Locator passwordInput() { return passwordInput; }
    public Locator loginButton() { return loginButton; }
    public Locator errorMessage() { return errorMessage; }
}
