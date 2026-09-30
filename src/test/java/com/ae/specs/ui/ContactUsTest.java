package com.ae.specs.ui;

import com.ae.config.Config;
import com.ae.fixtures.BaseUiTest;
import com.ae.pages.ContactUsPage;
import com.ae.pages.HomePage;
import com.ae.pages.components.Header;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

class ContactUsTest extends BaseUiTest {

    private static final String DIALOG_TEXT = "Press OK to proceed!";

    private ContactUsPage contact;
    private Header header;
    private final List<Path> tempFiles = new ArrayList<>();
    private final List<String> dialogs = new ArrayList<>();
    private String tag;

    @BeforeEach
    void initPages() {
        contact = new ContactUsPage(page);
        header = new Header(page);
        tag = String.valueOf(System.currentTimeMillis());
    }

    @AfterEach
    void deleteTempFiles() throws IOException {
        for (Path p : tempFiles) Files.deleteIfExists(p);
    }

    private String email() {
        return "ae.test+" + tag + "@example.com";
    }

    /** Small .txt under target/ (never committed), deleted in @AfterEach. */
    private Path smallFile() {
        return tempFile("ae-upload-", ".txt", "hello contact us\n".getBytes(StandardCharsets.UTF_8));
    }

    private Path tempFile(String prefix, String suffix, byte[] content) {
        try {
            Path dir = Files.createDirectories(Path.of("target", "tmp"));
            Path f = Files.createTempFile(dir, prefix, suffix);
            Files.write(f, content);
            tempFiles.add(f);
            return f;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** Registers the confirm handler BEFORE any Submit click; records every dialog. */
    private void handleDialogs(boolean accept) {
        page.onDialog(d -> {
            dialogs.add(d.message());
            if (accept) d.accept(); else d.dismiss();
        });
    }

    private void fillValid() {
        contact.fill("AE Tester " + tag, email(), "Subject " + tag, "Message " + tag);
    }

    private void assertSubmitted() {
        assertThat(contact.successMessage()).isVisible();
        assertThat(dialogs).containsExactly(DIALOG_TEXT);
    }

    /** Observed live: a blocked submit shows no dialog, no success text, and the form stays. */
    private void assertBlockedByBrowser() {
        assertThat(dialogs).isEmpty();
        assertThat(contact.form()).isVisible();
        assertThat(contact.successMessage()).not().isVisible();
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-CONTACTUS-01 submit the contact form and see the success text")
    void tcContactUs01_submitShowsSuccess() {
        new HomePage(page).open();
        assertThat(header.signupLoginLink).isVisible();
        header.contactUsLink.click();
        assertThat(contact.heading()).isVisible();
        fillValid();
        contact.attach(smallFile());
        handleDialogs(true);
        contact.submit();
        assertSubmitted();
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-CONTACTUS-02 Home button after success returns to the home page")
    void tcContactUs02_homeButtonReturnsHome() {
        contact.open();
        fillValid();
        contact.attach(smallFile());
        handleDialogs(true);
        contact.submit();
        assertThat(contact.successMessage()).isVisible();
        // Observed live: "Home" is a link (role LINK), not a button.
        contact.goHome();
        assertThat(page).hasURL(Config.baseUrl() + "/");
        assertThat(header.signupLoginLink).isVisible();
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-CONTACTUS-03 dismissing the confirm dialog does not submit")
    void tcContactUs03_dismissDoesNotSubmit() {
        contact.open();
        fillValid();
        contact.attach(smallFile());
        handleDialogs(false);
        contact.submit();
        // Observed live: one dialog, no success text, the form stays with the typed values.
        assertThat(dialogs).containsExactly(DIALOG_TEXT);
        assertThat(contact.form()).isVisible();
        assertThat(contact.nameInput()).hasValue("AE Tester " + tag);
        assertThat(contact.emailInput()).hasValue(email());
        assertThat(contact.successMessage()).not().isVisible();
    }

    // TC-04, one method per omitted field. Observed live: only Email is required (HTML5 validation).
    // Name, Subject, Message and the file are NOT validated: the form is submitted with the dialog and success text.
    // FLAG: these lax fields are a product question (site TC6 does not say they are optional).

    @Test
    @Tag("regression")
    @DisplayName("TC-CONTACTUS-04 empty name is still submitted")
    void tcContactUs04a_emptyName() {
        contact.open();
        contact.fill("", email(), "Subject " + tag, "Message " + tag);
        contact.attach(smallFile());
        handleDialogs(true);
        contact.submit();
        assertSubmitted();
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-CONTACTUS-04 empty email blocks the submit")
    void tcContactUs04b_emptyEmail() {
        contact.open();
        contact.fill("AE Tester " + tag, "", "Subject " + tag, "Message " + tag);
        contact.attach(smallFile());
        handleDialogs(true);
        contact.submit();
        assertBlockedByBrowser();
        assertThat(contact.emailInput().evaluate("e => e.validity.valueMissing")).isEqualTo(true);
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-CONTACTUS-04 empty subject is still submitted")
    void tcContactUs04c_emptySubject() {
        contact.open();
        contact.fill("AE Tester " + tag, email(), "", "Message " + tag);
        contact.attach(smallFile());
        handleDialogs(true);
        contact.submit();
        assertSubmitted();
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-CONTACTUS-04 empty message is still submitted")
    void tcContactUs04d_emptyMessage() {
        contact.open();
        contact.fill("AE Tester " + tag, email(), "Subject " + tag, "");
        contact.attach(smallFile());
        handleDialogs(true);
        contact.submit();
        assertSubmitted();
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-CONTACTUS-04 no file attached is still submitted")
    void tcContactUs04e_noFile() {
        contact.open();
        fillValid();
        handleDialogs(true);
        contact.submit();
        assertSubmitted();
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-CONTACTUS-05 invalid email format blocks the submit")
    void tcContactUs05_invalidEmail() {
        contact.open();
        contact.fill("AE Tester " + tag, "not-an-email", "Subject " + tag, "Message " + tag);
        contact.attach(smallFile());
        handleDialogs(true);
        contact.submit();
        // Observed live: the type=email input blocks the submit in the browser (typeMismatch), no dialog.
        assertBlockedByBrowser();
        assertThat(contact.emailInput().evaluate("e => e.validity.typeMismatch")).isEqualTo(true);
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-CONTACTUS-06 a file of a few MB is accepted")
    void tcContactUs06a_largeFile() {
        contact.open();
        fillValid();
        contact.attach(tempFile("ae-big-", ".txt", new byte[3 * 1024 * 1024]));
        handleDialogs(true);
        contact.submit();
        // Observed live: 3 MB is accepted with the normal success text.
        assertSubmitted();
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-CONTACTUS-06 a file without extension is accepted")
    void tcContactUs06b_fileWithoutExtension() {
        contact.open();
        fillValid();
        contact.attach(tempFile("ae-noext-", "", "no extension\n".getBytes(StandardCharsets.UTF_8)));
        handleDialogs(true);
        contact.submit();
        assertSubmitted();
    }
}
