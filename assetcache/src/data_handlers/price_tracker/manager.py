import json
from typing import Tuple, Dict, List
from sortedcontainers import SortedList

from src.data_handlers.price_tracker import PriceTracker
from src.data_handlers.rabbit_mq import Producer
from src.utils.rw_lock import ReadWriteLock
from src.utils.convert import rename_keys
from src.storages import DotEnvConfig
from src.metrics import MANAGER_PRICE_TRACKER_TOTAL, MANAGER_STORE_PRICE_TRACKER, MANAGER_MATCH_PRICE_TRACKER, \
    MANAGER_STORED_PRICE_TRACKER


class PriceTrackerManager:
    def __init__(self, conf: DotEnvConfig, producer: Producer):
        self.producer = producer
        self.crypto_container = Container(producer,
                                          'crypto')
        self.stock_container = Container(producer, 'stock')

    def on_message_from_rabbit(self, ch, method, properties, body) -> None:
        order: Dict = json.loads(body)
        MANAGER_PRICE_TRACKER_TOTAL.labels(order['asset_type'].lower()).inc()
        if order['asset_type'] == 'CRYPTO':
            self.crypto_container.on_message_from_rabbit(order)
        else:
            self.stock_container.on_message_from_rabbit(order)

    def start_producer_threads(self):
        self.stock_container.start_producer_thread()
        self.crypto_container.start_producer_thread()


class Container:
    BUY_INDEX = 0
    SELL_INDEX = 1

    def __init__(self, producer: Producer, asset_type: str):
        self._producer = producer
        self._rw_lock = ReadWriteLock()
        self._container: Dict[str, Tuple[SortedList[PriceTracker], SortedList[PriceTracker]]] = {}
        self._asset_type = asset_type

    # def start_producer_thread(self):
    #     self._producer.start()

    def on_message_from_rabbit(self, order) -> None:
        # TODO: add FIFO buffer for non-blocking behaviour
        symbol = order['symbol']
        price_tracker = PriceTracker(symbol, order['limit'], order['id'], order['account'],
                                     sell_position_id=order['sell_position_id'])
        with MANAGER_STORE_PRICE_TRACKER.labels(order['asset_type'].lower()).time():
            try:
                self._rw_lock.acquire_write()
                if not self._container.get(symbol):
                    # Tuple:
                    # 0. index for buy side
                    # 1. index for sell side
                    self._container[symbol] = (SortedList(), SortedList())
                symbol_sorted_list = None
                match order['side']:
                    case 'BUY':
                        symbol_sorted_list = self._container.get(symbol)[self.BUY_INDEX]
                    case 'SELL':
                        symbol_sorted_list = self._container.get(symbol)[self.SELL_INDEX]
                    case _:
                        print(order['side'])
                        raise ValueError()
                symbol_sorted_list.add(price_tracker)
                MANAGER_STORED_PRICE_TRACKER.labels(self._asset_type).inc()
            finally:
                self._rw_lock.release_write()

    async def on_message_from_stream_client(self, trade):
        # TODO: add FIFO buffer for non-blocking behaviour
        trade = rename_keys(trade)
        symbol = trade['symbol']
        trade_price = trade['price']
        filled_pts = []
        # Fake PriceTracker need to be created so that sorted containers can check the index it would land on.
        fake_pt = PriceTracker.of(trade_price)
        with MANAGER_MATCH_PRICE_TRACKER.labels('whole_match_process', self._asset_type).time():
            try:
                self._rw_lock.acquire_write()
                if not self._container.get(symbol):
                    return
                buy_sell_tuple = self._container.get(symbol)
                filled_pts += self._handle_buy_side(buy_sell_tuple[self.BUY_INDEX], fake_pt, trade_price)
                filled_pts += self._handle_sell_side(buy_sell_tuple[self.SELL_INDEX], fake_pt, trade_price)
            finally:
                self._rw_lock.release_write()
            if filled_pts:
                self._send_to_rabbit_mq(filled_pts)

    def _handle_buy_side(self, buy_side_list: SortedList, fake_pt: PriceTracker, trade_price: float):
        with MANAGER_MATCH_PRICE_TRACKER.labels('match_buy_side', self._asset_type).time():
            # use bisect_left to get the index
            # fulfill price_trackers between index and end of list
            filled_price_trackers = []
            start_index = buy_side_list.bisect_left(fake_pt)
            pop_sequence = len(buy_side_list) - start_index
            for _ in range(pop_sequence):
                # Popping price_trackers from the end (O(log(n) time access)
                popped = buy_side_list.pop(-1)
                popped.filled_price = trade_price
                filled_price_trackers.append(popped)
            return filled_price_trackers

    def _handle_sell_side(self, sell_side_list: SortedList, fake_pt: PriceTracker, trade_price: float):
        with MANAGER_MATCH_PRICE_TRACKER.labels('match_sell_side', self._asset_type).time():
            # use bisect_right to get the index
            # fulfill price_trackers between 0 and above index
            filled_price_trackers = []
            index_till = sell_side_list.bisect_right(fake_pt)
            for _ in range(index_till):
                # Popping price_trackers from the beginning (constant time access)
                popped = sell_side_list.pop(0)
                popped.filled_price = trade_price
                filled_price_trackers.append(popped)
            return filled_price_trackers

    def _send_to_rabbit_mq(self, filled_orders: List[PriceTracker]):
        try:
            for filled_order in filled_orders:
                with MANAGER_MATCH_PRICE_TRACKER.labels('push_to_rabbit', self._asset_type).time():
                    self._producer.produce(filled_order)
        finally:
            MANAGER_STORED_PRICE_TRACKER.labels(self._asset_type).dec(len(filled_orders))
