package com.ae.pages.components;

import com.ae.pages.CartPage;
import com.ae.pages.LoginPage;
import com.ae.pages.ProductsPage;
import com.ae.utils.Ads;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
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

    /** Clicks "Products" in the header; the site opens /products. */
    public ProductsPage openProducts() {
        Ads.dismissIfPresent(page);
        productsLink.click();
        // Click handlers on the next page bind at load; wait for it (see CONVENTIONS: Late-bound handlers).
        page.waitForLoadState(LoadState.LOAD);
        return new ProductsPage(page);
    }

    /** Clicks "Cart" in the header; the site opens /view_cart. */
    public CartPage openCart() {
        Ads.dismissIfPresent(page);
        cartLink.click();
        // Click handlers on the next page bind at load; wait for it (see CONVENTIONS: Late-bound handlers).
        page.waitForLoadState(LoadState.LOAD);
        return new CartPage(page);
    }

    /** Clicks "Signup / Login"; the site opens /login. */
    public LoginPage openLogin() {
        Ads.dismissIfPresent(page);
        signupLoginLink.click();
        // Click handlers on the next page bind at load; wait for it (see CONVENTIONS: Late-bound handlers).
        page.waitForLoadState(LoadState.LOAD);
        return new LoginPage(page);
    }

    /** Clicks "Logout"; the site redirects to /login. */
    public LoginPage logout() {
        Ads.dismissIfPresent(page);
        logoutLink.click();
        // Click handlers on the next page bind at load; wait for it (see CONVENTIONS: Late-bound handlers).
        page.waitForLoadState(LoadState.LOAD);
        return new LoginPage(page);
    }
}
