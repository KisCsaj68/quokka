# Asset Cache

## Responsibilities:
- This component is responsible for subscribing to the external data provider's streaming data.
- tracks individual limit orders which are waiting for their price to be met. When limit-price is met, it produces a message onto the order-price-met
- Provides api for other components
  - `GET  /api/v1/asset` returns the list of assets being tracked by assetcache. This list is persisted into Postgres.
  - `POST /api/v1/asset` registers an asset with assetcache if it could successfully subscribe with the external data provider for the given asset. Returns 201 (created) if successful, 404 otherwise.
  - `GET  /api/v1/asset/{ticker}/price` returns the current price for the requested asset and 200 (ok) if successful.
  - **(or should this be instead coming from a rabbit queue?)** `POST /api/v1/price-tracker` registers a price-tracker for a limit order.
  - `DELETE /api/v1/price-tracker` individual price trackers can be deleted from the price tracker, by their order id 


## The anatomy of price tracking

A naive approach for OMS would be to poll  `GET /api/v1/asset/{ticker}/price` endpoint for the latest price, to see if the limit order has met the price. 
This approach is bad, due to the lossy nature of the polling. No-matter-how fast OMS would poll (like evey 50ms), there could be always an interim peak of price between two consecutive polling, which could have fill the order only if we were so lucky to poll at the exact right time...

So instead of the heavy polling of assetcache, we register individual limit orders with assetcache, and let each incoming price datapoint be checked against the registered price checker. 
For this to be possible we need very fast datastructures for these assets price-trackers.
Ideally there should be a tuple of a two special data structure for each asset. 
This special datastructure needs to have such properties:
- should be very fast to search, - ideally O(log n) - since each trade tick for a given asset will need to match against this datastructure
- should be ordered, so that we only would need to bisect for the lower bound of a fill-able price-tracker. Beyond that all price-trackers are satisfiable
- should be fast to slice out the satisfied price-trackers
- should be fast to insert an item while keeping the order


```python
dict[ticker](buy_tree, sell_tree)
```

For example there are clients that have open orders for `AAPL`. `AAPL` is currently at `$150`
Client1 has a `buy` side limit order at `$148`
Client2 has a `buy` side limit order at `$147`
Client3 has a `sell` side limit order at `$151`
Client4 has a `sell` side limit order at `$152`

on the first price-tracker registration for `AAPL` it would be initialized like this:
```python
price_tracker = {}
buy_side_tree = ...
sell_side_tree = ...
price_tracker['aapl'] = (buy_side_tree, sell_side_tree)

# 
buy_side_tree.insert(price_tracker(order_id_for_client1, 148.0))
buy_side_tree.insert(price_tracker(order_id_for_client2, 147.0))
sell_side_tree.insert(price_tracker(order_id_for_client3, 151.0))
sell_side_tree.insert(price_tracker(order_id_for_client4, 152.0))
```

Searching in this ordered structure is O(log n) so our performance should be fine even for large number of orders.

An incoming datapoint for `AAPL` would check both buy and sell trees.
Price-trackers in the buy tree are considered fulfilled if it is equal to or greater than the incoming datapoints price value 
(eg.: if a price of 147.5 comes in for `AAPL` then it fills client1's order but not client2's).
(eg.: if a price of 150.5 comes in for `AAPL` then it does not fill any client orders).
(eg.: if a price of 152.5 comes in for `AAPL` then it fills client3 and client4's).


An incoming price can fill multiple price-trackers of course.
Filled price-trackers before they are deleted from the tree, produce an event onto a RabbitMQ queue (containing the order_id and the actual price that filled the order), which would be read by OMS.
