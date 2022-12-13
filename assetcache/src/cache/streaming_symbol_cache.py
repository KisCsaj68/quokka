from abc import ABC, abstractmethod
from typing import List, Dict, Any

from src.storages import DotEnvConfig
from src.utils.convert import rename_keys
from src.utils.rw_lock import ReadWriteLock
from alpaca_trade_api import REST
from alpaca_trade_api.entity_v2 import trade_mapping_v2

from src.utils import ParseLatestV2ToReadableDict


class SymbolCache(ABC):

    def __init__(self, symbols: List[str], conf: DotEnvConfig):
        self._rw_lock = ReadWriteLock()
        self._symbols_prices = {s: None for s in symbols}
        self._conf = conf
        self._trade_api = REST(key_id=conf['APCA_API_KEY_ID'],
                               secret_key=conf['APCA_API_SECRET_KEY'],
                               base_url=conf['APCA_API_BASE_URL'],
                               api_version=conf['APCA_API_VERSION'],
                               raw_data=True)
        self.initialized = True
        # this mas still cause issues with blocking the worker...
        # threading.Thread(target=self._set_initial_prices()).start()

    async def on_trade(self, trade):
        if not self.initialized:
            return
        """
        This is a callback method.
        This method updates the price on each incoming datapoints.
        :param trade:
        :return:
        """
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
        symbols = self._symbols_prices.keys()
        latest_prices = self._get_latest_price(symbols)
        try:
            self._rw_lock.acquire_write()
            for k, v in latest_prices.items():
                self._symbols_prices[k] = v['price']
        finally:
            self._rw_lock.release_write()
            self.initialized = True

    @abstractmethod
    def _get_latest_price(self, symbols: List[str]) -> Dict[str, Any]:
        """
        An abstract method
        Concrete implementors get the latest known prices from REST API.
        :param symbols:
        :return:
        """
        raise NotImplementedError()


class StockCache(SymbolCache):

    @ParseLatestV2ToReadableDict(trade_mapping_v2)
    def _get_latest_price(self, symbols: List[str]) -> Dict[str, Any]:
        return self._trade_api.get_latest_trades(symbols, 'iex')

    def __init__(self, symbols: List[str], conf: DotEnvConfig):
        super().__init__(symbols, conf)


class CryptoCache(SymbolCache):

    @ParseLatestV2ToReadableDict(trade_mapping_v2)
    def _get_latest_price(self, symbols: List[str]) -> Dict[str, Any]:
        return self._trade_api.get_latest_crypto_trades(symbols, 'CBSE')

    def __init__(self, symbols: List[str], conf: DotEnvConfig):
        super().__init__(symbols, conf)
