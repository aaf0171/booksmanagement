Feature: Disable a login

Description:
Allow an administrator to disable an existing login by setting its `enabled` field to false.

A disabled login cannot be used for authentication. The record is not deleted — it is soft-deactivated.
This is useful when an employee leaves or a borrower account needs to be temporarily suspended.

-------------------------------------------------------------------------------

Business rules

- A login must already exist.
- Only enabled logins can be disabled (already disabled returns a specific warning).
- The disabled flag is stored as `enabled = false` (or `0` in the database).
- A disabled login cannot be re-enabled through this endpoint (use re-enable endpoint later).
- The operation is idempotent — disabling an already disabled login still returns success.
- Disabling a login does not delete any associated borrower or administrator record.
- Associated borrowers/administrators still exist but cannot authenticate.

-------------------------------------------------------------------------------

Endpoint

PATCH /logins/{id}/disable

Request body

{
  "enabled": false
}

Responses

200 OK

The login has been successfully disabled.

Response body

{
  "id": 1,
  "username": "string",
  "enabled": false,
  "last_login": "2024-03-15T10:30:00",
  "created_at": "2024-03-15T10:30:00"
}

404 Not Found

The login does not exist.

-------------------------------------------------------------------------------

Validation

- id must be a positive integer.
- enabled must be false to disable.

-------------------------------------------------------------------------------

Persistence

Update the `enabled` column to false in the logins table.

Example SQL

UPDATE logins
SET enabled = false
WHERE id = :id;

-------------------------------------------------------------------------------

Acceptance criteria

✓ Existing enabled login is disabled (enabled = false).
✓ Disabled login cannot be used for authentication.
✓ Associated borrower/administrator records are not deleted.
✓ Unknown login returns 404.
✓ Operation is idempotent — disabling an already disabled login returns 200.

-------------------------------------------------------------------------------

Example cURL

Successful disable

curl --request PATCH \
     --url http://localhost:8082/api/logins/1/disable \
     --header 'Content-Type: application/json' \
     --data '{
       "enabled": false
     }'

Unknown login

curl --request PATCH \
     --url http://localhost:8082/api/logins/99999/disable \
     --header 'Content-Type: application/json' \
     --data '{
       "enabled": false
     }'

-------------------------------------------------------------------------------

Deliverables

Generate:

- Domain unit tests
- Application service tests
- Repository integration tests
- REST controller integration tests
- OpenAPI documentation
- cURL examples

-------------------------------------------------------------------------------

Domain unit tests

✓ shouldDisableLoginWhenEnabled()
✓ shouldRejectAlreadyDisabledLogin()

-------------------------------------------------------------------------------

Application service tests

✓ shouldDisableLogin()
✓ shouldReturnNotFoundWhenLoginDoesNotExist()
✓ shouldReturnWarningWhenAlreadyDisabled()

-------------------------------------------------------------------------------

Repository integration tests

✓ shouldUpdateEnabledToFalse()
✓ shouldReturnZeroAffectedRowsWhenLoginDoesNotExist()

-------------------------------------------------------------------------------

REST controller integration tests

✓ PATCH_disable_existing_login_returns_200()
✓ PATCH_disable_unknown_login_returns_404()
✓ PATCH_disable_already_disabled_returns_200()

-------------------------------------------------------------------------------

OpenAPI

PATCH /api/logins/{id}/disable

Request body
{
  "enabled": false
}

Responses
- 200 OK — Login disabled (id, username, enabled, last_login, created_at)
- 404 Not Found — Login not found

@EXECUTION
max_iterations: 10
