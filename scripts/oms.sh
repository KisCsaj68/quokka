#!/bin/bash

set -o errexit
set -o pipefail

URL="http://127.0.0.1:9000/api/v1/order"
declare -a CRYPTO=("BTCUSD" "ETHUSD")
declare -a STOCK=("AAPL" "TSLA")

for symbol in ${CRYPTO[@]}; do
  price=$(curl -s "http://localhost:8000/api/v1/crypto/$symbol" | jq '.price')
  price=${price%.*}
  updated_price=$((price+2))
  hey -z 2.5m  -m POST -H "Content-Type: application/json" -d '{
        "symbol" : "'${symbol}'",
        "type" : "LIMIT",
        "limit" : '${updated_price}',
        "side" : "BUY",
        "quantity" : "3",
        "account" : "15fcd9d0-c7c2-4487-bdde-b0ba7560b885",
        "asset_type" : "CRYPTO"
        }' $URL
done


for symbol in ${STOCK[@]}; do
    hey -z 150s  -m POST -H "Content-Type: application/json" -d '{
          "symbol" : "'${symbol}'",
          "type" : "MARKET",
          "side" : "BUY",
          "quantity" : "3",
          "account" : "15fcd9d0-c7c2-4487-bdde-b0ba7560b885",
          "asset_type" : "STOCK"
          }' $URL
done
