from abc import ABC
from abc import abstractmethod
from threading import RLock, Thread
from time import time, sleep
from typing import Dict, List

from alpaca_trade_api.rest import REST

from src.cache import BaseAssetCache, StockCache, CryptoCache


class BaseAssetStore(Thread, ABC):
    daemon: bool = True
    is_running: bool = True
    UPDATE_INTERVAL: int = 30
    UPDATE_SLEEP_FACTOR: int = 100

    def __init__(self) -> None:
        super().__init__()
        self._lock: RLock = RLock()
        self._assets_map: Dict[str, BaseAssetCache] = dict()

        self._api: REST = REST(api_version="v2")
        self._api_raw: REST = REST(api_version="v2", raw_data=True)

    def __getitem__(self, item: str) -> BaseAssetCache:
        with self._lock:
            return self._assets_map.get(item, None)

    @abstractmethod
    def add_asset(self, asset_name: str) -> bool:
        raise NotImplementedError()

    def get_all_asset_name(self) -> List[str]:
        with self._lock:
            return list(self._assets_map.keys())

    def run(self) -> None:
        sleep_time: float = self.UPDATE_INTERVAL / self.UPDATE_SLEEP_FACTOR
        prev_time: float = time()
        while self.is_running:
            current_time: float = time()
            if prev_time + self.UPDATE_INTERVAL <= current_time:
                prev_time = current_time
                for asset in self._assets_map.values():
                    asset.update()
            else:
                sleep(sleep_time)


class StockStore(BaseAssetStore):
    def __init__(self) -> None:
        super().__init__()
        self.name: str = self.__class__.__name__

    def add_asset(self, asset_name: str) -> bool:
        stock_cache: StockCache = StockCache(asset_name, self._api)
        with self._lock:
            self._assets_map[asset_name] = stock_cache
        return True


class CryptoStore(BaseAssetStore):
    def __init__(self) -> None:
        super().__init__()
        self.name: str = self.__class__.__name__

    def add_asset(self, asset_name: str) -> bool:
        crypto_cache: CryptoCache = CryptoCache(asset_name, self._api)
        with self._lock:
            self._assets_map[asset_name] = crypto_cache
        return True


if __name__ == '__main__':
    crypto_store: CryptoStore = CryptoStore()
    print(crypto_store.__class__.__name__)
