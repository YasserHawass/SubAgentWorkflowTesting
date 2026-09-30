package com.ae.pages.components;

import com.ae.pages.CartPage;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.AriaRole;

/** The "Added!" modal shown after "Add to cart" (products grid, detail page, home). */
public class CartModal {
    private final Page page;
    private final Locator heading;
    private final Locator viewCartLink;
    private final Locator continueShoppingButton;

    public CartModal(Page page) {
        this.page = page;
        heading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Added!"));
        // Exact: the header "Cart" link is a different, shorter name; the modal link is "View Cart".
        viewCartLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("View Cart").setExact(true));
        continueShoppingButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue Shopping"));
    }

    /** The "Added!" title. */
    public Locator heading() { return heading; }
    public Locator viewCartLink() { return viewCartLink; }
    public Locator continueShoppingButton() { return continueShoppingButton; }

    /** Clicks "View Cart" in the modal; the site opens /view_cart. */
    public CartPage viewCart() {
        viewCartLink.click();
        // Click handlers on the next page bind at load; wait for it (see CONVENTIONS: Late-bound handlers).
        page.waitForLoadState(LoadState.LOAD);
        return new CartPage(page);
    }

    /** Clicks "Continue Shopping"; the modal closes and the page stays as it was. */
    public void continueShopping() {
        continueShoppingButton.click();
    }
}
