# Test cases: contact-us

Plan: tests/plans/contact-us.md

Common setup for every case unless stated: fresh browser context, logged out. Ads blocked via `BaseUiTest`; call `Ads.dismissIfPresent` before clicking. Name, email (`ae.test+<epoch>@example.com` style), subject and message are generated per test. The upload file is a small generated temp .txt created in the test and deleted afterwards. The JS confirm dialog handler is registered BEFORE clicking Submit and counts how many times the dialog appears.

Items marked "probe" are unknown until the live site is observed. The automation-writer records the observed behaviour and the test asserts that single outcome. Exact texts appear in Expected only where site TC6 or docs/site-map.md states them.

## TC-CONTACTUS-01 Submit the contact form and see the success text (site TC6)  [P1] [ui] [smoke]
Preconditions: none
Steps:
1. Open the home page and verify it is visible
2. Click "Contact Us" in the header
3. Verify "GET IN TOUCH" is visible
4. Enter name, email, subject and message
5. Attach the small temp file
6. Click Submit and accept the confirm dialog
Expected:
- "GET IN TOUCH" is visible in step 3
- The confirm dialog appeared exactly once
- "Success! Your details have been submitted successfully." is visible (waited for, not read instantly)
Data: generated name/email/subject/message; temp .txt file
Cleanup: delete the temp file (nothing server-side to remove)
Observed (live, 2026-09-30): dialog text "Press OK to proceed!" appears once; success text is in the .status alert (the same text also sits hidden in the footer subscribe box, so scope to #contact-page). Form is hidden after success.

## TC-CONTACTUS-02 "Home" button after success returns to the home page  [P1] [ui] [regression]
Preconditions: none
Steps:
1. Open /contact_us directly
2. Fill all fields, attach the temp file, click Submit and accept the dialog
3. Wait for the success text
4. Click the "Home" button
Expected:
- URL is the base URL `/`
- Home page content is visible (the header "Signup / Login" link is shown)
- Probe: record whether "Home" is a link or button role; the test uses the observed role and asserts it.
Data: generated form data; temp .txt file
Cleanup: delete the temp file
Observed (live, 2026-09-30): "Home" is a LINK (green btn-success, href="/") inside #contact-page; the header also has a Home link. Click goes to /, header shows Signup / Login.

## TC-CONTACTUS-03 Dismissing the confirm dialog does not submit  [P2] [ui] [regression]
Preconditions: none
Steps:
1. Open /contact_us; fill all fields and attach the temp file
2. Click Submit and DISMISS the confirm dialog
Expected:
- Probe: record observed behaviour; test asserts that single outcome. Recorded aspects: the success text "Success! Your details have been submitted successfully." visibility and whether the typed values remain in the fields.
- The dialog appeared exactly once
Data: generated form data; temp .txt file
Cleanup: delete the temp file
Observed (live, 2026-09-30): dialog appears once; no success text; form stays visible with the typed values kept.

## TC-CONTACTUS-04 Each required field left empty blocks the submit  [P2] [ui] [regression]
Preconditions: none
Steps:
1. Open /contact_us
2. Fill every field except one of name, email, subject, message (one field per run, four runs in fresh contexts); attach the temp file
3. Click Submit with a dialog handler that accepts
Expected:
- Probe: record observed behaviour; test asserts that single outcome per field. Recorded aspects: whether a dialog appears and whether the success text "Success! Your details have been submitted successfully." is shown.
- Probe: record whether omitting the file alone also blocks submit; a fifth run asserts that single outcome.
Data: generated form data with one field blanked; temp .txt file
Cleanup: delete the temp file
Observed (live, 2026-09-30): only Email is required (HTML5): empty email -> no dialog, no success, form stays (valueMissing). Empty name, subject, message, or no file -> dialog once + success text (FLAG: not validated, product question).

## TC-CONTACTUS-05 Invalid email format  [P3] [ui] [regression]
Preconditions: none
Steps:
1. Open /contact_us
2. Enter "not-an-email" as email; fill the other fields validly; attach the temp file
3. Click Submit with a dialog handler that accepts
Expected:
- Probe: record observed behaviour; test asserts that single outcome. Recorded aspects: whether a dialog appears, whether the success text is shown, and any site message.
Data: email "not-an-email"; other generated data; temp .txt file
Cleanup: delete the temp file
Observed (live, 2026-09-30): type=email blocks in the browser (typeMismatch): no dialog, no success, form stays; no site message.

## TC-CONTACTUS-06 Large file and file without extension as upload  [P3] [ui] [regression]
Preconditions: none
Steps:
1. Open /contact_us; fill all fields validly
2. Attach a generated file of a few MB; click Submit and accept the dialog; record the outcome
3. Repeat in a fresh context with a generated file that has no extension
Expected:
- Probe: record observed behaviour per file; test asserts that single outcome. Site-map says any file is accepted, so the expected outcome is the success text "Success! Your details have been submitted successfully."; any size limit or error message is recorded and asserted as observed.
Data: generated temp files (few MB; no extension)
Cleanup: delete both temp files
Observed (live, 2026-09-30): a 3 MB file and a file without extension are both accepted: dialog once + success text; no size limit message.
