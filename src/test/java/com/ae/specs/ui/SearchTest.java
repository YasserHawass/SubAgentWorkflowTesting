package com.ae.specs.ui;

import com.ae.config.Config;
import com.ae.fixtures.BaseUiTest;
import com.ae.pages.ProductsPage;
import com.ae.pages.components.Header;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.options.FormData;
import com.microsoft.playwright.options.RequestOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

class SearchTest extends BaseUiTest {

    private static final LocatorAssertions.HasTextOptions INNER = new LocatorAssertions.HasTextOptions().setUseInnerText(true);

    private ProductsPage productsPage;
    private Header header;

    @BeforeEach
    void openProductsFromHome() {
        productsPage = new ProductsPage(page);
        header = new Header(page);
        // Site TC9 steps 1-3: home -> Products -> "All Products" is shown.
        page.navigate("/");
        header.openProducts();
        assertThat(page).hasURL(Config.baseUrl() + "/products");
        assertThat(productsPage.heading()).isVisible();
    }

    /** Searches and waits for the results URL (observed shape: /products?search=term). */
    private void searchAndWait(String term) {
        productsPage.search(term);
        assertThat(page).hasURL(Config.baseUrl() + "/products?search=" + term);
        assertThat(productsPage.searchedHeading()).isVisible();
    }

    private List<String> namesWhenLoaded() {
        assertThat(productsPage.firstCard().name()).isVisible();
        return productsPage.cardNames();
    }

    private int apiTotalProducts() {
        APIResponse all = page.request().get(Config.apiBaseUrl() + "/productsList");
        return JsonParser.parseString(all.text()).getAsJsonObject().getAsJsonArray("products").size();
    }

    /** POST /api/searchProduct with a search term; asserts responseCode 200 first, returns the products. */
    private List<JsonObject> apiSearch(String term) {
        APIResponse res = page.request().post(Config.apiBaseUrl() + "/searchProduct",
                RequestOptions.create().setForm(FormData.create().set("search_product", term)));
        JsonObject body = JsonParser.parseString(res.text()).getAsJsonObject();
        assertThat(body.get("responseCode").getAsInt()).isEqualTo(200);
        JsonArray products = body.getAsJsonArray("products");
        List<JsonObject> list = new ArrayList<>();
        for (JsonElement e : products) list.add(e.getAsJsonObject());
        return list;
    }

    private static String apiCategory(JsonObject p) {
        return p.getAsJsonObject("category").get("category").getAsString();
    }

    /** Observed live: the site matches the term against the product name OR its category name. */
    private static boolean matchesNameOrCategory(JsonObject p, String term) {
        String t = term.toLowerCase();
        return p.get("name").getAsString().toLowerCase().contains(t) || apiCategory(p).toLowerCase().contains(t);
    }

    /** Every UI card must be a product the API returns for the term, and that product must match by name or category. */
    private void assertEveryCardMatches(List<String> uiNames, String term) {
        List<JsonObject> api = apiSearch(term);
        for (String name : uiNames) {
            JsonObject p = api.stream().filter(x -> x.get("name").getAsString().equals(name)).findFirst().orElse(null);
            assertThat(p).as("API result for card '" + name + "'").isNotNull();
            assertThat(matchesNameOrCategory(p, term)).as("'" + name + "' matches '" + term + "' by name or category").isTrue();
        }
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-SEARCH-01 Search \"Tshirt\" shows the Searched Products view")
    void tcSearch01_searchTshirt() {
        searchAndWait("Tshirt");
        // Observed live: DOM text "Searched Products", CSS renders it upper-case; innerText sees "SEARCHED PRODUCTS".
        assertThat(productsPage.searchedHeading()).hasText("SEARCHED PRODUCTS", INNER);
        // Observed live: 6 results for "Tshirt".
        assertThat(productsPage.cards()).hasCount(6);
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-SEARCH-02 Every result relates to the searched term \"Dress\"")
    void tcSearch02_resultsRelateToDress() {
        searchAndWait("Dress");
        List<String> names = namesWhenLoaded();
        assertThat(names).isNotEmpty();
        // Observed live: the search also matches the category name, so "Sleeves Top and Short - Blue & Pink"
        // (category "Dress") is returned although its name lacks "Dress". Assert each card matches by name or category.
        assertThat(names).anyMatch(n -> !n.toLowerCase().contains("dress"));
        assertEveryCardMatches(names, "Dress");
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-SEARCH-03 Search is case-insensitive")
    void tcSearch03_caseInsensitive() {
        searchAndWait("tshirt");
        List<String> lower = namesWhenLoaded();
        searchAndWait("TSHIRT");
        List<String> upper = namesWhenLoaded();
        searchAndWait("Tshirt");
        List<String> mixed = namesWhenLoaded();
        // Observed live: identical count (6) and names in the same order for all three casings.
        assertThat(lower).isNotEmpty();
        assertThat(upper).isEqualTo(lower);
        assertThat(mixed).isEqualTo(lower);
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-SEARCH-04 Partial term \"shirt\" is a superset of \"Tshirt\"")
    void tcSearch04_partialTermSuperset() {
        searchAndWait("Tshirt");
        List<String> tshirt = namesWhenLoaded();
        searchAndWait("shirt");
        List<String> shirt = namesWhenLoaded();
        assertThat(shirt.size()).isGreaterThanOrEqualTo(tshirt.size());
        assertThat(shirt).containsAll(tshirt);
        // Observed live: "shirt" also returns names without it (category "Tops & Shirts"), so match by name or category.
        assertEveryCardMatches(shirt, "shirt");
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-SEARCH-05 Term with no matches shows no products and no error")
    void tcSearch05_noMatches() {
        searchAndWait("zzzxqv123");
        assertThat(productsPage.cards()).hasCount(0);
        assertThat(header.productsLink).isVisible();
        assertThat(productsPage.searchBox()).isVisible();
        // Observed live: the "Searched Products" heading is still shown.
        assertThat(productsPage.searchedHeading()).hasText("SEARCHED PRODUCTS", INNER);
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-SEARCH-06 Empty search returns the full catalog")
    void tcSearch06_emptySearch() {
        productsPage.search("");
        // Observed live: URL /products?search=, heading stays "All Products" (no Searched Products heading), all products listed.
        assertThat(page).hasURL(Config.baseUrl() + "/products?search=");
        assertThat(productsPage.heading()).isVisible();
        assertThat(productsPage.searchedHeading()).hasCount(0);
        assertThat(productsPage.cards()).hasCount(apiTotalProducts());
        assertThat(header.productsLink).isVisible();
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-SEARCH-07 Special characters and markup are handled safely")
    void tcSearch07_specialCharacters() {
        // Observed live: <b>x</b>, '; -- and % each return 0 cards under "Searched Products"; no bold element is created.
        for (String term : new String[]{"<b>x</b>", "'; --", "%"}) {
            productsPage.open();
            productsPage.search(term);
            assertThat(page).hasURL(Pattern.compile("^.*/products\\?search=.+"));
            assertThat(productsPage.searchedHeading()).isVisible();
            assertThat(productsPage.cards()).hasCount(0);
            assertThat(productsPage.searchBox()).hasValue(term);
            // CSS: a <b> anywhere on the page would mean the input was rendered as HTML (tag selector, no structure).
            assertThat(page.locator("b")).hasCount(0);
            assertThat(header.productsLink).isVisible();
        }
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-SEARCH-07 Ampersand term")
    void tcSearch07_ampersand() {
        productsPage.search("&");
        // Correct behaviour: the term is URL-encoded and the results equal the API search for "&" (not the full catalog).
        // Currently fails: the site sends /products?search=& (see bugs).
        assertThat(page).hasURL(Pattern.compile("^.*/products\\?search=%26$"));
        List<String> apiNames = new ArrayList<>();
        apiSearch("&").forEach(p -> apiNames.add(p.get("name").getAsString()));
        assertThat(productsPage.searchedHeading()).isVisible();
        assertThat(namesWhenLoaded()).containsExactlyInAnyOrderElementsOf(apiNames);
        assertThat(header.productsLink).isVisible();
        assertThat(productsPage.searchBox()).isVisible();
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-SEARCH-08 Search URL and reload keep the results")
    void tcSearch08_reloadKeepsResults() {
        searchAndWait("Tshirt");
        List<String> before = namesWhenLoaded();
        page.reload();
        assertThat(page).hasURL(Config.baseUrl() + "/products?search=Tshirt");
        assertThat(productsPage.searchedHeading()).hasText("SEARCHED PRODUCTS", INNER);
        assertThat(namesWhenLoaded()).isEqualTo(before);
    }

    @Test
    @Tag("api")
    @Tag("regression")
    @DisplayName("TC-SEARCH-09 API search matches the UI results")
    void tcSearch09_apiMatchesUi() {
        List<JsonObject> api = apiSearch("top");
        // Observed live: every returned product contains "top" in its name or its category.
        assertThat(api).isNotEmpty();
        for (JsonObject p : api) {
            assertThat(matchesNameOrCategory(p, "top")).as(p.get("name").getAsString()).isTrue();
        }
        searchAndWait("top");
        List<String> ui = namesWhenLoaded();
        assertThat(ui.size()).isEqualTo(api.size());
        List<String> apiNames = new ArrayList<>();
        api.forEach(p -> apiNames.add(p.get("name").getAsString()));
        assertThat(ui).containsExactlyInAnyOrderElementsOf(apiNames);
    }

    @Test
    @Tag("api")
    @Tag("regression")
    @DisplayName("TC-SEARCH-10 API search without the search_product parameter")
    void tcSearch10_apiMissingParam() {
        APIResponse res = page.request().post(Config.apiBaseUrl() + "/searchProduct");
        JsonObject body = JsonParser.parseString(res.text()).getAsJsonObject();
        assertThat(body.get("responseCode").getAsInt()).isEqualTo(400);
        assertThat(body.get("message").getAsString())
                .isEqualTo("Bad request, search_product parameter is missing in POST request.");
        // Observed live: HTTP status is 200 even though the body says 400.
        assertThat(res.status()).isEqualTo(200);
    }
}
