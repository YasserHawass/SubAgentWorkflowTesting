package com.ae.pages;

import com.ae.config.Config;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

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
}
