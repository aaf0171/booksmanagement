#!/bin/bash

curl --request POST \
     --url http://localhost:8082/api/auth/login \
     --header 'Content-Type: application/json' \
     --data '{
       "username": "jc.dusse",
       "password": "password123"
     }'