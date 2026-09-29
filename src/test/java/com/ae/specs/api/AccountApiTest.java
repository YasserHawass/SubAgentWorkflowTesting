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

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AccountApiTest extends BaseApiTest {

    private static final String NOT_FOUND_ACCOUNT = "Account not found!";
    private static final String NOT_FOUND_EMAIL = "Account not found with this email, try another email!";
    private static final String NOT_SUPPORTED = "This request method is not supported.";

    private final List<TestUser> created = new ArrayList<>();

    /** Deletes with the password the test registered; tolerates 404 "Account not found!" (JSON either way). */
    @AfterEach
    void cleanup() {
        for (TestUser u : created) {
            AccountApi.delete(u);
        }
        created.clear();
    }

    private void registerForCleanup(TestUser user) {
        created.add(user);
    }

    private TestUser registeredUser() {
        TestUser user = TestUsers.newUser();
        registerForCleanup(user);
        JsonObject res = AccountApi.create(user);
        assertThat(res.get("responseCode").getAsInt()).isEqualTo(201);
        assertThat(res.get("message").getAsString()).isEqualTo("User created!");
        return user;
    }

    private static TestUser with(TestUser u, String name, String password, String address1, String city) {
        return new TestUser(name, u.email(), password, u.title(), u.birthDay(), u.birthMonth(), u.birthYear(),
                u.firstName(), u.lastName(), u.company(), address1, u.address2(), u.country(), u.state(), city,
                u.zipcode(), u.mobile());
    }

    /** Full create/update field set; fields named in {@code omit} are left out. */
    private static FormData form(TestUser u, String... omit) {
        FormData f = FormData.create();
        String[][] fields = {
                {"name", u.name()}, {"email", u.email()}, {"password", u.password()}, {"title", u.title()},
                {"birth_date", u.birthDay()}, {"birth_month", u.birthMonth()}, {"birth_year", u.birthYear()},
                {"firstname", u.firstName()}, {"lastname", u.lastName()}, {"company", u.company()},
                {"address1", u.address1()}, {"address2", u.address2()}, {"country", u.country()},
                {"zipcode", u.zipcode()}, {"state", u.state()}, {"city", u.city()},
                {"mobile_number", u.mobile()}};
        for (String[] kv : fields) {
            if (!List.of(omit).contains(kv[0])) f.set(kv[0], kv[1]);
        }
        return f;
    }

    private APIResponse send(String method, String path, FormData form) {
        return api.fetch(path, RequestOptions.create().setMethod(method).setForm(form));
    }

    private APIResponse getDetail(String email) {
        return api.get("getUserDetailByEmail?email=" + URLEncoder.encode(email, StandardCharsets.UTF_8));
    }

    private static JsonObject json(APIResponse res) {
        return JsonParser.parseString(res.text()).getAsJsonObject();
    }

    private static void assertBody(APIResponse res, int code, String message) {
        JsonObject body = json(res);
        assertThat(body.get("responseCode").getAsInt()).isEqualTo(code);
        assertThat(body.get("message").getAsString()).isEqualTo(message);
        assertThat(res.status()).isEqualTo(200);
    }

    private static JsonObject userOf(APIResponse res) {
        JsonObject body = json(res);
        assertThat(body.get("responseCode").getAsInt()).isEqualTo(200);
        assertThat(body.has("user")).isTrue();
        assertThat(res.status()).isEqualTo(200);
        return body.getAsJsonObject("user");
    }

    /** Asserts every field the site returns in `user` matches what was sent. Observed: birth_date comes back as birth_day, firstname/lastname as first_name/last_name. */
    private static void assertUserMatches(JsonObject user, TestUser u) {
        assertThat(user.get("name").getAsString()).isEqualTo(u.name());
        assertThat(user.get("email").getAsString()).isEqualTo(u.email());
        assertThat(user.get("title").getAsString()).isEqualTo(u.title());
        assertThat(user.get("birth_day").getAsString()).isEqualTo(u.birthDay());
        assertThat(user.get("birth_month").getAsString()).isEqualTo(u.birthMonth());
        assertThat(user.get("birth_year").getAsString()).isEqualTo(u.birthYear());
        assertThat(user.get("first_name").getAsString()).isEqualTo(u.firstName());
        assertThat(user.get("last_name").getAsString()).isEqualTo(u.lastName());
        assertThat(user.get("company").getAsString()).isEqualTo(u.company());
        assertThat(user.get("address1").getAsString()).isEqualTo(u.address1());
        assertThat(user.get("address2").getAsString()).isEqualTo(u.address2());
        assertThat(user.get("country").getAsString()).isEqualTo(u.country());
        assertThat(user.get("zipcode").getAsString()).isEqualTo(u.zipcode());
        assertThat(user.get("state").getAsString()).isEqualTo(u.state());
        assertThat(user.get("city").getAsString()).isEqualTo(u.city());
    }

    private void assertVerifyLogin(String email, String password, int code, String message) {
        APIResponse res = api.post("verifyLogin", RequestOptions.create()
                .setForm(FormData.create().set("email", email).set("password", password)));
        assertBody(res, code, message);
    }

    @Test
    @DisplayName("TC-APIACCOUNT-01 Create account with all fields returns \"User created!\"")
    @Tag("api") @Tag("smoke")
    void tcApiAccount01_createAccountWithAllFields() {
        TestUser user = TestUsers.newUser();
        registerForCleanup(user);
        assertBody(send("POST", "createAccount", form(user)), 201, "User created!");
        assertVerifyLogin(user.email(), user.password(), 200, "User exists!");
    }

    @Test
    @DisplayName("TC-APIACCOUNT-02 Get user detail returns every sent field")
    @Tag("api") @Tag("smoke")
    void tcApiAccount02_getUserDetailReturnsSentFields() {
        TestUser user = registeredUser();
        JsonObject u = userOf(getDetail(user.email()));
        assertUserMatches(u, user);
        // Observed: password and mobile_number are not returned.
        assertThat(u.has("password")).isFalse();
        assertThat(u.has("mobile_number")).isFalse();
    }

    @Test
    @DisplayName("TC-APIACCOUNT-03 Update account changes name, city and address1")
    @Tag("api") @Tag("smoke")
    void tcApiAccount03_updateChangesNameCityAddress() {
        TestUser user = registeredUser();
        TestUser changed = with(user, "Updated Name", user.password(), "1 Updated Street", "Updated City");
        assertBody(send("PUT", "updateAccount", form(changed)), 200, "User updated!");
        assertUserMatches(userOf(getDetail(user.email())), changed);
    }

    @Test
    @DisplayName("TC-APIACCOUNT-04 Lifecycle: create, get, update, get, delete, get")
    @Tag("api") @Tag("smoke")
    void tcApiAccount04_lifecycle() {
        TestUser user = TestUsers.newUser();
        registerForCleanup(user);
        assertBody(send("POST", "createAccount", form(user)), 201, "User created!");
        assertUserMatches(userOf(getDetail(user.email())), user);

        TestUser changed = with(user, "Lifecycle Name", user.password(), user.address1(), "Lifecycle City");
        assertBody(send("PUT", "updateAccount", form(changed)), 200, "User updated!");
        assertUserMatches(userOf(getDetail(user.email())), changed);

        assertBody(send("DELETE", "deleteAccount",
                FormData.create().set("email", user.email()).set("password", user.password())),
                200, "Account deleted!");

        APIResponse gone = getDetail(user.email());
        assertBody(gone, 404, NOT_FOUND_EMAIL);
        assertThat(json(gone).has("user")).isFalse();
    }

    @Test
    @DisplayName("TC-APIACCOUNT-05 Delete account with correct credentials")
    @Tag("api") @Tag("smoke")
    void tcApiAccount05_deleteWithCorrectCredentials() {
        TestUser user = registeredUser();
        assertBody(send("DELETE", "deleteAccount",
                FormData.create().set("email", user.email()).set("password", user.password())),
                200, "Account deleted!");
        assertVerifyLogin(user.email(), user.password(), 404, "User not found!");
    }

    @Test
    @DisplayName("TC-APIACCOUNT-06 Create twice with the same email is rejected and keeps the first account")
    @Tag("api") @Tag("smoke")
    void tcApiAccount06_duplicateCreateRejected() {
        TestUser user = TestUsers.newUser();
        TestUser original = with(user, "Original Name", user.password(), user.address1(), user.city());
        registerForCleanup(original);
        assertBody(send("POST", "createAccount", form(original)), 201, "User created!");

        TestUser second = with(user, "Second Name", user.password(), user.address1(), user.city());
        assertBody(send("POST", "createAccount", form(second)), 400, "Email already exists!");

        JsonObject u = userOf(getDetail(user.email()));
        assertThat(u.get("name").getAsString()).isEqualTo("Original Name");
    }

    @Test
    @DisplayName("TC-APIACCOUNT-07 Create with a missing field (email, password, name, company)")
    @Tag("api") @Tag("regression")
    void tcApiAccount07_createWithMissingField() {
        TestUser noEmail = TestUsers.newUser();
        assertBody(send("POST", "createAccount", form(noEmail, "email")), 400,
                "Bad request, email parameter is missing in POST request.");

        TestUser noPassword = TestUsers.newUser();
        registerForCleanup(noPassword);
        assertBody(send("POST", "createAccount", form(noPassword, "password")), 400,
                "Bad request, password parameter is missing in POST request.");

        TestUser noName = TestUsers.newUser();
        registerForCleanup(noName);
        assertBody(send("POST", "createAccount", form(noName, "name")), 400,
                "Bad request, name parameter is missing in POST request.");

        TestUser noCompany = TestUsers.newUser();
        registerForCleanup(noCompany);
        assertBody(send("POST", "createAccount", form(noCompany, "company")), 201, "User created!");
    }

    @Test
    @DisplayName("TC-APIACCOUNT-08 Delete with correct email and wrong password is rejected")
    @Tag("api") @Tag("smoke")
    void tcApiAccount08_deleteWithWrongPassword() {
        TestUser user = registeredUser();
        assertThat(user.password()).isNotEqualTo("Wrong#Pass1");
        assertBody(send("DELETE", "deleteAccount",
                FormData.create().set("email", user.email()).set("password", "Wrong#Pass1")),
                404, NOT_FOUND_ACCOUNT);
        assertVerifyLogin(user.email(), user.password(), 200, "User exists!");
    }

    @Test
    @DisplayName("TC-APIACCOUNT-09 Get detail for unknown email and missing email param")
    @Tag("api") @Tag("regression")
    void tcApiAccount09_getDetailUnknownAndMissingEmail() {
        APIResponse unknown = getDetail(TestUsers.newUser().email());
        assertBody(unknown, 404, NOT_FOUND_EMAIL);
        assertThat(json(unknown).has("user")).isFalse();

        APIResponse missing = api.get("getUserDetailByEmail");
        assertBody(missing, 400, "Bad request, email parameter is missing in GET request.");
        assertThat(json(missing).has("user")).isFalse();
    }

    @Test
    @DisplayName("TC-APIACCOUNT-10 Wrong HTTP methods on the account endpoints")
    @Tag("api") @Tag("regression")
    void tcApiAccount10_wrongHttpMethods() {
        String email = TestUsers.newUser().email();
        FormData f = FormData.create().set("email", email).set("password", "SomePass1");
        // Observed: create/delete/update answer real HTTP 405 with a JSON {"detail": ...} body;
        // getUserDetailByEmail answers HTTP 200 with the api-auth style 405 body.
        String[][] detail405 = {
                {"GET", "createAccount"}, {"PUT", "createAccount"},
                {"POST", "deleteAccount"}, {"GET", "deleteAccount"},
                {"POST", "updateAccount"}, {"DELETE", "updateAccount"}};
        for (String[] c : detail405) {
            APIResponse res = send(c[0], c[1], f);
            assertThat(res.status()).as(c[0] + " " + c[1]).isEqualTo(405);
            assertThat(json(res).get("detail").getAsString()).as(c[0] + " " + c[1])
                    .isEqualTo("Method \"" + c[0] + "\" not allowed.");
        }
        for (String m : new String[]{"POST", "DELETE"}) {
            assertBody(send(m, "getUserDetailByEmail", f), 405, NOT_SUPPORTED);
        }
    }

    @Test
    @DisplayName("TC-APIACCOUNT-11 Update with unknown email and with wrong password is rejected")
    @Tag("api") @Tag("regression")
    void tcApiAccount11_updateUnknownEmailAndWrongPassword() {
        TestUser user = registeredUser();
        TestUser ghost = TestUsers.newUser();
        registerForCleanup(ghost);

        assertBody(send("PUT", "updateAccount", form(ghost)), 404, NOT_FOUND_ACCOUNT);
        assertBody(getDetail(ghost.email()), 404, NOT_FOUND_EMAIL);

        TestUser hacked = with(user, "Hacked Name", "Wrong#Pass1", user.address1(), user.city());
        assertBody(send("PUT", "updateAccount", form(hacked)), 404, NOT_FOUND_ACCOUNT);
        assertThat(userOf(getDetail(user.email())).get("name").getAsString()).isEqualTo(user.name());
    }

    @Test
    @DisplayName("TC-APIACCOUNT-12 Password change via update does not persist")
    @Tag("api") @Tag("regression")
    void tcApiAccount12_passwordChangeViaUpdate() {
        TestUser base = TestUsers.newUser();
        TestUser user = with(base, base.name(), "OldPass#1", base.address1(), base.city());
        registerForCleanup(user);
        assertBody(send("POST", "createAccount", form(user)), 201, "User created!");

        // Observed: update authenticates with the current password, so a new password is treated as a wrong one.
        TestUser changed = with(user, user.name(), "NewPass#2", user.address1(), user.city());
        assertBody(send("PUT", "updateAccount", form(changed)), 404, NOT_FOUND_ACCOUNT);
        assertVerifyLogin(user.email(), "NewPass#2", 404, "User not found!");
        assertVerifyLogin(user.email(), "OldPass#1", 200, "User exists!");
    }
}
