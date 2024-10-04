#!/bin/bash
function propagate_signal() {
    pid=$(cat /run/gunicorn.pid)
    echo "Going to kill $pid"
    kill -s TERM $pid
}

trap propagate_signal SIGTERM;

source .env

mkdir -p $PROMETHEUS_MULTIPROC_DIR
rm -f "$PROMETHEUS_MULTIPROC_DIR/*"

sed -i '' -e "s^/v1beta2/crypto^/v1beta3/crypto/us^g" $(export PYTHONUSERBASE=/usr/local;python -m site --user-site)/alpaca_trade_api/stream.py

gunicorn wsgi:application &
pid=$!
echo "waiting on pid $pid"
wait $pid