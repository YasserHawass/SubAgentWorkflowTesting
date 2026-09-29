package com.ae.pages;

import com.ae.config.Config;
import com.ae.pages.components.ProductCard;
import com.ae.utils.Ads;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import java.util.List;
import java.util.stream.Collectors;

/** /products grid. */
public class ProductsPage {
    private final Page page;
    // CSS: the product cards have no role/label/data-qa; .productinfo is the stable card wrapper (last resort).
    private final Locator firstCard;
    private final Locator continueShopping;
    private final Locator heading;
    private final Locator cards;

    public ProductsPage(Page page) {
        this.page = page;
        firstCard = page.locator(".features_items .productinfo").first();
        heading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("All Products"));
        // CSS: .product-image-wrapper is the card root (image, price, name, buttons and View Product link); no role/data-qa (last resort).
        cards = page.locator(".features_items .product-image-wrapper");
        continueShopping = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue Shopping"));
    }

    public ProductsPage open() {
        page.navigate(Config.baseUrl() + "/products");
        Ads.dismissIfPresent(page);
        return this;
    }

    public Locator heading() {
        return heading;
    }

    /** All card roots currently in the grid. */
    public Locator cards() {
        return cards;
    }

    /** Snapshot of the cards in DOM order; each is scoped to its own root, so no index is needed afterwards. */
    public List<ProductCard> allCards() {
        return cards.all().stream().map(c -> new ProductCard(page, c)).collect(Collectors.toList());
    }

    /** The first card in the grid (DOM order). "First" is the definition here, so .first() is the anchor. */
    public ProductCard firstCard() {
        return new ProductCard(page, cards.first());
    }

    /** Names of every card, in DOM order. */
    public List<String> cardNames() {
        return allCards().stream().map(c -> c.name().innerText().trim()).collect(Collectors.toList());
    }

    /** The card whose name is exactly this text (anchored on the name, no index). */
    public ProductCard card(String name) {
        Locator byName = cards.filter(new Locator.FilterOptions().setHas(
                page.locator(".productinfo p").getByText(name, new Locator.GetByTextOptions().setExact(true))));
        return new ProductCard(page, byName);
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

    public Locator searchBox() {
        return page.getByPlaceholder("Search Product");
    }

    public Locator searchButton() {
        // CSS: verified live, the button is an icon-only <button type="button" id="submit_search"> with no accessible name (last resort).
        return page.locator("#submit_search");
    }

    /** Types the term into the search box and clicks the search button. */
    public ProductsPage search(String term) {
        Ads.dismissIfPresent(page);
        searchBox().fill(term);
        searchButton().click();
        return this;
    }

    /** The "Searched Products" heading (DOM text "Searched Products", rendered upper-case by CSS). */
    public Locator searchedHeading() {
        return page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Searched Products"));
    }
}
