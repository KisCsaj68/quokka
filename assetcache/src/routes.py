import json

from typing import Tuple, Dict

from falcon import Request
from falcon import Response

from src.cache.streaming_symbol_cache import SymbolCache
from src.storages.primitive_json_db import PrimitiveJsonDB


def parse_dict_to_json_bytes(dictionary: dict) -> Tuple[bytes, int]:
    byte_json: bytes = bytes(
        json.dumps(dictionary, ensure_ascii=True, indent=4),
        encoding="utf-8"
    )
    return byte_json, len(byte_json)


class Ping:
    DEFAULT_CONTENT_TYPE: str = "application/json"
    __slots__ = ("data", "content_length")

    def __init__(self) -> None:
        message: Dict[str, str] = {"Ping": "Hi from AssetCacheProxy v1"}
        self.data, self.content_length = parse_dict_to_json_bytes(message)

    def on_get(self, req: Request, resp: Response) -> None:
        resp.data = self.data
        resp.content_length = self.content_length
        resp.content_type = self.DEFAULT_CONTENT_TYPE


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

    def on_get(self, req: Request, resp: Response) -> None:
        resp.data = self.asset_data
        resp.content_length = self.asset_length
        resp.content_type = self.DEFAULT_CONTENT_TYPE

    def on_get_crypto(self, req: Request, resp: Response) -> None:
        resp.data = self.crypto_data
        resp.content_length = self.crypto_length
        resp.content_type = self.DEFAULT_CONTENT_TYPE

    def on_get_stock(self, req: Request, resp: Response) -> None:
        resp.data = self.stock_data
        resp.content_length = self.stock_length
        resp.content_type = self.DEFAULT_CONTENT_TYPE


class LatestAssetRoute:
    DEFAULT_CONTENT_TYPE: str = "application/json"

    def __init__(self,
                 cache: SymbolCache) -> None:
        self._cache: SymbolCache = cache

    def on_get(self, req: Request, resp: Response, symbol: str) -> None:
        self.make_response(req, resp, symbol)

    def on_get_trades(self, req: Request, resp: Response, symbol: str) -> None:
        self.make_response(req, resp, symbol)

    def make_response(self, req: Request, resp: Response, symbol: str) -> None:
        if not self.is_valid(symbol):
            return
        symbol: str = symbol.upper()
        latest: Dict = {symbol: self._cache[symbol]}
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
