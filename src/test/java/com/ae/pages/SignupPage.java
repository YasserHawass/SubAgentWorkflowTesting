package com.ae.pages;

import com.ae.config.Config;
import com.ae.fixtures.TestUser;
import com.ae.utils.Ads;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

/** /signup account-information form, plus the /account_created and /delete_account confirmations. */
public class SignupPage {
    private final Page page;
    private final Locator heading;
    private final Locator nameInput;
    private final Locator emailInput;
    private final Locator passwordInput;
    private final Locator days;
    private final Locator months;
    private final Locator years;
    private final Locator newsletter;
    private final Locator offers;
    private final Locator firstName;
    private final Locator lastName;
    private final Locator company;
    private final Locator address1;
    private final Locator address2;
    private final Locator country;
    private final Locator state;
    private final Locator city;
    private final Locator zipcode;
    private final Locator mobile;
    private final Locator createAccount;
    private final Locator accountCreated;
    private final Locator continueButton;
    private final Locator accountDeleted;

    public SignupPage(Page page) {
        this.page = page;
        heading = page.getByText("Enter Account Information");
        nameInput = page.locator("[data-qa='name']");
        emailInput = page.locator("[data-qa='email']");
        passwordInput = page.locator("[data-qa='password']");
        days = page.locator("[data-qa='days']");
        months = page.locator("[data-qa='months']");
        years = page.locator("[data-qa='years']");
        // Tier 5: checkboxes have no data-qa (verified live); labels are also present but ids are stable.
        newsletter = page.locator("#newsletter");
        offers = page.locator("#optin");
        firstName = page.locator("[data-qa='first_name']");
        lastName = page.locator("[data-qa='last_name']");
        company = page.locator("[data-qa='company']");
        address1 = page.locator("[data-qa='address']");
        address2 = page.locator("[data-qa='address2']");
        country = page.locator("[data-qa='country']");
        state = page.locator("[data-qa='state']");
        city = page.locator("[data-qa='city']");
        zipcode = page.locator("[data-qa='zipcode']");
        mobile = page.locator("[data-qa='mobile_number']");
        createAccount = page.locator("[data-qa='create-account']");
        accountCreated = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Account Created!"));
        continueButton = page.locator("[data-qa='continue-button']");
        accountDeleted = page.locator("[data-qa='account-deleted']");
    }

    /** Fills every field of the form from the user. Does not submit. */
    public SignupPage fillAll(TestUser u) {
        Ads.dismissIfPresent(page);
        // Tier 5: title radios have no data-qa; ids id_gender1/id_gender2 are stable (verified live).
        page.locator("Mr".equals(u.title()) ? "#id_gender1" : "#id_gender2").check();
        passwordInput.fill(u.password());
        days.selectOption(u.birthDay());
        months.selectOption(u.birthMonth());
        years.selectOption(u.birthYear());
        newsletter.check();
        offers.check();
        firstName.fill(u.firstName());
        lastName.fill(u.lastName());
        company.fill(u.company());
        address1.fill(u.address1());
        address2.fill(u.address2());
        country.selectOption(u.country());
        state.fill(u.state());
        city.fill(u.city());
        zipcode.fill(u.zipcode());
        mobile.fill(u.mobile());
        return this;
    }

    public SignupPage selectDob(String day, String month, String year) {
        days.selectOption(day);
        months.selectOption(month);
        years.selectOption(year);
        return this;
    }

    public void submit() {
        Ads.dismissIfPresent(page);
        createAccount.click();
    }

    /** Clicks Continue on /account_created or /delete_account. */
    public void continueAfter() {
        Ads.dismissIfPresent(page);
        continueButton.click();
    }

    public Locator heading() { return heading; }
    public Locator nameInput() { return nameInput; }
    public Locator emailInput() { return emailInput; }
    public Locator passwordInput() { return passwordInput; }
    public Locator firstName() { return firstName; }
    public Locator lastName() { return lastName; }
    public Locator company() { return company; }
    public Locator address1() { return address1; }
    public Locator address2() { return address2; }
    public Locator state() { return state; }
    public Locator city() { return city; }
    public Locator zipcode() { return zipcode; }
    public Locator mobile() { return mobile; }
    public Locator years() { return years; }
    public Locator accountCreated() { return accountCreated; }
    public Locator accountDeleted() { return accountDeleted; }
}
