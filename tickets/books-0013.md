Feature: Create a borrower with login

Description:
Allow an administrator to create a new borrower account, which includes creating both the Borrower entity and its associated Login record in a single transaction.

The borrower represents a library patron.
The login provides authentication credentials for the borrower.
By default, the login is disabled until the administrator communicates the credentials to the borrower.

-------------------------------------------------------------------------------

Business rules

- A borrower must have a firstname and lastname (both mandatory).
- An email is optional.
- A login must be created simultaneously with the borrower.
- The login username is mandatory and must be unique across all logins.
- A password is auto-generated (16 characters, alphanumeric with special characters).
- The password is BCrypt-hashed before storage in the logins table.
- The login is disabled by default (enabled = false).
- The raw password is returned in the response so it can be communicated to the borrower.
- The raw password is NEVER stored in the database.
- The borrower and login are created in a single atomic transaction.
- If either creation fails, both are rolled back.
- Duplicate username returns 409 Conflict.

-------------------------------------------------------------------------------

Endpoint

POST /borrowers

Request body

{
  "firstname": "John",
  "lastname": "Doe",
  "email": "john.doe@example.com",
  "username": "johndoe"
}

Responses

201 Created

The borrower and login have been successfully created.

Response body

{
  "id": 1,
  "firstname": "John",
  "lastname": "Doe",
  "email": "john.doe@example.com",
  "username": "johndoe",
  "password": "GeneratedPassword1!",
  "loginEnabled": false,
  "createdAt": "2024-03-15T10:30:00"
}

The "password" field contains the raw generated password — this is the only time it is returned.
The "loginEnabled" field is always false on creation.

400 Bad Request

Validation error (blank firstname, blank lastname, blank username).

409 Conflict

A login with the same username already exists.

-------------------------------------------------------------------------------

Validation

- firstname: mandatory, non-blank, maximum 100 characters
- lastname: mandatory, non-blank, maximum 100 characters
- email: optional, maximum 255 characters if provided
- username: mandatory, non-blank, minimum 3 characters, maximum 100 characters

Password generation

- 16 characters long
- At least one uppercase letter
- At least one lowercase letter
- At least one digit
- At least one special character (!@#$%^&*...)

-------------------------------------------------------------------------------

Persistence

1. Generate a random password
2. BCrypt-hash the password
3. Create the Login record with enabled = false and the hashed password
4. Create the Borrower record with the login_id FK pointing to the new Login
5. Return the response with the raw password

Example SQL

INSERT INTO logins (username, password_hash, enabled, last_login, created_at)
VALUES (:username, :hashedPassword, false, NULL, CURRENT_TIMESTAMP);

INSERT INTO borrowers (login_id, firstname, lastname, email, created_at)
VALUES (LAST_INSERT_ID(), :firstname, :lastname, :email, CURRENT_TIMESTAMP);

-------------------------------------------------------------------------------

Acceptance criteria

✓ Borrower is created with valid data.
✓ Login is created simultaneously with a BCrypt-hashed password.
✓ Login is disabled by default (enabled = false).
✓ A random 16-character password is generated.
✓ The raw password is returned in the response.
✓ The raw password is NOT stored in the database.
✓ Borrower and login are linked via login_id FK.
✓ Duplicate username is rejected with 409.
✓ Transaction rolls back if either borrower or login creation fails.
✓ Blank firstname returns 400.
✓ Blank lastname returns 400.
✓ Blank username returns 400.
✓ Short username (less than 3 characters) returns 400.

-------------------------------------------------------------------------------

Example cURL

Successful creation

curl --request POST \
     --url http://localhost:8082/api/borrowers \
     --header 'Content-Type: application/json' \
     --data '{
       "firstname": "Jane",
       "lastname": "Smith",
       "email": "jane.smith@example.com",
       "username": "janesmith"
     }'

Blank firstname

curl --request POST \
     --url http://localhost:8082/api/borrowers \
     --header 'Content-Type: application/json' \
     --data '{
       "firstname": "",
       "lastname": "Smith",
       "username": "janesmith"
     }'

Duplicate username

curl --request POST \
     --url http://localhost:8082/api/borrowers \
     --header 'Content-Type: application/json' \
     --data '{
       "firstname": "Jane",
       "lastname": "Smith",
       "username": "janesmith"
     }'

-------------------------------------------------------------------------------

Deliverables

Generate:

- CreateBorrowerDTO
- CreateBorrowerResponseDTO
- CreateBorrowerService
- BorrowerCommandController
- PasswordGenerator utility (or inline in service)
- Domain unit tests
- Application service tests
- Repository integration tests
- REST controller integration tests
- OpenAPI documentation
- cURL examples

-------------------------------------------------------------------------------

Domain unit tests

✓ shouldGeneratePasswordWith16Characters()
✓ shouldGeneratePasswordWithUppercaseLetter()
✓ shouldGeneratePasswordWithLowercaseLetter()
✓ shouldGeneratePasswordWithDigit()
✓ shouldGeneratePasswordWithSpecialCharacter()
✓ shouldReturnRawPasswordInResponse()

-------------------------------------------------------------------------------

Application service tests

✓ shouldCreateBorrowerWithLogin()
✓ shouldDisableLoginByDefault()
✓ shouldHashPasswordWithBCrypt()
✓ shouldReturnRawPasswordInResponse()
✓ shouldThrowConflictWhenUsernameAlreadyExists()
✓ shouldThrowBadRequestWhenFirstnameBlank()
✓ shouldThrowBadRequestWhenLastnameBlank()
✓ shouldThrowBadRequestWhenUsernameBlank()
✓ shouldThrowBadRequestWhenUsernameTooShort()
✓ shouldRollbackOnTransactionFailure()

-------------------------------------------------------------------------------

Repository integration tests

✓ shouldSaveBorrowerWithLoginFK()
✓ shouldSaveLoginBeforeBorrower()
✓ shouldCascadeLoginAndBorrower()
✓ shouldRejectDuplicateUsername()

-------------------------------------------------------------------------------

REST controller integration tests

✓ POST_create_borrower_returns_201()
✓ POST_duplicate_username_returns_409()
✓ POST_blank_firstname_returns_400()
✓ POST_blank_lastname_returns_400()
✓ POST_blank_username_returns_400()
✓ POST_short_username_returns_400()
✓ POST_response_contains_raw_password()
✓ POST_response_login_disabled()

-------------------------------------------------------------------------------

OpenAPI

POST /api/borrowers

Request body
{
  "firstname": string (min 1, max 100),
  "lastname": string (min 1, max 100),
  "email": string (optional, max 255),
  "username": string (min 3, max 100)
}

Responses
- 201 Created — Borrower + login created (id, firstname, lastname, email, username, password, loginEnabled, createdAt)
- 400 Bad Request — Validation error
- 409 Conflict — Username already exists

@EXECUTION
max_iterations: 10
