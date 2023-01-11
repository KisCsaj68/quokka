#!/bin/bash
set -e

update_user_payload='{
"full_name":"Test_Asd"
}'

login_payload='{
"user_name" : "TestUser",
"password" : "asd"
}'

user_payload='{
"full_name": "User Asd",
"email_address": "test_user@asd.com",
"user_name" : "TestUser",
"password" : "asd"
}'

for num in {1..5000}; do
  id=$(curl --silent --request POST 'localhost:8080/api/v1/user' -H 'Content-Type: application/json' --data-raw "${user_payload}"  | jq -r '.id' );
  token=$(curl --silent --dump-header - -X POST 'localhost:8080/api/v1/login' -H 'Content-Type: application/json' --data-raw "${login_payload}" | awk '/Authorization/ {printf "%s", $3}' | tr -d '\r');
  curl --silent --output /dev/null -H "Authorization: Bearer ${token}" http://localhost:8080/api/v1/user/${id} ;
  curl --silent --output /dev/null -X PUT http://localhost:8080/api/v1/user/${id} -H 'Content-Type: application/json' -H "Authorization: Bearer ${token}" --data-raw "${update_user_payload}"

  user_delete_payload='{"id":"'${id}'"}'
  curl --silent --output /dev/null -X DELETE http://localhost:8080/api/v1/user -H 'Content-Type: application/json' -H "Authorization: Bearer ${token}" --data-raw "${user_delete_payload}"
done