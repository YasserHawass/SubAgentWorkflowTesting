package com.ae.pages;

import com.ae.config.Config;
import com.ae.pages.components.CartModal;
import com.ae.utils.Ads;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Response;
import com.microsoft.playwright.options.AriaRole;

import java.util.regex.Pattern;

/** /product_details/<id>. */
public class ProductDetailsPage {
    private final Page page;
    private final Locator info;
    private final Locator name;
    private final Locator price;
    private final Locator category;
    private final Locator availability;
    private final Locator condition;
    private final Locator brand;
    private final Locator quantity;
    private final Locator addToCart;
    private Response lastResponse;

    public ProductDetailsPage(Page page) {
        this.page = page;
        // CSS: the detail block has no role/label/data-qa; .product-information is its only wrapper (last resort).
        info = page.locator(".product-information");
        name = info.getByRole(AriaRole.HEADING);
        // getByText: the price is the only element in the block whose whole text is "Rs. <n>".
        price = info.getByText(Pattern.compile("^Rs\\. \\d+$"));
        // CSS: category/availability/condition/brand are bare <p> rows; filter by their label text (last resort).
        category = info.locator("p").filter(new Locator.FilterOptions().setHasText("Category:"));
        availability = info.locator("p").filter(new Locator.FilterOptions().setHasText("Availability:"));
        condition = info.locator("p").filter(new Locator.FilterOptions().setHasText("Condition:"));
        brand = info.locator("p").filter(new Locator.FilterOptions().setHasText("Brand:"));
        // CSS: the number input has no accessible name (its <label> is not linked); #quantity is its stable id (last resort).
        quantity = info.locator("#quantity");
        addToCart = info.getByRole(AriaRole.BUTTON, new Locator.GetByRoleOptions().setName("Add to cart"));
    }

    public ProductDetailsPage open(int id) {
        return open(String.valueOf(id));
    }

    /** Navigates to /product_details/<id>; the id is a string so malformed ids can be opened too. */
    public ProductDetailsPage open(String id) {
        lastResponse = page.navigate(Config.baseUrl() + "/product_details/" + id);
        Ads.dismissIfPresent(page);
        return this;
    }

    /** HTTP status of the last open(String) navigation. */
    public int status() { return lastResponse.status(); }

    public Locator name() { return name; }
    public Locator price() { return price; }
    /** Full row text, for example "Category: Women > Tops". */
    public Locator category() { return category; }
    /** Full row text, for example "Availability: In Stock". */
    public Locator availability() { return availability; }
    public Locator condition() { return condition; }
    public Locator brand() { return brand; }
    public Locator quantityInput() { return quantity; }
    public Locator addToCartButton() { return addToCart; }

    /** Sets the quantity to a whole number. */
    public void setQuantity(int qty) {
        quantity.fill(String.valueOf(qty));
    }

    /** Clicks "Add to cart" and returns the "Added!" modal. */
    public CartModal addToCart() {
        Ads.dismissIfPresent(page);
        addToCart.click();
        return new CartModal(page);
    }

    /** Types into the quantity input key by key (the input is type=number, so fill() rejects letters). */
    public void typeQuantity(String text) {
        quantity.clear();
        quantity.pressSequentially(text);
    }
}
