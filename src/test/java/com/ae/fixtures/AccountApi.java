package com.ae.fixtures;

import com.ae.config.Config;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.FormData;
import com.microsoft.playwright.options.RequestOptions;

/**
 * Setup/teardown through the public API so UI tests don't have to register through the UI
 * just to get a logged-in user. Endpoints from docs/site-map.md.
 * Bodies are form-encoded. The site answers HTTP 200 with a responseCode field for most errors.
 */
public final class AccountApi {
    private AccountApi() {}

    public static JsonObject create(TestUser u) {
        return call("post", "createAccount", form(u));
    }

    public static JsonObject delete(TestUser u) {
        return call("delete", "deleteAccount",
                FormData.create().set("email", u.email()).set("password", u.password()));
    }

    public static JsonObject verifyLogin(String email, String password) {
        return call("post", "verifyLogin",
                FormData.create().set("email", email).set("password", password));
    }

    private static FormData form(TestUser u) {
        return FormData.create()
                .set("name", u.name()).set("email", u.email()).set("password", u.password())
                .set("title", u.title())
                .set("birth_date", u.birthDay()).set("birth_month", u.birthMonth()).set("birth_year", u.birthYear())
                .set("firstname", u.firstName()).set("lastname", u.lastName())
                .set("company", u.company())
                .set("address1", u.address1()).set("address2", u.address2())
                .set("country", u.country()).set("zipcode", u.zipcode())
                .set("state", u.state()).set("city", u.city())
                .set("mobile_number", u.mobile());
    }

    private static JsonObject call(String method, String path, FormData body) {
        try (Playwright pw = Playwright.create()) {
            APIRequestContext api = pw.request().newContext();
            String url = Config.apiBaseUrl() + "/" + path;
            RequestOptions opts = RequestOptions.create().setForm(body);
            APIResponse res = switch (method) {
                case "post" -> api.post(url, opts);
                case "put" -> api.put(url, opts);
                case "delete" -> api.delete(url, opts);
                default -> throw new IllegalArgumentException(method);
            };
            String text = res.text();
            api.dispose();
            return JsonParser.parseString(text).getAsJsonObject();
        }
    }
}
