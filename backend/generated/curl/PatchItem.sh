#!/bin/bash

# PATCH /items/{id}/title
# Updates the title of a document.
# Rules: value is mandatory, max length 255, cannot be blank.
curl --request PATCH \
     --url http://localhost:8082/api/items/43/barcode \
     --header "Content-Type: application/json" \
     --data '{
       "value": "CODE128-1000000045"
     }'

echo ""

# PATCH /items/{id}/subtitle
# Updates the subtitle of a document.
# Rules: nullable, max length 255.
curl --request PATCH \
     --url http://localhost:8082/api/items/43/status \
     --header "Content-Type: application/json" \
     --data '{
       "value": "DAMAGED"
     }'

echo ""

# PATCH /items/{id}/document-type
# Updates the document type of a document.
# Rules: Accepted values are BOOK, DVD, GAME, DEVICE, OTHER.
curl --request PATCH \
     --url http://localhost:8082/api/items/43/location \
     --header "Content-Type: application/json" \
     --data '{
       "value": "Shelf A4"
     }'

echo ""

# PATCH /items/{id}/isbn
# Updates the ISBN of a document.
# Rules: nullable, max length 20.
curl --request PATCH \
     --url http://localhost:8082/api/items/43/acquisition-date \
     --header "Content-Type: application/json" \
     --data '{
       "value": "2026-07-01"
     }'

echo ""