#!/bin/bash

curl --request PATCH \
     --url http://localhost:8082/api/logins/2/disable \
     --header 'Content-Type: application/json' \
     --data '{
       "enabled": false
     }'

# echo " ";

# curl --request PATCH \
#      --url http://localhost:8082/api/logins/99999/disable \
#      --header 'Content-Type: application/json' \
#      --data '{
#        "enabled": false
#      }'