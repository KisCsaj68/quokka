#!/bin/bash

source .env

mkdir -p $PROMETHEUS_MULTIPROC_DIR
rm -f "$PROMETHEUS_MULTIPROC_DIR/*"

exec gunicorn wsgi:application