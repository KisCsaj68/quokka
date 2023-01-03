package com.codecool.quokka.oms.service;

import com.codecool.quokka.model.assets.Asset;
import com.codecool.quokka.model.mqconfig.Config;
import com.codecool.quokka.model.order.OrderType;
import com.codecool.quokka.model.order.Orders;
import com.codecool.quokka.model.order.OrderStatus;
import com.codecool.quokka.model.position.Position;
import com.codecool.quokka.oms.dal.OrderDal;
import com.codecool.quokka.oms.dal.PositionDal;
import com.codecool.quokka.oms.metrics.Metrics;
import com.codecool.quokka.oms.model.FilledOrder;
import com.google.common.collect.Maps;
import io.prometheus.client.Histogram;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@ConfigurationProperties
public class OrderService {

    private static final int BATCH_SIZE = 1000;
    private RabbitTemplate rabbitTemplate;
    private RestTemplate restTemplate;

    // Store open order by userId -> {orderId: order}
    private Map<UUID, Map<UUID, Orders>> inMemoryOrders;

    private OrderDal orderDal;

    private PositionDal positionDal;

    // Store position by userId -> {symbol: {positionId: position}}
    private Map<UUID, Map<String, Map<UUID, Position>>> inMemoryPositions;

    @Value("${quokka.service.assetcache.address}${quokka.service.assetcache.endpoint}")
    private String assetCacheURL;

    @Autowired
    public OrderService(RabbitTemplate template, OrderDal orderDal, PositionDal positionDal) {
        this.rabbitTemplate = template;
        this.inMemoryOrders = new ConcurrentHashMap<>();
        this.inMemoryPositions = new ConcurrentHashMap<>();
        this.restTemplate = new RestTemplate();
        this.orderDal = orderDal;
        this.positionDal = positionDal;
    }

    @PostConstruct
    public void initializeInMemoryStores() {
        // get open orders from db
        List<Orders> orders = new ArrayList<>();
        Slice<Orders> ordersSlice = orderDal.findAllByStatus(OrderStatus.OPEN, PageRequest.of(0, BATCH_SIZE));
        orders.addAll(ordersSlice.getContent());

        while (ordersSlice.hasNext()) {
            System.out.println("orders"+ " " +orders.size());
            ordersSlice = orderDal.findAllByStatus(OrderStatus.OPEN, ordersSlice.nextPageable());
            orders.addAll(ordersSlice.getContent());
        }
        orders.stream().forEach(o -> storeLimitOrder(o, Metrics.INITIALIZE_MEMORY_TIME_DURATION));
























        // get positions from db
        Slice<Position> positionSlice = positionDal.findAllByExitOrderIdIsNull(PageRequest.of(0, BATCH_SIZE));
        List<Position> positions = new ArrayList<>();
        Set<UUID> orderIds = new HashSet<>();
        positions.addAll(positionSlice.getContent());
        orderIds.addAll(positionSlice.getContent().stream().map(Position::getEntryOrderId).collect(Collectors.toSet()));
        while (positionSlice.hasNext()) {
            System.out.println("Positions" + " " + positions.size());
            positionSlice = positionDal.findAllByExitOrderIdIsNull(positionSlice.nextPageable());
            positions.addAll(positionSlice.getContent());
            orderIds.addAll(positionSlice.getContent().stream().map(Position::getEntryOrderId).collect(Collectors.toSet()));
        }

        positions.stream().forEach(p -> storeInMemoryPositions(p, Metrics.INITIALIZE_MEMORY_TIME_DURATION));
    }

    public ResponseEntity createOrder(Orders order, Histogram histogram) {
        Metrics.ORDER_REQUEST.labels(order.getType().toString().toLowerCase()).inc();
        order.setStatus(OrderStatus.OPEN);
        // Send open order to persister via RMQ

        try (Histogram.Timer ignored = histogram.labels("send_order_to_queue").startTimer()){
            rabbitTemplate.convertAndSend(Config.EXCHANGE, Config.ORDER_ROUTING_KEY, order);
        }
        switch (order.getType()) {
            case LIMIT -> handleLimitOrder(order);
            case MARKET -> handleMarketOrder(order);
        }
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    private void handleLimitOrder(Orders order) {
        storeLimitOrder(order, Metrics.LIMIT_ORDER_REQUEST_TIME_DURATION);
        try (Histogram.Timer ignored = Metrics.LIMIT_ORDER_REQUEST_TIME_DURATION.labels("send_order_to_queue").startTimer()){
            rabbitTemplate.convertAndSend(Config.EXCHANGE, Config.LIMIT_ORDER_ROUTING_KEY, order);
        }
    }

    private void handleMarketOrder(Orders order) {
        Asset asset = null;

        try (Histogram.Timer ignored = Metrics.MARKET_ORDER_REQUEST_TIME_DURATION.labels("update_order").startTimer()){
            // Ask the actual price from assetcache port 8000.
            asset = restTemplate.getForObject(assetCacheURL + order.getAssetType().toString().toLowerCase() + "/" + order.getSymbol(), Asset.class);
            // Fill the price to the order and update the order in DB.
        }
        order.setPrice(asset.getPrice());
        order.setStatus(OrderStatus.FILLED);

        try (Histogram.Timer ignored = Metrics.MARKET_ORDER_REQUEST_TIME_DURATION.labels("send_updated_order_to_queue").startTimer()){
            rabbitTemplate.convertAndSend(Config.EXCHANGE, Config.ORDER_ROUTING_KEY, order);
        }
        // Create position and persist db + in-memory
        switch (order.getOrderSide()) {
            case BUY -> handleBuy(order, Metrics.MARKET_ORDER_REQUEST_TIME_DURATION);
            case SELL -> handleSell(order, Metrics.MARKET_ORDER_REQUEST_TIME_DURATION);
        }
    }

    /**
     * Push the Position to RabbitMQ first(for consistency) and stores it in-memory.
     */
    private void persistPosition(Position position, Histogram histogram) {

        try (Histogram.Timer ignored = histogram.labels("send_position_to_queue").startTimer()){
            rabbitTemplate.convertAndSend(Config.EXCHANGE, Config.POSITION_ROUTING_KEY, position);
        }
        storeInMemoryPositions(position, histogram);
    }

    private void storeInMemoryPositions(Position position, Histogram histogram) {
        UUID accountId = position.getUserId();
        String symbol = position.getSymbol();
        UUID positionId = position.getId();
        try (Histogram.Timer timer = histogram.labels("persist_position_in_memory").startTimer()){
            if (!inMemoryPositions.containsKey(accountId)) {
                inMemoryPositions.put(accountId, Maps.newConcurrentMap());
            }
            if (!inMemoryPositions.get(accountId).containsKey(symbol)) {
                inMemoryPositions.get(accountId).put(symbol, Maps.newConcurrentMap());
            }
            inMemoryPositions.get(accountId).get(symbol).put(positionId, position);
        }
    }

    private void storeLimitOrder(Orders order, Histogram histogram) {

        try (Histogram.Timer timer = histogram.labels("persist_limit_order_in_memory").startTimer()){
            UUID accountId = order.getAccountId();
            if (!inMemoryOrders.containsKey(accountId)) {
                inMemoryOrders.put(accountId, Maps.newConcurrentMap());
            }
            inMemoryOrders.get(accountId).put(order.getId(), order);
        }
    }

    @RabbitListener(queues = Config.FILLED_ORDER_QUEUE)
    public void fillLimitOrder(FilledOrder filledOrder) {
        Orders order = null;
        try (Histogram.Timer ignored = Metrics.LIMIT_ORDER_REQUEST_TIME_DURATION.labels("get_order_from_in_memory").startTimer()) {
            order = inMemoryOrders.get(filledOrder.getAccountId()).get(filledOrder.getOrderId());
        }
        if (order == null) {
            // TODO: add logger
            System.out.println("order not found in the in memory store. Order: " + filledOrder + "; in-memory-store: " + inMemoryOrders);
            return;
        }
        order.setStatus(OrderStatus.FILLED);
        order.setPrice(BigDecimal.valueOf(filledOrder.getFilledPrice()));
//        try (Histogram.Timer ignored = Metrics.LIMIT_ORDER_REQUEST_TIME_DURATION.labels("update_order").startTimer()) {
//        }
        try (Histogram.Timer ignored = Metrics.LIMIT_ORDER_REQUEST_TIME_DURATION.labels("send_updated_order_to_queue").startTimer()) {
            rabbitTemplate.convertAndSend(Config.EXCHANGE, Config.ORDER_ROUTING_KEY, order);
        }
        try (Histogram.Timer ignored = Metrics.LIMIT_ORDER_REQUEST_TIME_DURATION.labels("store_updated_order_in_memory").startTimer()) {
            inMemoryOrders.get(filledOrder.getAccountId()).remove(order.getId());
        }
        switch (order.getOrderSide()) {
            case BUY -> handleBuy(order, Metrics.LIMIT_ORDER_REQUEST_TIME_DURATION);
            case SELL -> handleSell(order, Metrics.LIMIT_ORDER_REQUEST_TIME_DURATION);
        }
    }

    private void handleSell(Orders order, Histogram histogram) {
        Position position = null;
        try (Histogram.Timer ignored = histogram.labels("get_position_from_memory").startTimer()) {
            position = inMemoryPositions.get(order.getAccountId()).get(order.getSymbol()).remove(order.getSellPositionId());
        }
        position.setExitOrderId(order.getId());
        position.setPriceAtSell(order.getPrice());
        position.setSellAt(new Date());
//        try (Histogram.Timer ignored = histogram.labels("update_position").startTimer()) {
//        }
        try (Histogram.Timer ignored = histogram.labels("send_position_to_queue").startTimer()) {
            rabbitTemplate.convertAndSend(Config.EXCHANGE, Config.POSITION_ROUTING_KEY, position);
        }
    }

    private void handleBuy(Orders order, Histogram histogram) {
        Position position = new Position(order.getQuantity(), order.getAccountId(), order.getSymbol(), order.getPrice(), null, new Date(), order.getId(), null);
        persistPosition(position, histogram);
    }

    public void pushOrders() {
        for (UUID accountId : inMemoryOrders.keySet()) {
            for (Orders order : inMemoryOrders.get(accountId).values()) {
                if (order.getType() == OrderType.LIMIT && order.getStatus() == OrderStatus.OPEN) {
                    rabbitTemplate.convertAndSend(Config.EXCHANGE, Config.LIMIT_ORDER_ROUTING_KEY, order);
                }
            }
        }
    }
}
