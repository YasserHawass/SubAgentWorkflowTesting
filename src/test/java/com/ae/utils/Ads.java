package com.ae.utils;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;

/** Best-effort dismissal of the site's occasional full-page ad interstitial. Call before an action, not after a failure. */
public final class Ads {
    private Ads() {}

    public static void dismissIfPresent(Page page) {
        try {
            // Google "vignette" interstitials render in an iframe with a dismiss button; the route abort in
            // BaseUiTest prevents most of them. This handles the leftover.
            Locator close = page.locator("#dismiss-button, [aria-label='Close ad'], .ad-close");
            if (close.count() > 0 && close.first().isVisible()) {
                close.first().click(new Locator.ClickOptions().setTimeout(2000));
            }
            if (page.url().contains("#google_vignette")) {
                page.goBack();
            }
        } catch (PlaywrightException ignored) {
            // never fail a test because ad cleanup failed
        }
    }
}
