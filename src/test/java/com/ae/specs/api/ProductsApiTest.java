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

import static com.ae.fixtures.CatalogApi.NOT_SUPPORTED;
import static com.ae.fixtures.CatalogApi.assertBody;
import static com.ae.fixtures.CatalogApi.json;
import static com.ae.fixtures.CatalogApi.products;
import static com.ae.fixtures.CatalogApi.send;
import static org.assertj.core.api.Assertions.assertThat;

class ProductsApiTest extends BaseApiTest {

    @Test
    @DisplayName("TC-APIPRODUCTS-01 GET productsList returns the products array")
    @Tag("api") @Tag("smoke")
    void tcApiProducts01_getReturnsProductsArray() {
        APIResponse res = api.get("productsList");
        JsonObject body = json(res);
        assertThat(body.get("responseCode").getAsInt()).isEqualTo(200);
        assertThat(body.getAsJsonArray("products")).isNotEmpty();
        assertThat(res.status()).isEqualTo(200);
    }

    @Test
    @DisplayName("TC-APIPRODUCTS-02 Every product has the documented shape")
    @Tag("api") @Tag("smoke")
    void tcApiProducts02_everyProductHasShape() {
        JsonArray products = products(api);
        assertThat(products).isNotEmpty();
        for (JsonElement el : products) {
            JsonObject p = el.getAsJsonObject();
            assertThat(p.get("id").getAsJsonPrimitive().isNumber()).as("id of %s", p).isTrue();
            assertThat(p.get("name").getAsString()).as("name of %s", p).isNotBlank();
            assertThat(p.get("price").getAsString()).as("price of %s", p).matches("Rs\\. \\d+");
            assertThat(p.get("brand").getAsString()).as("brand of %s", p).isNotBlank();
            JsonObject category = p.getAsJsonObject("category");
            assertThat(category.getAsJsonObject("usertype").get("usertype").getAsString()).isNotBlank();
            assertThat(category.get("category").getAsString()).isNotBlank();
        }
    }

    @Test
    @DisplayName("TC-APIPRODUCTS-03 Catalogue has 34 unique products")
    @Tag("api") @Tag("smoke")
    void tcApiProducts03_catalogueHas34UniqueProducts() {
        JsonArray products = products(api);
        assertThat(products).hasSize(34);
        Set<Integer> ids = new HashSet<>();
        for (JsonElement el : products) {
            JsonObject p = el.getAsJsonObject();
            ids.add(p.get("id").getAsInt());
            assertThat(p.get("name").getAsString()).isNotBlank();
        }
        assertThat(ids).hasSize(34);
        // Observed: ids are not contiguous 1..34 (gaps at 9-10, 17, 25-27, ...), so contiguity is not asserted.
    }

    @Test
    @DisplayName("TC-APIPRODUCTS-04 POST productsList is rejected as unsupported")
    @Tag("api") @Tag("regression")
    void tcApiProducts04_postRejected() {
        // Observed: HTTP 200 with a body responseCode 405 (not a real HTTP 405).
        assertBody(send(api, "POST", "productsList"), 405, NOT_SUPPORTED);
    }

    @Test
    @DisplayName("TC-APIPRODUCTS-05 PUT and DELETE productsList wrong-method format")
    @Tag("api") @Tag("regression")
    void tcApiProducts05_putAndDeleteRejected() {
        for (String method : new String[]{"PUT", "DELETE"}) {
            APIResponse res = send(api, method, "productsList");
            assertBody(res, 405, NOT_SUPPORTED);
            assertThat(json(res).has("products")).as(method).isFalse();
        }
    }

    @Test
    @DisplayName("TC-APIPRODUCTS-06 Category and brand values are well-formed")
    @Tag("api") @Tag("regression")
    void tcApiProducts06_categoryAndBrandWellFormed() {
        Set<String> usertypes = new HashSet<>();
        for (JsonElement el : products(api)) {
            JsonObject p = el.getAsJsonObject();
            assertThat(p.has("brand") && !p.get("brand").isJsonNull()).as("brand of %s", p).isTrue();
            assertThat(p.get("brand").getAsString()).as("brand of %s", p).isNotEmpty();
            JsonObject category = p.getAsJsonObject("category");
            assertThat(category.get("category").isJsonNull()).as("category of %s", p).isFalse();
            assertThat(category.get("category").getAsString()).as("category of %s", p).isNotEmpty();
            usertypes.add(category.getAsJsonObject("usertype").get("usertype").getAsString());
        }
        assertThat(usertypes).containsExactlyInAnyOrder("Women", "Men", "Kids");
    }
}
