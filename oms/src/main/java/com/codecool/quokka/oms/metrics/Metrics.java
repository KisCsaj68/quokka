package com.codecool.quokka.oms.metrics;

import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;

public interface Metrics {
    Counter ORDER_REQUEST = Counter.build().namespace("quokka").subsystem("oms")
            .name("order_request")
            .labelNames("order_type")
            .help("total number of order placement").register();

    Histogram LIMIT_ORDER_REQUEST_TIME_DURATION = Histogram.build().namespace("quokka").subsystem("oms")
            .name("limit_order_request_time_duration")
            .labelNames( "phase")
            .help("total elapsed time from request to response").register();

    Histogram MARKET_ORDER_REQUEST_TIME_DURATION = Histogram.build().namespace("quokka").subsystem("oms")
            .name("market_order_request_time_duration")
            .labelNames( "phase")
            .help("total elapsed time from request to response").register();
    Histogram INITIALIZE_MEMORY_TIME_DURATION = Histogram.build().namespace("quokka").subsystem("oms")
            .name("initialize_memory_time_duration")
            .labelNames( "phase")
            .help("total elapsed time from request to response").register();
}
