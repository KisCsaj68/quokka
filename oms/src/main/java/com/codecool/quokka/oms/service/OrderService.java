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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
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
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;
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

    private ConnectionFactory factory;
    private Channel channel;
    private ObjectMapper mapper;

    @Value("${quokka.service.assetcache.address}${quokka.service.assetcache.endpoint}")
    private String assetCacheURL;

    @Autowired
    public OrderService(RabbitTemplate template, OrderDal orderDal, PositionDal positionDal, ObjectMapper mapper, ConnectionFactory factory) {
        this.rabbitTemplate = template;
        this.inMemoryOrders = new ConcurrentHashMap<>();
        this.inMemoryPositions = new ConcurrentHashMap<>();
        this.restTemplate = new RestTemplate();
        this.orderDal = orderDal;
        this.positionDal = positionDal;
        this.mapper = mapper;
        this.factory = factory;

    }

    @PostConstruct
    public void initializeInMemoryStores() {
        // get open orders from db
        List<Orders> orders = new ArrayList<>();
        Slice<Orders> ordersSlice = orderDal.findAllByStatus(OrderStatus.OPEN, PageRequest.of(0, BATCH_SIZE));
        orders.addAll(ordersSlice.getContent());

        while (ordersSlice.hasNext()) {
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
            positionSlice = positionDal.findAllByExitOrderIdIsNull(positionSlice.nextPageable());
            positions.addAll(positionSlice.getContent());
            orderIds.addAll(positionSlice.getContent().stream().map(Position::getEntryOrderId).collect(Collectors.toSet()));
        }

        positions.stream().forEach(p -> storeInMemoryPositions(p, Metrics.INITIALIZE_MEMORY_TIME_DURATION));
    }

    @PostConstruct
    public void initializeQueues() throws IOException, TimeoutException {
        factory.setHost("rabbitmq");
        Connection connection = factory.newConnection();
        channel = connection.createChannel();
        channel.exchangeDeclare(Config.EXCHANGE, "topic", true);
        boolean durable = true;
        channel.queueDeclare(Config.ORDER_QUEUE, durable, false, false, null);
        channel.queueBind(Config.ORDER_QUEUE, Config.EXCHANGE, Config.ORDER_ROUTING_KEY);
        channel.queueDeclare(Config.POSITION_QUEUE, durable, false, false, null);
        channel.queueBind(Config.POSITION_QUEUE, Config.EXCHANGE, Config.POSITION_ROUTING_KEY);
        channel.queueDeclare(Config.LIMIT_ORDER_QUEUE, durable, false, false, null);
        channel.queueBind(Config.LIMIT_ORDER_QUEUE, Config.EXCHANGE, Config.LIMIT_ORDER_ROUTING_KEY);
        channel.queueDeclare(Config.FILLED_ORDER_QUEUE, durable, false, false, null);
        channel.queueBind(Config.FILLED_ORDER_QUEUE, Config.EXCHANGE, Config.FILLED_ORDER_ROUTING_KEY);
    }

    public ResponseEntity createOrder(Orders order, Histogram histogram) throws IOException {
        Metrics.ORDER_REQUEST.labels(order.getType().toString().toLowerCase()).inc();
        order.setStatus(OrderStatus.OPEN);
        // Send open order to persister via RMQ
        byte[] data = mapper.writeValueAsBytes(order);
        try (Histogram.Timer ignored = histogram.labels("send_order_to_queue").startTimer()) {
            channel.basicPublish(Config.EXCHANGE, Config.ORDER_ROUTING_KEY, null, data);
        }
        switch (order.getType()) {
            case LIMIT -> handleLimitOrder(order);
            case MARKET -> handleMarketOrder(order);
        }
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    private void handleLimitOrder(Orders order) throws IOException {
        storeLimitOrder(order, Metrics.LIMIT_ORDER_REQUEST_TIME_DURATION);
        byte[] data = mapper.writeValueAsBytes(order);
        try (Histogram.Timer ignored = Metrics.LIMIT_ORDER_REQUEST_TIME_DURATION.labels("send_order_to_queue").startTimer()) {
            channel.basicPublish(Config.EXCHANGE, Config.LIMIT_ORDER_ROUTING_KEY, null, data);
        }
    }

    private void handleMarketOrder(Orders order) throws IOException {
        Asset asset = null;
        try (Histogram.Timer ignored = Metrics.MARKET_ORDER_REQUEST_TIME_DURATION.labels("get_price_via_api").startTimer()) {
            // Ask the actual price from assetcache port 8000.
            asset = restTemplate.getForObject(assetCacheURL + order.getAssetType().toString().toLowerCase() + "/" + order.getSymbol(), Asset.class);
            // Fill the price to the order and update the order in DB.
        }
        try (Histogram.Timer ignored = Metrics.MARKET_ORDER_REQUEST_TIME_DURATION.labels("update_order").startTimer()) {

            order.setPrice(asset.getPrice());
            order.setStatus(OrderStatus.FILLED);
        }
        byte[] data = mapper.writeValueAsBytes(order);
        try (Histogram.Timer ignored = Metrics.MARKET_ORDER_REQUEST_TIME_DURATION.labels("send_updated_order_to_queue").startTimer()) {
            channel.basicPublish(Config.EXCHANGE, Config.ORDER_ROUTING_KEY, null, data);
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
    private void persistPosition(Position position, Histogram histogram) throws IOException {
        byte[] data = mapper.writeValueAsBytes(position);
        try (Histogram.Timer ignored = histogram.labels("send_position_to_queue").startTimer()) {
            channel.basicPublish(Config.EXCHANGE, Config.POSITION_ROUTING_KEY, null, data);
        }
        storeInMemoryPositions(position, histogram);
    }

    private void storeInMemoryPositions(Position position, Histogram histogram) {
        UUID accountId = position.getUserId();
        String symbol = position.getSymbol();
        UUID positionId = position.getId();
        try (Histogram.Timer timer = histogram.labels("persist_position_in_memory").startTimer()) {
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
        try (Histogram.Timer timer = histogram.labels("persist_limit_order_in_memory").startTimer()) {
            UUID accountId = order.getAccountId();
            if (!inMemoryOrders.containsKey(accountId)) {
                inMemoryOrders.put(accountId, Maps.newConcurrentMap());
            }
            inMemoryOrders.get(accountId).put(order.getId(), order);
        }
    }

    @RabbitListener(queues = Config.FILLED_ORDER_QUEUE)
    public void fillLimitOrder(FilledOrder filledOrder) throws IOException {
        Orders order = null;
        try (Histogram.Timer ignored = Metrics.LIMIT_ORDER_REQUEST_TIME_DURATION.labels("get_order_from_in_memory").startTimer()) {
            order = inMemoryOrders.get(filledOrder.getAccountId()).get(filledOrder.getOrderId());
        }
        if (order == null) {
            // TODO: add logger
            System.out.println("order not found in the in memory store. Order: " + filledOrder);
            return;
        }
        order.setStatus(OrderStatus.FILLED);
        order.setPrice(BigDecimal.valueOf(filledOrder.getFilledPrice()));

        byte[] data = mapper.writeValueAsBytes(order);
        try (Histogram.Timer ignored = Metrics.LIMIT_ORDER_REQUEST_TIME_DURATION.labels("send_updated_order_to_queue").startTimer()) {
            channel.basicPublish(Config.EXCHANGE, Config.ORDER_ROUTING_KEY, null, data);
        }
        try (Histogram.Timer ignored = Metrics.LIMIT_ORDER_REQUEST_TIME_DURATION.labels("store_updated_order_in_memory").startTimer()) {
            inMemoryOrders.get(filledOrder.getAccountId()).remove(order.getId());
        }
        switch (order.getOrderSide()) {
            case BUY -> handleBuy(order, Metrics.LIMIT_ORDER_REQUEST_TIME_DURATION);
            case SELL -> handleSell(order, Metrics.LIMIT_ORDER_REQUEST_TIME_DURATION);
        }
    }

    private void handleSell(Orders order, Histogram histogram) throws IOException {
        Position position = null;
        try (Histogram.Timer ignored = histogram.labels("get_position_from_memory").startTimer()) {
            position = inMemoryPositions.get(order.getAccountId()).get(order.getSymbol()).remove(order.getSellPositionId());
        }
        position.setExitOrderId(order.getId());
        position.setPriceAtSell(order.getPrice());
        position.setSellAt(new Date());
        byte[] data = mapper.writeValueAsBytes(position);
        try (Histogram.Timer ignored = histogram.labels("send_position_to_queue").startTimer()) {
            channel.basicPublish(Config.EXCHANGE, Config.POSITION_ROUTING_KEY, null, data);
        }
    }

    private void handleBuy(Orders order, Histogram histogram) throws IOException {
        Position position = new Position(order.getQuantity(), order.getAccountId(), order.getSymbol(), order.getPrice(), null, new Date(), order.getId(), null);
        persistPosition(position, histogram);
    }

    public void pushOrders() throws IOException {
        for (UUID accountId : inMemoryOrders.keySet()) {
            for (Orders order : inMemoryOrders.get(accountId).values()) {
                if (order.getType() == OrderType.LIMIT && order.getStatus() == OrderStatus.OPEN) {
                    byte[] data = mapper.writeValueAsBytes(order);
                    channel.basicPublish(Config.EXCHANGE, Config.LIMIT_ORDER_ROUTING_KEY, null, data);
                }
            }
        }
    }
}
