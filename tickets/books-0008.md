Feature: Delete an Item

Description:
Allow a librarian to permanently delete an existing Item (physical copy).

An Item represents a physical copy of a Document.
An Item can only be deleted if it is not currently on loan.

-------------------------------------------------------------------------------

Business rules

- An Item must already exist.
- The identifier is immutable.
- An Item cannot be deleted while it has an active loan (return_date IS NULL).
- The deletion is permanent (hard delete).
- Associated Loans for this Item must also be deleted (cascade).
- Deleting an Item does not delete the parent Document.
- The operation is idempotent — deleting a non-existent Item returns 404.

-------------------------------------------------------------------------------

Endpoint

DELETE /items/{id}

Responses

204 No Content

The Item has been successfully deleted.

404 Not Found

The Item does not exist.

409 Conflict

The Item cannot be deleted because it has an active loan.

-------------------------------------------------------------------------------

Validation

- id must be a positive integer.

-------------------------------------------------------------------------------

Persistence

Delete the Item from the items table.
Delete associated Loan records from the loans table.

Example SQL

DELETE FROM loans WHERE item_id = :id;

DELETE FROM items WHERE id = :id;

-------------------------------------------------------------------------------

Acceptance criteria

✓ Existing Item without active loan is deleted.
✓ Item no longer exists after deletion.
✓ Deleting an unknown Item returns 404.
✓ Deleting an Item with an active loan returns 409.
✓ Parent Document is not modified or deleted.
✓ Associated Loan records are deleted before the Item.

-------------------------------------------------------------------------------

Example cURL

Successful deletion

curl --request DELETE \
     --url http://localhost:8082/api/items/42

Item with active loan

curl --request DELETE \
     --url http://localhost:8082/api/items/99

Unknown item

curl --request DELETE \
     --url http://localhost:8082/api/items/99999

-------------------------------------------------------------------------------

Deliverables

Generate:

- REST endpoint
- Application service
- Repository implementation
- SQL queries
- OpenAPI documentation
- cURL examples
- Domain unit tests
- Application service tests
- Repository integration tests
- REST controller integration tests

-------------------------------------------------------------------------------

Domain unit tests

✓ shouldDeleteItemWhenNoActiveLoanExists()

✓ shouldRejectDeletionWhenItemHasActiveLoan()

-------------------------------------------------------------------------------

Application service tests

✓ shouldDeleteItem()

✓ shouldReturnNotFoundWhenItemDoesNotExist()

✓ shouldReturnConflictWhenActiveLoanExists()

-------------------------------------------------------------------------------

Repository integration tests

✓ shouldDeleteExistingItem()

✓ shouldReturnZeroAffectedRowsWhenItemDoesNotExist()

✓ shouldDeleteAssociatedLoans()

-------------------------------------------------------------------------------

REST controller integration tests

✓ DELETE_existing_item_returns_204()

✓ DELETE_unknown_item_returns_404()

✓ DELETE_item_with_active_loan_returns_409()

-------------------------------------------------------------------------------

OpenAPI

DELETE /items/{id}

Responses

204 No Content

404 Not Found

409 Conflict

@EXECUTION
max_iterations: 10
