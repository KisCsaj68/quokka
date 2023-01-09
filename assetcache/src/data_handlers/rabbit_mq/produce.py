import threading
from threading import Thread
from time import sleep

import pika

from src.storages import DotEnvConfig
import jsonpickle


# class Producer:
class Producer(Thread):
    def __init__(self, conf: DotEnvConfig, queue: str, exchange: str, routing_key: str):
        super().__init__(daemon=True)
        self.name = 'producer_thread'
        self.local = threading.local()
        self.routing_key = routing_key
        self.params = pika.URLParameters(conf['RABBITMQ_URL'])
        self.exchange = exchange
        self.queue = queue
        self.connection = None
        self.channel = None
        self.init_connection()

    def produce(self, message):
        self.connection.add_callback_threadsafe(lambda: self._produce(message))

    def _produce(self, message):
        self.check_setup()
        message = jsonpickle.encode(message)
        self.channel.basic_publish(exchange=self.exchange, routing_key=self.routing_key,
                                   body=message)

    def run(self):
        while True:
            self.check_setup()
            self.connection.process_data_events(time_limit=1)
            sleep(2)

    def check_setup(self):
        if not self.connection or not self.channel or self.connection.is_closed or self.channel.is_closed:
            self.init_connection()

    def init_connection(self):
        print("init conn", flush=True)
        connection = pika.BlockingConnection(self.params)
        channel = connection.channel()
        channel.exchange_declare(exchange=self.exchange, exchange_type='topic', durable=True)
        channel.queue_declare(queue=self.queue, durable=True)
        channel.queue_bind(exchange=self.exchange, queue=self.queue, routing_key=self.routing_key)
        self.connection = connection
        self.channel = channel
