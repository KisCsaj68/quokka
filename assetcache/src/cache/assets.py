from abc import ABC
from abc import abstractmethod
from threading import RLock
from typing import List, Union, Dict

from alpaca_trade_api.entity_v2 import BarsV2, LatestBarsV2, BarV2
from alpaca_trade_api.entity_v2 import bar_mapping_v2
from alpaca_trade_api.rest import REST
from alpaca_trade_api.rest import TimeFrame
from numpy import mean

BarDictType = Dict[str, Union[str, float, int]]


def parse_bars_v2_to_dict(bars_v2: Dict[str, Dict]
                          ) -> Dict[str, Union[str, float, int]]:
    if isinstance(bars_v2, dict):
        symbol: str = tuple(bars_v2.keys())[0]
        bar_raw = bars_v2[symbol]
        bar_raw["symbol"] = symbol
        return {bar_mapping_v2.get(key, key): val
                for key, val in bar_raw.items()
                }

    return {}


class BaseAssetCache(ABC):
    # Redis in memory nosql cache
    def __init__(self, asset_name: str, api: REST, api_raw: REST) -> None:
        self._asset_name: str = asset_name
        self._api: REST = api
        self._api_raw: REST = api_raw
        self._lock: RLock = RLock()
        self._daily_bars: List[BarsV2] = None
        self._last_bar: BarDictType = None
        self._mean_24h: float = None

        self.update()

    def update(self) -> None:
        self.update_daily_bars()
        self.update_last_bar()
        self.update_mean_24h()

    def update_mean_24h(self) -> None:
        mean_24h: float = float(mean(
            [bar_v2.o for bar_v2 in self._daily_bars])
        )
        with self._lock:
            self._mean_24h = mean_24h

    @abstractmethod
    def update_daily_bars(self) -> None:
        pass

    @abstractmethod
    def update_last_bar(self) -> None:
        pass

    @property
    def last_bar(self) -> BarDictType:
        with self._lock:
            return self._last_bar

    @property
    def mean_24h(self) -> float:
        with self._lock:
            return self._mean_24h

    @property
    def asset_name(self) -> str:
        return self._asset_name

    def __eq__(self, other) -> bool:
        return (
                self.__class__ == other.__class__
                and self._asset_name == other.asset_name
        )

    def __hash__(self) -> int:
        return hash(self._asset_name)


class StockCache(BaseAssetCache):
    def __init__(self, asset_name: str, api: REST, api_raw: REST) -> None:
        super().__init__(asset_name, api, api_raw)

    def update_daily_bars(self) -> None:
        daily_bars: List[BarsV2] = self._api.get_bars(self._asset_name,
                                                      TimeFrame.Minute)
        with self._lock:
            self._daily_bars = daily_bars

    def update_last_bar(self) -> None:
        last_bar: BarV2 = self._api_raw.get_latest_bar(self._asset_name)
        last_bar_with_symbol: Dict[str, dict] = {self._asset_name: last_bar}
        parsed_last_bar: BarDictType = parse_bars_v2_to_dict(
            last_bar_with_symbol
        )

        with self._lock:
            self._last_bar = parsed_last_bar


class CryptoCache(BaseAssetCache):
    BASE_EXCHANGE_CODE: str = "CBSE"

    def __init__(self, asset_name: str, api: REST, api_raw: REST) -> None:
        super().__init__(asset_name, api, api_raw)

    def update_daily_bars(self) -> None:
        daily_bars: List[BarsV2] = self._api.get_crypto_bars(
            symbol=self._asset_name,
            timeframe=TimeFrame.Minute
        )
        with self._lock:
            self._daily_bars = daily_bars

    def update_last_bar(self) -> None:
        last_bar: LatestBarsV2 = self._api_raw.get_latest_crypto_bars(
            [self._asset_name],
            self.BASE_EXCHANGE_CODE
        )
        parsed_last_bar: dict = parse_bars_v2_to_dict(last_bar)
        with self._lock:
            self._last_bar = parsed_last_bar
