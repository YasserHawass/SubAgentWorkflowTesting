package com.ae.specs.ui;

import com.ae.config.Config;
import com.ae.fixtures.BaseUiTest;
import com.ae.pages.ProductDetailsPage;
import com.ae.pages.ProductsPage;
import com.ae.pages.components.Header;
import com.ae.pages.components.ProductCard;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.assertions.LocatorAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

class ProductsTest extends BaseUiTest {

    private ProductsPage productsPage;
    private ProductDetailsPage detailsPage;
    private Header header;

    @BeforeEach
    void initPages() {
        productsPage = new ProductsPage(page);
        detailsPage = new ProductDetailsPage(page);
        header = new Header(page);
    }

    /** GET /api/productsList; asserts responseCode 200 first and returns the products array. */
    private JsonArray apiProducts() {
        APIResponse res = page.request().get(Config.apiBaseUrl() + "/productsList");
        JsonObject body = JsonParser.parseString(res.text()).getAsJsonObject();
        assertThat(body.get("responseCode").getAsInt()).isEqualTo(200);
        return body.getAsJsonArray("products");
    }

    private static String apiCategory(JsonObject product) {
        JsonObject cat = product.getAsJsonObject("category");
        return cat.getAsJsonObject("usertype").get("usertype").getAsString() + " > " + cat.get("category").getAsString();
    }

    private static String unescapeHtml(String s) {
        return s.replace("&lt;", "<").replace("&gt;", ">").replace("&quot;", "\"")
                .replace("&#x27;", "'").replace("&#39;", "'").replace("&amp;", "&");
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-PRODUCTS-01 opens All Products from the home header")
    void tcProducts01_openFromHeader() {
        page.navigate("/");
        assertThat(page).hasTitle("Automation Exercise");
        header.openProducts();
        assertThat(page).hasURL(Config.baseUrl() + "/products");
        // Observed live: the DOM text is "All Products", CSS renders it upper-case; innerText sees "ALL PRODUCTS".
        assertThat(productsPage.heading()).hasText("ALL PRODUCTS",
                new LocatorAssertions.HasTextOptions().setUseInnerText(true));
        assertThat(productsPage.firstCard().name()).isVisible();
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-PRODUCTS-02 View Product of the first card opens its detail page")
    void tcProducts02_viewFirstProduct() {
        productsPage.open();
        productsPage.firstCard().viewProduct();
        assertThat(page).hasURL(Pattern.compile("^.*/product_details/\\d+$"));
        assertThat(detailsPage.name()).isVisible();
        assertThat(detailsPage.name()).not().isEmpty();
        assertThat(detailsPage.price()).isVisible();
        assertThat(detailsPage.category()).hasText(Pattern.compile("^Category: \\S.*"));
        assertThat(detailsPage.availability()).hasText(Pattern.compile("^Availability: \\S.*"));
        assertThat(detailsPage.condition()).hasText(Pattern.compile("^Condition: \\S.*"));
        assertThat(detailsPage.brand()).hasText(Pattern.compile("^Brand: \\S.*"));
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-PRODUCTS-03 grid count and names match GET /api/productsList")
    void tcProducts03_gridMatchesApi() {
        JsonArray products = apiProducts();
        List<String> apiNames = new ArrayList<>();
        products.forEach(p -> apiNames.add(p.getAsJsonObject().get("name").getAsString()));

        productsPage.open();
        assertThat(productsPage.firstCard().name()).isVisible();
        // Observed live: not paginated and not lazy-loaded, all products are on one page.
        assertThat(productsPage.cards()).hasCount(products.size());
        assertThat(new HashSet<>(productsPage.cardNames())).isEqualTo(new HashSet<>(apiNames));
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-PRODUCTS-04 every card shows image, price, name, Add to cart and View Product")
    void tcProducts04_cardContents() {
        productsPage.open();
        assertThat(productsPage.firstCard().name()).isVisible();
        List<ProductCard> cards = productsPage.allCards();
        assertThat(cards).isNotEmpty();
        for (ProductCard card : cards) {
            assertThat(card.image()).hasCount(1);
            assertThat(card.price()).hasText(Pattern.compile("^Rs\\. \\d+$"));
            assertThat(card.name()).not().isEmpty();
            assertThat(card.addToCartButton()).hasCount(1);
            assertThat(card.viewProductLink()).hasCount(1);
        }
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-PRODUCTS-05 detail page name and price match the clicked card")
    void tcProducts05_detailMatchesCard() {
        productsPage.open();
        ProductCard card = productsPage.firstCard();
        String name = card.name().innerText().trim();
        String price = card.price().innerText().trim();
        card.viewProduct();
        assertThat(detailsPage.name()).hasText(name);
        assertThat(detailsPage.price()).hasText(price);
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-PRODUCTS-06 detail page labels for category, availability, condition, brand")
    void tcProducts06_detailLabels() {
        JsonObject first = apiProducts().get(0).getAsJsonObject();
        detailsPage.open(first.get("id").getAsInt());
        // Observed live: rows read "Category: Women > Tops", "Availability: In Stock", "Condition: New", "Brand: Polo".
        assertThat(detailsPage.category()).hasText("Category: " + apiCategory(first));
        assertThat(detailsPage.availability()).hasText(Pattern.compile("^Availability: \\S.*"));
        assertThat(detailsPage.condition()).hasText(Pattern.compile("^Condition: \\S.*"));
        assertThat(detailsPage.brand()).hasText("Brand: " + first.get("brand").getAsString());
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-PRODUCTS-07 quantity input on the detail page")
    void tcProducts07_quantityInput() {
        int id = apiProducts().get(0).getAsJsonObject().get("id").getAsInt();
        detailsPage.open(id);
        assertThat(detailsPage.quantityInput()).hasValue("1");
        assertThat(detailsPage.addToCartButton()).isVisible();
        detailsPage.typeQuantity("3");
        assertThat(detailsPage.quantityInput()).hasValue("3");
        // Observed live (probe): 0 stays "0", -1 stays "-1", 1.5 stays "1.5"; letters are rejected (value "").
        detailsPage.typeQuantity("0");
        assertThat(detailsPage.quantityInput()).hasValue("0");
        detailsPage.typeQuantity("abc");
        assertThat(detailsPage.quantityInput()).hasValue("");
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-PRODUCTS-08 every API product id opens a populated detail page")
    void tcProducts08_everyIdOpens() {
        // Plain HTTP GETs instead of one browser navigation per product: the page is server-rendered, so the HTML holds name and price.
        Pattern namePattern = Pattern.compile("product-information\">.*?<h2>([^<]*)</h2>", Pattern.DOTALL);
        Pattern pricePattern = Pattern.compile("<span>\\s*<span>([^<]*)</span>");
        for (var element : apiProducts()) {
            JsonObject p = element.getAsJsonObject();
            String id = p.get("id").getAsString();
            APIResponse res = page.request().get(Config.baseUrl() + "/product_details/" + id);
            assertThat(res.status()).as("status for id " + id).isEqualTo(200);
            String html = res.text();
            Matcher name = namePattern.matcher(html);
            assertThat(name.find()).as("name heading for id " + id).isTrue();
            assertThat(unescapeHtml(name.group(1)).trim()).as("name for id " + id).isEqualTo(p.get("name").getAsString());
            Matcher price = pricePattern.matcher(html);
            assertThat(price.find()).as("price for id " + id).isTrue();
            assertThat(price.group(1).trim()).as("price for id " + id).isEqualTo(p.get("price").getAsString());
        }
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-PRODUCTS-09 nonexistent product id")
    void tcProducts09_nonexistentId() {
        detailsPage.open("999999");
        // Observed live: HTTP 200, URL unchanged, an empty detail page (blank name, price and category), header rendered.
        assertThat(detailsPage.status()).isEqualTo(200);
        assertThat(page).hasURL(Config.baseUrl() + "/product_details/999999");
        assertThat(detailsPage.name()).isEmpty();
        assertThat(detailsPage.price()).hasCount(0);
        assertThat(detailsPage.category()).hasText("Category:");
        assertThat(header.signupLoginLink).isVisible();
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-PRODUCTS-10 malformed product id abc")
    void tcProducts10_nonNumericId() {
        detailsPage.open("abc");
        assertThat(detailsPage.status()).isEqualTo(404);
        assertThat(page).hasURL(Config.baseUrl() + "/product_details/abc");
        // Observed live: HTTP 404 with a Django DEBUG page (URLconf listing, "DEBUG = True") and no site header.
        // The sane behaviour asserted here is a clean 404 that still renders the site header.
        assertThat(page.content()).doesNotContain("DEBUG = True").doesNotContain("URLconf");
        assertThat(header.signupLoginLink).isVisible();
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-PRODUCTS-10 malformed product id 0")
    void tcProducts10_zeroId() {
        detailsPage.open("0");
        // Observed live: same as an unknown id: HTTP 200, empty detail page, header rendered.
        assertThat(detailsPage.status()).isEqualTo(200);
        assertThat(page).hasURL(Config.baseUrl() + "/product_details/0");
        assertThat(detailsPage.name()).isEmpty();
        assertThat(page.content()).doesNotContain("DEBUG = True");
        assertThat(header.signupLoginLink).isVisible();
    }
}
