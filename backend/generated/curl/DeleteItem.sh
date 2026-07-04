#!/bin/bash

# PATCH /items/{id}/title
# Updates the title of a document.
# Rules: value is mandatory, max length 255, cannot be blank.
curl --request DELETE \
     --url http://localhost:8082/api/items/46
echo ""