Feature: Add Item to Document

Description:
Allow a librarian to add a physical item (exemplaire) to an existing Document.

A Document represents a bibliographic record.
An Item represents a physical copy of a Document.

An Item has a physical state (clean, lost, damaged, repair).
Availability is not handled here (it depends on Loans).

-------------------------------------------------------------------------------

Business rules

- A Document must already exist.
- An Item must always be linked to a Document.
- The barcode must be unique across all Items.
- The physical status defaults to CLEAN.
- A Document can have multiple Items.
- The operation is idempotent only if barcode is reused (reject otherwise).
- No Loan is created by this operation.

-------------------------------------------------------------------------------

Endpoint

POST /documents/{documentId}/items

Request body

{
  "barcode": "string",
  "location": "string",
  "status": "CLEAN | LOST | DAMAGED | REPAIR",
  "acquisitionDate": "date (optional)"
}

Responses

201 Created

The Item has been successfully created.

404 Not Found

The Document does not exist.

409 Conflict

An Item with the same barcode already exists.

-------------------------------------------------------------------------------

Validation

- documentId must be a positive integer or UUID (depending on model)
- barcode must not be empty
- status must be one of: CLEAN, LOST, DAMAGED, REPAIR

-------------------------------------------------------------------------------

Persistence

Insert a new record into the items table.

Example SQL

INSERT INTO items (
  document_id,
  barcode,
  location,
  physical_status,
  acquisition_date
)
VALUES (
  :documentId,
  :barcode,
  :location,
  :status,
  :acquisitionDate
);

-------------------------------------------------------------------------------

Acceptance criteria

✓ Item is created when Document exists
✓ Item is linked to the correct Document
✓ Duplicate barcode is rejected
✓ Document is not modified structurally
✓ No Loan is created
✓ Default status is CLEAN when not provided

-------------------------------------------------------------------------------

Example cURL

Successful creation

curl --request POST \
     --url http://localhost:8082/api/documents/42/items \
     --header 'Content-Type: application/json' \
     --data '{
       "barcode": "ITEM-001",
       "location": "Shelf A3",
       "status": "CLEAN"
     }'

Document not found

curl --request POST \
     --url http://localhost:8082/api/documents/99999/items \
     --header 'Content-Type: application/json' \
     --data '{
       "barcode": "ITEM-001"
     }'

-------------------------------------------------------------------------------

Deliverables

Generate:

- REST endpoint
- Application service
- OpenAPI documentation
- cURL examples
- Domain unit tests
- Application service tests
- REST controller integration tests

-------------------------------------------------------------------------------

Domain unit tests

✓ shouldCreateItemWhenDocumentExists()

✓ shouldRejectDuplicateBarcode()

✓ shouldDefaultStatusToClean()

-------------------------------------------------------------------------------

Application service tests

✓ shouldCreateItem()

✓ shouldThrowNotFoundWhenDocumentMissing()

✓ shouldThrowConflictWhenBarcodeAlreadyExists()

-------------------------------------------------------------------------------

REST controller integration tests

✓ POST_item_returns_201()

✓ POST_item_returns_404_when_document_missing()

✓ POST_item_returns_409_when_duplicate_barcode()