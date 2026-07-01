```dsl
Feature: Delete a Document

Description:
Allow a librarian to permanently delete an existing Document.

A Document represents a bibliographic record.
A Document can only be deleted if no Item references it.

-------------------------------------------------------------------------------

Business rules

- A Document must already exist.
- The identifier is immutable.
- A Document cannot be deleted while at least one Item references it.
- The deletion is permanent (hard delete).
- Associated Loans are never deleted directly because they belong to Items.
- The operation is idempotent.

-------------------------------------------------------------------------------

Endpoint

DELETE /documents/{id}

Responses

204 No Content

The Document has been successfully deleted.

404 Not Found

The Document does not exist.

409 Conflict

The Document cannot be deleted because one or more Items are still attached.

-------------------------------------------------------------------------------

Validation

- id must be a positive integer.

-------------------------------------------------------------------------------

Persistence

Delete the Document from the documents table.

Example SQL

DELETE
FROM documents
WHERE id = :id;

-------------------------------------------------------------------------------

Acceptance criteria

✓ Existing document without Item is deleted.
✓ Document no longer exists after deletion.
✓ Deleting an unknown Document returns 404.
✓ Deleting a Document having at least one Item returns 409.
✓ No Item is deleted automatically.
✓ No Loan is deleted automatically.

-------------------------------------------------------------------------------

Example cURL

Successful deletion

curl --request DELETE \
     --url http://localhost:8080/api/documents/42

Unknown document

curl --request DELETE \
     --url http://localhost:8080/api/documents/99999

-------------------------------------------------------------------------------

Deliverables

Generate:

- REST endpoint
- Application service
- Repository implementation
- SQL query
- OpenAPI documentation
- cURL examples
- Domain unit tests
- Application service tests
- Repository integration tests
- REST controller integration tests

-------------------------------------------------------------------------------

Domain unit tests

✓ shouldDeleteDocumentWhenNoItemExists()

✓ shouldRejectDeletionWhenDocumentHasItems()

-------------------------------------------------------------------------------

Application service tests

✓ shouldDeleteDocument()

✓ shouldReturnNotFoundWhenDocumentDoesNotExist()

✓ shouldReturnConflictWhenItemsExist()

-------------------------------------------------------------------------------

Repository integration tests

✓ shouldDeleteExistingDocument()

✓ shouldReturnZeroAffectedRowsWhenDocumentDoesNotExist()

-------------------------------------------------------------------------------

REST controller integration tests

✓ DELETE_existing_document_returns_204()

✓ DELETE_unknown_document_returns_404()

✓ DELETE_document_with_items_returns_409()

-------------------------------------------------------------------------------

OpenAPI

DELETE /documents/{id}

Responses

204 No Content

404 Not Found

409 Conflict
```