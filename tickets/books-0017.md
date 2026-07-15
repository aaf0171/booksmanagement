Feature: JWT Authentication with Refresh Token

Description:
Implement a complete authentication system using JWT access tokens and refresh tokens.
The backend exposes three endpoints: login (generates both tokens), refresh (generates a new
access token from a valid refresh token), and logout (revokes the refresh token).

The JWT access token is short-lived (15 minutes). The refresh token is long-lived (30 days)
and stored as a hash in a dedicated table. This design minimises the impact of a stolen
access token (it expires quickly) while allowing secure session renewal.

-------------------------------------------------------------------------------

Business rules

- Login POST /api/auth/login authenticates the user by username and password.
- On successful authentication, the system generates:
  - A JWT access token (valid 15 minutes) containing sub, login, roles, iat, exp.
  - A refresh token (valid 30 days) — a random value stored as a cryptographic hash.
- The JWT sub field is the technical identifier from the logins table, not the username.
- The JWT contains the user's roles (e.g. BORROWER, ADMIN).
- The access token is returned in the response, NOT stored server-side.
- The refresh token is hashed (SHA-256 or similar) and stored in the refresh_tokens table.
- Only the plaintext refresh token is returned to the client once at creation.
- A login can have multiple active refresh tokens (one per session/device).
- Expired tokens cannot be used.
- Revoked tokens cannot be used.
- POST /api/auth/refresh validates the refresh token and issues a new access token.
- POST /api/auth/logout revokes the refresh token (marks it as revoked).
- The password is verified using the existing PasswordEncoder from the logins table.
- The account must be active (already activated) to authenticate.
- The system uses Spring Security with a JWT filter for stateless authentication.

-------------------------------------------------------------------------------

Architecture

AuthController
       │
       ▼
AuthService
       │
       ├── LoginService ──► LoginRepository + PasswordEncoder + JwtTokenGenerator
       │
       └── RefreshTokenService ──► RefreshTokenRepository

RefreshToken entity:
       │
       ▼
RefreshTokenRepository
       │
       ▼
refresh_tokens table

Spring Security:
       │
       ▼
JwtAuthenticationFilter ──► reads Authorization: Bearer <token> from request header
       │
       ▼
populates SecurityContext with authentication

Frontend (VueJS):
       │
       ▼
authStore.js ──► stores accessToken and refreshToken
authService.js ──► calls auth endpoints
axios.js ──► request interceptor adds Bearer token
         ──► response interceptor handles 401 → retry after refresh

-------------------------------------------------------------------------------

Database schema

New table: refresh_tokens

CREATE TABLE refresh_tokens (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    login_id    BIGINT       NOT NULL,
    token_hash  VARCHAR(64)  NOT NULL,
    expires_at  TIMESTAMP    NOT NULL,
    revoked     BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_refresh_token_login
        FOREIGN KEY (login_id)
        REFERENCES logins(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_refresh_token_hash
    ON refresh_tokens(token_hash);

CREATE INDEX idx_refresh_token_login
    ON refresh_tokens(login_id);

-------------------------------------------------------------------------------

Endpoints

1. Login

POST /api/auth/login

Request body

{
  "username": "jc.dusse",
  "password": "secret"
}

Responses

200 OK

{
  "accessToken": "eyJhbGciOiJIUzI1NiIs...",
  "refreshToken": "a9f82c4b7d1e6f...",
  "expiresIn": 900
}

401 Unauthorized

Invalid credentials or account not active.

2. Refresh

POST /api/auth/refresh

Request body

{
  "refreshToken": "a9f82c4b7d1e6f..."
}

Responses

200 OK

{
  "accessToken": "eyJhbGciOiJIUzI1NiIs...",
  "expiresIn": 900
}

401 Unauthorized

Refresh token is expired, revoked, or invalid.

3. Logout

POST /api/auth/logout

Request body

{
  "refreshToken": "a9f82c4b7d1e6f..."
}

Responses

204 No Content

The refresh token has been revoked.

401 Unauthorized

Refresh token is expired, revoked, or invalid.

-------------------------------------------------------------------------------

JWT Token payload

{
  "sub": "12345",
  "login": "jc.dusse",
  "roles": ["BORROWER"],
  "iat": 1784100000,
  "exp": 1784100900
}

- sub: technical login identifier (logins.id)
- login: username
- roles: list of roles/authorities
- iat: issued at timestamp
- exp: expiration timestamp (iat + 900 for 15 minutes)

-------------------------------------------------------------------------------

Service contracts

AuthService (orchestrator)

- login(username, password): LoginResponse
- refresh(refreshToken): RefreshResponse
- logout(refreshToken): void

LoginService

1. Find login by username.
2. Verify password with PasswordEncoder.
3. Check account is active.
4. Load roles/authorities.
5. Generate JWT access token.
6. Generate and store refresh token (hash it first).
7. Return both tokens.

RefreshTokenService

create(loginId): String (plaintext token)
1. Generate random token (SecureRandom, 32 bytes, base64-encoded).
2. Hash the token.
3. Create RefreshToken entity with expiration (30 days).
4. Persist.
5. Return plaintext token.

validateAndRevoke(token): boolean
1. Hash the received token.
2. Look up in refresh_tokens table.
3. Check: exists, not expired, not revoked.
4. If valid → mark as revoked, return true.
5. Otherwise → return false.

JwtTokenGenerator

generate(login, roles): String (JWT)
1. Build JWT with sub, login, roles, iat, exp.
2. Sign with secret key (externalised in config).
3. Return encoded token string.

-------------------------------------------------------------------------------

Spring Security configuration

- WebSecurityConfig defines:
  - POST /api/auth/login → permit all
  - POST /api/auth/refresh → permit all
  - POST /api/auth/logout → permit all
  - All other endpoints → authenticated
- JwtAuthenticationFilter:
  - Intercepts requests before Spring Security filter chain
  - Extracts Bearer token from Authorization header
  - Validates token signature and expiration
  - Populates SecurityContext
- Stateless session management (no session created)

-------------------------------------------------------------------------------

Frontend integration (VueJS)

authStore.js (Pinia store)

- Stores accessToken and refreshToken (in memory).
- Provides setAccessToken(), setRefreshToken(), clear() methods.
- clear() removes both tokens (called on logout).

authService.js

- login(username, password) → calls POST /api/auth/login
- refresh(refreshToken) → calls POST /api/auth/refresh
- logout(refreshToken) → calls POST /api/auth/logout

axios.js (HTTP client)

Request interceptor:
  - Reads accessToken from authStore.
  - If present, adds Authorization: Bearer <token> to request headers.

Response interceptor:
  - On 401 Unauthorized:
    - Call POST /api/auth/refresh with stored refreshToken.
    - If refresh succeeds → update accessToken in store, retry original request.
    - If refresh fails → clear tokens, redirect to /login.

router/guards.js

- Global before guard on admin routes.
- If no accessToken → redirect to /login.
- If accessToken exists but is invalid → let the 401 interceptor handle it.

-------------------------------------------------------------------------------

Configuration

application.yml

auth:
  jwt:
    secret-key: "${JWT_SECRET_KEY}"
    access-token-expiry-seconds: 900
  refresh-token:
    expiry-days: 30

-------------------------------------------------------------------------------

Acceptance criteria

✓ POST /api/auth/login authenticates valid credentials and returns access token + refresh token.
✓ POST /api/auth/login returns 401 for invalid credentials.
✓ POST /api/auth/login returns 401 for inactive/not-activated accounts.
✓ JWT access token contains sub (login id), login (username), roles, iat, exp.
✓ JWT access token expires after 15 minutes (900 seconds).
✓ POST /api/auth/refresh generates a new access token from a valid refresh token.
✓ POST /api/auth/refresh returns 401 for expired refresh token.
✓ POST /api/auth/refresh returns 401 for revoked refresh token.
✓ POST /api/auth/refresh returns 401 for invalid refresh token.
✓ POST /api/auth/logout revokes the refresh token.
✓ Revoked refresh token cannot be used for /auth/refresh.
✓ Refresh token is stored as a hash in refresh_tokens table (not plaintext).
✓ Refresh token has a 30-day expiration.
✓ Spring Security uses JwtAuthenticationFilter for stateless authentication.
✓ Protected endpoints return 401 when no token or invalid token is provided.
✓ Frontend axios interceptors handle 401 and token refresh automatically.

-------------------------------------------------------------------------------

Example cURL

Login

curl --request POST \
     --url http://localhost:8082/api/auth/login \
     --header 'Content-Type: application/json' \
     --data '{
       "username": "jc.dusse",
       "password": "secret"
     }'

Expected: 200 with accessToken, refreshToken, expiresIn.

Refresh

curl --request POST \
     --url http://localhost:8082/api/auth/refresh \
     --header 'Content-Type: application/json' \
     --data '{
       "refreshToken": "a9f82c4b7d1e6f..."
     }'

Expected: 200 with new accessToken.

Logout

curl --request POST \
     --url http://localhost:8082/api/auth/logout \
     --header 'Content-Type: application/json' \
     --data '{
       "refreshToken": "a9f82c4b7d1e6f..."
     }'

Expected: 204 No Content.

Protected endpoint (e.g. GET /me)

curl --request GET \
     --url http://localhost:8082/api/auth/me \
     --header 'Authorization: Bearer eyJhbGciOiJIUzI1NiIs...'

Expected: 200 with current user info.

-------------------------------------------------------------------------------

Deliverables

Generate:

- RefreshToken entity
- RefreshTokenDTO
- RefreshTokenRepository
- RefreshTokenService
- JwtTokenGenerator
- AuthService (login, refresh, logout orchestration)
- AuthController (POST /login, POST /refresh, POST /logout)
- JwtAuthenticationFilter
- WebSecurityConfig (JWT filter, permit all for auth endpoints)
- application.yml (JWT secret, token expiry config)
- SQL migration for refresh_tokens table
- Domain unit tests
- Application service tests
- Repository integration tests
- REST controller integration tests
- OpenAPI documentation
- cURL examples

Frontend (VueJS):

- src/api/authApi.js
- src/auth/authStore.js (Pinia store for tokens)
- src/auth/authService.js
- src/api/axios.js (with request/response interceptors)
- src/router/guards.js (authentication guard)

-------------------------------------------------------------------------------

Domain unit tests

✓ shouldGenerateJwtTokenWithCorrectClaims()
✓ shouldValidateJwtSignature()
✓ shouldRejectExpiredJwt()
✓ shouldGenerateSecureRandomRefreshToken()
✓ shouldHashRefreshTokenBeforeStorage()
✓ shouldRejectInvalidCredentials()
✓ shouldRejectInactiveAccount()
✓ shouldCreateRefreshTokenWith30DayExpiry()

-------------------------------------------------------------------------------

Application service tests

✓ shouldLoginSuccessfullyAndReturnBothTokens()
✓ shouldReturn401WhenCredentialsAreInvalid()
✓ shouldReturn401WhenAccountIsNotActivated()
✓ shouldRefreshAccessTokenWithValidRefreshToken()
✓ shouldRejectExpiredRefreshToken()
✓ shouldRejectRevokedRefreshToken()
✓ shouldRejectInvalidRefreshToken()
✓ shouldLogoutAndRevokeRefreshToken()
✓ shouldNotReuseRevokedRefreshToken()

-------------------------------------------------------------------------------

Repository integration tests

✓ shouldSaveRefreshToken()
✓ shouldFindRefreshTokenByHash()
✓ shouldMarkRefreshTokenAsRevoked()
✓ shouldReturnZeroRowsWhenTokenNotExists()
✓ shouldExpireRefreshToken()

-------------------------------------------------------------------------------

REST controller integration tests

✓ POST_login_valid_credentials_returns_200_with_tokens()
✓ POST_login_invalid_credentials_returns_401()
✓ POST_login_inactive_account_returns_401()
✓ POST_login_tokens_not_null()
✓ POST_login_accessToken_is_jwt()
✓ POST_login_refreshToken_is_44_characters()
✓ POST_refresh_valid_token_returns_200_with_new_accessToken()
✓ POST_refresh_expired_token_returns_401()
✓ POST_refresh_revoked_token_returns_401()
✓ POST_logout_revokes_refresh_token()
✓ POST_logout_revoked_token_returns_401()
✓ GET_protected_endpoint_without_token_returns_401()
✓ GET_protected_endpoint_with_valid_token_returns_200()

-------------------------------------------------------------------------------

OpenAPI

POST /api/auth/login
  Request: { username: string, password: string }
  Response 200: { accessToken: string, refreshToken: string, expiresIn: int }
  Response 401: { message: string }

POST /api/auth/refresh
  Request: { refreshToken: string }
  Response 200: { accessToken: string, expiresIn: int }
  Response 401: { message: string }

POST /api/auth/logout
  Request: { refreshToken: string }
  Response 204: No Content
  Response 401: { message: string }

GET /api/auth/me
  Headers: Authorization: Bearer <token>
  Response 200: { sub: string, login: string, roles: string[] }
  Response 401: { message: string }

@EXECUTION
max_iterations: 10
