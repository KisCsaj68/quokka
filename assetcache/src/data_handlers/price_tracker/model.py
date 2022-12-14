from uuid import UUID


class PriceTracker:
    def __init__(self, symbol: str = None, limit_price: float = None, order_id: UUID = None, account_id: UUID = None,
                 sell_position_id: UUID = None):
        """
        This model contains information about the limit sell/buy orders from OMS.
        These trackers are kept in / managed by the price_tracker_manager.
        :param symbol:
        :param limit_price:
        :param order_id:
        :param account_id:
        :param sell_position_id:
        """
        self.symbol = symbol
        self.limit_price = limit_price
        self.order_id = order_id
        self.account_id = account_id
        self.sell_position_id = sell_position_id
        self.filled_price: float = None

    def __lt__(self, other):
        return self.limit_price < other.limit_price

    def __repr__(self):
        return f'Pt(acc: {self.account_id}, o_id: {self.order_id}, lp: {self.limit_price}, s: {self.symbol},' \
               f'fp: {self.filled_price})'

    @staticmethod
    def of(price: float):
        return PriceTracker(limit_price=price)
