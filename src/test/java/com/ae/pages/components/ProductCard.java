package com.ae.pages.components;

import com.ae.pages.ProductDetailsPage;
import com.ae.utils.Ads;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

/**
 * One product card in a product grid (/products, search results, category and brand pages).
 * Wraps the card root; all locators are scoped to it. The "productinfo" block is the visible face of the card;
 * the hover overlay repeats name/price/Add to cart, so everything is scoped to .productinfo to avoid duplicates.
 */
public class ProductCard {
    private final Page page;
    private final Locator root;
    private final Locator info;

    public ProductCard(Page page, Locator root) {
        this.page = page;
        this.root = root;
        // CSS: card face has no role/label/data-qa; .productinfo is the stable wrapper (last resort).
        this.info = root.locator(".productinfo");
    }

    public Locator image() {
        return info.getByRole(AriaRole.IMG);
    }

    public Locator price() {
        // CSS: verified live, the h2 in .productinfo is the price, not the name (last resort).
        return info.locator("h2");
    }

    public Locator name() {
        // CSS: verified live, the name is the only <p> in .productinfo; no data-qa/role (last resort).
        return info.locator("p");
    }

    public Locator addToCartButton() {
        // getByText: the button is an <a> without href, so it has no link/button role.
        return info.getByText("Add to cart");
    }

    public Locator viewProductLink() {
        return root.getByRole(AriaRole.LINK, new Locator.GetByRoleOptions().setName("View Product"));
    }

    /** Clicks "Add to cart" and returns the "Added!" modal. */
    public CartModal addToCart() {
        Ads.dismissIfPresent(page);
        addToCartButton().click();
        return new CartModal(page);
    }

    /** Clicks "View Product" and returns the detail page. */
    public ProductDetailsPage viewProduct() {
        Ads.dismissIfPresent(page);
        viewProductLink().click();
        return new ProductDetailsPage(page);
    }
}
