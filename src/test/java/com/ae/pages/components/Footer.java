package com.ae.pages.components;

import com.ae.utils.Ads;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

/** Site footer: the subscription box (present on every page, used on home and cart) and the scroll-up arrow. */
public class Footer {
    private final Page page;
    private final Locator root;

    public Footer(Page page) {
        this.page = page;
        this.root = page.getByRole(AriaRole.CONTENTINFO);
    }

    /** Scrolls the footer into view (the bottom of the page). */
    public void scrollIntoView() {
        root.scrollIntoViewIfNeeded();
    }

    /** The "Subscription" heading (CSS upper-cases it; role names match case-insensitively). */
    public Locator subscriptionHeading() {
        return root.getByRole(AriaRole.HEADING, new Locator.GetByRoleOptions().setName("Subscription"));
    }

    public Locator emailInput() {
        return root.getByPlaceholder("Your email address");
    }

    /** The arrow button. */
    public Locator subscribeButton() {
        // CSS: verified live, the button is icon-only with no accessible name; #subscribe is its stable id (last resort).
        return root.locator("#subscribe");
    }

    /** "You have been successfully subscribed!", injected by ajax and hidden again after a few seconds. */
    public Locator successMessage() {
        return root.getByText("You have been successfully subscribed!");
    }

    /** Scrolls to the footer, types the email and clicks the arrow. */
    public void subscribe(String email) {
        scrollIntoView();
        emailInput().fill(email);
        Ads.dismissIfPresent(page);
        subscribeButton().click();
    }

    /** The scroll-up arrow: in the DOM but hidden at the top, shown after scrolling down. */
    public Locator scrollUpArrow() {
        // CSS: verified live, the jquery.scrollUp arrow is <a id="scrollUp"> with only an icon inside; no role name or data-qa (last resort).
        return page.locator("#scrollUp");
    }

    public void clickScrollUp() {
        scrollUpArrow().click();
    }
}
