package com.ae.pages;

import com.ae.config.Config;
import com.ae.utils.Ads;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

/** /products grid. */
public class ProductsPage {
    private final Page page;
    // CSS: the product cards have no role/label/data-qa; .productinfo is the stable card wrapper (last resort).
    private final Locator firstCard;
    private final Locator continueShopping;

    public ProductsPage(Page page) {
        this.page = page;
        firstCard = page.locator(".features_items .productinfo").first();
        continueShopping = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue Shopping"));
    }

    public ProductsPage open() {
        page.navigate(Config.baseUrl() + "/products");
        Ads.dismissIfPresent(page);
        return this;
    }

    public String firstProductName() {
        // CSS: verified live, card has no data-qa/role for the name; the h2 is the price and the name is the only <p> in .productinfo (last resort).
        return firstCard.locator("p").innerText().trim();
    }

    public void addFirstProductToCart() {
        Ads.dismissIfPresent(page);
        firstCard.getByText("Add to cart").click();
    }

    public void continueShopping() {
        continueShopping.click();
    }
}
