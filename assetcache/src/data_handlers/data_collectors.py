import json

from src.data_handlers.rabbit_mq import Consumer
from src.storages import DotEnvConfig


class DataCollectors:

    def __init__(self, conf: DotEnvConfig) -> None:
        self.consumer: Consumer = Consumer(conf, 'limit_order_queue',
                                           self.callback)  # TODO: this may have a better place somewhere else!
        self.start_threads()

    def start_threads(self) -> None:
        self.consumer.start()  # TODO: this may have a better place somewhere else!

    def callback(self, ch, method, properties, body) -> None:
        parsed = json.loads(body)
        print(parsed, flush=True)
