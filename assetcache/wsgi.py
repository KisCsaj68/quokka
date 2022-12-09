from src import AssetCacheAPI, StreamClient
from src.cache import CryptoCache, StockCache
from src.data_handlers import DataCollectors
from src.storages import PrimitiveJsonDB
from env import conf

app_config: PrimitiveJsonDB = PrimitiveJsonDB()

data_collectors: DataCollectors = DataCollectors(app_config, conf)
asset_cache_api: AssetCacheAPI = AssetCacheAPI(app_config, data_collectors)

# TODO: move these if needed, they are only here for demonstration purposes
# vvv
crypto_cache = CryptoCache(app_config['assets']['crypto'], conf)
stock_cache = StockCache(app_config['assets']['stock'], conf)

stream_client: StreamClient = StreamClient(stocks=app_config['assets']['stock'],
                                           on_stock_trade=stock_cache.on_trade,
                                           cryptos=app_config['assets']['crypto'],
                                           on_crypto_trade=crypto_cache.on_trade,
                                           config=conf)
# ^^^
application = asset_cache_api.app
