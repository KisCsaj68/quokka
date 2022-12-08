import pika
import json
from threading import Thread
from src.storages import DotEnvConfig


class Consumer(Thread):

    def __init__(self, conf: DotEnvConfig):
        super().__init__()
        params = pika.URLParameters(conf['RABBITMQ_URL'])
        self._connection = pika.BlockingConnection(params)
        self._channel = self._connection.channel()
        self._channel.queue_declare(queue='limit_order_queue', durable=True)
        self._channel.basic_consume(queue='limit_order_queue', on_message_callback=Consumer.callback, auto_ack=True)

    def run(self):
        self._channel.start_consuming()

    @staticmethod
    def callback(ch, method, properties, body):
        print('hello from callback start', flush=True)
        print(body, flush=True)
        parsed = json.loads(body)
        print(parsed, flush=True)
