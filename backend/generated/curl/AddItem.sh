#!/bin/bash


curl --request POST \
     --url http://localhost:8082/api/documents/1/items \
     --header 'Content-Type: application/json' \
     --data '{
       "barcode": "ITEM-001",
       "location": "Shelf A3",
       "status": "CLEAN"
     }'

