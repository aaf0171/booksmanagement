Feature: Create a login

Description:
Allow the system to create a new login record with a BCrypt-hashed password.

A login is used to authenticate both administrators and borrowers on the platform.
The password is hashed using BCryptPasswordEncoder before being stored.
No plaintext password is ever stored in the database.

-------------------------------------------------------------------------------

Business rules

- A login must have a username (unique, non-empty).
- A login must have a raw password that will be BCrypt-hashed before storage.
- A login is enabled by default.
- The password must not be blank.
- The username must be unique across all logins (administrators and borrowers share the same table).
- The raw password is never returned in any response.
- The last_login field is null until the first authentication.
- The operation is idempotent — creating a login with an existing username returns 409.

-------------------------------------------------------------------------------

Endpoint

POST /logins

Request body

{
  "username": "string",
  "password": "string"
}

Responses

201 Created

The login has been successfully created.

Response body

{
  "id": 1,
  "username": "string",
  "enabled": true,
  "created_at": "2024-03-15T10:30:00"
}

The response must NOT include the password hash.

400 Bad Request

The username is blank, or the password is blank.

409 Conflict

A login with the same username already exists.

-------------------------------------------------------------------------------

Validation

- username: mandatory, non-blank, minimum 3 characters, maximum 100 characters
- password: mandatory, non-blank, minimum 8 characters

-------------------------------------------------------------------------------

Persistence

Insert a new record into the logins table.
The password is hashed using BCryptPasswordEncoder before insertion.

Example SQL

INSERT INTO logins (
  username,
  password_hash,
  enabled,
  last_login,
  created_at
)
VALUES (
  :username,
  :hashedPassword,
  true,
  NULL,
  CURRENT_TIMESTAMP
);

-------------------------------------------------------------------------------

Acceptance criteria

✓ Login is created with BCrypt-hashed password when data is valid.
✓ The raw password is never returned in the response.
✓ Duplicate username is rejected with 409.
✓ Blank username is rejected with 400.
✓ Blank password is rejected with 400.
✓ Password shorter than 8 characters is rejected with 400.
✓ Username shorter than 3 characters is rejected with 400.
✓ Created login is queryable from the database.

-------------------------------------------------------------------------------

Example cURL

Successful creation

curl --request POST \
     --url http://localhost:8082/api/logins \
     --header 'Content-Type: application/json' \
     --data '{
       "username": "admin1",
       "password": "securePass123"
     }'

Duplicate username

curl --request POST \
     --url http://localhost:8082/api/logins \
     --header 'Content-Type: application/json' \
     --data '{
       "username": "admin1",
       "password": "anotherPassword"
     }'

Blank password

curl --request POST \
     --url http://localhost:8082/api/logins \
     --header 'Content-Type: application/json' \
     --data '{
       "username": "admin1",
       "password": ""
     }'

Short password

curl --request POST \
     --url http://localhost:8082/api/logins \
     --header 'Content-Type: application/json' \
     --data '{
       "username": "admin2",
       "password": "short"
     }'

-------------------------------------------------------------------------------

Deliverables

Generate:

- Login entity (model)
- LoginDTO and CreateLoginDTO
- LoginRepository
- LoginService
- LoginController
- GlobalExceptionHandler entries for LoginConflictException and LoginValidationException
- BCryptPasswordEncoder bean in SecurityConfig
- Domain unit tests
- Application service tests
- Repository integration tests
- REST controller integration tests
- OpenAPI documentation

-------------------------------------------------------------------------------

Domain unit tests

✓ shouldHashPasswordWithBCrypt()
✓ shouldRejectBlankUsername()
✓ shouldRejectBlankPassword()
✓ shouldRejectShortPassword()
✓ shouldRejectShortUsername()
✓ shouldCreateLoginWithValidData()

-------------------------------------------------------------------------------

Application service tests

✓ shouldCreateLogin()
✓ shouldThrowConflictWhenUsernameAlreadyExists()
✓ shouldThrowBadRequestWhenUsernameBlank()
✓ shouldThrowBadRequestWhenPasswordBlank()
✓ shouldThrowBadRequestWhenPasswordTooShort()
✓ shouldThrowBadRequestWhenUsernameTooShort()

-------------------------------------------------------------------------------

Repository integration tests

✓ shouldSaveLogin()
✓ shouldFindLoginByUsername()
✓ shouldRejectDuplicateUsername()
✓ shouldReturnEmptyWhenUsernameNotFound()

-------------------------------------------------------------------------------

REST controller integration tests

✓ POST_create_login_returns_201()
✓ POST_duplicate_username_returns_409()
✓ POST_blank_password_returns_400()
✓ POST_short_password_returns_400()
✓ POST_short_username_returns_400()

-------------------------------------------------------------------------------

OpenAPI

POST /api/logins

Request body
{
  "username": string (min 3, max 100),
  "password": string (min 8)
}

Responses
- 201 Created — Login created successfully (id, username, enabled, created_at)
- 400 Bad Request — Validation error (blank or too short username/password)
- 409 Conflict — Username already exists

@EXECUTION
max_iterations: 10
