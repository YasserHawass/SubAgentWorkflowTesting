package com.ae.pages;

import com.ae.utils.Ads;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.AriaRole;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * /payment_done/&lt;id&gt;: order confirmation. Verified live: heading "Order Placed!" (data-qa order-placed),
 * text "Congratulations! Your order has been confirmed!", "Download Invoice" (link to /download_invoice/&lt;id&gt;)
 * and "Continue" (link to /). The id in the URL equals the cart total in Rs. at payment time (500 for Blue Top, 0 for an empty cart).
 * Shared by the order and invoice specs.
 */
public class PaymentDonePage {
    private static final Pattern URL_ID = Pattern.compile("/payment_done/([0-9]+)");

    private final Page page;
    private final Locator heading;
    private final Locator message;
    private final Locator downloadInvoiceLink;
    private final Locator continueButton;

    public PaymentDonePage(Page page) {
        this.page = page;
        heading = page.locator("[data-qa='order-placed']");
        message = page.getByText("Congratulations! Your order has been confirmed!");
        downloadInvoiceLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Download Invoice"));
        continueButton = page.locator("[data-qa='continue-button']");
    }

    /** The "Order Placed!" title (DOM text "Order Placed!", rendered upper-case by CSS). */
    public Locator heading() { return heading; }
    public Locator message() { return message; }
    /** "Download Invoice" (an anchor to /download_invoice/&lt;id&gt;; the invoice is a .txt download). */
    public Locator downloadInvoiceLink() { return downloadInvoiceLink; }
    public Locator continueButton() { return continueButton; }

    /** The id segment of the current /payment_done/&lt;id&gt; URL (read now, no waiting). */
    public String orderId() {
        Matcher m = URL_ID.matcher(page.url());
        if (!m.find()) throw new IllegalStateException("Not on /payment_done/<id>: " + page.url());
        return m.group(1);
    }

    /** Clicks "Continue"; the site opens the home page (/). */
    public HomePage continueToHome() {
        Ads.dismissIfPresent(page);
        continueButton.click();
        // Click handlers on the next page bind at load; wait for it (see CONVENTIONS: Late-bound handlers).
        page.waitForLoadState(LoadState.LOAD);
        return new HomePage(page);
    }
}
