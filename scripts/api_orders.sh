#!/bin/bash
set -e

user_payload='{
"full_name": "User Asd",
"email_address": "asd@asd.com",
"user_name" : "TestUser",
"password" : "asd"
}'

login_payload='{
"user_name" : "TestUser",
"password" : "asd"
}'

update_user_payload='{
"full_name":"Test_Asd"
}'

apple_payload='{
"symbol": "AAPL",
"type": "MARKET",
"side": "BUY",
"qty": 3
}'

btcusd_payload='{
"symbol": "BTCUSD",
"type": "MARKET",
"side": "BUY",
"qty": 3
}'


for num in {1}; do
#  user registration, login
  id=$(curl --silent --request POST 'localhost:8080/api/v1/user' -H 'Content-Type: application/json' --data-raw "${user_payload}"  | jq -r '.id' );
  token=$(curl --silent --dump-header - -X POST 'localhost:8080/api/v1/login' -H 'Content-Type: application/json' --data-raw "${login_payload}" | awk '/Authorization/ {printf "%s", $3}' | tr -d '\r');

#  update user info
#  make trades
  hey -z 40s -m GET -H "Content-Type: application/json" -H "Authorization: Bearer ${token}" http://localhost:8080/api/v1/asset/crypto/BTCUD
  hey -z 40s -m POST -H "Content-Type: application/json" -H "Authorization: Bearer ${token}" -d "${btcusd_payload}" http://localhost:8080/api/v1/order/crypto
  hey -z 40s -m GET -H "Content-Type: application/json" -H "Authorization: Bearer ${token}" http://localhost:8080/api/v1/asset/stock/APL
  hey -z 40s -m POST -H "Content-Type: application/json" -H "Authorization: Bearer ${token}" -d "${apple_payload}" http://localhost:8080/api/v1/order/stock

#  set up payload for delete user
#  delete user
  user_delete_payload='{"id":"'${id}'"}'
  curl -s -X DELETE http://localhost:8080/api/v1/user -H 'Content-Type: application/json' -H "Authorization: Bearer ${token}" --data-raw "${user_delete_payload}"

done


