package com.ae.pages;

import com.ae.config.Config;
import com.ae.pages.components.BrandSidebar;
import com.ae.pages.components.CategorySidebar;
import com.ae.pages.components.ProductCard;
import com.ae.utils.Ads;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Response;
import com.microsoft.playwright.options.AriaRole;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Filtered listing: /category_products/&lt;id&gt; and /brand_products/&lt;brand&gt;.
 * Heading DOM text is mixed case ("Women - Dress Products", "Brand - Polo Products") and only rendered upper case by CSS.
 */
public class ProductListingPage {
    private final Page page;
    private final Locator heading;
    private final Locator cards;

    public ProductListingPage(Page page) {
        this.page = page;
        // Role heading whose name has the " - ... Products" shape; price headings ("Rs. 500"), "Category" and "Brands" don't match.
        heading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName(Pattern.compile("-.*Products")));
        // CSS: verified live, cards have no role/data-qa; .product-image-wrapper is the card root (last resort).
        cards = page.locator(".features_items .product-image-wrapper");
    }

    /** Navigates to /category_products/&lt;id&gt; and returns the HTTP response (for status checks). */
    public Response openCategory(String id) {
        Response r = page.navigate(Config.baseUrl() + "/category_products/" + id);
        Ads.dismissIfPresent(page);
        return r;
    }

    /** Navigates to /brand_products/&lt;brand&gt; (Playwright encodes spaces) and returns the HTTP response. */
    public Response openBrand(String brand) {
        Response r = page.navigate(Config.baseUrl() + "/brand_products/" + brand);
        Ads.dismissIfPresent(page);
        return r;
    }

    public Locator heading() {
        return heading;
    }

    public Locator cards() {
        return cards;
    }

    public CategorySidebar categories() {
        return new CategorySidebar(page);
    }

    public BrandSidebar brands() {
        return new BrandSidebar(page);
    }

    /** Names of the cards currently listed, in DOM order (call after the heading is visible). */
    public List<String> cardNames() {
        return cards.all().stream()
                .map(c -> new ProductCard(page, c).name().innerText().trim())
                .collect(Collectors.toList());
    }

    /** The card whose name is exactly this text (anchored on the name, no index). */
    public ProductCard card(String name) {
        // CSS: verified live, the name is the only <p> in .productinfo (last resort, same as ProductsPage).
        Locator byName = cards.filter(new Locator.FilterOptions().setHas(
                page.locator(".productinfo p").getByText(name, new Locator.GetByTextOptions().setExact(true))));
        return new ProductCard(page, byName);
    }

    /** Pattern matching the whole heading text ignoring surrounding whitespace, for hasText(). */
    public static Pattern exactText(String text) {
        String[] words = text.trim().split("\\s+");
        StringBuilder re = new StringBuilder("^\\s*");
        for (int i = 0; i < words.length; i++) {
            if (i > 0) re.append("\\s+");
            re.append(ProductListingPage.esc(words[i]));
        }
        return Pattern.compile(re.append("\\s*$").toString());
    }

    /** Escapes regex metacharacters. Pattern.quote() emits \Q..\E, which Playwright's JS regex engine does not understand. */
    public static String esc(String text) {
        return text.replaceAll("[\\\\.*+?^${}()|\\[\\]/-]", "\\\\$0");
    }
}
