Feature: Partial update of an Item

Description:
Allow the client to update a single Item property through dedicated PATCH endpoints.
Each endpoint updates only one field of the Item entity.
The identifier and barcode cannot be modified.

Business rules:

- An Item must already exist.
- The id is immutable.
- The barcode is immutable (unique constraint).
- Only the requested property is modified.
- Every successful update returns HTTP 204 No Content.
- Unknown item returns HTTP 404.
- Validation errors return HTTP 400.
- Invalid physical status returns HTTP 400.

Deliverables

Generate:

- Domain unit tests
- Application service tests
- Repository integration tests
- REST controller integration tests
- OpenAPI documentation
- cURL examples for every endpoint

-------------------------------------------------------------------------------

PATCH /items/{id}/barcode

Request body

{
  "value": "ITEM-002"
}

Rules

- value is mandatory
- value must not be blank

Updates

Item.barcode

...

Example cURL

curl --request PATCH \
     --url http://localhost:8082/api/items/42/barcode \
     --header "Content-Type: application/json" \
     --data '{
       "value": "ITEM-002"
     }'

-------------------------------------------------------------------------------

PATCH /items/{id}/status

Request body

{
  "value": "DAMAGED"
}

Rules

- value is mandatory
- Accepted values: CLEAN, LOST, DAMAGED, REPAIR
- Unknown status returns HTTP 400

Updates

Item.physicalStatus

...

Example cURL

curl --request PATCH \
     --url http://localhost:8082/api/items/42/status \
     --header "Content-Type: application/json" \
     --data '{
       "value": "DAMAGED"
     }'

-------------------------------------------------------------------------------

PATCH /items/{id}/location

Request body

{
  "value": "Shelf B5"
}

Rules

- nullable
- value must not be blank when provided

Updates

Item.location

...

Example cURL

curl --request PATCH \
     --url http://localhost:8082/api/items/42/location \
     --header "Content-Type: application/json" \
     --data '{
       "value": "Shelf B5"
     }'

curl --request PATCH \
     --url http://localhost:8082/api/items/42/location \
     --header "Content-Type: application/json" \
     --data '{
       "value": ""
     }'

-------------------------------------------------------------------------------

PATCH /items/{id}/acquisition-date

Request body

{
  "value": "2024-03-15"
}

Rules

- nullable
- ISO-8601 date format (YYYY-MM-DD)
- invalid date format returns HTTP 400

Updates

Item.acquisitionDate

...

Example cURL

curl --request PATCH \
     --url http://localhost:8082/api/items/42/acquisition-date \
     --header "Content-Type: application/json" \
     --data '{
       "value": "2024-03-15"
     }'

-------------------------------------------------------------------------------

Acceptance criteria

✓ Existing item barcode is updated when valid.
✓ Existing item status is updated with valid value.
✓ Existing item location is updated or cleared.
✓ Existing item acquisition date is updated or cleared.
✓ Item not found returns 404.
✓ Invalid status value returns 400.
✓ Invalid date format returns 400.
✓ Blank value for barcode returns 400.
✓ Unrequested fields remain unchanged.

-------------------------------------------------------------------------------

Domain unit tests

✓ shouldUpdateBarcodeWhenValid()
✓ shouldRejectBlankBarcode()
✓ shouldUpdateStatusWhenValid()
✓ shouldRejectInvalidStatus()
✓ shouldUpdateLocationWhenValid()
✓ shouldUpdateAcquisitionDateWhenValid()
✓ shouldRejectInvalidAcquisitionDate()

-------------------------------------------------------------------------------

Application service tests

✓ shouldUpdateBarcode()
✓ shouldUpdateStatus()
✓ shouldUpdateLocation()
✓ shouldUpdateAcquisitionDate()
✓ shouldReturnNotFoundWhenItemMissing()
✓ shouldThrowBadRequestWhenBarcodeBlank()
✓ shouldThrowBadRequestWhenInvalidStatus()
✓ shouldThrowBadRequestWhenInvalidDate()
✓ shouldNotModifyUnrequestedFields()

-------------------------------------------------------------------------------

Repository integration tests

✓ shouldUpdateBarcodeExistingItem()
✓ shouldUpdateStatusExistingItem()
✓ shouldUpdateLocationExistingItem()
✓ shouldUpdateAcquisitionDateExistingItem()
✓ shouldReturnZeroAffectedRowsWhenItemDoesNotExist()

-------------------------------------------------------------------------------

REST controller integration tests

✓ PATCH_barcode_returns_204()
✓ PATCH_status_returns_204()
✓ PATCH_location_returns_204()
✓ PATCH_acquisition_date_returns_204()
✓ PATCH_unknown_item_returns_404()
✓ PATCH_invalid_status_returns_400()
✓ PATCH_blank_barcode_returns_400()

-------------------------------------------------------------------------------

OpenAPI

PATCH /items/{id}/barcode
Request body: { "value": string }
Responses:
- 204 No Content
- 400 Bad Request
- 404 Not Found

PATCH /items/{id}/status
Request body: { "value": "CLEAN" | "LOST" | "DAMAGED" | "REPAIR" }
Responses:
- 204 No Content
- 400 Bad Request
- 404 Not Found

PATCH /items/{id}/location
Request body: { "value": string | null }
Responses:
- 204 No Content
- 400 Bad Request
- 404 Not Found

PATCH /items/{id}/acquisition-date
Request body: { "value": "YYYY-MM-DD" | null }
Responses:
- 204 No Content
- 400 Bad Request
- 404 Not Found

@EXECUTION
max_iterations: 10