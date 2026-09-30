package com.ae.fixtures;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.FormData;
import com.microsoft.playwright.options.RequestOptions;

import static org.assertj.core.api.Assertions.assertThat;

/** Shared JSON helpers for the catalogue API tests (products, brands, search). */
public final class CatalogApi {

    public static final String NOT_SUPPORTED = "This request method is not supported.";

    private CatalogApi() {
    }

    public static JsonObject json(APIResponse res) {
        return JsonParser.parseString(res.text()).getAsJsonObject();
    }

    /** Asserts body responseCode, then message, then HTTP status (observed: always 200 on these endpoints). */
    public static void assertBody(APIResponse res, int code, String message) {
        JsonObject body = json(res);
        assertThat(body.get("responseCode").getAsInt()).isEqualTo(code);
        assertThat(body.get("message").getAsString()).isEqualTo(message);
        assertThat(res.status()).isEqualTo(200);
    }

    /** Asserts responseCode 200 and returns the named array; HTTP status is asserted last. */
    public static JsonArray listOf(APIResponse res, String key) {
        JsonObject body = json(res);
        assertThat(body.get("responseCode").getAsInt()).isEqualTo(200);
        assertThat(body.has(key)).isTrue();
        assertThat(res.status()).isEqualTo(200);
        return body.getAsJsonArray(key);
    }

    public static JsonArray products(APIRequestContext api) {
        return listOf(api.get("productsList"), "products");
    }

    public static APIResponse search(APIRequestContext api, String term) {
        return api.post("searchProduct", RequestOptions.create()
                .setForm(FormData.create().set("search_product", term)));
    }

    public static APIResponse send(APIRequestContext api, String method, String path) {
        return api.fetch(path, RequestOptions.create().setMethod(method).setForm(FormData.create()));
    }
}
