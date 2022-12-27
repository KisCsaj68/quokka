package com.codecool.quokka.persister.metrics;

import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;

public interface Metrics {

    Counter PERSIST_REQUEST = Counter.build().namespace("quokka").subsystem("persister")
            .name("requests")
            .labelNames("resource")
            .help("total number of persisted items").register();

    Histogram PERSIST_TIME_DURATION = Histogram.build().namespace("quokka").subsystem("persister")
            .name("persist_time_duration")
            .labelNames("resource")
            .help("total elapsed time from request to persist").register();

}
