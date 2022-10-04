from alpaca_trade_api import REST

from src.data_handlers.collectors import LatestCryptoTicker
from src.data_handlers.collectors import LatestStockTicker
from src.storages import DotEnvConfig
from src.storages import PrimitiveJsonDB


class DataCollectors:

    def __init__(self, db: PrimitiveJsonDB, conf: DotEnvConfig,
                 auto_start: bool = True) -> None:
        api: REST = REST(
            key_id=conf["APCA_API_KEY_ID"],
            secret_key=conf["APCA_API_SECRET_KEY"],
            base_url=conf["APCA_API_BASE_URL"],
            api_version=conf["APCA_API_VERSION"],
            raw_data=True
        )
        self.latest_stock_ticker: LatestStockTicker = \
            LatestStockTicker(api, db)
        self.latest_crypto_ticker: LatestCryptoTicker = \
            LatestCryptoTicker(api, db)

        if auto_start is True:
            self.start_threads()

    def start_threads(self) -> None:
        self.latest_stock_ticker.start()
        self.latest_crypto_ticker.start()
