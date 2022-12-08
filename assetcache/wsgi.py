from src import AssetCacheAPI, StreamClient
from src.data_handlers import DataCollectors
from src.storages import PrimitiveJsonDB
from env import conf

dummy_db: PrimitiveJsonDB = PrimitiveJsonDB()

data_collectors: DataCollectors = DataCollectors(dummy_db, conf)
asset_cache_api: AssetCacheAPI = AssetCacheAPI(dummy_db, data_collectors)
stream_client: StreamClient = StreamClient(dummy_db, conf)
application = asset_cache_api.app
