import json
from time import time
from typing import Tuple, Dict, List

from falcon import Request
from falcon import Response

from src.cache import BaseAssetStore, BaseAssetCache
from src.cache.assets import BarDictType


def parse_dict_to_json_bytes(dictionary: dict) -> Tuple[bytes, int]:
    byte_json: bytes = bytes(json.dumps(dictionary, ensure_ascii=True),
                             encoding="utf-8"
                             )
    return byte_json, len(byte_json)


class AllAssetRoute:
    DEFAULT_CONTENT_TYPE: str = "application/json"
    UPDATE_INTERVAL: int = 600
    __slots__ = ("stores", "assets", "update_time")

    def __init__(self, stores: Tuple[BaseAssetStore, ...]) -> None:
        self.stores: Tuple[BaseAssetStore] = stores
        self.assets: Tuple[bytes, int] = (b'', 0)
        self.update_time: int = int(time()) + self.UPDATE_INTERVAL
        self.__update_assets_list()

    def on_get(self, req: Request, resp: Response) -> None:
        if self.update_time < time():
            self.update_time: int = int(time()) + self.UPDATE_INTERVAL
            self.__update_assets_list()

        resp.data, resp.content_length = self.assets
        resp.content_type = self.DEFAULT_CONTENT_TYPE

    def __update_assets_list(self) -> None:
        combined_asset_list: List[str] = []

        for store in self.stores:
            combined_asset_list.extend(store.get_all_asset_name())

        assets: Dict[str, List[str]] = {"all_assets": combined_asset_list}
        self.assets = parse_dict_to_json_bytes(assets)


class AssetRoute:
    DEFAULT_CONTENT_TYPE: str = "application/json"
    __slots__ = "store"

    def __init__(self, store: BaseAssetStore) -> None:
        self.store: BaseAssetStore = store

    def on_get(self, req: Request, resp: Response) -> None:
        assets: Dict[str, List[str]] = {
            self.store.name: self.store.get_all_asset_name()
        }
        resp.data, resp.content_length = parse_dict_to_json_bytes(assets)
        resp.content_type = self.DEFAULT_CONTENT_TYPE

    def on_get_asset(self, req: Request, resp: Response, name: str) -> None:
        if 10 < len(name):
            return
        name: str = name.upper()
        asset_cache: BaseAssetCache = self.store[name]
        if asset_cache is None:
            return
        asset: BarDictType = asset_cache.last_bar
        resp.data, resp.content_length = parse_dict_to_json_bytes(asset)
        resp.content_type = self.DEFAULT_CONTENT_TYPE


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
