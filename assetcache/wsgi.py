from os.path import join, dirname
from dotenv import load_dotenv
from src import AssetCacheAPI

dotenv_path: str = join(dirname(__file__), '.env')
load_dotenv(dotenv_path)

asset_cache_api: AssetCacheAPI = AssetCacheAPI()
application = asset_cache_api.app