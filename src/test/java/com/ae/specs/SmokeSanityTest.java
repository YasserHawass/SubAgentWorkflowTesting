package com.ae.specs;

import com.ae.fixtures.BaseUiTest;
import com.ae.pages.components.Header;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Bootstrap sanity: proves the browser launches, the site answers, ad-blocking doesn't break the page,
 * and the Header component's locators resolve. Not a feature test. Agents don't touch this file.
 */
@Tag("smoke")
class SmokeSanityTest extends BaseUiTest {

    @Test
    @DisplayName("SANITY-01 home page loads and header renders")
    void sanity01_homeLoads() {
        page.navigate("/");
        assertThat(page).hasTitle("Automation Exercise");
        Header header = new Header(page);
        assertThat(header.signupLoginLink).isVisible();
        assertThat(header.productsLink).isVisible();
    }
}
