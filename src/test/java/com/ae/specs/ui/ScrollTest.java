package com.ae.specs.ui;

import com.ae.config.Config;
import com.ae.fixtures.BaseUiTest;
import com.ae.pages.HomePage;
import com.ae.pages.components.Footer;
import com.microsoft.playwright.assertions.LocatorAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

class ScrollTest extends BaseUiTest {

    private HomePage home;
    private Footer footer;

    @BeforeEach
    void init() {
        home = new HomePage(page);
        footer = new Footer(page);
    }

    private void openAndScrollToBottom() {
        home.open();
        assertThat(home.heroHeading()).isInViewport();
        footer.scrollIntoView();
        assertThat(footer.subscriptionHeading()).isInViewport();
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-SCROLL-01 scroll down, then scroll up with the arrow")
    void tcScroll01_withArrow() {
        openAndScrollToBottom();
        assertThat(footer.scrollUpArrow()).isVisible();
        footer.clickScrollUp();
        assertThat(home.heroHeading()).isInViewport();
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-SCROLL-02 scroll down, then scroll up without the arrow")
    void tcScroll02_withoutArrow() {
        openAndScrollToBottom();
        home.heroHeading().scrollIntoViewIfNeeded();
        assertThat(home.heroHeading()).isInViewport();
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-SCROLL-03 arrow is hidden at the top and appears after scrolling down")
    void tcScroll03_arrowAppearsAfterScroll() {
        home.open();
        // Observed live: the arrow is in the DOM but hidden at the top.
        assertThat(footer.scrollUpArrow()).hasCount(1);
        assertThat(footer.scrollUpArrow()).isHidden();
        footer.scrollIntoView();
        assertThat(footer.scrollUpArrow()).isVisible();
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-SCROLL-04 arrow hides again after it is used")
    void tcScroll04_arrowHidesAfterUse() {
        home.open();
        footer.scrollIntoView();
        assertThat(footer.scrollUpArrow()).isVisible();
        footer.clickScrollUp();
        assertThat(home.heroHeading()).isInViewport();
        // Observed live: hidden again once back at the top.
        assertThat(footer.scrollUpArrow()).isHidden();
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-SCROLL-05 top text stays in the viewport on the next carousel slide")
    void tcScroll05_carouselSlides() {
        home.open();
        assertThat(home.heroHeading()).isInViewport();
        // The carousel auto-advances about every 5 s; wait until the first slide has lost "active" (longer timeout for that reason).
        assertThat(home.firstHeroSlide()).not().hasClass(Pattern.compile("active"),
                new LocatorAssertions.HasClassOptions().setTimeout(20000));
        assertThat(home.heroHeading()).isInViewport();
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-SCROLL-06 arrow appears on /products after scrolling down")
    void tcScroll06_arrowOnProducts() {
        page.navigate(Config.baseUrl() + "/products");
        footer.scrollIntoView();
        // Observed live: the arrow is shown on /products too.
        assertThat(footer.scrollUpArrow()).isVisible();
    }
}
