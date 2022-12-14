package com.codecool.quokka.oms.service;

import com.codecool.quokka.model.assets.Asset;
import com.codecool.quokka.model.mqconfig.Config;
import com.codecool.quokka.model.order.Orders;
import com.codecool.quokka.model.order.OrderStatus;
import com.codecool.quokka.model.position.Position;
import com.codecool.quokka.oms.dal.OrderDal;
import com.codecool.quokka.oms.dal.PositionDal;
import com.codecool.quokka.oms.model.FilledOrder;
import com.google.common.collect.Maps;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@ConfigurationProperties
public class OrderService {

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
        List<Orders> orders = orderDal.findAllByStatus(OrderStatus.OPEN);
        orders.stream().forEach(o -> storeLimitOrder(o));
        // get positions from db
        List<Position> positions = positionDal.findAllByExitOrderIdIsNull();
        Set<UUID> orderIds = positions.stream().map(Position::getEntryOrderId).collect(Collectors.toSet());
        Map<UUID, Orders> ordersByPositions = orderDal.findAllByIdIn(orderIds).stream().collect(Collectors.toMap(o -> o.getId(), o -> o));
        positions.stream()
                .forEach(p -> storeInMemoryPositions(p, ordersByPositions.get(p.getEntryOrderId())));
    }

    public ResponseEntity createOrder(Orders order) {
        order.setStatus(OrderStatus.OPEN);
        // Send open order to persister via RMQ
        rabbitTemplate.convertAndSend(Config.EXCHANGE, Config.ORDER_ROUTING_KEY, order);
        switch (order.getType()) {
            case LIMIT -> handleLimitOrder(order);
            case MARKET -> handleMarketOrder(order);
        }
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    private void handleLimitOrder(Orders order) {
        storeLimitOrder(order);
        rabbitTemplate.convertAndSend(Config.EXCHANGE, Config.LIMIT_ORDER_ROUTING_KEY, order);
    }

    private void handleMarketOrder(Orders order) {
        // Ask the actual price from assetcache port 8000.
        Asset asset = restTemplate.getForObject(assetCacheURL + order.getAssetType().toString().toLowerCase() + "/" + order.getSymbol(), Asset.class);
        // Fill the price to the order and update the order in DB.
        order.setPrice(asset.getPrice());
        order.setStatus(OrderStatus.FILLED);
        rabbitTemplate.convertAndSend(Config.EXCHANGE, Config.ORDER_ROUTING_KEY, order);
        // Create position and persist db + in-memory
        switch (order.getOrderSide()) {
            case BUY -> handleBuy(order);
            case SELL -> handleSell(order);
        }
    }

    /**
     * Push the Position to RabbitMQ first(for consistency) and stores it in-memory.
     */
    private void persistPosition(Position position, Orders order) {
        rabbitTemplate.convertAndSend(Config.EXCHANGE, Config.POSITION_ROUTING_KEY, position);
        storeInMemoryPositions(position, order);
    }

    private void storeInMemoryPositions(Position position, Orders order) {
        UUID accountId = order.getAccountId();
        String symbol = order.getSymbol();
        UUID positionId = position.getId();
        if (!inMemoryPositions.containsKey(accountId)) {
            inMemoryPositions.put(accountId, Maps.newConcurrentMap());
        }
        if (!inMemoryPositions.get(accountId).containsKey(symbol)) {
            inMemoryPositions.get(accountId).put(symbol, Maps.newConcurrentMap());
        }
        inMemoryPositions.get(accountId).get(symbol).put(positionId, position);
    }

    private void storeLimitOrder(Orders order) {
        UUID accountId = order.getAccountId();
        if (!inMemoryOrders.containsKey(accountId)) {
            inMemoryOrders.put(accountId, Maps.newConcurrentMap());
        }
        inMemoryOrders.get(accountId).put(order.getId(), order);
    }

    @RabbitListener(queues = Config.FILLED_ORDER_QUEUE)
    public void fillLimitOrder(FilledOrder filledOrder) {
        Orders order = inMemoryOrders.get(filledOrder.getAccountId()).get(filledOrder.getOrderId());
        if (order == null) {
            // TODO: add logger
            System.out.println("order not found in the in memory store. Order: " + filledOrder + "; in-memory-store: " + inMemoryOrders);
            return;
        }
        order.setStatus(OrderStatus.FILLED);
        order.setPrice(BigDecimal.valueOf(filledOrder.getFilledPrice()));
        rabbitTemplate.convertAndSend(Config.EXCHANGE, Config.ORDER_ROUTING_KEY, order);
        inMemoryOrders.get(filledOrder.getAccountId()).remove(order.getId());
        switch (order.getOrderSide()) {
            case BUY -> handleBuy(order);
            case SELL -> handleSell(order);
        }
    }

    private void handleSell(Orders order) {
        Position position = inMemoryPositions.get(order.getAccountId()).get(order.getSymbol()).remove(order.getSellPositionId());
        position.setExitOrderId(order.getId());
        position.setPriceAtSell(order.getPrice());
        position.setSellAt(new Date());
        rabbitTemplate.convertAndSend(Config.EXCHANGE, Config.POSITION_ROUTING_KEY, position);
    }

    private void handleBuy(Orders order) {
        Position position = new Position(order.getQuantity(), order.getAccountId(), order.getSymbol(), order.getPrice(), null, new Date(), order.getId(), null);
        persistPosition(position, order);
    }

    public void pushOrders() {
        for (UUID accountId : inMemoryOrders.keySet()) {
            for (Orders order : inMemoryOrders.get(accountId).values()) {
                rabbitTemplate.convertAndSend(Config.EXCHANGE, Config.LIMIT_ORDER_ROUTING_KEY, order);
            }
        }
    }
}
