from typing import Callable

from src.data_handlers.price_tracker import PriceTrackerManager
from src.data_handlers.rabbit_mq import Consumer, Producer
from src.data_handlers.streaming_client import StreamClient
from src.storages import DotEnvConfig, PrimitiveJsonDB
from src.cache import CryptoCache, StockCache


class DataCollectors:

    def __init__(self, app_config: PrimitiveJsonDB, conf: DotEnvConfig) -> None:
        self.manager: PriceTrackerManager = PriceTrackerManager(conf)
        self.consumer: Consumer = Consumer(conf, 'limit_order_queue',
                                           self.manager.on_message_from_rabbit)
        self.crypto_cache = CryptoCache(app_config['assets']['crypto'], conf)
        self.stock_cache = StockCache(app_config['assets']['stock'], conf)

        on_stock_trade_composite = \
            self.on_message_from_streaming_client(self.stock_cache.on_trade,
                                                  self.manager.stock_container.on_message_from_stream_client)
        on_crypto_trade_composite = \
            self.on_message_from_streaming_client(self.crypto_cache.on_trade,
                                                  self.manager.crypto_container.on_message_from_stream_client)
        self._stream_client: StreamClient = StreamClient(stocks=app_config['assets']['stock'],
                                                         on_stock_trade=on_stock_trade_composite,
                                                         cryptos=app_config['assets']['crypto'],
                                                         on_crypto_trade=on_crypto_trade_composite,
                                                         config=conf)

        self.start_threads()

    def start_threads(self) -> None:
        self.crypto_cache.start()
        self.stock_cache.start()
        self.consumer.start()
        self._stream_client.start()
        self.manager.start_producer_threads()
        # self._producer.start()

    def on_message_from_streaming_client(self, *callbacks: Callable) -> Callable:
        async def outer(trade):
            for fn in callbacks:
                await fn(trade)
        return outer
