#!/bin/bash

curl --request POST \
     --url http://localhost:8082/api/borrowers \
     --header 'Content-Type: application/json' \
     --data '{
       "firstname": "Jane",
       "lastname": "Smith",
       "email": "jane.smith@example.com",
       "username": "janesmith"
     }'