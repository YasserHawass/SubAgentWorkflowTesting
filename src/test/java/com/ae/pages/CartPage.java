package com.ae.pages;

import com.ae.config.Config;
import com.ae.pages.components.GuestCheckoutModal;
import com.ae.utils.Ads;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;

/** /view_cart. */
public class CartPage {
    private final Page page;

    public CartPage(Page page) {
        this.page = page;
    }

    public CartPage open() {
        page.navigate(Config.baseUrl() + "/view_cart");
        return this;
    }

    public Locator productLink(String name) {
        // Tier 5: verified live, the table has no role/data-qa hook (role=table absent), #cart_info_table is the only stable id.
        return page.locator("#cart_info_table").getByRole(AriaRole.LINK,
                new Locator.GetByRoleOptions().setName(name).setExact(true));
    }

    public Locator emptyMessage() {
        return page.getByText("Cart is empty!");
    }

    /** The whole empty-state block: "Cart is empty! Click here to buy products." (verified live). */
    public Locator emptyState() {
        // CSS: verified live, the empty-state span has no role/data-qa; #empty_cart is its stable id (last resort).
        return page.locator("#empty_cart");
    }

    /** The "here" link inside the empty-state text; goes to /products. */
    public Locator emptyStateLink() {
        return emptyState().getByRole(AriaRole.LINK, new Locator.GetByRoleOptions().setName("here"));
    }

    /** "Proceed To Checkout" (an <a> without href, so it has no link role). */
    public Locator checkoutButton() {
        return page.getByText("Proceed To Checkout");
    }

    /** Logged-in user: clicks "Proceed To Checkout"; the site opens /checkout. */
    public CheckoutPage proceedToCheckout() {
        // The click handler is bound by an inline script at the end of <body>, so a click before the load event
        // is a no-op. Wait for load (a state, not a sleep) first.
        page.waitForLoadState(LoadState.LOAD);
        Ads.dismissIfPresent(page);
        checkoutButton().click();
        return new CheckoutPage(page);
    }

    /** Guest: clicks "Proceed To Checkout"; the site shows the "Register / Login" prompt and stays on /view_cart. */
    public GuestCheckoutModal proceedToCheckoutAsGuest() {
        // Verified live: the click handler is bound by an inline script at the end of the body, so a click before
        // the load event can do nothing. Wait for load (a state, not a sleep) first.
        page.waitForLoadState(LoadState.LOAD);
        Ads.dismissIfPresent(page);
        checkoutButton().click();
        return new GuestCheckoutModal(page);
    }

    /** Every product row in the table body. */
    public Locator rows() {
        // CSS: verified live, rows are <tr id="product-<id>"> in the #cart_info_table body; no role/data-qa (last resort).
        return page.locator("#cart_info_table tbody tr");
    }

    /** The row of the product with this exact name (anchored on the name link, no index). */
    public Locator row(String name) {
        // The has-locator is resolved relative to each row, so it must not repeat the table scope.
        return rows().filter(new Locator.FilterOptions().setHas(page.getByRole(AriaRole.LINK,
                new Page.GetByRoleOptions().setName(name).setExact(true))));
    }

    public Locator price(String name) {
        // CSS: verified live, cells carry the classes cart_price / cart_quantity / cart_total_price (last resort, no other hook).
        return row(name).locator(".cart_price");
    }

    /** The quantity cell, for example "1" (rendered inside a disabled button). */
    public Locator quantity(String name) {
        return row(name).locator(".cart_quantity");
    }

    public Locator total(String name) {
        return row(name).locator(".cart_total_price");
    }

    /** Clicks the X on this product's row. */
    public void remove(String name) {
        Ads.dismissIfPresent(page);
        // CSS: verified live, the X is <a class="cart_quantity_delete"> with only an icon inside (last resort).
        row(name).locator(".cart_quantity_delete").click();
    }
}
