package com.ae.pages;

import com.ae.config.Config;
import com.ae.utils.Ads;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;

import java.nio.file.Path;

/** /contact_us. Submit triggers a JS confirm dialog; the test registers the dialog handler before calling submit(). */
public class ContactUsPage {
    private final Page page;
    private final Locator nameInput;
    private final Locator emailInput;
    private final Locator subjectInput;
    private final Locator messageInput;
    private final Locator fileInput;
    private final Locator submitButton;

    public ContactUsPage(Page page) {
        this.page = page;
        // data-qa verified live on the form inputs.
        nameInput = page.locator("[data-qa='name']");
        emailInput = page.locator("[data-qa='email']");
        subjectInput = page.locator("[data-qa='subject']");
        messageInput = page.locator("[data-qa='message']");
        // CSS: verified live, the file input has no data-qa, label or placeholder; name=upload_file is its stable hook (last resort).
        fileInput = page.locator("input[name='upload_file']");
        submitButton = page.locator("[data-qa='submit-button']");
    }

    public ContactUsPage open() {
        page.navigate(Config.baseUrl() + "/contact_us");
        Ads.dismissIfPresent(page);
        return this;
    }

    public Locator heading() {
        return page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Get In Touch"));
    }

    public Locator nameInput() { return nameInput; }
    public Locator emailInput() { return emailInput; }
    public Locator subjectInput() { return subjectInput; }
    public Locator messageInput() { return messageInput; }
    public Locator form() {
        // CSS: verified live, the form has no role name; #contact-us-form is its id (last resort).
        return page.locator("#contact-us-form");
    }

    public void fill(String name, String email, String subject, String message) {
        // Verified live: when reached by a header click, typing before the load event can be wiped; wait for load (a state, not a sleep).
        page.waitForLoadState(LoadState.LOAD);
        nameInput.fill(name);
        emailInput.fill(email);
        subjectInput.fill(subject);
        messageInput.fill(message);
    }

    public void attach(Path file) {
        fileInput.setInputFiles(file);
    }

    public void submit() {
        Ads.dismissIfPresent(page);
        submitButton.click();
        // Click handlers on the next page bind at load; wait for it (see CONVENTIONS: Late-bound handlers).
        page.waitForLoadState(LoadState.LOAD);
    }

    public Locator successMessage() {
        // CSS: verified live, the same text also sits (hidden) in the footer's subscribe box, so scope to #contact-page (last resort).
        return page.locator("#contact-page").getByText("Success! Your details have been submitted successfully.");
    }

    /** The green "Home" link shown after a successful submit (scoped to the page body so the header link is excluded). */
    public Locator homeButton() {
        // CSS: verified live, #contact-page is the page container; the header also has a "Home" link (last resort scope).
        return page.locator("#contact-page").getByRole(AriaRole.LINK, new Locator.GetByRoleOptions().setName("Home"));
    }

    public HomePage goHome() {
        Ads.dismissIfPresent(page);
        homeButton().click();
        // Click handlers on the next page bind at load; wait for it (see CONVENTIONS: Late-bound handlers).
        page.waitForLoadState(LoadState.LOAD);
        return new HomePage(page);
    }
}
