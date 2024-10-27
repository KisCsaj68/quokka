import os
import requests
from requests.adapters import TimeoutSauce
from env import conf 

REQUESTS_TIMEOUT_SECONDS = float(os.getenv("REQUESTS_TIMEOUT_SECONDS", 1))


class CustomTimeout(TimeoutSauce):
    def __init__(self, *args, **kwargs):
        if kwargs["connect"] is None:
            kwargs["connect"] = REQUESTS_TIMEOUT_SECONDS
        if kwargs["read"] is None:
            kwargs["read"] = REQUESTS_TIMEOUT_SECONDS
        super().__init__(*args, **kwargs)


def post_worker_init(worker):
    # Set it globally, instead of specifying ``timeout=..`` kwarg on each call.
#     requests.adapters.TimeoutSauce = CustomTimeout
    worker.log.info('greetings from post worker init hook')
    res = requests.post(f'http://{conf["OMS_HOST"]}/api/v1/internal/open-orders')
    worker.log.info(res)



