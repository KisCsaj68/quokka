import falcon

from src.data_handlers.data_collectors import DataCollectors
from src.storages.primitive_json_db import PrimitiveJsonDB
from src.routes import Ping, AssetNamesRoute, LatestAssetRoute


class AssetCacheAPI:

    def __init__(self, db: PrimitiveJsonDB, collectors: DataCollectors) -> None:
        self._app: falcon.App = falcon.App(cors_enable=True)
        self.collectors: DataCollectors = collectors

        self.ping: Ping = Ping()
        self.asset_names: AssetNamesRoute = AssetNamesRoute(db)
        self.latest_stock: LatestAssetRoute = LatestAssetRoute(
            self.collectors.latest_stock_ticker
        )
        self.latest_crypto: LatestAssetRoute = LatestAssetRoute(
            self.collectors.latest_crypto_ticker
        )

        self.add_routes()


    @property
    def app(self) -> falcon.App:
        return self._app

    def add_routes(self) -> None:
        self._app.add_route("/ping", self.ping)
        self._app.add_route("/api/v1/assets", self.asset_names)

        self._app.add_route("/api/v1/stock", self.asset_names, suffix="stock")
        self._app.add_route("/api/v1/stock/{symbol}", self.latest_stock)
        self._app.add_route("/api/v1/stock/{symbol}/bars", self.latest_stock,
                            suffix="bars")
        self._app.add_route("/api/v1/stock/{symbol}/trades", self.latest_stock,
                            suffix="trades")
        self._app.add_route("/api/v1/stock/{symbol}/quotes", self.latest_stock,
                            suffix="quotes")

        self._app.add_route("/api/v1/crypto", self.asset_names,
                            suffix="crypto")
        self._app.add_route("/api/v1/crypto/{symbol}", self.latest_crypto)
        self._app.add_route("/api/v1/crypto/{symbol}/bars", self.latest_crypto,
                            suffix="bars")
        self._app.add_route("/api/v1/crypto/{symbol}/trades",
                            self.latest_crypto, suffix="trades")
        self._app.add_route("/api/v1/crypto/{symbol}/quotes",
                            self.latest_crypto, suffix="quotes")

