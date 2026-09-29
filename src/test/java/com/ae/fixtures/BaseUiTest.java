package com.ae.fixtures;

import com.ae.config.Config;
import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * One Browser per test class, one BrowserContext + Page per test.
 * Blocks ad networks, records a trace, and on failure saves screenshot + trace under target/.
 * Also runs API cleanup for users registered via {@link #registerForCleanup(TestUser)}.
 */
public abstract class BaseUiTest {

    private static final Pattern AD_HOSTS = Pattern.compile(
            ".*(googlesyndication|doubleclick|adservice|googleads|adnxs|adsafeprotected|pagead2).*");

    private static Playwright playwright;
    private static Browser browser;

    protected BrowserContext context;
    protected Page page;

    private final List<TestUser> cleanupUsers = new ArrayList<>();
    private String testName = "unknown";

    @BeforeAll
    static void launchBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(Config.headless())
                .setSlowMo(Config.slowMo()));
    }

    @AfterAll
    static void closeBrowser() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }

    @BeforeEach
    void createContext(TestInfo info) {
        testName = info.getTestClass().map(Class::getSimpleName).orElse("Test")
                + "-" + info.getTestMethod().map(m -> m.getName()).orElse("method");

        context = browser.newContext(new Browser.NewContextOptions()
                .setBaseURL(Config.baseUrl())
                .setViewportSize(1366, 900));
        context.route(AD_HOSTS, Route::abort);
        context.tracing().start(new Tracing.StartOptions().setScreenshots(true).setSnapshots(true));
        page = context.newPage();
    }

    @AfterEach
    void closeContext(TestInfo info) {
        // JUnit doesn't tell us pass/fail here; we always keep the trace, and take a screenshot
        // if the page is still open. Cheap enough for a small suite; the triager reads them.
        Path traceDir = Paths.get("target", "traces");
        Path shotDir = Paths.get("target", "screenshots");
        try {
            if (page != null && !page.isClosed()) {
                page.screenshot(new Page.ScreenshotOptions()
                        .setPath(shotDir.resolve(testName + ".png")).setFullPage(true));
            }
        } catch (PlaywrightException ignored) { }
        try {
            context.tracing().stop(new Tracing.StopOptions().setPath(traceDir.resolve(testName + ".zip")));
        } catch (PlaywrightException ignored) { }
        context.close();

        for (TestUser u : cleanupUsers) {
            try { AccountApi.delete(u); } catch (RuntimeException ignored) { }
        }
        cleanupUsers.clear();
    }

    /** Ensure this user's account is deleted via API after the test, pass or fail. */
    protected void registerForCleanup(TestUser user) {
        cleanupUsers.add(user);
    }
}
