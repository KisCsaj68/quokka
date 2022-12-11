from src import AssetCacheAPI

from src.data_handlers import DataCollectors
from src.storages import PrimitiveJsonDB
from env import conf

app_config: PrimitiveJsonDB = PrimitiveJsonDB()

data_collectors: DataCollectors = DataCollectors(app_config, conf)
asset_cache_api: AssetCacheAPI = AssetCacheAPI(app_config, data_collectors)

application = asset_cache_api.app
