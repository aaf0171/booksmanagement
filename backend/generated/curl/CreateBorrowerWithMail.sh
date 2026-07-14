#!/bin/bash

curl --request POST \
     --url http://localhost:8082/api/borrowers \
     --header 'Content-Type: application/json' \
     --data '{
       "firstname": "Alice",
       "lastname": "Dupont",
       "email": "alice.dupont@example.com",
       "username": "alicedupont"
     }'
