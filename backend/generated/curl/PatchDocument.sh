#!/bin/bash

# PATCH /documents/{id}/title
# Updates the title of a document.
# Rules: value is mandatory, max length 255, cannot be blank.
curl --request PATCH \
     --url http://localhost:8082/api/documents/1/title \
     --header "Content-Type: application/json" \
     --data '{
       "value": "The Lord of the Rings"
     }'

echo ""

# PATCH /documents/{id}/subtitle
# Updates the subtitle of a document.
# Rules: nullable, max length 255.
curl --request PATCH \
     --url http://localhost:8082/api/documents/1/subtitle \
     --header "Content-Type: application/json" \
     --data '{
       "value": "The Fellowship of the Ring"
     }'

echo ""

# PATCH /documents/{id}/document-type
# Updates the document type of a document.
# Rules: Accepted values are BOOK, DVD, GAME, DEVICE, OTHER.
curl --request PATCH \
     --url http://localhost:8082/api/documents/1/document-type \
     --header "Content-Type: application/json" \
     --data '{
       "value": "BOOK"
     }'

echo ""

# PATCH /documents/{id}/isbn
# Updates the ISBN of a document.
# Rules: nullable, max length 20.
curl --request PATCH \
     --url http://localhost:8082/api/documents/1/isbn \
     --header "Content-Type: application/json" \
     --data '{
       "value": "9780261103573"
     }'

echo ""

# PATCH /documents/{id}/publisher
# Updates the publisher of a document.
# Rules: nullable, max length 255.
curl --request PATCH \
     --url http://localhost:8082/api/documents/1/publisher \
     --header "Content-Type: application/json" \
     --data '{
       "value": "Allen & Unwin"
     }'

echo ""

# PATCH /documents/{id}/publication-year
# Updates the publication year of a document.
# Rules: nullable, positive integer, between 0 and current year + 1.
curl --request PATCH \
     --url http://localhost:8082/api/documents/1/publication-year \
     --header "Content-Type: application/json" \
     --data '{
       "value": 1954
     }'

echo ""

# PATCH /documents/{id}/language
# Updates the language of a document.
# Rules: nullable, max length 50.
curl --request PATCH \
     --url http://localhost:8082/api/documents/1/language \
     --header "Content-Type: application/json" \
     --data '{
       "value": "français"
     }'

echo ""

# PATCH /documents/{id}/description
# Updates the description of a document.
# Rules: nullable.
curl --request PATCH \
     --url http://localhost:8082/api/documents/1/description \
     --header "Content-Type: application/json" \
     --data '{
       "value": "Epic fantasy novel."
     }'

echo ""

# PATCH /documents/{id}/cover-url
# Updates the cover URL of a document.
# Rules: nullable, max length 500, must be a valid URL.
curl --request PATCH \
     --url http://localhost:8082/api/documents/1/cover-url \
     --header "Content-Type: application/json" \
     --data '{
       "value": "https://example.com/cover.jpg"
     }'

echo ""

# PATCH /documents/{id}/created-at
# Updates the created at timestamp of a document.
# Rules: ISO-8601 timestamp, nullable.
curl --request PATCH \
     --url http://localhost:8082/api/documents/1/created-at \
     --header "Content-Type: application/json" \
     --data '{
       "value": "2026-06-30T15:42:00"
     }'
