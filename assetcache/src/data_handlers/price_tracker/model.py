from uuid import UUID


class PriceTracker:
    def __init__(self, symbol: str, limit_price: float, order_id: UUID, account_id: UUID):
        """
        This model contains information about the limit sell/buy orders from OMS.
        These trackers are kept in / managed by the price_tracker_manager.
        :param symbol:
        :param limit_price:
        :param order_id:
        :param account_id:
        """
        self.account_id = account_id
        self.order_id = order_id
        self.limit_price = limit_price
        self.symbol = symbol
        self.filled_price: float = None

    def __lt__(self, other):
        return self.limit_price < other.limit_price


