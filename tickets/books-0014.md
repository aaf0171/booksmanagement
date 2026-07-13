Feature: Generate an activation token

Description:
Allow the system to generate a secure activation token for an existing login.
An activation token is used to securely reset or set a password after account creation.

A login must already exist before a token can be generated.
The token is stored as a cryptographic hash in the database to prevent exposure
if the database is compromised.

-------------------------------------------------------------------------------

Business rules

- An activation token can only be created for an existing login (login must exist).
- A login can have at most one unused activation token at a time.
- If an unused token already exists for the login, it is invalidated (marked as used) before generating a new one.
- The token is a random value (32 bytes, base64-encoded, 44 characters).
- The token hash is stored in the database, never the plaintext token.
- The plaintext token is returned once (in the response) and sent to the user via email.
- The token has a configurable expiration time (default: 1 hour).
- Expired tokens cannot be used to reset a password.
- Used tokens cannot be reused.
- The operation is idempotent — requesting a new token for the same login invalidates the previous unused token.
- Deleting or invalidating a token does not delete the login.

-------------------------------------------------------------------------------

Database schema

New table: activation_tokens

CREATE TABLE activation_tokens (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    login_id    BIGINT       NOT NULL,
    type        VARCHAR(30)  NOT NULL,
    token_hash  VARCHAR(64)  NOT NULL,
    expires_at  DATETIME     NOT NULL,
    used_at     DATETIME     DEFAULT NULL,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_activation_token_login
        FOREIGN KEY (login_id) REFERENCES logins(id)
        ON DELETE CASCADE
);

CREATE UNIQUE INDEX uk_activation_token_hash
    ON activation_tokens(token_hash);

-------------------------------------------------------------------------------

Endpoint

POST /logins/{id}/activation-token

Request body

(none)

Responses

201 Created

A new activation token has been generated and its plaintext value is returned once.

Response body

{
  "id": 1,
  "loginId": 42,
  "type": "ACTIVATION",
  "token": "aBcDeFgHiJkLmNoPqRsTuVwXyZ0123456789_-aB",
  "expiresAt": "2024-03-15T11:30:00",
  "createdAt": "2024-03-15T10:30:00"
}

The `token` field contains the plaintext token. It is returned ONLY once at creation time.
Subsequent GET requests (if any) must return the token field as null or omit it.

400 Bad Request

The login does not exist or is disabled.

404 Not Found

The login does not exist.

-------------------------------------------------------------------------------

Validation

- id must be a positive integer.
- token must be generated using a cryptographically secure random generator (SecureRandom).
- token expiration is configurable (default: 3600 seconds = 1 hour).
- type must be "ACTIVATION".

-------------------------------------------------------------------------------

Persistence

Insert a new record into the activation_tokens table with the hashed token.
If an unused token already exists for the given login_id, mark it as used (set used_at = NOW()).

Example SQL

-- Invalidate any existing unused token
UPDATE activation_tokens
SET used_at = NOW()
WHERE login_id = :id
  AND used_at IS NULL
  AND expires_at > NOW()
  AND type = 'ACTIVATION';

-- Insert the new token
INSERT INTO activation_tokens (
  login_id,
  type,
  token_hash,
  expires_at,
  created_at
)
VALUES (
  :loginId,
  'ACTIVATION',
  :tokenHash,
  :expiresAt,
  NOW()
);

-------------------------------------------------------------------------------

Acceptance criteria

✓ New activation token is generated with valid data.
✓ Plaintext token is returned ONCE at creation time.
✓ Token hash is stored in the database (not plaintext).
✓ Token expires after the configured expiration time.
✓ Existing unused token is invalidated before a new one is created.
✓ Duplicate login creation returns 400.
✓ Non-existent login returns 404.
✓ Disabled login returns 400.
✓ Token is 44 characters (32 bytes base64-encoded).

-------------------------------------------------------------------------------

Example cURL

Successful token generation

curl --request POST \
     --url http://localhost:8082/api/logins/1/activation-token \
     --header 'Content-Type: application/json'

Non-existent login

curl --request POST \
     --url http://localhost:8082/api/logins/99999/activation-token \
     --header 'Content-Type: application/json'

Disabled login

curl --request POST \
     --url http://localhost:8082/api/logins/5/activation-token \
     --header 'Content-Type: application/json'

-------------------------------------------------------------------------------

Deliverables

Generate:

- ActivationToken entity
- ActivationTokenDTO
- ActivationTokenRepository
- ActivationTokenService
- ActivationTokenController
- Domain unit tests
- Application service tests
- Repository integration tests
- REST controller integration tests
- SQL migration (V2__create_activation_tokens_table.sql)
- OpenAPI documentation
- cURL examples

-------------------------------------------------------------------------------

Domain unit tests

✓ shouldGenerateSecureRandomToken()
✓ shouldHashTokenBeforeStorage()
✓ shouldRejectNonExistentLogin()
✓ shouldRejectDisabledLogin()
✓ shouldInvalidateExistingUnusedToken()
✓ shouldCreateNewTokenWhenExpiredTokenExists()
✓ shouldValidateExpirationTime()

-------------------------------------------------------------------------------

Application service tests

✓ shouldGenerateToken()
✓ shouldReturnNotFoundWhenLoginDoesNotExist()
✓ shouldReturnBadRequestWhenLoginDisabled()
✓ shouldInvalidatePreviousUnusedToken()
✓ shouldReturnPlaintextTokenOnce()
✓ shouldRejectExpiredToken()

-------------------------------------------------------------------------------

Repository integration tests

✓ shouldSaveActivationToken()
✓ shouldFindByTokenHash()
✓ shouldFindActiveByLoginId()
✓ shouldMarkTokenAsUsed()
✓ shouldReturnZeroAffectedRowsWhenLoginDoesNotExist()

-------------------------------------------------------------------------------

REST controller integration tests

✓ POST_generate_activation_token_returns_201()
✓ POST_non_existent_login_returns_404()
✓ POST_disabled_login_returns_400()
✓ POST_reuse_login_invalidates_previous_token()
✓ POST_token_is_44_characters()

-------------------------------------------------------------------------------

OpenAPI

POST /api/logins/{id}/activation-token

Responses
- 201 Created — Activation token generated (id, loginId, type, token, expiresAt, createdAt)
- 400 Bad Request — Login does not exist or is disabled
- 404 Not Found — Login not found

@EXECUTION
max_iterations: 10
