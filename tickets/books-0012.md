Feature: Re-enable a login

Description:
Allow an administrator to re-enable an existing disabled login by setting its `enabled` field to true.

Re-enabling a login restores authentication capability. The record was previously disabled
but was never deleted. This is the inverse operation of the disable endpoint.

-------------------------------------------------------------------------------

Business rules

- A login must already exist.
- Only disabled logins can be re-enabled (already enabled returns success, idempotent).
- The enabled flag is stored as `enabled = true` (or `1` in the database).
- Re-enabling a login does not create any new records.
- The operation is idempotent — re-enabling an already enabled login still returns success.
- Re-enabling a login does not modify any associated borrower or administrator record.
- Associated borrowers/administrators still exist and can now authenticate again.
- The last_login field is not reset when re-enabling.

-------------------------------------------------------------------------------

Endpoint

PATCH /logins/{id}/enable

Request body

{
  "enabled": true
}

Responses

200 OK

The login has been successfully re-enabled.

Response body

{
  "id": 1,
  "username": "string",
  "enabled": true,
  "last_login": "2024-03-15T10:30:00",
  "created_at": "2024-03-15T10:30:00"
}

404 Not Found

The login does not exist.

-------------------------------------------------------------------------------

Validation

- id must be a positive integer.
- enabled must be true to re-enable.

-------------------------------------------------------------------------------

Persistence

Update the `enabled` column to true in the logins table.

Example SQL

UPDATE logins
SET enabled = true
WHERE id = :id;

-------------------------------------------------------------------------------

Acceptance criteria

✓ Existing disabled login is re-enabled (enabled = true).
✓ Re-enabled login can be used for authentication again.
✓ Associated borrower/administrator records are not deleted.
✓ Unknown login returns 404.
✓ Operation is idempotent — re-enabling an already enabled login returns 200.
✓ last_login field is preserved when re-enabling.

-------------------------------------------------------------------------------

Example cURL

Successful re-enable

curl --request PATCH \
     --url http://localhost:8082/api/logins/1/enable \
     --header 'Content-Type: application/json' \
     --data '{
       "enabled": true
     }'

Unknown login

curl --request PATCH \
     --url http://localhost:8082/api/logins/99999/enable \
     --header 'Content-Type: application/json' \
     --data '{
       "enabled": true
     }'

Already enabled login

curl --request PATCH \
     --url http://localhost:8082/api/logins/1/enable \
     --header 'Content-Type: application/json' \
     --data '{
       "enabled": true
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

✓ shouldEnableLoginWhenDisabled()
✓ shouldAcceptAlreadyEnabledLogin()

-------------------------------------------------------------------------------

Application service tests

✓ shouldEnableLogin()
✓ shouldReturnNotFoundWhenLoginDoesNotExist()
✓ shouldReturnSuccessWhenAlreadyEnabled()

-------------------------------------------------------------------------------

Repository integration tests

✓ shouldUpdateEnabledToTrue()
✓ shouldReturnZeroAffectedRowsWhenLoginDoesNotExist()

-------------------------------------------------------------------------------

REST controller integration tests

✓ PATCH_enable_existing_disabled_login_returns_200()
✓ PATCH_enable_unknown_login_returns_404()
✓ PATCH_enable_already_enabled_returns_200()

-------------------------------------------------------------------------------

OpenAPI

PATCH /api/logins/{id}/enable

Request body
{
  "enabled": true
}

Responses
- 200 OK — Login re-enabled (id, username, enabled, last_login, created_at)
- 404 Not Found — Login not found

@EXECUTION
max_iterations: 10
