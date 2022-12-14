import requests


def post_worker_init(worker):
    worker.log.info('hello form post worker init')
    res = requests.post("http://oms:9000/api/v1/internal/open-orders")
    worker.log.info(res)
