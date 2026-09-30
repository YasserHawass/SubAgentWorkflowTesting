package com.ae.specs.ui;

import com.ae.config.Config;
import com.ae.fixtures.BaseUiTest;
import com.ae.fixtures.CatalogApi;
import com.ae.pages.HomePage;
import com.ae.pages.ProductListingPage;
import com.ae.pages.ProductsPage;
import com.ae.pages.components.CategorySidebar;
import com.ae.pages.components.Header;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.playwright.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static com.ae.pages.ProductListingPage.exactText;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

class CategoriesTest extends BaseUiTest {

    private static final Pattern CATEGORY_URL = Pattern.compile(".*/category_products/\\d+$");

    private HomePage home;
    private ProductsPage products;
    private ProductListingPage listing;
    private CategorySidebar sidebar;
    private Header header;

    @BeforeEach
    void initPages() {
        home = new HomePage(page);
        products = new ProductsPage(page);
        listing = new ProductListingPage(page);
        sidebar = new CategorySidebar(page);
        header = new Header(page);
    }

    /** Products from GET /api/productsList (ground truth). */
    private JsonArray apiProducts() {
        return CatalogApi.listOf(page.request().get(Config.baseUrl() + "/api/productsList"), "products");
    }

    private static Set<String> apiNames(JsonArray all, String usertype, String category) {
        Set<String> names = new HashSet<>();
        for (JsonElement e : all) {
            JsonObject p = e.getAsJsonObject();
            JsonObject cat = p.getAsJsonObject("category");
            if (cat.getAsJsonObject("usertype").get("usertype").getAsString().equals(usertype)
                    && cat.get("category").getAsString().equals(category)) {
                names.add(p.get("name").getAsString());
            }
        }
        return names;
    }

    /** Status below 500, no Django debug output, site chrome present. */
    private void assertCleanPage(Response response) {
        assertThat(response.status()).isLessThan(500);
        String html = page.content();
        assertThat(html).doesNotContain("DEBUG = True").doesNotContain("URLconf").doesNotContain("Traceback");
        assertThat(header.productsLink).isVisible();
        assertThat(sidebar.heading()).isVisible();
    }

    private String firstSubcategory(String group) {
        sidebar.expand(group);
        return sidebar.subcategoryNames(group).get(0);
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-CATEGORIES-01 Women subcategory page from the home sidebar")
    void tcCategories01_womenSubcategoryPageFromHome() {
        home.open();
        assertThat(sidebar.heading()).isVisible();
        assertThat(sidebar.group("Women")).isVisible();
        assertThat(sidebar.group("Men")).isVisible();
        assertThat(sidebar.group("Kids")).isVisible();

        String sub = firstSubcategory("Women");
        sidebar.open("Women", sub);

        assertThat(page).hasURL(CATEGORY_URL);
        // Observed: DOM text is mixed case ("Women - Dress Products"); CSS renders it upper case.
        assertThat(listing.heading()).hasText(exactText("Women - " + sub + " Products"));
        // Asserts at least one card exists; position is irrelevant.
        assertThat(listing.cards().first()).isVisible();
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-CATEGORIES-02 Switch from Women to a Men subcategory")
    void tcCategories02_switchFromWomenToMen() {
        home.open();
        assertThat(sidebar.heading()).isVisible();
        String womenSub = firstSubcategory("Women");
        sidebar.open("Women", womenSub);
        assertThat(listing.heading()).hasText(exactText("Women - " + womenSub + " Products"));
        String womenUrl = page.url();
        List<String> womenNames = listing.cardNames();

        String menSub = firstSubcategory("Men");
        sidebar.open("Men", menSub);

        assertThat(listing.heading()).hasText(exactText("Men - " + menSub + " Products"));
        assertThat(page).hasURL(CATEGORY_URL);
        assertThat(page.url()).isNotEqualTo(womenUrl);
        // Asserts at least one card exists; position is irrelevant. The next line reads the cards by name.
        assertThat(listing.cards().first()).isVisible();
        assertThat(listing.cardNames()).doesNotContainAnyElementsOf(womenNames);
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-CATEGORIES-03 Listed products belong to the category")
    void tcCategories03_listedProductsBelongToCategory() {
        home.open();
        String sub = firstSubcategory("Women");
        sidebar.open("Women", sub);
        assertThat(listing.heading()).hasText(exactText("Women - " + sub + " Products"));
        // Asserts at least one card exists; position is irrelevant. The next line reads the cards by name.
        assertThat(listing.cards().first()).isVisible();

        Set<String> expected = apiNames(apiProducts(), "Women", sub);
        assertThat(expected).isNotEmpty();
        assertThat(listing.cards()).hasCount(expected.size());
        assertThat(new HashSet<>(listing.cardNames())).isEqualTo(expected);
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-CATEGORIES-04 Category sidebar on /products leads to the same kind of page")
    void tcCategories04_categorySidebarOnProducts() {
        products.open();
        assertThat(sidebar.heading()).isVisible();
        assertThat(sidebar.group("Women")).isVisible();
        assertThat(sidebar.group("Men")).isVisible();
        assertThat(sidebar.group("Kids")).isVisible();

        String sub = firstSubcategory("Kids");
        sidebar.open("Kids", sub);

        assertThat(page).hasURL(CATEGORY_URL);
        assertThat(listing.heading()).hasText(exactText("Kids - " + sub + " Products"));
        // Observed: Kids is not empty (Dress 6 cards). Card count equals the API count for usertype + category.
        int expected = apiNames(apiProducts(), "Kids", sub).size();
        assertThat(expected).isGreaterThan(0);
        assertThat(listing.cards()).hasCount(expected);
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-CATEGORIES-05 Accordion expands and collapses")
    void tcCategories05_accordionExpandsAndCollapses() {
        products.open();
        assertThat(sidebar.panel("Women")).isHidden();
        assertThat(sidebar.panel("Men")).isHidden();

        sidebar.expand("Women");
        String women = sidebar.subcategoryNames("Women").get(0);
        assertThat(sidebar.subcategory("Women", women)).isVisible();

        sidebar.expand("Men");
        String men = sidebar.subcategoryNames("Men").get(0);
        assertThat(sidebar.subcategory("Men", men)).isVisible();
        // Observed: opening Men collapses Women (single-open accordion).
        assertThat(sidebar.panel("Women")).isHidden();
        assertThat(sidebar.subcategory("Women", women)).isHidden();
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-CATEGORIES-06 Nonexistent category id 9999 shows a clean page")
    void tcCategories06_nonexistentCategoryId() {
        Response response = listing.openCategory("9999");

        assertCleanPage(response);
        // Observed: HTTP 200, heading DOM text " -  Products" (empty usertype and subcategory), no cards.
        assertThat(response.status()).isEqualTo(200);
        assertThat(listing.heading()).hasText(Pattern.compile("^\\s*-\\s+Products\\s*$"));
        assertThat(listing.cards()).hasCount(0);
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-CATEGORIES-07 Malformed category id abc never shows a DEBUG page")
    void tcCategories07a_malformedIdAbc() {
        // Observed: HTTP 404 with the Django DEBUG page ("DEBUG = True", URLconf). Product bug: this stays red.
        assertMalformedIdIsClean("abc");
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-CATEGORIES-07 Malformed category id 0 never shows a DEBUG page")
    void tcCategories07b_malformedIdZero() {
        // Observed: HTTP 200, same empty page as 9999 (heading " -  Products", no cards).
        assertMalformedIdIsClean("0");
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-CATEGORIES-07 Malformed category id -1 never shows a DEBUG page")
    void tcCategories07c_malformedIdMinusOne() {
        // Observed: HTTP 404 with the Django DEBUG page. Product bug: this stays red.
        assertMalformedIdIsClean("-1");
    }

    private void assertMalformedIdIsClean(String id) {
        Response response = listing.openCategory(id);
        assertCleanPage(response);
        assertThat(listing.cards()).hasCount(0);
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-CATEGORIES-08 Every sidebar subcategory link resolves and lists products")
    void tcCategories08_everySubcategoryLinkResolves() {
        JsonArray all = apiProducts();
        products.open();

        List<String[]> links = new ArrayList<>(); // {group, name, href}
        for (String group : new String[] {"Women", "Men", "Kids"}) {
            sidebar.expand(group);
            List<String> names = sidebar.subcategoryNames(group);
            List<String> hrefs = sidebar.subcategoryHrefs(group);
            for (int i = 0; i < names.size(); i++) {
                links.add(new String[] {group, names.get(i), hrefs.get(i)});
            }
        }
        assertThat(links).isNotEmpty();

        int sum = 0;
        for (String[] l : links) {
            assertThat(page.request().get(Config.baseUrl() + l[2]).status()).isEqualTo(200);
            page.navigate(Config.baseUrl() + l[2]);
            assertThat(listing.heading()).hasText(exactText(l[0] + " - " + l[1] + " Products"));
            int expected = apiNames(all, l[0], l[1]).size();
            assertThat(listing.cards()).hasCount(expected);
            sum += expected;
        }
        // Observed: the per-subcategory counts add up to the full catalogue (34 at last observation).
        assertThat(sum).isEqualTo(all.size());
    }
}
