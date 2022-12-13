import json
from typing import Tuple, Dict, List
from sortedcontainers import SortedList

from src.data_handlers.price_tracker import PriceTracker
from src.data_handlers.rabbit_mq import Producer
from src.utils.rw_lock import ReadWriteLock
from src.utils.convert import rename_keys
from src.storages import DotEnvConfig


class PriceTrackerManager:
    def __init__(self, conf: DotEnvConfig, producer: Producer):
        self._producer = producer
        self.crypto_container = Container(self._producer)
        self.stock_container = Container(self._producer)


    def on_message_from_rabbit(self, ch, method, properties, body) -> None:
        self.stock_container.on_message_from_rabbit(body)
        self.crypto_container.on_message_from_rabbit(body)


class Container:
    BUY_INDEX = 0
    SELL_INDEX = 1

    def __init__(self, producer: Producer):
        self._producer = producer
        self._rw_lock = ReadWriteLock()
        self._container: Dict[str, Tuple[SortedList[PriceTracker], SortedList[PriceTracker]]] = {}

    def on_message_from_rabbit(self, body) -> None:
        # TODO: add FIFO buffer for non-blocking behaviour
        order: Dict = json.loads(body)
        symbol = order['symbol']
        price_tracker = PriceTracker(symbol, order['limit'], order['id'], order['accountId'])
        print(price_tracker)
        try:
            self._rw_lock.acquire_write()
            if not self._container.get(symbol):
                # Tuple:
                # 0. index for buy side
                # 1. index for sell side
                self._container[symbol] = (SortedList(), SortedList())
            symbol_sorted_list = None
            match order['orderSide']:
                case 'BUY':
                    symbol_sorted_list = self._container.get(symbol)[self.BUY_INDEX]
                case 'SELL':
                    symbol_sorted_list = self._container.get(symbol)[self.SELL_INDEX]
                case _:
                    print(order['orderSide'])
                    raise ValueError()
            symbol_sorted_list.add(price_tracker)
            print(symbol_sorted_list, flush=True)
        finally:
            self._rw_lock.release_write()

    async def on_message_from_stream_client(self, trade):
        # TODO: add FIFO buffer for non-blocking behaviour
        trade = rename_keys(trade)
        symbol = trade['symbol']
        trade_price = trade['price']
        filled_pts = []
        fake_pt = PriceTracker.of(trade_price)
        try:
            self._rw_lock.acquire_write()
            if not self._container.get(symbol):
                return
            buy_sell_tuple = self._container.get(symbol)
            filled_pts += self._handle_buy_side(buy_sell_tuple[self.BUY_INDEX], fake_pt, trade_price)
            filled_pts += self._handle_sell_side(buy_sell_tuple[self.SELL_INDEX], fake_pt, trade_price)
        finally:
            self._rw_lock.release_write()
        self._send_to_rabbit_mq(filled_pts)

    def _handle_buy_side(self, buy_side_list: SortedList, fake_pt: PriceTracker, trade_price: float):
        # use bisect_left to get the index
        # fullfil price_trackers between index and end of list
        filled_price_trackers = []
        start_index = buy_side_list.bisect_left(fake_pt)
        pop_sequence = len(buy_side_list) - start_index
        for _ in range(pop_sequence):
            # Popping price_trackers from the end (constant time access)
            popped = buy_side_list.pop(-1)
            popped.filled_price = trade_price
            filled_price_trackers.append(popped)
        return filled_price_trackers

    def _handle_sell_side(self, sell_side_list: SortedList, fake_pt: PriceTracker, trade_price: float):
        # use bisect_right to get the index
        # fullfil price_trackers between 0 and above index
        filled_price_trackers = []
        index_till = sell_side_list.bisect_right(fake_pt)
        for _ in range(index_till):
            # Popping price_trackers from the beginning (constant time access)
            popped = sell_side_list.pop(0)
            popped.filled_price = trade_price
            filled_price_trackers.append(popped)
        return filled_price_trackers

    def _send_to_rabbit_mq(self, filled_orders: List[PriceTracker]):
        for filled_order in filled_orders:
            self._producer.produce(filled_order)
