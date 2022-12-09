
from typing import List, Callable, Awaitable
from src.storages import DotEnvConfig, PrimitiveJsonDB
from alpaca_trade_api import Stream


class StreamClient:

    def __init__(self, stocks: List[str], on_stock_trade: Callable, cryptos: List[str], on_crypto_trade: Callable, config: DotEnvConfig):
        self._stream = Stream(key_id=config['APCA_API_KEY_ID'],
                              secret_key=config['APCA_API_SECRET_KEY'],
                              data_stream_url=config['APCA_API_STREAM_URL'],
                              raw_data=True,
                              data_feed='iex')
        self._stream.subscribe_trades(on_stock_trade, *stocks)
        self._stream.subscribe_crypto_trades(on_crypto_trade, *cryptos)
        self._stream.run()
