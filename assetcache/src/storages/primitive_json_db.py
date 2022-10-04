import json
from os import getcwd
from os.path import isfile, join
from threading import RLock
from typing import Dict, Any, Union


class PrimitiveJsonDB:
    __slots__ = ("_src", "_db", "_lock")

    def __init__(self, src: str = None) -> None:
        if src is None:
            src: str = join(getcwd(), f"{self.__class__.__name__}.json")
        self._src = src
        self._lock: RLock = RLock()

        if not isfile(src):
            self.__create_base_db()
        else:
            self.__read_existing_db()

    def __create_base_db(self) -> None:
        with open(self._src, 'a'):
            pass
        self._db: Dict[str, Any] = {}
        self["assets"] = {"crypto": None, "stock": None}

    def __read_existing_db(self) -> None:
        with open(self._src, "r") as json_file:
            try:
                self._db: Dict[str, Any] = json.load(json_file)
            except ValueError:
                raise ValueError(
                    f"{self.__class__.__name__}: Cannot load json file at: "
                    f"{self._src}"
                )

    def __save_db(self) -> None:
        json_data: str = json.dumps(self._db, ensure_ascii=True, indent=4,
                                    sort_keys=True)
        with open(self._src, "w+") as json_file:
            json_file.write(json_data)

    def __getitem__(self, item: str) -> Any:
        with self._lock:
            return self._db.get(item, None)

    def __setitem__(self, key: str, value: Any) -> None:
        with self._lock:
            self._db[key] = value
        self.__save_db()

    def __delitem__(self, key: str) -> Union[Any, None]:
        with self._lock:
            if key in self._db:
                return self._db.pop(key)
        return None

    def drop_db(self) -> None:
        with open(self._src, "w+") as f:
            f.truncate(0)
