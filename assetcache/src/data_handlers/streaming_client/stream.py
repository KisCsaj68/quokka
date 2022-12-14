from threading import Thread
from typing import List, Callable
from src.storages import DotEnvConfig
from alpaca_trade_api import Stream


class StreamClient(Thread):

    def __init__(self, stocks: List[str], on_stock_trade: Callable, cryptos: List[str], on_crypto_trade: Callable,
                 config: DotEnvConfig):
        super().__init__(daemon=True)
        self._stream = Stream(key_id=config['APCA_API_KEY_ID'],
                              secret_key=config['APCA_API_SECRET_KEY'],
                              data_stream_url=config['APCA_API_STREAM_URL'],
                              raw_data=True,
                              data_feed='iex')
        self._stream.subscribe_trades(on_stock_trade, *stocks)
        self._stream.subscribe_crypto_trades(on_crypto_trade, *cryptos)

    def run(self):
        self._stream.run()
