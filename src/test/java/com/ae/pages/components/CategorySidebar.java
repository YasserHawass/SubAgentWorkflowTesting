package com.ae.pages.components;

import com.ae.pages.ProductListingPage;
import com.ae.utils.Ads;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/** The "Category" accordion (Women / Men / Kids) on home, /products and the listing pages. */
public class CategorySidebar {
    private final Page page;

    public CategorySidebar(Page page) {
        this.page = page;
    }

    public Locator heading() {
        return page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Category").setExact(true));
    }

    /**
     * The accordion toggle, e.g. "Women". Verified live: its accessible name is the Font Awesome glyph U+F067 plus
     * the text, and CSS upper-cases the rendering, so allow the glyph and match case-insensitively.
     */
    public Locator group(String name) {
        return page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions()
                .setName(Pattern.compile("^[\\s]*" + ProductListingPage.esc(name) + "\\s*$", Pattern.CASE_INSENSITIVE)));
    }

    /** The collapsible panel of a group; visible only while expanded. */
    public Locator panel(String group) {
        // CSS: verified live, the panel is <div id="Women" class="panel-collapse collapse">; its id is the only hook (last resort).
        return page.locator("#" + group);
    }

    /** The subcategory link inside a group's panel, matched by name. */
    public Locator subcategory(String group, String name) {
        return panel(group).getByRole(AriaRole.LINK, new Locator.GetByRoleOptions()
                .setName(Pattern.compile("^\\s*" + ProductListingPage.esc(name) + "\\s*$", Pattern.CASE_INSENSITIVE)));
    }

    /** Clicks the group toggle. Does not wait for the panel; see {@link #expand}. */
    public void toggle(String group) {
        // The accordion handlers are bound by scripts at the end of <body>; a click before load can be a no-op.
        page.waitForLoadState(LoadState.LOAD);
        Ads.dismissIfPresent(page);
        group(group).click();
    }

    /** Expands the group and waits until its panel is visible (the accordion animates and collapses the others). */
    public void expand(String group) {
        toggle(group);
        assertThat(panel(group)).isVisible();
    }

    /** Subcategory names of an expanded group, trimmed, in sidebar order. */
    public List<String> subcategoryNames(String group) {
        return panel(group).getByRole(AriaRole.LINK).allTextContents().stream()
                .map(String::trim).collect(Collectors.toList());
    }

    /** href attributes of an expanded group's subcategory links, in sidebar order. */
    public List<String> subcategoryHrefs(String group) {
        return panel(group).getByRole(AriaRole.LINK).all().stream()
                .map(l -> l.getAttribute("href")).collect(Collectors.toList());
    }

    /** Expands the group if collapsed, waits for the subcategory link to be visible, clicks it. */
    public ProductListingPage open(String group, String subcategory) {
        if (!panel(group).isVisible()) {
            expand(group);
        }
        Locator link = subcategory(group, subcategory);
        assertThat(link).isVisible();
        Ads.dismissIfPresent(page);
        link.click();
        // This site binds click handlers (add-to-cart, accordion, checkout) in scripts that run at load, so actions
        // right after a click-navigation can be no-ops; wait for load before returning.
        page.waitForLoadState(LoadState.LOAD);
        return new ProductListingPage(page);
    }
}
