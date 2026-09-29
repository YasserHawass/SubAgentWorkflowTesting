# Test suite conventions

Every agent reads this before writing or reviewing code. If a rule here conflicts with what an agent "usually does", this file wins.

## Stack

- Java 17, Maven, `com.microsoft.playwright:playwright`, JUnit 5 (Jupiter), AssertJ.
- Playwright's own `assertThat` (`com.microsoft.playwright.assertions.PlaywrightAssertions`) for locators; AssertJ for plain values.
- One browser per JVM, one context + page per test (see `BaseUiTest`). Chromium only.
- Config via system properties with defaults in `Config.java`: `base.url`, `playwright.headless`, `playwright.slowmo`.
- Reports: Surefire XML in `target/surefire-reports/`. On failure `BaseUiTest` saves a screenshot and a trace zip under `target/screenshots/` and `target/traces/`.

## Layout

```
pom.xml
CLAUDE.md
docs/site-map.md
tests/
  CONVENTIONS.md        this file
  plans/                spec-reader output, one .md per feature
  cases/                case-designer output, one .md per feature
  reports/              agent outputs (run.json, summaries, bugs/)
src/test/java/com/ae/
  config/Config.java
  fixtures/
    BaseUiTest.java     browser/context/page lifecycle, ad blocking, failure artifacts
    BaseApiTest.java    APIRequestContext lifecycle
    TestUsers.java      user data factory
    AccountApi.java     create/delete account via API for setup/teardown
  pages/
    components/Header.java
    <Page>Page.java
  utils/
    Ads.java
  specs/
    ui/<Feature>Test.java
    api/<Feature>ApiTest.java
src/test/resources/
  junit-platform.properties
```

Package root is `com.ae`. Don't create packages outside it.

## Naming

- Classes: `LoginPage`, `ProductsPage`, `CartPage`; tests `LoginTest`, `ProductsApiTest`.
- Test methods: `tcLogin02_showsErrorForWrongPassword()`; annotate `@DisplayName("TC-LOGIN-02 shows error for wrong password")`.
  The TC id must match the id in `tests/cases/<feature>.md`.
- Tags: `@Tag("smoke")` for the critical path, `@Tag("regression")` for everything else, `@Tag("api")` for API tests. Every test has at least one tag.
- Page object methods are user actions: `loginAs(user)`, `addToCart(name)`.

## Page objects

- Constructor takes `Page page`. Locators are `private final Locator` fields initialised in the constructor.
- No assertions inside page objects. They act and expose state (return `Locator` or values); tests assert.
- One `open()` per page object that navigates to that page's route via `Config.baseUrl()`.
- Shared chrome (header nav, footer) lives in `pages/components/`.
- Methods that lead to another page return that page object.

## Selectors (priority order)

1. `getByRole(AriaRole.X, new GetByRoleOptions().setName("..."))`
2. `getByLabel`, `getByPlaceholder`
3. `data-qa` attributes — this site uses them on forms: `page.locator("[data-qa='login-email']")`
4. `getByText` for static, unique text
5. CSS by stable id/class — last resort, must be justified in a comment
6. XPath — forbidden

Never chain more than two levels of structural CSS (`div > div > ul li:nth-child(3)` is a violation).

## Waiting

- Never `page.waitForTimeout()` or `Thread.sleep()`. Use `assertThat(locator).isVisible()` (auto-waits), `page.waitForURL`, `page.waitForResponse`.
- Timeouts: leave defaults. Raise per-call only with a comment explaining why.

## Dealing with ads

The site injects ad iframes and occasionally a full-page ad overlay. Rules:

- `BaseUiTest` registers `context.route(...)` that aborts `googlesyndication`, `doubleclick`, `adservice`, `googleads` requests. All UI tests extend `BaseUiTest`.
- If an overlay still appears, page objects call `Ads.dismissIfPresent(page)` before the action, not after a failure.

## Test data

- Users: `TestUsers.newUser()` generates a unique email `ae.test+<epoch>@example.com`. Never hardcode an email.
- Accounts created during a test are deleted in `@AfterEach` via `AccountApi.delete(email, password)`. Register the user with `registerForCleanup(user)` in the test so cleanup runs even on failure.
- No shared mutable state between tests. Each test creates what it needs. No `static` mutable fields.

## API tests

- Extend `BaseApiTest`; use the `api` (`APIRequestContext`) field. Base URL from `Config`.
- Bodies are **form-encoded**: `RequestOptions.create().setForm(FormData.create().set("email", ...))`.
- Assert `responseCode` from the JSON body first, then `message`, then shape. HTTP status is frequently 200 even for errors.
- Parse JSON with Gson (`JsonParser.parseString(response.text())`).

## Assertions

- One behaviour per test. Multiple asserts are fine if they verify the same behaviour.
- Prefer `assertThat(locator).hasText(...)` / `containsText(...)` over reading text and comparing with AssertJ.
- Error messages: assert the exact text the site shows (case-sensitive), copied from the live page.

## What agents must NOT do

- Modify `pom.xml`, `BaseUiTest`, `BaseApiTest` or `Config` without the orchestrator's explicit instruction.
- Add `@Disabled` to make a run green. Report the failure instead.
- Add retry logic (loops, `@RepeatedTest`) to a test.
- Catch and swallow exceptions in page objects.
- Leave `System.out.println` debugging in committed tests.

## Definition of done for a test class

- `mvn -q test-compile` passes.
- `mvn test -Dtest=<Class>` passes locally, twice in a row.
- Every TC id from the cases file is present, or its absence is listed in the writer's return note with a reason.
