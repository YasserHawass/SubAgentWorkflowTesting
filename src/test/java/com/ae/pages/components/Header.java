package com.ae.pages.components;

import com.ae.pages.LoginPage;
import com.ae.utils.Ads;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

/** Site header nav, present on every page. Extend here; don't duplicate header locators in page objects. */
public class Header {
    private final Page page;
    private final Locator root;

    public final Locator signupLoginLink;
    public final Locator logoutLink;
    public final Locator deleteAccountLink;
    public final Locator productsLink;
    public final Locator cartLink;
    public final Locator contactUsLink;
    public final Locator loggedInAs;

    public Header(Page page) {
        this.page = page;
        this.root = page.getByRole(AriaRole.BANNER);
        signupLoginLink = root.getByRole(AriaRole.LINK, new Locator.GetByRoleOptions().setName("Signup / Login"));
        logoutLink = root.getByRole(AriaRole.LINK, new Locator.GetByRoleOptions().setName("Logout"));
        deleteAccountLink = root.getByRole(AriaRole.LINK, new Locator.GetByRoleOptions().setName("Delete Account"));
        productsLink = root.getByRole(AriaRole.LINK, new Locator.GetByRoleOptions().setName("Products"));
        cartLink = root.getByRole(AriaRole.LINK, new Locator.GetByRoleOptions().setName("Cart"));
        contactUsLink = root.getByRole(AriaRole.LINK, new Locator.GetByRoleOptions().setName("Contact us"));
        loggedInAs = root.getByText("Logged in as");
    }

    /** Clicks "Logout"; the site redirects to /login. */
    public LoginPage logout() {
        Ads.dismissIfPresent(page);
        logoutLink.click();
        return new LoginPage(page);
    }
}
