#!/bin/bash

curl --request POST \
     --url http://localhost:8082/api/logins \
     --header 'Content-Type: application/json' \
     --data '{
       "username": "admin1",
       "password": "securePass123"
     }'

curl --request POST \
     --url http://localhost:8082/api/logins \
     --header 'Content-Type: application/json' \
     --data '{
       "username": "admin1",
       "password": "anotherPassword"
     }'