from os import environ, getcwd
from os.path import isfile, join
from typing import Dict, Union, Tuple

from dotenv import load_dotenv


class DotEnvConfig:
    def __init__(self, env_vars: Tuple[str], env_path: str = None) -> None:
        if env_path is None:
            env_path: str = join(getcwd(), '.env')
        if not isfile(env_path):
            raise FileNotFoundError()
        load_dotenv(env_path)
        self._env_cache: Dict[str, str] = dict()
        self.__load_env_vars(env_vars)

    def __load_env_vars(self, env_vars: Tuple[str]) -> None:
        for item in env_vars:
            self._env_cache[item] = environ[item]

    def __getitem__(self, item: str) -> Union:
        return self._env_cache.get(item, None)

    def __repr__(self) -> str:
        cache_content: str = ', '.join([f'{key}={val}' for key, val
                                        in self._env_cache.items()])
        return f"{self.__class__.__name__}({cache_content})"

    def __str__(self) -> str:
        cache_keys: str = ', '.join(self._env_cache.keys())
        return f"{self.__class__.__name__}({cache_keys})"
