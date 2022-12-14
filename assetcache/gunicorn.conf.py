#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from src import hooks

bind = '0.0.0.0:8000'
workers = 1
threads = 1
loglevel = "info"

# Server Hooks
post_worker_init = hooks.post_worker_init
