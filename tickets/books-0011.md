Feature: Delete a login

Description:
Allow an administrator to permanently delete an existing login.

Deleting a login removes the authentication record from the system.
Because of the ON DELETE CASCADE foreign key constraints, any linked borrower or administrator record is also removed.

-------------------------------------------------------------------------------

Business rules

- A login must already exist.
- The deletion is permanent (hard delete).
- The ON DELETE CASCADE on the foreign key ensures associated borrower or administrator records are deleted.
- If the login is not linked to a borrower or administrator, only the login is deleted.
- The operation is idempotent — deleting a non-existent login returns 404.
- Deleted login credentials cannot be recovered.
- Active loans or other business data are not affected (logins are not directly linked to loans).

-------------------------------------------------------------------------------

Endpoint

DELETE /logins/{id}

Responses

204 No Content

The login has been successfully deleted.

404 Not Found

The login does not exist.

-------------------------------------------------------------------------------

Validation

- id must be a positive integer.

-------------------------------------------------------------------------------

Persistence

Delete the login from the logins table.
The database foreign key constraint handles cascading deletion of linked borrower or administrator records.

Example SQL

DELETE FROM logins WHERE id = :id;

-------------------------------------------------------------------------------

Acceptance criteria

✓ Existing login is deleted.
✓ Login no longer exists after deletion.
✓ Deleting an unknown login returns 404.
✓ Associated borrower or administrator is deleted via cascade.
✓ Active loans are not affected.

-------------------------------------------------------------------------------

Example cURL

Successful deletion

curl --request DELETE \
     --url http://localhost:8082/api/logins/1

Unknown login

curl --request DELETE \
     --url http://localhost:8082/api/logins/99999

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

✓ shouldDeleteLoginWhenExists()
✓ shouldRejectDeletingNonExistentLogin()

-------------------------------------------------------------------------------

Application service tests

✓ shouldDeleteLogin()
✓ shouldReturnNotFoundWhenLoginDoesNotExist()
✓ shouldCascadeDeleteAssociatedBorrowerOrAdministrator()

-------------------------------------------------------------------------------

Repository integration tests

✓ shouldDeleteExistingLogin()
✓ shouldReturnZeroAffectedRowsWhenLoginDoesNotExist()
✓ shouldCascadeDeleteAssociatedRecords()

-------------------------------------------------------------------------------

REST controller integration tests

✓ DELETE_existing_login_returns_204()
✓ DELETE_unknown_login_returns_404()

-------------------------------------------------------------------------------

OpenAPI

DELETE /api/logins/{id}

Responses
- 204 No Content — Login deleted
- 404 Not Found — Login not found

@EXECUTION
max_iterations: 10
