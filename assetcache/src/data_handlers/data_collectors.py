import json

from src.data_handlers.rabbit_mq import Consumer
from src.data_handlers.streaming_client import StreamClient
from src.storages import DotEnvConfig, PrimitiveJsonDB
from src.cache import CryptoCache, StockCache


class DataCollectors:

    def __init__(self, app_config: PrimitiveJsonDB, conf: DotEnvConfig) -> None:
        self.consumer: Consumer = Consumer(conf, 'limit_order_queue',
                                           self.callback)  # TODO: this may have a better place somewhere else!
        self.crypto_cache = CryptoCache(app_config['assets']['crypto'], conf)
        self.stock_cache = StockCache(app_config['assets']['stock'], conf)

        self._stream_client: StreamClient = StreamClient(stocks=app_config['assets']['stock'],
                                                         on_stock_trade=self.stock_cache.on_trade,
                                                         cryptos=app_config['assets']['crypto'],
                                                         on_crypto_trade=self.crypto_cache.on_trade,
                                                         config=conf)

        self.start_threads()

    def start_threads(self) -> None:
        self.consumer.start()
        self._stream_client.start()  # TODO: this may have a better place somewhere else!

    def callback(self, ch, method, properties, body) -> None:
        parsed = json.loads(body)
        print(parsed, flush=True)
