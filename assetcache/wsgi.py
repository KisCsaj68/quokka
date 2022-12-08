from typing import Tuple

from src import AssetCacheAPI, StreamClient
from src.data_handlers import DataCollectors
from src.storages import DotEnvConfig
from src.storages import PrimitiveJsonDB

ENV_VARS: Tuple[str, ...] = ("APCA_API_KEY_ID", "APCA_API_SECRET_KEY",
                             "APCA_API_BASE_URL", "APCA_API_VERSION")
conf: DotEnvConfig = DotEnvConfig(ENV_VARS)
dummy_db: PrimitiveJsonDB = PrimitiveJsonDB()

data_collectors: DataCollectors = DataCollectors(dummy_db, conf)
asset_cache_api: AssetCacheAPI = AssetCacheAPI(dummy_db, data_collectors)
stream_client: StreamClient = StreamClient(dummy_db, conf)
application = asset_cache_api.app
