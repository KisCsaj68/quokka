from prometheus_client import Histogram, Counter, Gauge

# Stream metrics
STREAM_TRADE_TOTAL = Counter(namespace='quokka', subsystem='assetcache', name='stream_trade_total',
                             labelnames=['asset_type'],
                             documentation='total numbers of incoming trades from websocket')
# trade_counter2 = Counter(namespace='quokka', subsystem='assetcache', name='', labelnames='', documentation='')
# Cache metrics
CACHE_UPDATE_PRICE = Histogram(namespace='quokka', subsystem='assetcache', name='cache_update_price',
                               labelnames=['asset_type'],
                               documentation='total elapsed time from incoming trade to store in-memory')
CACHE_READ_PRICE = Histogram(namespace='quokka', subsystem='assetcache', name='cache_read_price',
                             labelnames=['asset_type'],
                             documentation='total elapsed time of cache read')
CACHE_INIT_PRICE = Histogram(namespace='quokka', subsystem='assetcache', name='cache_init_price',
                             labelnames=['asset_type'],
                             documentation='total elapsed time to init the cache')
# Manager metrics
MANAGER_PRICE_TRACKER_TOTAL = Counter(namespace='quokka', subsystem='assetcache', name='manager_price_tracker_total',
                                      labelnames=['asset_type'],
                                      documentation='total number of price trackers received from rabbitMQ')
MANAGER_STORE_PRICE_TRACKER = Histogram(namespace='quokka', subsystem='assetcache', name='manager_store_price_tracker',
                                        labelnames=['asset_type'],
                                        documentation='total elapsed time from rabbitMQ to store price tracker')
MANAGER_MATCH_PRICE_TRACKER = Histogram(namespace='quokka', subsystem='assetcache',
                                        name='manager_handle_price_tracker',
                                        labelnames=['step', 'asset_type'],
                                        documentation='total elapsed time from datapoint to send response to OMS')
MANAGER_STORED_PRICE_TRACKER = Gauge(namespace='quokka', subsystem='assetcache',
                                     name='manager_stored_price_tracker',
                                     labelnames=['asset_type'],
                                     documentation='Measures the number of price trackers stored in-memory')
