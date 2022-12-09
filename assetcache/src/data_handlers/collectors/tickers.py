from abc import abstractmethod
from threading import RLock, Thread
from time import time, sleep
from typing import List, Dict, Callable

from alpaca_trade_api import REST
from alpaca_trade_api.entity_v2 import bar_mapping_v2
from alpaca_trade_api.entity_v2 import quote_mapping_v2
from alpaca_trade_api.entity_v2 import trade_mapping_v2

from src.data_handlers.collectors.decorators import ParseLatestV2ToReadableDict
from src.storages import PrimitiveJsonDB



class _Tickers(Thread):
    daemon = True
    IS_RUNNING: bool = True
    UPDATE_INTERVAL: float = 10

    def __init__(self, api: REST) -> None:
        super().__init__()
        self._api: REST = api
        self.name: str = self.__class__.__name__

        self._lock: RLock = RLock()

        self.assets: Dict[str, Dict[str, List[str]]] = {}
        self._latest_bars: Dict = {}
        self._latest_trades: Dict = {}
        self._latest_quotes: Dict = {}

    @property
    def latest_bars(self) -> Dict:
        while self._lock:
            return self._latest_bars

    @property
    def latest_trades(self) -> Dict:
        while self._lock:
            return self._latest_trades

    @property
    def latest_quotes(self) -> Dict:
        while self._lock:
            return self._latest_quotes

    @abstractmethod
    def _update_latest_bars(self) -> Dict:
        raise NotImplementedError()

    @abstractmethod
    def _update_latest_trades(self) -> Dict:
        raise NotImplementedError()

    @abstractmethod
    def _update_latest_quotes(self) -> Dict:
        raise NotImplementedError()

    def get_latest_asset_bar(self, key: str) -> Dict:
        while self._lock:
            return self._latest_bars.get(key, {})

    def get_latest_asset_trade(self, key: str) -> Dict:
        while self._lock:
            return self._latest_trades.get(key, {})

    def get_latest_asset_quote(self, key: str) -> Dict:
        while self._lock:
            return self._latest_quotes.get(key, {})

    def update(self) -> None:
        latest_bars: Dict = self._update_latest_bars()
        latest_trades: Dict = self._update_latest_trades()
        latest_quotes: Dict = self._update_latest_quotes()
        with self._lock:
            self._latest_bars = latest_bars
            self._latest_trades = latest_trades
            self._latest_quotes = latest_quotes

    def run(self) -> None:
        delay: float = self.UPDATE_INTERVAL / 5
        last_time: float = time()
        self.update()
        while self.IS_RUNNING:
            if last_time + self.UPDATE_INTERVAL < time():
                self.update()
                last_time: float = time()
            sleep(delay)


class LatestStockTicker(_Tickers):
    def __init__(self, api: REST, db: PrimitiveJsonDB) -> None:
        super().__init__(api)
        self._stocks: List[str] = db["assets"]["stock"]

    @ParseLatestV2ToReadableDict(bar_mapping_v2)
    def _update_latest_bars(self) -> Dict:
        if self._stocks:
            return self._api.get_latest_bars(self._stocks)

    @ParseLatestV2ToReadableDict(trade_mapping_v2)
    def _update_latest_trades(self) -> Dict:
        if self._stocks:
            return self._api.get_latest_trades(self._stocks)

    @ParseLatestV2ToReadableDict(quote_mapping_v2)
    def _update_latest_quotes(self) -> Dict:
        if self._stocks:
            return self._api.get_latest_quotes(self._stocks)


class LatestCryptoTicker(_Tickers):
    BASE_EXCHANGE_CODE: str = "CBSE"

    def __init__(self, api: REST, db: PrimitiveJsonDB) -> None:
        super().__init__(api)
        self._cryptos: List[str] = db["assets"]["crypto"]

    @ParseLatestV2ToReadableDict(bar_mapping_v2)
    def _update_latest_bars(self) -> Dict:
        if self._cryptos:
            return self._api.get_latest_crypto_bars(
                self._cryptos,
                self.BASE_EXCHANGE_CODE)

    @ParseLatestV2ToReadableDict(trade_mapping_v2)
    def _update_latest_trades(self) -> Dict:
        if self._cryptos:
            return self._api.get_latest_crypto_trades(
                self._cryptos,
                self.BASE_EXCHANGE_CODE)

    @ParseLatestV2ToReadableDict(quote_mapping_v2)
    def _update_latest_quotes(self) -> Dict:
        if self._cryptos:
            return self._api.get_latest_crypto_quotes(
                self._cryptos,
                self.BASE_EXCHANGE_CODE)
