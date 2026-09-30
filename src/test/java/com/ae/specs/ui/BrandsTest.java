package com.ae.specs.ui;

import com.ae.config.Config;
import com.ae.fixtures.BaseUiTest;
import com.ae.fixtures.CatalogApi;
import com.ae.pages.CartPage;
import com.ae.pages.HomePage;
import com.ae.pages.ProductListingPage;
import com.ae.pages.ProductsPage;
import com.ae.pages.components.BrandSidebar;
import com.ae.pages.components.CartModal;
import com.ae.pages.components.Header;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.playwright.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static com.ae.pages.ProductListingPage.exactText;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

class BrandsTest extends BaseUiTest {

    private static final Pattern BRAND_URL = Pattern.compile(".*/brand_products/.+");

    private HomePage home;
    private ProductsPage products;
    private ProductListingPage listing;
    private BrandSidebar sidebar;
    private Header header;

    @BeforeEach
    void initPages() {
        home = new HomePage(page);
        products = new ProductsPage(page);
        listing = new ProductListingPage(page);
        sidebar = new BrandSidebar(page);
        header = new Header(page);
    }

    private JsonArray apiProducts() {
        return CatalogApi.listOf(page.request().get(Config.baseUrl() + "/api/productsList"), "products");
    }

    private static Set<String> apiNames(JsonArray all, String brand) {
        Set<String> names = new HashSet<>();
        for (JsonElement e : all) {
            JsonObject p = e.getAsJsonObject();
            if (p.get("brand").getAsString().equals(brand)) names.add(p.get("name").getAsString());
        }
        return names;
    }

    /** DOM heading text is mixed case: "Brand - <name> Products" (CSS upper-cases it). */
    private static Pattern brandHeading(String brand) {
        return exactText("Brand - " + brand + " Products");
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-BRANDS-01 Brands sidebar on /products")
    void tcBrands01_brandsSidebarOnProducts() {
        home.open();
        header.openProducts();

        assertThat(page).hasURL(Config.baseUrl() + "/products");
        assertThat(sidebar.heading()).isVisible();
        // Asserts at least one brand link exists; position is irrelevant.
        assertThat(sidebar.links().first()).isVisible();
        // Observed: each link's text is "(N)Name", N being the product count, e.g. "(6)Polo".
        assertThat(sidebar.linkTexts()).isNotEmpty().allMatch(t -> t.matches("(?s)^\\s*\\(\\d+\\)\\s*\\S.*"));
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-BRANDS-02 Click a brand and see its page")
    void tcBrands02_clickBrandShowsItsPage() {
        products.open();
        assertThat(sidebar.heading()).isVisible();
        String brand = sidebar.brandNames().get(0);
        sidebar.open(brand);

        assertThat(page).hasURL(BRAND_URL);
        assertThat(listing.heading()).hasText(brandHeading(brand));
        // Asserts at least one card exists; position is irrelevant.
        assertThat(listing.cards().first()).isVisible();
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-BRANDS-03 Switch to a different brand")
    void tcBrands03_switchToDifferentBrand() {
        products.open();
        assertThat(sidebar.heading()).isVisible();
        List<String> brands = sidebar.brandNames();
        String first = brands.get(0);
        String second = brands.get(1);

        sidebar.open(first);
        assertThat(listing.heading()).hasText(brandHeading(first));
        // Asserts at least one card exists; position is irrelevant. The next line reads the cards by name.
        assertThat(listing.cards().first()).isVisible();
        List<String> firstNames = listing.cardNames();

        sidebar.open(second);

        assertThat(listing.heading()).hasText(brandHeading(second));
        assertThat(page).hasURL(BRAND_URL);
        // Asserts at least one card exists; position is irrelevant. The next line reads the cards by name.
        assertThat(listing.cards().first()).isVisible();
        assertThat(listing.cardNames()).doesNotContainAnyElementsOf(firstNames);
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-BRANDS-04 Only the chosen brand's products are listed")
    void tcBrands04_onlyChosenBrandProductsListed() {
        products.open();
        assertThat(sidebar.heading()).isVisible();
        String brand = sidebar.brandNames().get(0);
        sidebar.open(brand);
        assertThat(listing.heading()).hasText(brandHeading(brand));
        // Asserts at least one card exists; position is irrelevant. The next line reads the cards by name.
        assertThat(listing.cards().first()).isVisible();

        Set<String> expected = apiNames(apiProducts(), brand);
        assertThat(expected).isNotEmpty();
        assertThat(listing.cards()).hasCount(expected.size());
        assertThat(new HashSet<>(listing.cardNames())).isEqualTo(expected);
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-BRANDS-05 Sidebar count matches listed count and API count")
    void tcBrands05_sidebarCountMatchesListedAndApiCount() {
        JsonArray all = apiProducts();
        products.open();
        assertThat(sidebar.heading()).isVisible();
        List<String> brands = sidebar.brandNames();
        assertThat(brands).isNotEmpty();

        for (String brand : brands) {
            int sidebarCount = sidebar.countOf(brand);
            sidebar.open(brand);
            assertThat(listing.heading()).hasText(brandHeading(brand));
            int apiCount = apiNames(all, brand).size();
            assertThat(sidebarCount).as("sidebar count for " + brand).isEqualTo(apiCount);
            assertThat(listing.cards()).hasCount(apiCount);
        }
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-BRANDS-06 Unknown brand shows a clean page")
    void tcBrands06a_unknownBrandShowsCleanPage() {
        Response response = listing.openBrand("NoSuchBrand");

        assertCleanPage(response);
        // Observed: HTTP 200, heading echoes the name: "Brand - NoSuchBrand Products", no cards.
        assertThat(response.status()).isEqualTo(200);
        assertThat(listing.heading()).hasText(brandHeading("NoSuchBrand"));
        assertThat(listing.cards()).hasCount(0);
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-BRANDS-06 Brand names are case sensitive in the URL")
    void tcBrands06b_brandUrlIsCaseSensitive() {
        products.open();
        assertThat(sidebar.heading()).isVisible();
        String brand = sidebar.brandNames().get(0);
        String lower = brand.toLowerCase();
        assertThat(lower).isNotEqualTo(brand);

        Response response = listing.openBrand(lower);

        assertCleanPage(response);
        // Observed: /brand_products/polo is HTTP 200 with the echoed heading "Brand - polo Products" and no cards.
        assertThat(listing.heading()).hasText(brandHeading(lower));
        assertThat(listing.cards()).hasCount(0);
    }

    private void assertCleanPage(Response response) {
        assertThat(response.status()).isLessThan(500);
        String html = page.content();
        assertThat(html).doesNotContain("DEBUG = True").doesNotContain("URLconf").doesNotContain("Traceback");
        assertThat(header.productsLink).isVisible();
        assertThat(sidebar.heading()).isVisible();
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-BRANDS-07 Add a brand product to the cart and view it")
    void tcBrands07_addBrandProductToCart() {
        products.open();
        assertThat(sidebar.heading()).isVisible();
        sidebar.open(sidebar.brandNames().get(0));
        // Asserts at least one card exists; position is irrelevant. The next line reads the cards by name.
        assertThat(listing.cards().first()).isVisible();
        String name = listing.cardNames().get(0);

        CartModal modal = listing.card(name).addToCart();
        assertThat(modal.heading()).isVisible();
        CartPage cart = modal.viewCart();

        assertThat(page).hasURL(Config.baseUrl() + "/view_cart");
        assertThat(cart.rows()).hasCount(1);
        assertThat(cart.row(name)).isVisible();
        assertThat(cart.quantity(name)).hasText("1");
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-BRANDS-08 Brands with spaces or & in the name resolve and list products")
    void tcBrands08_everyApiBrandResolvesAndListsProducts() {
        JsonArray brandRows = CatalogApi.listOf(
                page.request().get(Config.baseUrl() + "/api/brandsList"), "brands");
        Set<String> apiBrands = new LinkedHashSet<>();
        for (JsonElement e : brandRows) apiBrands.add(e.getAsJsonObject().get("brand").getAsString());
        JsonArray all = apiProducts();

        products.open();
        assertThat(sidebar.heading()).isVisible();
        assertThat(sidebar.brandNames()).containsAll(apiBrands);

        for (String brand : apiBrands) {
            // Observed: hrefs are not percent-encoded: spaces and & stay literal, e.g. /brand_products/Mast & Harbour.
            String href = sidebar.hrefOf(brand);
            assertThat(href).isEqualTo("/brand_products/" + brand);
            assertThat(page.request().get(Config.baseUrl() + href).status()).isEqualTo(200);

            sidebar.open(brand);
            assertThat(listing.heading()).hasText(brandHeading(brand));
            int expected = apiNames(all, brand).size();
            assertThat(expected).isGreaterThan(0);
            assertThat(listing.cards()).hasCount(expected);
        }
    }
}
