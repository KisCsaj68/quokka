from typing import Dict, Callable, Any


class ParseLatestV2ToReadableDict:
    __slots__ = "mapper"

    def __init__(self, mapper: Dict[str, str]) -> None:
        self.mapper: Dict[str, str] = mapper

    def __call__(self, func: Callable):
        def wrapper(*args):
            latest_v2: Dict[str, Dict] = func(*args)
            try:
                for name, bar_mapping in latest_v2.items():
                    latest_v2[name] = {
                        self.mapper.get(key, key): val
                        for key, val in bar_mapping.items()
                    }
            except AttributeError:
                latest_v2 = {}
            return latest_v2

        return wrapper


class ParseRawStreamToReadableDict:
    __slots__ = "mapper"

    def __init__(self, mapper: Dict[str, str]) -> None:
        self.mapper: Dict[str, str] = mapper

    def __call__(self, func: Callable):
        def wrapper(*args):
            raw_from_stream: Dict[str, Any] = func(*args)
            readable = {}
            try:
                for short_key, value in raw_from_stream.items():
                    readable[self.mapper.get(short_key, short_key)] = value
            except AttributeError:
                readable = {}
            return readable

        return wrapper
