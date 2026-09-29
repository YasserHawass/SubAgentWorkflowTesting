package com.ae.fixtures;

import com.ae.config.Config;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

/** APIRequestContext per test class. Bodies on this site are form-encoded; see CONVENTIONS "API tests". */
public abstract class BaseApiTest {

    private static Playwright playwright;
    protected static APIRequestContext api;

    @BeforeAll
    static void createApi() {
        playwright = Playwright.create();
        api = playwright.request().newContext(new APIRequest.NewContextOptions()
                .setBaseURL(Config.apiBaseUrl() + "/"));
    }

    @AfterAll
    static void disposeApi() {
        if (api != null) api.dispose();
        if (playwright != null) playwright.close();
    }
}
