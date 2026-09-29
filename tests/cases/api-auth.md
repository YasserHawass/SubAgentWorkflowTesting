# Test cases: api-auth

Plan: tests/plans/api-auth.md

Common rules for every case: requests are form-encoded (`email`, `password`) via `setForm`, base `/api`, class extends `BaseApiTest`. Assert order: JSON body `responseCode`, then `message`, then HTTP status. Where an account is needed, create it via `AccountApi.create(user)` using `TestUsers.newUser()` (assert responseCode 201, "User created!") and call `registerForCleanup(user)`. Guard JSON parsing with Gson.

Value sourcing: responseCode and message are copied verbatim from the docs/site-map.md API table (rows 7-10). Anything not in that table (HTTP status, GET/PUT, empty strings, email case, missing email) is marked OBSERVE: probe live once, write the observed value into the case, and the test asserts that single outcome. OBSERVE values are unknown until the first probe; they are not guesses.

## TC-APIAUTH-01 Valid credentials return "User exists!"  [P1] [api] [smoke]
Preconditions: a registered user created via AccountApi immediately before the call
Steps:
1. POST /api/verifyLogin with form email and password of the user
Expected:
- Body responseCode is 200
- Body message is "User exists!"
- HTTP status is 200 (observed)
Data: TestUsers.newUser()
Cleanup: delete account via AccountApi.delete(email, password)

## TC-APIAUTH-02 Wrong password for a real account returns "User not found!"  [P1] [api] [smoke]
Preconditions: a registered user
Steps:
1. POST /api/verifyLogin with the user's email and password "Wrong#Pass1"
Expected:
- Body responseCode is 404
- Body message is "User not found!"
- HTTP status is 200 (observed)
Data: TestUsers.newUser(); wrong password "Wrong#Pass1" (ensure it differs from the real one)
Cleanup: delete account via AccountApi.delete(email, password)

## TC-APIAUTH-03 Unregistered email returns the same response as wrong password  [P2] [api] [regression]
Preconditions: none; generate a valid-format email that was never registered
Steps:
1. POST /api/verifyLogin with that email and any password
Expected:
- Body responseCode is 404
- Body message is "User not found!" (identical to TC-APIAUTH-02, so no user enumeration difference)
- HTTP status is 200 (observed; one probe returned a transient Cloudflare 520, not reproduced)
Data: TestUsers.newUser().email() without registering it; password "SomePass1"
Cleanup: none

## TC-APIAUTH-04 Missing password parameter returns 400  [P1] [api] [smoke]
Preconditions: none
Steps:
1. POST /api/verifyLogin with form containing only `email` (the `password` key omitted entirely)
Expected:
- Body responseCode is 400
- Body message is "Bad request, email or password parameter is missing in POST request."
- HTTP status is 200 (observed)
Data: generated unregistered email
Cleanup: none

## TC-APIAUTH-05 Missing email parameter (record observed behaviour)  [P2] [api] [regression]
Preconditions: none
Steps:
1. POST /api/verifyLogin with form containing only `password` (the `email` key omitted entirely)
Expected:
- Body responseCode is 400 (observed)
- Body message is "Bad request, email or password parameter is missing in POST request." (observed; same as TC-APIAUTH-04)
- HTTP status is 200 (observed)
- Test asserts that single observed outcome
Data: password "SomePass1"
Cleanup: none

## TC-APIAUTH-06 DELETE on /verifyLogin is not supported  [P1] [api] [smoke]
Preconditions: none (a valid-looking form is sent; no account needed)
Steps:
1. Send DELETE /api/verifyLogin with form email and password
Expected:
- Body responseCode is 405
- Body message is "This request method is not supported."
- HTTP status is 200 (observed)
Data: generated unregistered email; password "SomePass1"
Cleanup: none

## TC-APIAUTH-07 GET on /verifyLogin (record observed behaviour)  [P2] [api] [regression]
Preconditions: none
Steps:
1. Send GET /api/verifyLogin with no params
Expected:
- Body responseCode is 405 (observed)
- Body message is "This request method is not supported." (observed)
- HTTP status is 200 (observed)
- Record the raw body too; if it is not JSON, assert the observed raw shape instead
- Test asserts that single observed outcome
Data: none
Cleanup: none

## TC-APIAUTH-08 PUT on /verifyLogin (record observed behaviour)  [P2] [api] [regression]
Preconditions: none
Steps:
1. Send PUT /api/verifyLogin with form email and password
Expected:
- Body responseCode is 405 (observed)
- Body message is "This request method is not supported." (observed)
- HTTP status is 200 (observed)
- Test asserts that single observed outcome
Data: generated unregistered email; password "SomePass1"
Cleanup: none

## TC-APIAUTH-09 Empty-string email and password (record observed behaviour)  [P3] [api] [regression]
Preconditions: a registered user (needed only for the "one empty, one valid" variant)
Steps (data variants, each asserted separately with its own recorded outcome):
1. POST /api/verifyLogin with `email=` and `password=` both empty
2. POST with `email=` empty and the user's valid password
3. POST with the user's valid email and `password=` empty
Expected:
- For each variant (all three observed identical): responseCode 404, message "User not found!", HTTP status 200 (empty strings count as present, not missing)
- Test asserts the single observed outcome per variant
Data: TestUsers.newUser()
Cleanup: delete account via AccountApi.delete(email, password)

## TC-APIAUTH-10 Upper-cased email of a real account (record observed behaviour)  [P2] [api] [regression]
Preconditions: a registered user whose email contains letters
Steps:
1. POST /api/verifyLogin with the email upper-cased and the correct password
Expected:
- Body responseCode is 404 (observed; email is case-sensitive)
- Body message is "User not found!" (observed)
- HTTP status is 200 (observed)
- Test asserts that single observed outcome
Data: TestUsers.newUser(); email.toUpperCase() (assert it differs from the original)
Cleanup: delete account via AccountApi.delete(email, password)

## TC-APIAUTH-11 Deleted account can no longer verify  [P2] [api] [regression]
Preconditions: a registered user created, then deleted via AccountApi.delete before the check (assert responseCode 200, "Account deleted!")
Steps:
1. POST /api/verifyLogin with the deleted user's email and password
Expected:
- Body responseCode is 404
- Body message is "User not found!" (not "User exists!")
- HTTP status is 200 (observed; one probe returned a transient Cloudflare 520, not reproduced)
Data: TestUsers.newUser()
Cleanup: none beyond the delete already done (registerForCleanup may call delete again; tolerate a not-found response)
