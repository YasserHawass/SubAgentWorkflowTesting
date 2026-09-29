package com.ae.pages;

import com.ae.config.Config;
import com.ae.fixtures.TestUser;
import com.ae.utils.Ads;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

/** /login: Login and Signup forms. Only the Login form is modelled here. */
public class LoginPage {
    private final Page page;
    private final Locator heading;
    private final Locator emailInput;
    private final Locator passwordInput;
    private final Locator loginButton;
    private final Locator errorMessage;

    public LoginPage(Page page) {
        this.page = page;
        heading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Login to your account"));
        emailInput = page.locator("[data-qa='login-email']");
        passwordInput = page.locator("[data-qa='login-password']");
        loginButton = page.locator("[data-qa='login-button']");
        errorMessage = page.getByText("Your email or password is incorrect!");
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
        return this;
    }

    public LoginPage loginAs(TestUser user) {
        return submit(user.email(), user.password());
    }

    public Locator heading() { return heading; }
    public Locator emailInput() { return emailInput; }
    public Locator passwordInput() { return passwordInput; }
    public Locator loginButton() { return loginButton; }
    public Locator errorMessage() { return errorMessage; }
}
