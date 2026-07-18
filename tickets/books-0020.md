@DESCRIPTION
Feature: Refactor activation flow — explicit business result instead of exceptions

Description:
The account activation flow currently uses business exceptions to handle functional
cases (token expired, token invalid, account already activated).

These situations are part of the normal account lifecycle and should not be
considered technical errors.

The goal is to evolve the backend so that the activation service returns an
explicit business result as a DTO, allowing the frontend to adapt its behaviour
based on the returned status.

-------------------------------------------------------------------------------

Business rules

- The activation endpoint returns an explicit business result (ActivationResponseDTO).
- ActivationStatus enum represents all possible outcomes of the activation process.
- Exceptions are reserved for technical or unexpected errors only.
- The user email is returned only when the token is known and expired (TOKEN_EXPIRED),
  so the frontend can offer to resend the activation link without asking the user
  to re-enter their email address.
- No user email or login identifier is ever disclosed for an invalid token (TOKEN_INVALID)
  to prevent information leakage.
- HTTP status codes are adapted per scenario:
  - 200 OK: activation success, account already activated
  - 410 Gone: token expired (permanent — the token cannot be used)
  - 400 Bad Request: token invalid (malformed, not found, already used)

-------------------------------------------------------------------------------

Architecture

AuthController
       │
       ▼
AuthService
       │
       ├── ActivationTokenService ──► ActivationTokenRepository
       │       └── findTokenResult(token) → ActivationResponseDTO
       │
       └── LoginsRepository

-------------------------------------------------------------------------------

Endpoint

1. Activate account (refactored)

POST /api/auth/activate

Request body

{
  "token": "aBcDeFgHiJkLmNoPqRsTuVwXyZ0123456789_-aB"
}

Responses

200 OK — Activation successful

{
  "status": "SUCCESS"
}

200 OK — Account already activated

{
  "status": "ALREADY_ACTIVATED"
}

410 Gone — Token expired

{
  "status": "TOKEN_EXPIRED",
  "email": "utilisateur@example.com"
}

400 Bad Request — Token invalid

{
  "status": "TOKEN_INVALID"
}

-------------------------------------------------------------------------------

Service contracts

ActivationTokenService

findTokenResult(token): ActivationResponseDTO
1. Hash the received plaintext token (SHA-256).
2. Look up by tokenHash in activation_tokens table.
3. If not found → return ActivationResponseDTO(TOKEN_INVALID, null).
4. If usedAt is not null → check associated login:
   - If login.enabled = true → return ActivationResponseDTO(ALREADY_ACTIVATED, null).
   - If login.enabled = false → return ActivationResponseDTO(TOKEN_INVALID, null).
5. If expiresAt is before now:
   - If a login is associated → return ActivationResponseDTO(TOKEN_EXPIRED, login.email).
   - If no login is associated → return ActivationResponseDTO(TOKEN_INVALID, null).
6. Otherwise → token is valid, proceed with activation logic.

activate(token): ActivationResponseDTO
1. Call ActivationTokenService.findTokenResult(token).
2. If status is TOKEN_EXPIRED or TOKEN_INVALID or ALREADY_ACTIVATED → return the result.
3. If token is valid:
   - Set login.enabled = true.
   - Persist the updated login.
   - Mark the activation token as used (used_at = NOW()).
   - Return ActivationResponseDTO(SUCCESS, null).

-------------------------------------------------------------------------------

Java types

ActivationStatus.java (enum)

public enum ActivationStatus {
    SUCCESS,
    TOKEN_EXPIRED,
    TOKEN_INVALID,
    ALREADY_ACTIVATED
}

ActivationResponseDTO.java (record)

public record ActivationResponseDTO(
    ActivationStatus status,
    String message,
    String email
) {}

- status is always present.
- message is optional, used to provide a human-readable hint to the frontend.
- email is optional, only populated for TOKEN_EXPIRED with a known account.

-------------------------------------------------------------------------------

HTTP status code mapping

Scenario                  | HTTP Status | Email in response
--------------------------|-------------|-------------------
SUCCESS                   | 200 OK      | No
ALREADY_ACTIVATED         | 200 OK      | No
TOKEN_EXPIRED             | 410 Gone    | Yes (if login exists)
TOKEN_INVALID             | 400 Bad     | No

-------------------------------------------------------------------------------

Exceptions that remain

The following exceptions should be kept (technical/unexpected errors only):

- Database connection failure
- Persistence layer exceptions
- Unexpected null values in domain logic
- Infrastructure issues (e.g. mail service unavailable during token generation)

The following exceptions should be removed (replaced by ActivationResponseDTO):

- Token expired exception
- Token invalid exception
- Account already activated exception

-------------------------------------------------------------------------------

Acceptance criteria

✓ Activation service returns an ActivationResponseDTO instead of throwing business exceptions.
✓ ActivationStatus enum exists and represents all possible outcomes (SUCCESS, TOKEN_EXPIRED, TOKEN_INVALID, ALREADY_ACTIVATED).
✓ ActivationResponseDTO record exists with status, message, and email fields.
✓ No expected business scenario throws an exception.
✓ User email is returned only for TOKEN_EXPIRED with a known account.
✓ No email or login identifier is disclosed for TOKEN_INVALID.
✓ HTTP status codes are correct: 200 for SUCCESS and ALREADY_ACTIVATED, 410 for TOKEN_EXPIRED, 400 for TOKEN_INVALID.
✓ Old business exceptions for token expired, token invalid, and account already activated are removed.
✓ Unit tests and integration tests are updated and pass successfully.

-------------------------------------------------------------------------------

Deliverables

Generate:

- ActivationStatus.java (enum)
- ActivationResponseDTO.java (record)
- ActivationTokenService (updated — return ActivationResponseDTO)
- AuthService (updated — return ActivationResponseDTO)
- AuthController (updated — correct HTTP status codes)
- WebSecurityConfig (unchanged, /api/auth/activate remains permitAll)
- Domain unit tests
- Application service tests
- Repository integration tests
- REST controller integration tests
- OpenAPI documentation
- cURL examples

-------------------------------------------------------------------------------

Domain unit tests

✓ shouldReturnSuccessStatusForValidToken()
✓ shouldReturnExpiredStatusForExpiredToken()
✓ shouldReturnInvalidStatusForUnknownToken()
✓ shouldReturnAlreadyActivatedStatusForUsedTokenWithEnabledLogin()
✓ shouldReturnInvalidStatusForUsedTokenWithDisabledLogin()
✓ shouldNotExposeEmailForInvalidToken()
✓ shouldExposeEmailForExpiredTokenWithKnownUser()
✓ shouldNotExposeEmailForExpiredTokenWithoutUser()

-------------------------------------------------------------------------------

Application service tests

✓ shouldActivateAccountSuccessfullyAndReturnSuccess()
✓ shouldSetLoginEnabledToTrueOnActivation()
✓ shouldMarkTokenAsUsedAfterActivation()
✓ shouldReturnTokenExpiredWithEmailWhenTokenIsExpired()
✓ shouldReturnTokenInvalidWithoutEmailWhenTokenDoesNotExist()
✓ shouldReturnAlreadyActivatedWhenTokenIsUsedAndLoginEnabled()
✓ shouldReturnTokenInvalidWhenTokenIsUsedAndLoginDisabled()
✓ shouldThrowOnlyForTechnicalErrors()

-------------------------------------------------------------------------------

Repository integration tests

✓ shouldSaveActivationToken()
✓ shouldFindByTokenHash()
✓ shouldFindActiveByLoginId()
✓ shouldMarkTokenAsUsed()
✓ shouldReturnZeroRowsWhenTokenNotExists()
✓ shouldUpdateLoginEnabled()
✓ shouldFindExpiredTokenByHash()

-------------------------------------------------------------------------------

REST controller integration tests

✓ POST_activate_valid_token_returns_200_with_SUCCESS()
✓ POST_activate_login_enabled_set_to_true()
✓ POST_activate_token_marked_as_used()
✓ POST_activate_expired_token_returns_410_with_email()
✓ POST_activate_expired_token_without_user_returns_410_without_email()
✓ POST_activate_invalid_token_returns_400_without_email()
✓ POST_activate_already_activated_returns_200_with_ALREADY_ACTIVATED()
✓ POST_activate_without_token_returns_400()
✓ POST_activate_no_user_data_leaked_for_invalid_token()

-------------------------------------------------------------------------------

OpenAPI

POST /api/auth/activate
  Request: { token: string }
  Response 200: { status: "SUCCESS" }
  Response 200: { status: "ALREADY_ACTIVATED" }
  Response 410: { status: "TOKEN_EXPIRED", email: string }
  Response 400: { status: "TOKEN_INVALID" }

-------------------------------------------------------------------------------

Example cURL

Successful activation

curl --request POST \
     --url http://localhost:8082/api/auth/activate \
     --header 'Content-Type: application/json' \
     --data '{
       "token": "aBcDeFgHiJkLmNoPqRsTuVwXyZ0123456789_-aB"
     }'

Expected: 200 with {"status": "SUCCESS"}

Account already activated

curl --request POST \
     --url http://localhost:8082/api/auth/activate \
     --header 'Content-Type: application/json' \
     --data '{
       "token": "usedTokenValue"
     }'

Expected: 200 with {"status": "ALREADY_ACTIVATED"}

Token expired

curl --request POST \
     --url http://localhost:8082/api/auth/activate \
     --header 'Content-Type: application/json' \
     --data '{
       "token": "expiredTokenValue"
     }'

Expected: 410 with {"status": "TOKEN_EXPIRED", "email": "utilisateur@example.com"}

Token invalid

curl --request POST \
     --url http://localhost:8082/api/auth/activate \
     --header 'Content-Type: application/json' \
     --data '{
       "token": "invalidTokenValue"
     }'

Expected: 400 with {"status": "TOKEN_INVALID"}

-------------------------------------------------------------------------------

@EXECUTION
max_iterations: 10
