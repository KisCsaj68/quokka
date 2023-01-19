#!/bin/bash

set -o errexit
set -o pipefail

URL="http://127.0.0.1:9000/api/v1/order"
declare -a CRYPTO=("BTCUSD")
declare -a STOCK=("AAPL" "TSLA")
sleep 210
for symbol in ${CRYPTO[@]}; do
  price=$(curl -s "http://localhost:8000/api/v1/crypto/$symbol" | jq '.price')
  price=${price%.*}
  buy_price=$((price+200))
  hey -z 4m  -m POST -H "Content-Type: application/json" -d '{
        "symbol" : "'${symbol}'",
        "type" : "LIMIT",
        "limit" : '${buy_price}',
        "side" : "BUY",
        "quantity" : "3",
        "account" : "15fcd9d0-c7c2-4487-bdde-b0ba7560b885",
        "asset_type" : "CRYPTO"
        }' $URL
done


for symbol in ${STOCK[@]}; do
      echo $symbol
      hey -z 2m "http://localhost:8000/api/v1/stock/${symbol}"
done
