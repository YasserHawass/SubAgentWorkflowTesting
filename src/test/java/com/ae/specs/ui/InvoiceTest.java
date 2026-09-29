package com.ae.specs.ui;

import com.ae.config.Config;
import com.ae.fixtures.BaseUiTest;
import com.ae.fixtures.OrderFlow;
import com.ae.fixtures.TestUser;
import com.ae.fixtures.TestUsers;
import com.ae.pages.LoginPage;
import com.ae.pages.PaymentDonePage;
import com.ae.pages.components.Header;
import com.ae.utils.Ads;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Download;
import com.microsoft.playwright.Page;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.atomic.AtomicInteger;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Invoice download after an order. Observed live: "Download Invoice" links to /download_invoice/&lt;id&gt; (id = cart total,
 * 500 for Blue Top); the file is "invoice.txt", text/plain, content "Hi &lt;first name&gt; &lt;last name&gt;,
 * Your total purchase amount is &lt;total&gt;. Thank you". The greeting uses the account's first/last name (from the
 * address data), not the signup name and not the card name. The name comes from the session, so another user's
 * session gets its own name.
 */
class InvoiceTest extends BaseUiTest {

    private static final AtomicInteger SEQ = new AtomicInteger();
    private static final String CONFIRMATION = "Congratulations! Your order has been confirmed!";

    /** A fresh user whose first/last name is unique, because the invoice greets by that name. */
    private TestUser newInvoiceUser() {
        TestUser u = TestUsers.newUser();
        String tag = System.nanoTime() + "x" + SEQ.incrementAndGet();
        TestUser user = new TestUser(u.name(), u.email(), u.password(), u.title(), u.birthDay(), u.birthMonth(),
                u.birthYear(), "Inv" + tag, "Payer", u.company(), u.address1(), u.address2(), u.country(), u.state(),
                u.city(), u.zipcode(), u.mobile());
        registerForCleanup(user);
        OrderFlow.createAccount(user);
        return user;
    }

    private static String greetingName(TestUser u) {
        return u.firstName() + " " + u.lastName();
    }

    /** Product price in Rs. from /api/productsList (digits of "Rs. N"). */
    private String productPrice() {
        APIResponse r = page.request().get(Config.baseUrl() + "/api/productsList");
        for (JsonElement p : JsonParser.parseString(r.text()).getAsJsonObject().getAsJsonArray("products")) {
            if (OrderFlow.PRODUCT.equals(p.getAsJsonObject().get("name").getAsString())) {
                return p.getAsJsonObject().get("price").getAsString().replaceAll("[^0-9]", "");
            }
        }
        throw new AssertionError("Product not in /api/productsList: " + OrderFlow.PRODUCT);
    }

    private PaymentDonePage orderAs(TestUser user) {
        PaymentDonePage done = OrderFlow.placeOrder(page, user);
        assertThat(done.message()).hasText(CONFIRMATION);
        return done;
    }

    private Download downloadInvoice(PaymentDonePage done) {
        Ads.dismissIfPresent(page);
        return page.waitForDownload(() -> done.downloadInvoiceLink().click());
    }

    private static String read(Download d) throws IOException {
        return Files.readString(d.path());
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-INVOICE-01 Download Invoice after paying triggers a .txt download")
    void tcInvoice01_downloadIsTxt() throws IOException {
        PaymentDonePage done = orderAs(newInvoiceUser());

        Download d = downloadInvoice(done);

        // Observed: the file is always named invoice.txt.
        assertThat(d.suggestedFilename()).isEqualTo("invoice.txt");
        assertThat(read(d)).isNotEmpty();
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-INVOICE-02 Invoice content shows the account name and the order total")
    void tcInvoice02_contentHasNameAndTotal() throws IOException {
        TestUser user = newInvoiceUser();
        PaymentDonePage done = orderAs(user);

        String text = read(downloadInvoice(done));

        // Observed: "Hi <firstName> <lastName>, Your total purchase amount is <total>. Thank you"
        assertThat(text).contains(greetingName(user));
        assertThat(text).contains("total purchase amount is " + productPrice());
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-INVOICE-03 Downloading the invoice twice gives identical content")
    void tcInvoice03_twiceIdentical() throws IOException {
        PaymentDonePage done = orderAs(newInvoiceUser());
        String url = page.url();

        Download first = downloadInvoice(done);
        Download second = downloadInvoice(done);

        assertThat(read(second)).isNotEmpty().isEqualTo(read(first));
        // Observed: both files are named invoice.txt (the names do not differ).
        assertThat(first.suggestedFilename()).isEqualTo("invoice.txt");
        assertThat(second.suggestedFilename()).isEqualTo("invoice.txt");
        assertThat(page).hasURL(url);
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-INVOICE-04 Continue after downloading leaves the confirmation page")
    void tcInvoice04_continueAfterDownload() {
        TestUser user = newInvoiceUser();
        PaymentDonePage done = orderAs(user);
        downloadInvoice(done);

        done.continueToHome();

        // Observed: Continue goes to the home page.
        assertThat(page).hasURL(Config.baseUrl() + "/");
        assertThat(new Header(page).loggedInAs).containsText("Logged in as " + user.name());
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-INVOICE-05 Fetching the invoice URL directly in the same session returns the same content")
    void tcInvoice05_directFetchSameSession() throws IOException {
        TestUser user = newInvoiceUser();
        PaymentDonePage done = orderAs(user);
        String href = done.downloadInvoiceLink().getAttribute("href");
        String downloaded = read(downloadInvoice(done));

        APIResponse response = page.request().get(Config.baseUrl() + href);

        assertThat(response.status()).isEqualTo(200);
        assertThat(response.text()).isEqualTo(downloaded);
        assertThat(response.text()).contains(greetingName(user));
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-INVOICE-06 Another user's invoice is not served (tampered order id)")
    void tcInvoice06_otherUsersInvoice() {
        TestUser userA = newInvoiceUser();
        TestUser userB = newInvoiceUser();
        BrowserContext contextB = context.browser().newContext();
        try {
            // User A orders in the default page; user B orders in a separate session.
            PaymentDonePage doneA = orderAs(userA);
            String href = doneA.downloadInvoiceLink().getAttribute("href");
            Page pageB = contextB.newPage();
            OrderFlow.placeOrder(pageB, userB);

            // Observed: the id only encodes the cart total, so both users' invoice URLs are identical; the
            // session decides whose name is printed. B requests A's href and must not receive A's data.
            APIResponse response = pageB.request().get(Config.baseUrl() + href);

            assertThat(response.text()).doesNotContain(greetingName(userA));
        } finally {
            contextB.close();
        }
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-INVOICE-07 Invoice URL without a session does not serve invoice content")
    void tcInvoice07_noSession() {
        TestUser user = newInvoiceUser();
        PaymentDonePage done = orderAs(user);
        String href = done.downloadInvoiceLink().getAttribute("href");
        BrowserContext anonymous = context.browser().newContext();
        try {
            APIRequestContext api = anonymous.request();

            APIResponse response = api.get(Config.baseUrl() + href);

            // Observed: HTTP 200 text/plain "Hi , Your total purchase amount is 500. Thank you" (empty name, the
            // amount is echoed from the URL). FLAG for triage: an invoice page is served to a session-less request.
            assertThat(response.text()).doesNotContain(greetingName(user));
            assertThat(response.text()).doesNotContain("purchase amount is 500");
        } finally {
            anonymous.close();
        }
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-INVOICE-08 An invoice for an arbitrary amount cannot be forged")
    void tcInvoice08_forgedAmount() {
        TestUser user = newInvoiceUser();
        Header header = new Header(page);
        LoginPage login = new LoginPage(page).open();
        login.loginAs(user);
        assertThat(header.loggedInAs).containsText("Logged in as " + user.name());

        APIResponse response = page.request().get(Config.baseUrl() + "/download_invoice/999999");

        // Safe behaviour: no invoice claiming an amount the user never paid. Per docs/site-map.md the amount comes
        // from the URL, so this is expected to fail on the site.
        assertThat(response.text()).doesNotContain("purchase amount is 999999");
    }
}
