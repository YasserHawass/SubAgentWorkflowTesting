package com.ae.specs.api;

import com.ae.fixtures.AccountApi;
import com.ae.fixtures.BaseApiTest;
import com.ae.fixtures.TestUser;
import com.ae.fixtures.TestUsers;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.FormData;
import com.microsoft.playwright.options.RequestOptions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AuthApiTest extends BaseApiTest {

    private static final String PATH = "verifyLogin";
    private static final String NOT_FOUND = "User not found!";
    private static final String BAD_REQUEST = "Bad request, email or password parameter is missing in POST request.";
    private static final String NOT_SUPPORTED = "This request method is not supported.";

    private final List<TestUser> created = new ArrayList<>();

    @AfterEach
    void cleanup() {
        for (TestUser u : created) {
            AccountApi.delete(u);
        }
        created.clear();
    }

    private TestUser registeredUser() {
        TestUser user = TestUsers.newUser();
        registerForCleanup(user);
        JsonObject res = AccountApi.create(user);
        assertThat(res.get("responseCode").getAsInt()).isEqualTo(201);
        assertThat(res.get("message").getAsString()).isEqualTo("User created!");
        return user;
    }

    private void registerForCleanup(TestUser user) {
        created.add(user);
    }

    private static FormData form(String email, String password) {
        FormData f = FormData.create();
        if (email != null) f.set("email", email);
        if (password != null) f.set("password", password);
        return f;
    }

    private void assertBody(APIResponse res, int code, String message) {
        JsonObject body = JsonParser.parseString(res.text()).getAsJsonObject();
        assertThat(body.get("responseCode").getAsInt()).isEqualTo(code);
        assertThat(body.get("message").getAsString()).isEqualTo(message);
        assertThat(res.status()).isEqualTo(200);
    }

    private APIResponse post(String email, String password) {
        return api.post(PATH, RequestOptions.create().setForm(form(email, password)));
    }

    @Test
    @DisplayName("TC-APIAUTH-01 Valid credentials return \"User exists!\"")
    @Tag("api") @Tag("smoke")
    void tcApiAuth01_validCredentialsReturnUserExists() {
        TestUser user = registeredUser();
        assertBody(post(user.email(), user.password()), 200, "User exists!");
    }

    @Test
    @DisplayName("TC-APIAUTH-02 Wrong password for a real account returns \"User not found!\"")
    @Tag("api") @Tag("smoke")
    void tcApiAuth02_wrongPasswordReturnsUserNotFound() {
        TestUser user = registeredUser();
        assertThat(user.password()).isNotEqualTo("Wrong#Pass1");
        assertBody(post(user.email(), "Wrong#Pass1"), 404, NOT_FOUND);
    }

    @Test
    @DisplayName("TC-APIAUTH-03 Unregistered email returns the same response as wrong password")
    @Tag("api") @Tag("regression")
    void tcApiAuth03_unregisteredEmailReturnsUserNotFound() {
        assertBody(post(TestUsers.newUser().email(), "SomePass1"), 404, NOT_FOUND);
    }

    @Test
    @DisplayName("TC-APIAUTH-04 Missing password parameter returns 400")
    @Tag("api") @Tag("smoke")
    void tcApiAuth04_missingPasswordReturns400() {
        assertBody(post(TestUsers.newUser().email(), null), 400, BAD_REQUEST);
    }

    @Test
    @DisplayName("TC-APIAUTH-05 Missing email parameter returns 400")
    @Tag("api") @Tag("regression")
    void tcApiAuth05_missingEmailReturns400() {
        assertBody(post(null, "SomePass1"), 400, BAD_REQUEST);
    }

    @Test
    @DisplayName("TC-APIAUTH-06 DELETE on /verifyLogin is not supported")
    @Tag("api") @Tag("smoke")
    void tcApiAuth06_deleteNotSupported() {
        APIResponse res = api.delete(PATH,
                RequestOptions.create().setForm(form(TestUsers.newUser().email(), "SomePass1")));
        assertBody(res, 405, NOT_SUPPORTED);
    }

    @Test
    @DisplayName("TC-APIAUTH-07 GET on /verifyLogin returns 405")
    @Tag("api") @Tag("regression")
    void tcApiAuth07_getNotSupported() {
        assertBody(api.get(PATH), 405, NOT_SUPPORTED);
    }

    @Test
    @DisplayName("TC-APIAUTH-08 PUT on /verifyLogin returns 405")
    @Tag("api") @Tag("regression")
    void tcApiAuth08_putNotSupported() {
        APIResponse res = api.put(PATH,
                RequestOptions.create().setForm(form(TestUsers.newUser().email(), "SomePass1")));
        assertBody(res, 405, NOT_SUPPORTED);
    }

    @Test
    @DisplayName("TC-APIAUTH-09 Empty-string email and password return \"User not found!\"")
    @Tag("api") @Tag("regression")
    void tcApiAuth09_emptyStringsReturnUserNotFound() {
        TestUser user = registeredUser();
        assertBody(post("", ""), 404, NOT_FOUND);
        assertBody(post("", user.password()), 404, NOT_FOUND);
        assertBody(post(user.email(), ""), 404, NOT_FOUND);
    }

    @Test
    @DisplayName("TC-APIAUTH-10 Upper-cased email of a real account returns \"User not found!\"")
    @Tag("api") @Tag("regression")
    void tcApiAuth10_upperCasedEmailReturnsUserNotFound() {
        TestUser user = registeredUser();
        String upper = user.email().toUpperCase();
        assertThat(upper).isNotEqualTo(user.email());
        assertBody(post(upper, user.password()), 404, NOT_FOUND);
    }

    @Test
    @DisplayName("TC-APIAUTH-11 Deleted account can no longer verify")
    @Tag("api") @Tag("regression")
    void tcApiAuth11_deletedAccountCannotVerify() {
        TestUser user = registeredUser();
        JsonObject del = AccountApi.delete(user);
        assertThat(del.get("responseCode").getAsInt()).isEqualTo(200);
        assertThat(del.get("message").getAsString()).isEqualTo("Account deleted!");
        assertBody(post(user.email(), user.password()), 404, NOT_FOUND);
    }
}
