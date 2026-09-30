package com.ae.specs.api;

import com.ae.fixtures.BaseApiTest;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static com.ae.fixtures.CatalogApi.NOT_SUPPORTED;
import static com.ae.fixtures.CatalogApi.assertBody;
import static com.ae.fixtures.CatalogApi.json;
import static com.ae.fixtures.CatalogApi.listOf;
import static com.ae.fixtures.CatalogApi.products;
import static com.ae.fixtures.CatalogApi.search;
import static com.ae.fixtures.CatalogApi.send;
import static org.assertj.core.api.Assertions.assertThat;

class SearchApiTest extends BaseApiTest {

    private JsonArray results(String term) {
        return listOf(search(api, term), "products");
    }

    private static Set<Integer> ids(JsonArray products) {
        Set<Integer> out = new TreeSet<>();
        for (JsonElement el : products) out.add(el.getAsJsonObject().get("id").getAsInt());
        return out;
    }

    @Test
    @DisplayName("TC-APISEARCH-01 Search \"top\" matches by name or category")
    @Tag("api") @Tag("smoke")
    void tcApiSearch01_topMatchesNameOrCategory() {
        APIResponse res = search(api, "top");
        assertThat(json(res).get("responseCode").getAsInt()).isEqualTo(200);
        JsonArray products = json(res).getAsJsonArray("products");
        assertThat(products).isNotEmpty();
        for (JsonElement el : products) {
            JsonObject p = el.getAsJsonObject();
            String haystack = (p.get("name").getAsString() + " "
                    + p.getAsJsonObject("category").get("category").getAsString()).toLowerCase();
            assertThat(haystack).as("product %s", p).contains("top");
        }
        // Observed: 14 results, same as the UI.
        assertThat(products).hasSize(14);
        assertThat(res.status()).isEqualTo(200);
    }

    @Test
    @DisplayName("TC-APISEARCH-02 Search is case-insensitive and matches category")
    @Tag("api") @Tag("smoke")
    void tcApiSearch02_caseInsensitiveAndCategory() {
        Set<Integer> lower = ids(results("top"));
        assertThat(ids(results("TOP"))).isEqualTo(lower);
        assertThat(ids(results("Top"))).isEqualTo(lower);

        JsonArray dress = results("Dress");
        // Observed: 9 results, same as the UI.
        assertThat(dress).hasSize(9);
        boolean categoryOnlyMatch = false;
        for (JsonElement el : dress) {
            JsonObject p = el.getAsJsonObject();
            if (!p.get("name").getAsString().toLowerCase().contains("dress")
                    && p.getAsJsonObject("category").get("category").getAsString().equals("Dress")) {
                categoryOnlyMatch = true;
            }
        }
        assertThat(categoryOnlyMatch).as("a Dress-category product whose name lacks 'dress'").isTrue();
    }

    @Test
    @DisplayName("TC-APISEARCH-03 Result entries equal their productsList entries")
    @Tag("api") @Tag("regression")
    void tcApiSearch03_resultsEqualCatalogueEntries() {
        Map<Integer, JsonObject> catalogue = new HashMap<>();
        for (JsonElement el : products(api)) {
            JsonObject p = el.getAsJsonObject();
            catalogue.put(p.get("id").getAsInt(), p);
        }
        JsonArray results = results("top");
        assertThat(results).isNotEmpty();
        for (JsonElement el : results) {
            JsonObject p = el.getAsJsonObject();
            assertThat(p.keySet()).containsExactlyInAnyOrder("id", "name", "price", "brand", "category");
            assertThat(p.get("price").getAsString()).matches("Rs\\. \\d+");
            assertThat(p).isEqualTo(catalogue.get(p.get("id").getAsInt()));
        }
    }

    @Test
    @DisplayName("TC-APISEARCH-04 No-match and special-character input")
    @Tag("api") @Tag("regression")
    void tcApiSearch04_noMatchAndSpecialCharacters() {
        for (String term : new String[]{"zzzxqv123", "<b>x</b>", "'; --", "%"}) {
            APIResponse res = search(api, term);
            String raw = res.text();
            // Observed: HTTP 200, responseCode 200 and an empty products array for all four terms.
            JsonObject body = json(res);
            assertThat(body.get("responseCode").getAsInt()).as(term).isEqualTo(200);
            assertThat(body.getAsJsonArray("products")).as(term).isEmpty();
            assertThat(res.status()).as(term).isEqualTo(200);
            assertThat(raw).as(term).doesNotContain(term);
        }
    }

    @Test
    @DisplayName("TC-APISEARCH-05 Empty search_product")
    @Tag("api") @Tag("regression")
    void tcApiSearch05_emptySearchTerm() {
        // Observed: an empty string counts as present and returns the whole catalogue (34), like the UI.
        APIResponse res = search(api, "");
        assertThat(json(res).get("responseCode").getAsInt()).isEqualTo(200);
        assertThat(json(res).getAsJsonArray("products")).hasSize(34);
        assertThat(res.status()).isEqualTo(200);
    }

    @Test
    @DisplayName("TC-APISEARCH-06 GET, PUT and DELETE on searchProduct")
    @Tag("api") @Tag("regression")
    void tcApiSearch06_wrongMethods() {
        for (String method : new String[]{"GET", "PUT", "DELETE"}) {
            APIResponse res = send(api, method, "searchProduct");
            assertBody(res, 405, NOT_SUPPORTED);
            assertThat(json(res).has("products")).as(method).isFalse();
        }
    }

    @Test
    @DisplayName("TC-APISEARCH-07 JSON body instead of form")
    @Tag("api") @Tag("regression")
    void tcApiSearch07_jsonBodyIgnored() {
        APIResponse res = api.post("searchProduct", RequestOptions.create()
                .setHeader("Content-Type", "application/json")
                .setData("{\"search_product\":\"top\"}"));
        // Observed: only form data is read, so the parameter counts as missing (HTTP 200, body 400).
        assertBody(res, 400, "Bad request, search_product parameter is missing in POST request.");
    }
}
