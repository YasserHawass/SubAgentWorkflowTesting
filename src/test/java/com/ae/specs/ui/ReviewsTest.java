package com.ae.specs.ui;

import com.ae.fixtures.AccountApi;
import com.ae.fixtures.BaseUiTest;
import com.ae.fixtures.TestUser;
import com.ae.fixtures.TestUsers;
import com.ae.pages.ProductDetailsPage;
import com.ae.pages.ProductsPage;
import com.ae.pages.components.Header;
import com.google.gson.JsonObject;
import com.microsoft.playwright.Locator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

class ReviewsTest extends BaseUiTest {

    private static final String THANKS = "Thank you for your review.";
    private static final Pattern DETAIL_URL = Pattern.compile("^.*/product_details/\\d+$");

    private ProductDetailsPage details;
    private Header header;

    @BeforeEach
    void initPages() {
        details = new ProductDetailsPage(page);
        header = new Header(page);
    }

    /** Home -> Products -> View Product on the first card (site TC21 steps 1-6). */
    private void openFirstProduct() {
        page.navigate("/");
        header.openProducts();
        ProductsPage products = new ProductsPage(page);
        assertThat(products.heading()).isVisible();
        products.firstCard().viewProduct();
        assertThat(page).hasURL(DETAIL_URL);
    }

    private static String stamp() {
        return String.valueOf(System.currentTimeMillis());
    }

    private static String email() {
        return "ae.review+" + stamp() + "@example.com";
    }

    private static boolean flag(Locator field, String validityProperty) {
        return (Boolean) field.evaluate("e => e.validity." + validityProperty);
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-REVIEWS-01 review form is visible on the product detail page")
    void tcReviews01_formVisible() {
        openFirstProduct();
        assertThat(details.reviewHeading()).isVisible();
        assertThat(details.reviewHeading()).hasText("Write Your Review");
        // Observed live: placeholders "Your Name", "Email Address", "Add Review Here!"; no <label>s.
        assertThat(details.reviewNameInput()).isVisible();
        assertThat(details.reviewEmailInput()).isVisible();
        assertThat(details.reviewTextInput()).isVisible();
        assertThat(details.reviewSubmitButton()).isVisible();
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-REVIEWS-02 guest submits a valid review and sees the success text")
    void tcReviews02_guestSubmits() {
        openFirstProduct();
        List<String> nonGet = new ArrayList<>();
        page.onRequest(r -> {
            // /cdn-cgi/rum is Cloudflare's analytics beacon, unrelated to the form.
            if (!"GET".equals(r.method()) && !r.url().contains("/cdn-cgi/")) nonGet.add(r.method() + " " + r.url());
        });
        details.submitReview("Review Tester", email(), "Great product, works as described.");
        // Observed live: inline alert (not a dialog), exact text, shown for about 2 seconds.
        assertThat(details.reviewSuccess()).isVisible();
        assertThat(details.reviewSuccess()).hasText(THANKS);
        // Observed live: afterwards the alert hides and all three fields are cleared.
        assertThat(details.reviewSuccess()).not().isVisible();
        assertThat(details.reviewNameInput()).isEmpty();
        assertThat(details.reviewEmailInput()).isEmpty();
        assertThat(details.reviewTextInput()).isEmpty();
        // Observed live: the submit is handled client-side; no non-GET request is sent apart from Cloudflare's /cdn-cgi/rum beacon.
        assertThat(nonGet).isEmpty();
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-REVIEWS-03a name empty blocks submission")
    void tcReviews03a_nameEmpty() {
        openFirstProduct();
        String url = page.url();
        details.submitReview("", email(), "Great product");
        // Observed live: all three fields carry `required`; the browser blocks the submit.
        assertThat(flag(details.reviewNameInput(), "valueMissing")).isTrue();
        assertThat(details.reviewSuccess()).not().isVisible();
        assertThat(page).hasURL(url);
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-REVIEWS-03b email empty blocks submission")
    void tcReviews03b_emailEmpty() {
        openFirstProduct();
        String url = page.url();
        details.submitReview("Review Tester", "", "Great product");
        assertThat(flag(details.reviewEmailInput(), "valueMissing")).isTrue();
        assertThat(details.reviewSuccess()).not().isVisible();
        assertThat(page).hasURL(url);
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-REVIEWS-03c review empty blocks submission")
    void tcReviews03c_reviewEmpty() {
        openFirstProduct();
        String url = page.url();
        details.submitReview("Review Tester", email(), "");
        assertThat(flag(details.reviewTextInput(), "valueMissing")).isTrue();
        assertThat(details.reviewSuccess()).not().isVisible();
        assertThat(page).hasURL(url);
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-REVIEWS-03d all empty blocks submission")
    void tcReviews03d_allEmpty() {
        openFirstProduct();
        String url = page.url();
        details.submitReview("", "", "");
        assertThat(flag(details.reviewNameInput(), "valueMissing")).isTrue();
        assertThat(flag(details.reviewEmailInput(), "valueMissing")).isTrue();
        assertThat(flag(details.reviewTextInput(), "valueMissing")).isTrue();
        assertThat(details.reviewSuccess()).not().isVisible();
        assertThat(page).hasURL(url);
    }

    private void assertInvalidEmailBlocked(String bad) {
        openFirstProduct();
        String url = page.url();
        details.submitReview("Review Tester", bad, "Great product");
        // Observed live: type=email validation fails (typeMismatch) and the browser blocks the submit.
        assertThat(flag(details.reviewEmailInput(), "typeMismatch")).isTrue();
        assertThat(details.reviewSuccess()).not().isVisible();
        assertThat(page).hasURL(url);
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-REVIEWS-04a email 'abc' is blocked")
    void tcReviews04a_emailAbc() {
        assertInvalidEmailBlocked("abc");
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-REVIEWS-04b email 'a@' is blocked")
    void tcReviews04b_emailAt() {
        assertInvalidEmailBlocked("a@");
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-REVIEWS-04c email 'a b@x.com' is blocked")
    void tcReviews04c_emailSpace() {
        assertInvalidEmailBlocked("a b@x.com");
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-REVIEWS-05 HTML and script in name and review are not rendered or executed")
    void tcReviews05_markupNotRendered() {
        openFirstProduct();
        List<String> dialogs = new ArrayList<>();
        page.onDialog(d -> {
            dialogs.add(d.message());
            d.dismiss();
        });
        // Baseline: the page has its own <b> elements; the input must not add any.
        // CSS: count all <b> elements on the page to verify no markup was injected by the form submission (last resort, no role/text hook for bold markup).
        int bBefore = page.locator("b").count();
        details.submitReview("<b>Tester</b>", email(), "<script>alert(1)</script><b>bold</b>");
        // Observed live: the normal success alert is shown; the input is never rendered anywhere.
        assertThat(details.reviewSuccess()).isVisible();
        // CSS: same <b> count as the baseline above (tier 5, justified there).
        assertThat(page.locator("b")).hasCount(bBefore);
        page.reload();
        assertThat(details.reviewHeading()).isVisible();
        // CSS: same <b> count as the baseline above (tier 5, justified there).
        assertThat(page.locator("b")).hasCount(bBefore);
        assertThat(dialogs).isEmpty();
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-REVIEWS-06 submitted review is not displayed after reload")
    void tcReviews06_notDisplayedAfterReload() {
        openFirstProduct();
        String text = "ae-review-" + stamp();
        String name = "Review Tester " + stamp();
        details.submitReview(name, email(), text);
        assertThat(details.reviewSuccess()).isVisible();
        page.reload();
        assertThat(details.reviewHeading()).isVisible();
        // Observed live: there is no review list; neither the text nor the name appears in the page.
        assertThat(page.getByText(text)).hasCount(0);
        assertThat(page.getByText(name)).hasCount(0);
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-REVIEWS-07 logged-in user submits a review")
    void tcReviews07_loggedIn() {
        TestUser user = TestUsers.newUser();
        registerForCleanup(user);
        JsonObject res = AccountApi.create(user);
        assertThat(res.get("responseCode").getAsInt()).isEqualTo(201);
        assertThat(res.get("message").getAsString()).isEqualTo("User created!");
        page.navigate("/");
        header.openLogin().loginAs(user);
        assertThat(header.loggedInAs).containsText("Logged in as " + user.name());
        header.openProducts();
        new ProductsPage(page).firstCard().viewProduct();
        assertThat(page).hasURL(DETAIL_URL);
        // Observed live: name and email are NOT prefilled from the account.
        assertThat(details.reviewNameInput()).isEmpty();
        assertThat(details.reviewEmailInput()).isEmpty();
        details.submitReview(user.name(), user.email(), "Logged-in review text");
        assertThat(details.reviewSuccess()).isVisible();
        assertThat(details.reviewSuccess()).hasText(THANKS);
    }
}
