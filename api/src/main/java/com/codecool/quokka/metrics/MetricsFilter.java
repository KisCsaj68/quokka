package com.codecool.quokka.metrics;

import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;
import io.prometheus.client.SimpleTimer;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Enumeration;

@Configuration
public class MetricsFilter implements Filter {

    private static final Counter api_request_total = Counter.build().namespace("quokka").subsystem("api")
            .name("request_total")
            .labelNames("path", "http_method", "response_code")
            .help("total number of api requests").register();

    public static final Histogram api_request_time_duration = Histogram.build().namespace("quokka").subsystem("api")
            .name("request_time_duration")
            .labelNames("path", "http_method", "response_code")
            .help("total elapsed time from request to response")
            .register();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        SimpleTimer st = new SimpleTimer();
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            // BEST_MATCHING_PATTERN_ATTRIBUTE return the api URI with variables e.g.: /api/user/{id}, not the concrete URL.
            String pathTemplate = (String) servletRequest.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
            pathTemplate = pathTemplate == null ? request.getRequestURI() : pathTemplate;
            var status = HttpStatus.Series.valueOf(response.getStatus());
            api_request_time_duration.labels(pathTemplate, request.getMethod(), status.name()).observe(st.elapsedSeconds());
            api_request_total.labels(pathTemplate, request.getMethod(), status.name()).inc();
        }
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
