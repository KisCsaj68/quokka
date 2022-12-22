from prometheus_client import Histogram, Counter, Gauge

buckets = [0.000001, 0.000003, 0.000005, 0.000007, 0.00001, 0.00002, 0.00003, 0.00004,
           0.00005, 0.0001, 0.0003, 0.0005, 0.001]
api_buckets = [0.000009, 0.00001, 0.00002, 0.00003, 0.00004,
               0.00005, 0.00007, 0.00009, 0.0001, 0.0003, 0.0005, 0.0007, 0.0009, 0.001, 0.003, 0.005]

# Stream metrics
STREAM_TRADE_TOTAL = Counter(namespace='quokka', subsystem='assetcache', name='stream_trade_total',
                             labelnames=['asset_type'],
                             documentation='total numbers of incoming trades from websocket')
# trade_counter2 = Counter(namespace='quokka', subsystem='assetcache', name='', labelnames='', documentation='')
# Cache metrics
CACHE_UPDATE_PRICE = Histogram(namespace='quokka', subsystem='assetcache', name='cache_update_price',
                               labelnames=['asset_type'],
                               documentation='total elapsed time from incoming trade to store in-memory',
                               buckets=buckets)
CACHE_READ_PRICE = Histogram(namespace='quokka', subsystem='assetcache', name='cache_read_price',
                             labelnames=['asset_type'],
                             documentation='total elapsed time of cache read',
                             buckets=buckets)
CACHE_INIT_PRICE = Histogram(namespace='quokka', subsystem='assetcache', name='cache_init_price',
                             labelnames=['asset_type'],
                             documentation='total elapsed time to init the cache',
                             buckets=buckets)
# Manager metrics
MANAGER_PRICE_TRACKER_TOTAL = Counter(namespace='quokka', subsystem='assetcache', name='manager_price_tracker_total',
                                      labelnames=['asset_type'],
                                      documentation='total number of price trackers received from rabbitMQ')
MANAGER_STORE_PRICE_TRACKER = Histogram(namespace='quokka', subsystem='assetcache', name='manager_store_price_tracker',
                                        labelnames=['asset_type'],
                                        documentation='total elapsed time from rabbitMQ to store price tracker',
                                        buckets=buckets)
MANAGER_MATCH_PRICE_TRACKER = Histogram(namespace='quokka', subsystem='assetcache',
                                        name='manager_match_price_tracker',
                                        labelnames=['step', 'asset_type'],
                                        documentation='total elapsed time from datapoint to send response to OMS',
                                        buckets=buckets)
MANAGER_STORED_PRICE_TRACKER = Gauge(namespace='quokka', subsystem='assetcache',
                                     name='manager_stored_price_tracker',
                                     labelnames=['asset_type'],
                                     documentation='Measures the number of price trackers stored in-memory',
                                     )
# API metrics
API_REQUEST_TOTAL = Counter(namespace='quokka', subsystem='assetcache', name='api_request_total',
                            labelnames=['path', 'http_method'],
                            documentation='total number of received API requests')
API_RESPONSE = Histogram(namespace='quokka', subsystem='assetcache', name='api_response_duration',
                         labelnames=['path', 'http_method', 'asset_type'],
                         documentation='total elapsed time from request to response',
                         buckets=api_buckets)
