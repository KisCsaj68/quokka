from abc import ABC, abstractmethod
from typing import List, Dict, Any

from src.data_handlers.collectors import ParseRawStreamToReadableDict, ParseLatestV2ToReadableDict
from src.storages import DotEnvConfig
from src.rw_lock import ReadWriteLock
from alpaca_trade_api import REST
from alpaca_trade_api.entity_v2 import trade_mapping_v2


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
        self.set_initial_prices()

    async def on_trade(self, trade):
        trade = self.rename_keys(trade)
        symbol = trade['symbol']
        price = trade['price']
        self._rw_lock.acquire_write()
        try:
            self._symbols_prices[symbol] = price
        finally:
            self._rw_lock.release_write()

    @ParseRawStreamToReadableDict(trade_mapping_v2)
    def rename_keys(self, raw_trade: Dict[str, Any]) -> Dict[str, Any]:
        return raw_trade

    def __getitem__(self, item):
        self._rw_lock.acquire_read()
        try:
            return self._symbols_prices[item]
        finally:
            self._rw_lock.release_read()

    def set_initial_prices(self) -> None:
        symbols = self._symbols_prices.keys()
        latest_prices = self.get_latest_price(symbols)
        self._rw_lock.acquire_write()
        try:
            for k, v in latest_prices.items():
                self._symbols_prices[k] = v['price']
        finally:
            self._rw_lock.release_write()

    @abstractmethod
    def get_latest_price(self, symbols: List[str]) -> Dict[str, Any]:
        raise NotImplementedError()


class StockCache(SymbolCache):

    @ParseLatestV2ToReadableDict(trade_mapping_v2)
    def get_latest_price(self, symbols: List[str]) -> Dict[str, Any]:
        return self._trade_api.get_latest_trades(symbols, 'iex')

    def __init__(self, symbols: List[str], conf: DotEnvConfig):
        super().__init__(symbols, conf)


class CryptoCache(SymbolCache):

    @ParseLatestV2ToReadableDict(trade_mapping_v2)
    def get_latest_price(self, symbols: List[str]) -> Dict[str, Any]:
        return self._trade_api.get_latest_crypto_trades(symbols, 'CBSE')

    def __init__(self, symbols: List[str], conf: DotEnvConfig):
        super().__init__(symbols, conf)
