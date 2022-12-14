from threading import Thread
from time import sleep

import pika

from src.storages import DotEnvConfig
import jsonpickle


# class Producer:
class Producer(Thread):
    def __init__(self, conf: DotEnvConfig, queue: str, exchange: str, routing_key: str):
        super().__init__(daemon=True)
        self.routing_key = routing_key
        params = pika.URLParameters(conf['RABBITMQ_URL'])
        self.connection = pika.BlockingConnection(params)
        self.exchange = exchange
        self.channel = self.connection.channel()
        self.channel.exchange_declare(exchange=self.exchange, exchange_type='topic', durable=True)
        self.channel.queue_declare(queue=queue, durable=True)
        self.channel.queue_bind(exchange=exchange, queue=queue, routing_key=self.routing_key)

    def produce(self, message):
        message = jsonpickle.encode(message)
        self.channel.basic_publish(exchange=self.exchange, routing_key=self.routing_key, body=message)

    def run(self):
        while True:
            self.connection.process_data_events()
            sleep(15)
