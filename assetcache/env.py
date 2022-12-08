from typing import Tuple

from src.storages import DotEnvConfig

ENV_VARS: Tuple[str, ...] = ("APCA_API_KEY_ID", "APCA_API_SECRET_KEY",
                             "APCA_API_BASE_URL", "APCA_API_VERSION",
                             "RABBITMQ_URL")

conf: DotEnvConfig = DotEnvConfig(ENV_VARS)
