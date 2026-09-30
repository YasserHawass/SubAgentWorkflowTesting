package com.ae.specs.ui;

import com.ae.config.Config;
import com.ae.fixtures.BaseUiTest;
import com.ae.pages.HomePage;
import com.ae.pages.components.Footer;
import com.ae.pages.components.Header;
import com.microsoft.playwright.assertions.LocatorAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

class SubscriptionTest extends BaseUiTest {

    private Footer footer;
    private String email;

    @BeforeEach
    void init() {
        footer = new Footer(page);
        email = "ae.test+" + System.currentTimeMillis() + "@example.com";
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-SUBSCRIPTION-01 subscribe from the home page footer")
    void tcSubscription01_homeFooter() {
        new HomePage(page).open();
        footer.scrollIntoView();
        assertThat(footer.subscriptionHeading()).isVisible();
        footer.subscribe(email);
        assertThat(footer.successMessage()).isVisible();
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-SUBSCRIPTION-02 subscribe from the cart page footer")
    void tcSubscription02_cartFooter() {
        new HomePage(page).open();
        new Header(page).openCart();
        assertThat(page).hasURL(Config.baseUrl() + "/view_cart");
        footer.scrollIntoView();
        assertThat(footer.subscriptionHeading()).isVisible();
        footer.subscribe(email);
        assertThat(footer.successMessage()).isVisible();
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-SUBSCRIPTION-03 empty email is not subscribed")
    void tcSubscription03_emptyEmail() {
        new HomePage(page).open();
        footer.scrollIntoView();
        footer.subscribeButton().click();
        // Observed live: the required type=email input blocks the submit in the browser (valueMissing).
        assertThat(footer.emailInput().evaluate("e => e.validity.valueMissing")).isEqualTo(true);
        assertThat(footer.successMessage()).not().isVisible();
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-SUBSCRIPTION-04 invalid email format is not subscribed")
    void tcSubscription04_invalidEmail() {
        new HomePage(page).open();
        footer.scrollIntoView();
        footer.emailInput().fill("not-an-email");
        footer.subscribeButton().click();
        // Observed live: the type=email input blocks the submit in the browser (typeMismatch); no site message.
        assertThat(footer.emailInput().evaluate("e => e.validity.typeMismatch")).isEqualTo(true);
        assertThat(footer.successMessage()).not().isVisible();
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-SUBSCRIPTION-05 same email subscribed twice in a row")
    void tcSubscription05_twice() {
        new HomePage(page).open();
        footer.subscribe(email);
        assertThat(footer.successMessage()).isVisible();
        // The success text hides itself after a few seconds; wait for that so the second check is not the first one.
        // Longer timeout: it hides after about 5 s, the default is 5 s.
        assertThat(footer.successMessage()).not().isVisible(new LocatorAssertions.IsVisibleOptions().setTimeout(15000));
        footer.subscribe(email);
        // Observed live: the same success text again, no "already subscribed" message.
        assertThat(footer.successMessage()).isVisible();
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-SUBSCRIPTION-06 success text is in the footer, input keeps its value, text hides itself")
    void tcSubscription06_placementAndInput() {
        new HomePage(page).open();
        footer.subscribe(email);
        // footer.successMessage() is scoped to the footer (contentinfo), so visible means inside the footer.
        assertThat(footer.successMessage()).isVisible();
        // Observed live: the input is NOT cleared.
        assertThat(footer.emailInput()).hasValue(email);
        // Observed live: the text hides itself after a few seconds; longer timeout for that reason.
        assertThat(footer.successMessage()).not().isVisible(new LocatorAssertions.IsVisibleOptions().setTimeout(15000));
    }
}
