import functools
import json
import time
from timeit import default_timer

from typing import Tuple, Dict

from falcon import Request
from falcon import Response
from prometheus_client import multiprocess
from prometheus_client import generate_latest, CollectorRegistry, CONTENT_TYPE_LATEST

from src.cache.streaming_symbol_cache import SymbolCache
from src.storages.primitive_json_db import PrimitiveJsonDB
from src.metrics import API_RESPONSE, API_REQUEST_TOTAL


def parse_dict_to_json_bytes(dictionary: dict) -> Tuple[bytes, int]:
    byte_json: bytes = bytes(
        json.dumps(dictionary, ensure_ascii=True, indent=4),
        encoding="utf-8"
    )
    return byte_json, len(byte_json)


def get_response_code_family(response: Response) -> str:
    http_code = int(response.status[:3])
    if 100 <= http_code < 200:
        return 'INFORMATIONAL'
    elif 200 <= http_code < 300:
        return 'SUCCESSFUL'
    elif 300 <= http_code < 400:
        return 'REDIRECTION'
    elif 400 <= http_code < 500:
        return 'CLIENT_ERROR'
    return 'SERVER_ERROR'


def with_metrics(asset_type: str = None):
    def decorator(func):
        def wrapper(s, request, response, *args, **kwargs):
            _asset_type = asset_type
            if not asset_type:
                _asset_type = s.asset_type
            start = default_timer()
            func(s, request, response, *args, **kwargs)
            API_REQUEST_TOTAL.labels(request.uri_template, request.method, get_response_code_family(response)).inc()
            API_RESPONSE.labels(request.uri_template, request.method, _asset_type,
                                get_response_code_family(response)).observe(max(default_timer() - start, 0))

        return wrapper

    return decorator


class AssetNamesRoute:
    DEFAULT_CONTENT_TYPE: str = "application/json"

    def __init__(self, db: PrimitiveJsonDB) -> None:
        self.db: PrimitiveJsonDB = db
        self.asset_data, self.asset_length = parse_dict_to_json_bytes(
            {"assets": db["assets"]}
        )
        self.crypto_data, self.crypto_length = parse_dict_to_json_bytes(
            {"crypto": db["assets"]["crypto"]}
        )
        self.stock_data, self.stock_length = parse_dict_to_json_bytes(
            {"stock": db["assets"]["stock"]}
        )

    @with_metrics('all')
    def on_get(self, req: Request, resp: Response) -> None:
        resp.data = self.asset_data
        resp.content_length = self.asset_length
        resp.content_type = self.DEFAULT_CONTENT_TYPE

    @with_metrics('crypto')
    def on_get_crypto(self, req: Request, resp: Response) -> None:
        resp.data = self.crypto_data
        resp.content_length = self.crypto_length
        resp.content_type = self.DEFAULT_CONTENT_TYPE

    @with_metrics('stock')
    def on_get_stock(self, req: Request, resp: Response) -> None:
        resp.data = self.stock_data
        resp.content_length = self.stock_length
        resp.content_type = self.DEFAULT_CONTENT_TYPE


 class HealthCheckRoute:

    def __init__(self, stock_cache: SymbolCache, crypto_cache: SymbolCache):
        self.stock_cache = stock_cache
        self.crypto_cache = crypto_cache

    def on_get(self, _: Request, resp: Response):
        if self.stock_cache.ready and self.crypto_cache.ready:
            resp.status = 200
            return
        resp.status = 400


class LatestAssetRoute:
    DEFAULT_CONTENT_TYPE: str = "application/json"

    def __init__(self,
                 cache: SymbolCache, asset_type: str) -> None:
        self.asset_type = asset_type
        self._cache: SymbolCache = cache

    @with_metrics()
    def on_get(self, req: Request, resp: Response, symbol: str) -> None:
        self.make_response(req, resp, symbol)

    @with_metrics()
    def on_get_trades(self, req: Request, resp: Response, symbol: str) -> None:
        self.make_response(req, resp, symbol)

    def make_response(self, req: Request, resp: Response, symbol: str) -> None:
        if not self.is_valid(symbol):
            return
        symbol: str = symbol.upper()
        latest: Dict = {'price': self._cache[symbol], 'symbol': symbol, 'type': self.asset_type}
        resp.data, resp.content_length = parse_dict_to_json_bytes(latest)
        resp.content_type = self.DEFAULT_CONTENT_TYPE

    @staticmethod
    def is_valid(symbol: str) -> bool:
        if 10 < len(symbol):
            return False
        if not symbol.isalpha():
            return False
        if not symbol.isascii():
            return False
        return True


class Metrics:
    def on_get(self, req: Request, resp: Response):
        registry = CollectorRegistry()
        multiprocess.MultiProcessCollector(registry)
        data = generate_latest(registry)
        resp.data, resp.content_length = data, len(data)
        resp.content_type = CONTENT_TYPE_LATEST
