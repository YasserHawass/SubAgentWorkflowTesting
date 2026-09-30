package com.ae.pages;

import com.ae.config.Config;
import com.ae.pages.components.ProductCard;
import com.ae.utils.Ads;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import java.util.List;
import java.util.stream.Collectors;

/** Home page (/): featured products and recommended items. */
public class HomePage {
    private final Page page;

    public HomePage(Page page) {
        this.page = page;
    }

    public HomePage open() {
        page.navigate(Config.baseUrl() + "/");
        Ads.dismissIfPresent(page);
        return this;
    }

    /** Featured product card roots (the grid under "Features Items"). */
    public Locator featuredCards() {
        // CSS: verified live, cards have no role/data-qa; .features_items .product-image-wrapper is the card root (last resort).
        return page.locator(".features_items .product-image-wrapper");
    }

    /** The first featured card in DOM order. "First" is the definition, so .first() is the anchor. */
    public ProductCard firstFeaturedCard() {
        return new ProductCard(page, featuredCards().first());
    }

    public Locator recommendedHeading() {
        // DOM text is lower-case "recommended items"; CSS upper-cases it. Role names match case-insensitively.
        return page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("recommended items"));
    }

    /** Cards on the carousel slide that is currently shown. */
    public Locator visibleRecommendedCards() {
        // CSS: verified live, #recommended-item-carousel has slides `.item`; the shown one carries `.active` (last resort, bootstrap carousel).
        return page.locator("#recommended-item-carousel .item.active .product-image-wrapper");
    }

    /**
     * The recommended card whose name is exactly this text, on any slide. The carousel auto-advances every few seconds,
     * so anchoring on the name (not on ".active") keeps the card stable between reading its name and clicking it.
     */
    public ProductCard recommendedCard(String name) {
        // CSS: verified live, all slides live under #recommended-item-carousel (last resort).
        Locator byName = page.locator("#recommended-item-carousel .product-image-wrapper").filter(
                new Locator.FilterOptions().setHas(page.locator(".productinfo p")
                        .getByText(name, new Locator.GetByTextOptions().setExact(true))));
        return new ProductCard(page, byName);
    }

    public List<ProductCard> visibleRecommended() {
        return visibleRecommendedCards().all().stream().map(c -> new ProductCard(page, c)).collect(Collectors.toList());
    }

    /** The hero heading of the carousel slide that is currently shown. */
    public Locator heroHeading() {
        // CSS: verified live, all 3 slides carry the same h2 text; #slider-carousel .item.active is the shown slide (bootstrap carousel, last resort).
        return page.locator("#slider-carousel .item.active").getByRole(AriaRole.HEADING,
                new Locator.GetByRoleOptions().setName("Full-Fledged practice website for Automation Engineers"));
    }

    /** The first hero slide in DOM order (it is the one shown on load). "First" is the definition, so .first() is the anchor. */
    public Locator firstHeroSlide() {
        // CSS: verified live, slides are .item under #slider-carousel (last resort).
        return page.locator("#slider-carousel .item").first();
    }
}
