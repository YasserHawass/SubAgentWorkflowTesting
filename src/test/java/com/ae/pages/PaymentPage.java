package com.ae.pages;

import com.ae.config.Config;
import com.ae.utils.Ads;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

/**
 * /payment: card form. Verified live: all five inputs carry data-qa and the HTML "required" attribute; the form
 * POSTs to /payment. The page renders for anyone, logged in or not, with any cart.
 */
public class PaymentPage {
    private final Page page;
    private final Locator heading;
    private final Locator nameOnCard;
    private final Locator cardNumber;
    private final Locator cvc;
    private final Locator expiryMonth;
    private final Locator expiryYear;
    private final Locator payButton;

    public PaymentPage(Page page) {
        this.page = page;
        heading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Payment").setExact(true));
        nameOnCard = page.locator("[data-qa='name-on-card']");
        cardNumber = page.locator("[data-qa='card-number']");
        cvc = page.locator("[data-qa='cvc']");
        expiryMonth = page.locator("[data-qa='expiry-month']");
        expiryYear = page.locator("[data-qa='expiry-year']");
        payButton = page.locator("[data-qa='pay-button']");
    }

    public PaymentPage open() {
        page.navigate(Config.baseUrl() + "/payment");
        Ads.dismissIfPresent(page);
        return this;
    }

    public Locator heading() { return heading; }
    public Locator nameOnCard() { return nameOnCard; }
    public Locator cardNumber() { return cardNumber; }
    public Locator cvc() { return cvc; }
    public Locator expiryMonth() { return expiryMonth; }
    public Locator expiryYear() { return expiryYear; }
    public Locator payButton() { return payButton; }

    /** Fills the five card fields. Does not submit. */
    public PaymentPage fillCard(String name, String number, String cvcCode, String month, String year) {
        Ads.dismissIfPresent(page);
        nameOnCard.fill(name);
        cardNumber.fill(number);
        cvc.fill(cvcCode);
        expiryMonth.fill(month);
        expiryYear.fill(year);
        return this;
    }

    /** Clicks "Pay and Confirm Order". Success leads to /payment_done/<id>; the caller decides what to expect. */
    public PaymentDonePage pay() {
        Ads.dismissIfPresent(page);
        payButton.click();
        return new PaymentDonePage(page);
    }
}
