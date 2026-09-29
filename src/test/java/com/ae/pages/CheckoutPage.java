package com.ae.pages;

import com.ae.config.Config;
import com.ae.utils.Ads;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import java.util.List;
import java.util.stream.Collectors;

/**
 * /checkout: delivery and billing address blocks, "Review Your Order" table, comment box, "Place Order".
 * Shared by the checkout, order and invoice specs. "Place Order" is exposed as a locator only; what it leads to
 * (/payment) belongs to the order writer's page object.
 */
public class CheckoutPage {
    private final Page page;
    private final Locator deliveryBlock;
    private final Locator billingBlock;
    private final Locator reviewRows;

    public CheckoutPage(Page page) {
        this.page = page;
        // CSS: verified live, both address lists share classes and have no role name/data-qa; these ids are the stable hooks (last resort).
        deliveryBlock = page.locator("#address_delivery");
        billingBlock = page.locator("#address_invoice");
        // CSS: verified live, the review table has no role name/data-qa; #cart_info is the wrapper div (last resort).
        reviewRows = page.locator("#cart_info tbody tr");
    }

    public CheckoutPage open() {
        page.navigate(Config.baseUrl() + "/checkout");
        Ads.dismissIfPresent(page);
        return this;
    }

    public Locator addressDetailsHeading() {
        return page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Address Details").setExact(true));
    }

    public Locator reviewHeading() {
        return page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Review Your Order"));
    }

    /**
     * The delivery address block's lines in DOM order: title ("Your delivery address"), "<Title>. <First> <Last>",
     * company, address1, address2, "<city> <state> <zipcode>", country, phone. Company/address1/address2 share the
     * same CSS classes, so position is the only structural difference and the order itself is what is asserted.
     */
    public Locator deliveryLines() {
        return deliveryBlock.getByRole(AriaRole.LISTITEM);
    }

    /** The billing address block's lines, same layout as {@link #deliveryLines()} with the title "Your billing address". */
    public Locator billingLines() {
        return billingBlock.getByRole(AriaRole.LISTITEM);
    }

    /** Delivery lines as whitespace-normalised text (textContent, so CSS upper-casing is not applied; no waiting). */
    public List<String> deliveryLineTexts() {
        return normalised(deliveryLines());
    }

    public List<String> billingLineTexts() {
        return normalised(billingLines());
    }

    private static List<String> normalised(Locator lines) {
        return lines.allTextContents().stream().map(t -> t.replaceAll("\s+", " ").trim()).collect(Collectors.toList());
    }

    /** Product rows of the review table (excludes the "Total Amount" row). */
    public Locator productRows() {
        // CSS: verified live, product rows are <tr id="product-<id>"> (last resort); the total row has no id.
        return page.locator("#cart_info tbody tr[id^='product-']");
    }

    /** The row of the product with this exact name (anchored on the name link, no index). */
    public Locator row(String name) {
        // The has-locator is resolved relative to each row, so it must not repeat the table scope.
        return productRows().filter(new Locator.FilterOptions().setHas(page.getByRole(AriaRole.LINK,
                new Page.GetByRoleOptions().setName(name).setExact(true))));
    }

    public Locator price(String name) {
        // CSS: verified live, cells carry the classes cart_price / cart_quantity / cart_total_price (last resort, no other hook).
        return row(name).locator(".cart_price");
    }

    /** The quantity cell, for example "1" (a disabled button, as on /view_cart). */
    public Locator quantity(String name) {
        return row(name).locator(".cart_quantity");
    }

    public Locator total(String name) {
        return row(name).locator(".cart_total_price");
    }

    /** Whole review rows as whitespace-normalised text, in DOM order (no waiting). */
    public List<String> productRowTexts() {
        return normalised(productRows());
    }

    /** The "Total Amount" row: label plus grand total (present even when the cart is empty). */
    public Locator totalAmountRow() {
        return reviewRows.filter(new Locator.FilterOptions().setHasText("Total Amount"));
    }

    /** The grand total cell, for example "Rs. 1000". */
    public Locator totalAmount() {
        // CSS: verified live, the grand total cell carries the class cart_total_price like the row totals (last resort, no other hook).
        return totalAmountRow().locator(".cart_total_price");
    }

    /** The order comment textarea. */
    public Locator commentBox() {
        // CSS: verified live, the textarea has no placeholder and its <label> is not linked, so no role name/label hook; name="message" is its stable attribute (last resort).
        return page.locator("textarea[name='message']");
    }

    /** The text shown above the comment textarea. */
    public Locator commentLabel() {
        return page.getByText("If you would like to add a comment about your order, please write it in the field below.");
    }

    public void fillComment(String text) {
        commentBox().fill(text);
    }

    /** "Place Order" (an <a href="/payment">). Not clicked here; the order writer owns what follows. */
    public Locator placeOrderLink() {
        return page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Place Order"));
    }
}
