package com.ae.pages.components;

import com.ae.pages.ProductListingPage;
import com.ae.utils.Ads;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** The "Brands" list on /products, home and listing pages. Link text is "(N)Name"; the (N) is the product count. */
public class BrandSidebar {
    private static final Pattern TEXT = Pattern.compile("^\\s*\\((\\d+)\\)\\s*(.+?)\\s*$");

    private final Page page;
    private final Locator links;

    public BrandSidebar(Page page) {
        this.page = page;
        // Every brand link's accessible name starts with its count, e.g. "(6) Polo"; no other link does.
        links = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(Pattern.compile("^\\(\\d+\\)")));
    }

    public Locator heading() {
        return page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Brands").setExact(true));
    }

    public Locator links() {
        return links;
    }

    /** Link for one brand, matched by name. */
    public Locator link(String brand) {
        return page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions()
                .setName(Pattern.compile("^\\(\\d+\\)\\s*" + ProductListingPage.esc(brand) + "\\s*$", Pattern.CASE_INSENSITIVE)));
    }

    /** Raw link texts ("(6)Polo"); textContent because CSS upper-cases innerText. */
    public List<String> linkTexts() {
        return links.allTextContents();
    }

    /** Brand names in sidebar order. */
    public List<String> brandNames() {
        List<String> names = new ArrayList<>();
        for (String t : linkTexts()) {
            Matcher m = TEXT.matcher(t);
            if (m.matches()) names.add(m.group(2));
        }
        return names;
    }

    /** The number shown in the brand's link text. */
    public int countOf(String brand) {
        for (String t : linkTexts()) {
            Matcher m = TEXT.matcher(t);
            if (m.matches() && m.group(2).equals(brand)) return Integer.parseInt(m.group(1));
        }
        throw new IllegalStateException("No sidebar link for brand " + brand);
    }

    public String hrefOf(String brand) {
        return link(brand).getAttribute("href");
    }

    /** Clicks the brand's link. */
    public ProductListingPage open(String brand) {
        Ads.dismissIfPresent(page);
        link(brand).click();
        // This site binds click handlers (add-to-cart, accordion, checkout) in scripts that run at load, so actions
        // right after a click-navigation can be no-ops; wait for load before returning.
        page.waitForLoadState(LoadState.LOAD);
        return new ProductListingPage(page);
    }
}
