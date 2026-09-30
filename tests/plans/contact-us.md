# Test plan: contact-us

Source: site TC6 (Contact Us Form). Route `/contact_us`.

## Scope
In: form fields (name, email, subject, message, file upload), Submit, JS confirm dialog, success text, "Home" button.
Out: email delivery on the backend (not observable); header/footer chrome.

## Preconditions & data
- No account needed. Guest is enough. Open via the header "Contact Us" link and via direct URL.
- Name/email/subject/message generated (email like `ae.test+<epoch>@example.com`, never hardcoded).
- Upload file is a generated temp file (small .txt), created in the test and deleted afterwards. No committed fixture.
- Register a dialog handler BEFORE clicking Submit (see quirks).

## Scenarios
| # | Scenario | Expected | Priority | Type |
|---|---|---|---|---|
| 1 | TC6 happy path: open Contact Us, "GET IN TOUCH" visible, fill all fields, attach small temp file, Submit, accept the confirm dialog | Success text visible: "Success! Your details have been submitted successfully." Dialog was shown once. | P1 | ui |
| 2 | After success click the "Home" button | Lands on home page (URL is base `/`, home content visible) | P1 | ui |
| 3 | Submit, then DISMISS the confirm dialog | Probe live and record: expected form is not submitted and success text is not shown; fields keep values | P2 | ui |
| 4 | Required fields: submit with each of name, email, subject, message empty (one at a time), dialog handler accepting | Probe live and record: expect HTML5 required blocks submit, no dialog, no success text. Record whether the file is required | P2 | ui |
| 5 | Invalid email (e.g. "not-an-email") with other fields valid | Probe live and record: expect browser email-type validation blocks submit; record any site message | P3 | ui |
| 6 | Large file (e.g. a few MB) and odd type (e.g. .exe or no extension) as upload | Probe live and record: site map says any file is accepted; record success text or error, and any size limit | P3 | ui |

Realistic break (covered by 1): the confirm dialog is never handled, so Submit hangs; or the success text is asserted before the page reloads.

## Risks & quirks
- The JS `confirm` dialog must be accepted, and the handler must be registered before the click. An unhandled dialog is auto-dismissed by Playwright, which may make scenario 1 fail.
- Ads/interstitial can cover the Submit button; use the ads rule in CONVENTIONS.
- Upload uses the file input, not an OS dialog.
- The success alert may sit above the form and the "Home" button follows it; do not assume the form stays visible.

## Out of scope / open questions
- Max file size and allowed types: probe live and record.
- Does dismissing the dialog keep the typed values: probe live and record.
- Exact required-field behaviour (browser-native vs. server): probe live and record.
- Is the "Home" button a link or button role: probe live and record.
