from typing import Dict, Any

from src.utils import ParseRawStreamToReadableDict
from alpaca_trade_api.entity_v2 import trade_mapping_v2


@ParseRawStreamToReadableDict(trade_mapping_v2)
def rename_keys(raw_trade: Dict[str, Any]) -> Dict[str, Any]:
    """
    This method changes the format of the incoming JSON data
    with the decorator.
    :param raw_trade:
    :return: raw_trade
    """
    return raw_trade
