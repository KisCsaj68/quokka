#!/bin/bash

pip install --upgrade pip
pip install -e .

gunicorn --workers=1 wsgi:application