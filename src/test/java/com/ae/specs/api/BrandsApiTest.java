package com.ae.specs.api;

import com.ae.fixtures.BaseApiTest;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.playwright.APIResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import static com.ae.fixtures.CatalogApi.NOT_SUPPORTED;
import static com.ae.fixtures.CatalogApi.assertBody;
import static com.ae.fixtures.CatalogApi.json;
import static com.ae.fixtures.CatalogApi.listOf;
import static com.ae.fixtures.CatalogApi.products;
import static com.ae.fixtures.CatalogApi.send;
import static org.assertj.core.api.Assertions.assertThat;

class BrandsApiTest extends BaseApiTest {

    private static final Set<String> BRAND_NAMES = Set.of(
            "Polo", "H&M", "Madame", "Mast & Harbour", "Babyhug", "Allen Solly Junior", "Kookie Kids", "Biba");

    private JsonArray brands() {
        return listOf(api.get("brandsList"), "brands");
    }

    private static Set<String> names(JsonArray brands) {
        Set<String> out = new TreeSet<>();
        for (JsonElement el : brands) out.add(el.getAsJsonObject().get("brand").getAsString());
        return out;
    }

    @Test
    @DisplayName("TC-APIBRANDS-01 GET brandsList returns the brands array")
    @Tag("api") @Tag("smoke")
    void tcApiBrands01_getReturnsBrandsArray() {
        APIResponse res = api.get("brandsList");
        JsonObject body = json(res);
        assertThat(body.get("responseCode").getAsInt()).isEqualTo(200);
        // Observed: 34 entries (only 8 distinct names).
        assertThat(body.getAsJsonArray("brands")).hasSize(34);
        assertThat(res.status()).isEqualTo(200);
    }

    @Test
    @DisplayName("TC-APIBRANDS-02 Each brand has an id and a name, ids are unique")
    @Tag("api") @Tag("smoke")
    void tcApiBrands02_brandShapeAndUniqueIds() {
        JsonArray brands = brands();
        Set<Integer> ids = new HashSet<>();
        for (JsonElement el : brands) {
            JsonObject b = el.getAsJsonObject();
            assertThat(b.get("id").getAsJsonPrimitive().isNumber()).as("id of %s", b).isTrue();
            assertThat(b.get("brand").getAsString()).as("brand of %s", b).isNotBlank();
            ids.add(b.get("id").getAsInt());
        }
        assertThat(ids).hasSize(brands.size());
        // Observed: names repeat (34 entries, 8 distinct) and ids are not contiguous; neither is treated as a contract.
        assertThat(names(brands)).containsExactlyInAnyOrderElementsOf(BRAND_NAMES);
    }

    @Test
    @DisplayName("TC-APIBRANDS-03 Every product brand appears in brandsList")
    @Tag("api") @Tag("regression")
    void tcApiBrands03_productBrandsAreListed() {
        Set<String> productBrands = new TreeSet<>();
        for (JsonElement el : products(api)) {
            productBrands.add(el.getAsJsonObject().get("brand").getAsString());
        }
        Set<String> listed = names(brands());
        assertThat(listed).as("brandsList names must contain every product brand exactly").containsAll(productBrands);
    }

    @Test
    @DisplayName("TC-APIBRANDS-04 PUT brandsList is rejected as unsupported")
    @Tag("api") @Tag("regression")
    void tcApiBrands04_putRejected() {
        // Observed: HTTP 200 with a body responseCode 405 (not a real HTTP 405).
        assertBody(send(api, "PUT", "brandsList"), 405, NOT_SUPPORTED);
    }

    @Test
    @DisplayName("TC-APIBRANDS-05 POST and DELETE brandsList wrong-method format")
    @Tag("api") @Tag("regression")
    void tcApiBrands05_postAndDeleteRejected() {
        for (String method : new String[]{"POST", "DELETE"}) {
            APIResponse res = send(api, method, "brandsList");
            assertBody(res, 405, NOT_SUPPORTED);
            assertThat(json(res).has("brands")).as(method).isFalse();
        }
    }

    @Test
    @DisplayName("TC-APIBRANDS-06 Brands with no products (orphans)")
    @Tag("api") @Tag("regression")
    void tcApiBrands06_noOrphanBrands() {
        Set<String> used = new HashSet<>();
        for (JsonElement el : products(api)) {
            used.add(el.getAsJsonObject().get("brand").getAsString());
        }
        Set<String> orphans = new TreeSet<>(names(brands()));
        orphans.removeAll(used);
        // Observed: the orphan set is empty.
        assertThat(orphans).isEmpty();
    }
}
