package com.ae.pages.components;

import com.ae.pages.LoginPage;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.AriaRole;

/**
 * The prompt a guest gets from "Proceed To Checkout" on /view_cart. Verified live: it is an in-page modal
 * (title "Checkout", text, a "Register / Login" link to /login and a "Continue On Cart" button); the URL does not change.
 */
public class GuestCheckoutModal {
    private final Locator root;
    private final Locator heading;
    private final Locator message;
    private final Locator registerLoginLink;
    private final Locator continueOnCartButton;
    private final Page page;

    public GuestCheckoutModal(Page page) {
        this.page = page;
        // CSS: verified live, the modal has no role=dialog/data-qa; #checkoutModal is its stable id (last resort).
        root = page.locator("#checkoutModal");
        heading = root.getByRole(AriaRole.HEADING, new Locator.GetByRoleOptions().setName("Checkout"));
        message = root.getByText("Register / Login account to proceed on checkout.");
        registerLoginLink = root.getByRole(AriaRole.LINK, new Locator.GetByRoleOptions().setName("Register / Login"));
        continueOnCartButton = root.getByRole(AriaRole.BUTTON, new Locator.GetByRoleOptions().setName("Continue On Cart"));
    }

    public Locator heading() { return heading; }
    public Locator message() { return message; }
    public Locator registerLoginLink() { return registerLoginLink; }
    public Locator continueOnCartButton() { return continueOnCartButton; }

    /** Clicks "Register / Login"; the site opens /login. */
    public LoginPage registerLogin() {
        registerLoginLink.click();
        // Click handlers on the next page bind at load; wait for it (see CONVENTIONS: Late-bound handlers).
        page.waitForLoadState(LoadState.LOAD);
        return new LoginPage(page);
    }

    /** Clicks "Continue On Cart"; the modal closes and the cart stays as it was. */
    public void continueOnCart() {
        continueOnCartButton.click();
    }
}
