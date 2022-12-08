from typing import Callable
import pika
import json
from threading import Thread
from src.storages import DotEnvConfig


class Consumer(Thread):

    def __init__(self, conf: DotEnvConfig, queue: str, on_message: Callable):
        """
            Subscribes to the given RabbitMQ queue, runs as a background thread.
           conf: configs coming from environment variables
           queue: name of the queue
           on_message: callback function executed on each incoming message from the queue
            on_message(channel, method, properties, body)
             - channel: BlockingChannel
             - method: spec.Basic.Deliver
             - properties: spec.BasicProperties
             - body: bytes
        """
        super().__init__(daemon=True)
        params = pika.URLParameters(conf['RABBITMQ_URL'])
        self._connection = pika.BlockingConnection(params)
        self._channel = self._connection.channel()
        self._channel.queue_declare(queue=queue, durable=True)
        self._channel.basic_consume(queue, on_message, auto_ack=True)

    def run(self):
        self._channel.start_consuming()
