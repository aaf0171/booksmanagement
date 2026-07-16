Feature: Account activation endpoint

Description:
Expose a public endpoint to activate a borrower's account using the activation
token that was generated and emailed during registration (books-0013, books-0014).

When the user clicks the activation link in the email, the frontend extracts the
token from the URL query parameter and sends it to this endpoint.
The backend validates the token, activates the login account
(`enabled = true`), and marks the token as used.

-------------------------------------------------------------------------------

Business rules

- The endpoint is public — no JWT authentication required.
- The request body contains a single `token` field (the plaintext activation token).
- The token is hashed (SHA-256) before database lookup.
- The token must exist in `activation_tokens`, be unused, and not be expired.
- On successful activation:
  - The login's `enabled` field is set to `true`.
  - The activation token is marked as used (`used_at = NOW()`).
- If the token is invalid, expired, or already used, a 400 error is returned.
- The endpoint does NOT return the plaintext token — it only returns a success message.
- Only one login can be associated with a given activation token (enforced by the data model).

-------------------------------------------------------------------------------

Architecture

AuthController
       │
       ▼
AuthService
       │
       ├── ActivationTokenService ──► ActivationTokenRepository
       │       ├── validateToken(token) → Optional<ActivationToken>
       │       └── markTokenAsUsed(tokenId)
       │
       └── LoginsRepository
               └── findById(loginId) → Login

-------------------------------------------------------------------------------

Endpoint

1. Activate account

POST /api/auth/activate

Request body

{
  "token": "aBcDeFgHiJkLmNoPqRsTuVwXyZ0123456789_-aB"
}

Responses

200 OK

{
  "message": "Account activated successfully"
}

400 Bad Request

Token is invalid, expired, or already used.

Response body

{
  "message": "Invalid or expired activation token"
}

-------------------------------------------------------------------------------

Service contracts

ActivationTokenService

findValidToken(token): Optional<ActivationToken>
1. Hash the received plaintext token (SHA-256).
2. Look up by tokenHash in activation_tokens table.
3. If not found → return empty.
4. If usedAt is not null → return empty (already used).
5. If expiresAt is before now → return empty (expired).
6. Otherwise → return the ActivationToken.

markAsUsed(tokenId): void
1. Set used_at = NOW() in activation_tokens table.

AuthService

activate(token): void
1. Call ActivationTokenService.findValidToken(token).
2. If token not found → throw IllegalArgumentException("Invalid or expired activation token").
3. Retrieve the login by loginId from the token.
4. Set login.enabled = true.
5. Persist the updated login.
6. Call ActivationTokenService.markAsUsed(token.getId()).
7. Return success.

-------------------------------------------------------------------------------

Spring Security configuration

Add `/api/auth/activate` to the permit-all list:

.requestMatchers(
    "/api/auth/login",
    "/api/auth/refresh",
    "/api/auth/logout",
    "/api/auth/activate"
).permitAll()

-------------------------------------------------------------------------------

Acceptance criteria

✓ POST /api/auth/activate with a valid token returns 200.
✓ POST /api/auth/activate activates the login (enabled = true).
✓ POST /api/auth/activate marks the activation token as used.
✓ POST /api/auth/activate returns 400 for an invalid token.
✓ POST /api/auth/activate returns 400 for an expired token.
✓ POST /api/auth/activate returns 400 for an already-used token.
✓ POST /api/auth/activate does NOT require JWT authentication.
✓ The endpoint is added to WebSecurityConfig permit-all list.
✓ The token is hashed before database lookup (never stored or compared in plaintext).

-------------------------------------------------------------------------------

Deliverables

Generate:

- AuthController (add POST /api/auth/activate endpoint)
- ActivationTokenService (add findValidToken method)
- AuthService (add activate method)
- WebSecurityConfig (add /api/auth/activate to permitAll)
- ActivationRequestDTO
- ActivationResponseDTO
- Domain unit tests
- Application service tests
- Repository integration tests
- REST controller integration tests
- OpenAPI documentation
- cURL examples

-------------------------------------------------------------------------------

Domain unit tests

✓ shouldHashTokenCorrectly()
✓ shouldFindValidTokenByHash()
✓ shouldReturnEmptyForInvalidToken()
✓ shouldReturnEmptyForExpiredToken()
✓ shouldReturnEmptyForUsedToken()
✓ shouldMarkTokenAsUsed()

-------------------------------------------------------------------------------

Application service tests

✓ shouldActivateAccountWithValidToken()
✓ shouldSetLoginEnabledToTrue()
✓ shouldMarkTokenAsUsedAfterActivation()
✓ shouldReturn400WhenTokenIsInvalid()
✓ shouldReturn400WhenTokenIsExpired()
✓ shouldReturn400WhenTokenIsAlreadyUsed()

-------------------------------------------------------------------------------

Repository integration tests

✓ shouldSaveActivationToken()
✓ shouldFindByTokenHash()
✓ shouldFindActiveByLoginId()
✓ shouldMarkTokenAsUsed()
✓ shouldReturnZeroRowsWhenTokenNotExists()
✓ shouldUpdateLoginEnabled()

-------------------------------------------------------------------------------

REST controller integration tests

✓ POST_activate_valid_token_returns_200()
✓ POST_activate_invalid_token_returns_400()
✓ POST_activate_expired_token_returns_400()
✓ POST_activate_used_token_returns_400()
✓ POST_activate_login_enabled_set_to_true()
✓ POST_activate_token_marked_as_used()
✓ POST_activate_without_token_returns_400()

-------------------------------------------------------------------------------

OpenAPI

POST /api/auth/activate
  Request: { token: string }
  Response 200: { message: string }
  Response 400: { message: string }

-------------------------------------------------------------------------------

Example cURL

Successful activation

curl --request POST \
     --url http://localhost:8082/api/auth/activate \
     --header 'Content-Type: application/json' \
     --data '{
       "token": "aBcDeFgHiJkLmNoPqRsTuVwXyZ0123456789_-aB"
     }'

Expected: 200 with message "Account activated successfully".

Invalid token

curl --request POST \
     --url http://localhost:8082/api/auth/activate \
     --header 'Content-Type: application/json' \
     --data '{
       "token": "invalidTokenValue"
     }'

Expected: 400 with error message.

Expired token

curl --request POST \
     --url http://localhost:8082/api/auth/activate \
     --header 'Content-Type: application/json' \
     --data '{
       "token": "expiredTokenValue"
     }'

Expected: 400 with error message.

Already used token

curl --request POST \
     --url http://localhost:8082/api/auth/activate \
     --header 'Content-Type: application/json' \
     --data '{
       "token": "usedTokenValue"
     }'

Expected: 400 with error message.

-------------------------------------------------------------------------------

@EXECUTION
max_iterations: 10
