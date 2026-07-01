Feature: Partial update of a Document

Description:
Allow the client to update a single Document property through dedicated PATCH endpoints.
Each endpoint updates only one field of the Document aggregate.
The identifier cannot be modified.

Business rules:

- A Document must already exist.
- The id is immutable.
- Only the requested property is modified.
- Every successful update returns HTTP 204 No Content.
- Unknown document returns HTTP 404.
- Validation errors return HTTP 400.

Deliverables

Generate:

- Domain unit tests
- Application service tests
- Repository integration tests
- REST controller integration tests
- SQL migration if required
- OpenAPI documentation
- cURL examples for every endpoint

-------------------------------------------------------------------------------

PATCH /documents/{id}/title

Request body

{
    "value": "The Lord of the Rings"
}

Rules

- value is mandatory
- maximum length: 255
- cannot be blank

Updates

Document.title

...

Example cURL

curl --request PATCH \
     --url http://localhost:8082/api/documents/42/title \
     --header "Content-Type: application/json" \
     --data '{
       "value": "The Lord of the Rings"
     }'

-------------------------------------------------------------------------------

PATCH /documents/{id}/subtitle

Request body

{
    "value": "The Fellowship of the Ring"
}

Rules

- nullable
- maximum length: 255

Updates

Document.subtitle

...

Example cURL

curl --request PATCH \
     --url http://localhost:8082/api/documents/42/subtitle \
     --header "Content-Type: application/json" \
     --data '{
       "value": "The subtitle"
     }'


-------------------------------------------------------------------------------

PATCH /documents/{id}/document-type

Request body

{
    "value": "BOOK"
}

Rules

Accepted values

- BOOK
- DVD
- GAME
- DEVICE
- OTHER

Updates

Document.documentType

...

Example cURL

curl --request PATCH \
     --url http://localhost:8080/api/documents/42/document-type \
     --header "Content-Type: application/json" \
     --data '{
       "value": "BOOK"
     }'

-------------------------------------------------------------------------------

PATCH /documents/{id}/isbn

Request body

{
    "value": "9780261103573"
}

Rules

- nullable
- maximum length: 20

Updates

Document.isbn

...

Example cURL

curl --request PATCH \
     --url http://localhost:8080/api/documents/42/isbn \
     --header "Content-Type: application/json" \
     --data '{
       "value": "9780261103573"
     }'

-------------------------------------------------------------------------------

PATCH /documents/{id}/publisher

Request body

{
    "value": "Allen & Unwin"
}

Rules

- nullable
- maximum length: 255

Updates

Document.publisher

...

Example cURL

curl --request PATCH \
     --url http://localhost:8080/api/documents/42/publisher \
     --header "Content-Type: application/json" \
     --data '{
       "value": "Allen & Unwin"
     }'

-------------------------------------------------------------------------------

PATCH /documents/{id}/publication-year

Request body

{
    "value": 1954
}

Rules

- nullable
- positive integer
- must be between 0 and current year + 1

Updates

Document.publicationYear

...

Example cURL

curl --request PATCH \
     --url http://localhost:8080/api/documents/42/publication-year \
     --header "Content-Type: application/json" \
     --data '{
       "value": 1954
     }'

-------------------------------------------------------------------------------

PATCH /documents/{id}/language

Request body

{
    "value": "English"
}

Rules

- nullable
- maximum length: 50

Updates

Document.language

...

Example cURL

curl --request PATCH \
     --url http://localhost:8080/api/documents/42/language \
     --header "Content-Type: application/json" \
     --data '{
       "value": "français"
     }'

-------------------------------------------------------------------------------

PATCH /documents/{id}/description

Request body

{
    "value": "Epic fantasy novel."
}

Rules

- nullable

Updates

Document.description

...

Example cURL

curl --request PATCH \
     --url http://localhost:8080/api/documents/42/description \
     --header "Content-Type: application/json" \
     --data '{
       "value": "Epic fantasy novel."
     }'


-------------------------------------------------------------------------------

PATCH /documents/{id}/cover-url

Request body

{
    "value": "https://..."
}

Rules

- nullable
- maximum length: 500
- valid URL

Updates

Document.coverUrl

...

Example cURL

curl --request PATCH \
     --url http://localhost:8080/api/documents/42/cover-url \
     --header "Content-Type: application/json" \
     --data '{
       "value": "https://..."
     }'

-------------------------------------------------------------------------------

PATCH /documents/{id}/created-at

Request body

{
    "value": "2026-06-30T15:42:00Z"
}

Rules

- ISO-8601 timestamp
- nullable

Updates

Document.createdAt