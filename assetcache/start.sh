#!/bin/bash

pip install --upgrade pip
pip install -e .

gunicorn wsgi:application