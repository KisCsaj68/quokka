
from src.storages import DotEnvConfig, PrimitiveJsonDB
from alpaca_trade_api import Stream


class StreamClient:

    def __init__(self, db: PrimitiveJsonDB, config: DotEnvConfig):
        self._stream = Stream(key_id=config['APCA_API_KEY_ID'],
                              secret_key=config['APCA_API_SECRET_KEY'],
                              data_stream_url=config['APCA_API_STREAM_URL'],
                              data_feed='iex')
        stocks = db['assets']['stock']
        cryptos = db['assets']['crypto']
        self._stream.subscribe_trades(self.on_trade, *stocks)
        self._stream.subscribe_crypto_trades(self.on_trade, *cryptos)
        self._stream.run()

    async def on_trade(self, b):
        print(b)
        # TODO: implement storage into cache
        pass
