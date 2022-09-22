import falcon

from src.cache import CryptoStore
from src.cache import StockStore
from src.routes import AllAssetRoute, Ping, AssetRoute


class AssetCacheAPI:
    def __init__(self) -> None:
        self._app: falcon.App = falcon.App()
        self.crypto_store = None
        self.stock_store = None
        self.crypto_route = None
        self.stock_route = None
        self.ping = None
        self.all_asset_route = None

        self.create_fields()
        self.add_routes()
        self.start_store_threads()

    @property
    def app(self) -> falcon.App:
        return self._app

    def start_store_threads(self) -> None:
        self.crypto_store.start()
        self.stock_store.start()

    def create_fields(self) -> None:
        self.crypto_store: CryptoStore = CryptoStore()
        self.crypto_store.add_asset("BTCUSD")
        self.stock_store: StockStore = StockStore()
        self.stock_store.add_asset("AAPL")
        self.all_asset_route: AllAssetRoute = AllAssetRoute(
            (self.crypto_store, self.stock_store)
        )
        self.stock_route: AssetRoute = AssetRoute(self.stock_store)
        self.crypto_route: AssetRoute = AssetRoute(self.crypto_store)
        self.ping: Ping = Ping()

    def add_routes(self) -> None:
        self._app.add_route("/ping", self.ping)
        self._app.add_route("/api/v1/asset", self.all_asset_route)
        self._app.add_route("/api/v1/stock", self.stock_route)
        self._app.add_route("/api/v1/stock/{name}", self.stock_route,
                            suffix="asset")
        self._app.add_route("/api/v1/crypto", self.crypto_route)
        self._app.add_route("/api/v1/crypto/{name}", self.crypto_route,
                            suffix="asset")


def run() -> falcon.App:
    cache: AssetCacheAPI = AssetCacheAPI()
    return cache.app
