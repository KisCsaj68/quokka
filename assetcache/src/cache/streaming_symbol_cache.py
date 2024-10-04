import time
from threading import Thread
from abc import ABC, abstractmethod
from typing import List, Dict, Any

from src.storages import DotEnvConfig
from src.utils.convert import rename_keys
from src.utils.rw_lock import ReadWriteLock
from alpaca_trade_api import REST
from alpaca_trade_api.entity_v2 import trade_mapping_v2

from src.utils import ParseLatestV2ToReadableDict
from src.metrics import STREAM_TRADE_TOTAL, CACHE_UPDATE_PRICE, CACHE_READ_PRICE, CACHE_INIT_PRICE


class SymbolCache(ABC, Thread):

    def __init__(self, symbols: List[str], conf: DotEnvConfig):
        super().__init__(daemon=True)
        self._rw_lock = ReadWriteLock()
        self._symbols_prices = {s: None for s in symbols}
        self._conf = conf
        self._trade_api = REST(key_id=conf['APCA_API_KEY_ID'],
                               secret_key=conf['APCA_API_SECRET_KEY'],
                               base_url=conf['APCA_API_BASE_URL'],
                               api_version=conf['APCA_API_VERSION'],
                               raw_data=True)
        self.initialized = False
        self.ready = False

    async def on_trade(self, trade):
        """
        This is a callback method.
        This method updates the price on each incoming datapoints.
        :param trade:
        :return:
        """
        STREAM_TRADE_TOTAL.labels(self._get_metric_label()).inc()
        if not self.initialized:
            return
        with CACHE_UPDATE_PRICE.labels(self._get_metric_label()).time():
            trade = rename_keys(trade)
            symbol = trade['symbol']
            price = trade['price']
            try:
                self._rw_lock.acquire_write()
                self._symbols_prices[symbol] = price
            finally:
                self._rw_lock.release_write()

    def __getitem__(self, symbol):
        """
        Magic method to get the actual price of a symbol.
        price = my_cache[symbol]
        :param symbol: 
        :return: 
        """
        with CACHE_READ_PRICE.labels(self._get_metric_label()).time():
            try:
                self._rw_lock.acquire_read()
                return self._symbols_prices[symbol]
            finally:
                self._rw_lock.release_read()

    def _set_initial_prices(self) -> None:
        """
        Sets up latest known prices to the cache on creation.
        :return: None
        """
        if not self.initialized:
            with CACHE_INIT_PRICE.labels(self._get_metric_label()).time():
                symbols = self._symbols_prices.keys()
                latest_prices = self._get_latest_price(symbols)
                print(latest_prices, flush=True)
                try:
                    self._rw_lock.acquire_write()
                    for k, v in latest_prices.items():
                        print(k + " " + str(v['price']), flush=True)
                        self._symbols_prices[k] = v['price']
                finally:
                    self._rw_lock.release_write()
                    self.initialized = True

    def run(self):
        self._set_initial_prices()
        print(f'{self._get_metric_label()} cache initialized with prices', flush=True)

    @abstractmethod
    def _get_latest_price(self, symbols: List[str]) -> Dict[str, Any]:
        """
        An abstract method
        Concrete implementors get the latest known prices from REST API.
        :param symbols:
        :return:
        """
        raise NotImplementedError()

    @abstractmethod
    def _get_metric_label(self):
        raise NotImplementedError()


class StockCache(SymbolCache):

    def _get_metric_label(self):
        return 'stock'

    @ParseLatestV2ToReadableDict(trade_mapping_v2)
    def _get_latest_price(self, symbols: List[str]) -> Dict[str, Any]:
        try:
            result = self._trade_api.get_latest_trades(symbols, 'iex')
            self.ready = True
            return result

        except Exception as e:
            self.ready = False
            print(e)

    def __init__(self, symbols: List[str], conf: DotEnvConfig):
        super().__init__(symbols, conf)


class CryptoCache(SymbolCache):

    def _get_metric_label(self):
        return 'crypto'

    @ParseLatestV2ToReadableDict(trade_mapping_v2)
    def _get_latest_price(self, symbols: List[str]) -> Dict[str, Any]:
        try:
            result = self._trade_api.get_latest_crypto_trades(symbols, 'us')
            self.ready = True
            return result
        except Exception as e:
            self.ready = False
            print(e)

    def __init__(self, symbols: List[str], conf: DotEnvConfig):
        super().__init__(symbols, conf)
