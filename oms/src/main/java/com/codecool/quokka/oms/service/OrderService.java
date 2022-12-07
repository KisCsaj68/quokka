package com.codecool.quokka.oms.service;

import com.codecool.quokka.model.assets.Asset;
import com.codecool.quokka.model.mqconfig.Config;
import com.codecool.quokka.model.order.Orders;
import com.codecool.quokka.model.order.OrderStatus;
import com.codecool.quokka.model.position.Position;
import com.codecool.quokka.oms.dal.OrderDal;
import com.codecool.quokka.oms.dal.PositionDal;
import com.google.common.collect.Maps;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@ConfigurationProperties
public class OrderService {

    private RabbitTemplate rabbitTemplate;
    private RestTemplate restTemplate;

    private Map<UUID, Orders> inMemoryOrders;

    private OrderDal orderDal;

    private PositionDal positionDal;

    // Store position by user -> {symbol: {positionId: position}}
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
        List<Orders> orders = orderDal.findAllByStatus(OrderStatus.OPEN);
        orders.stream().forEach(o -> storeLimitOrder(o));
        List<Position> positions = positionDal.findAllByExitOrderIdIsNull();
        Set<UUID> orderIds = positions.stream().map(Position::getEntryOrderId).collect(Collectors.toSet());
        Map<UUID, Orders> ordersByPositions = orderDal.findAllByIdIn(orderIds).stream().collect(Collectors.toMap(o -> o.getId(), o -> o));
        positions.stream()
                .forEach(p -> storeInMemoryPositions(p, ordersByPositions.get(p.getEntryOrderId())));
        System.out.println(inMemoryPositions);
    }

    public ResponseEntity createOrder(Orders order) {
        System.out.println(order);
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
        rabbitTemplate.convertAndSend(Config.EXCHANGE, Config.LIMIT_ORDER_ROUTING_KEY, order);
        storeLimitOrder(order);
    }

    private void handleMarketOrder(Orders order) {
        // Ask the actual price from assetcache port 8000.
        Asset asset = restTemplate.getForObject(assetCacheURL + order.getAssetType().toString().toLowerCase() + "/" + order.getSymbol(), Asset.class);
        // Fill the price to the order and update the order in DB.
        order.setPrice(asset.getOpen());
        order.setStatus(OrderStatus.FILLED);
        rabbitTemplate.convertAndSend(Config.EXCHANGE, Config.ORDER_ROUTING_KEY, order);
        // Create position and persist db + in-memory
        Position position = new Position(order.getQuantity(), order.getAccountId(), order.getSymbol(), order.getPrice(), null, new Date(), order.getId(), null);
        persistPosition(position, order);
    }

    /**
     * Push the Position to RabbitMQ first(for consistency) and stores it in-memory.
     */
    private void persistPosition(Position position, Orders order) {
        rabbitTemplate.convertAndSend(Config.EXCHANGE, Config.POSITION_ROUTING_KEY, position);
        storeInMemoryPositions(position, order);
//        if (!inMemoryPositions.containsKey(order.getAccountId())) {
//            inMemoryPositions.put(order.getAccountId(), Maps.newConcurrentMap());
//        }
//        if (!inMemoryPositions.get(order.getAccountId()).containsKey(order.getSymbol())) {
//            inMemoryPositions.get(order.getAccountId()).put(order.getSymbol(), Maps.newConcurrentMap());
//        }
//        inMemoryPositions.get(order.getAccountId()).get(order.getSymbol()).put(position.getId(), position);
    }

    private void storeInMemoryPositions(Position position, Orders order) {
        if (!inMemoryPositions.containsKey(order.getAccountId())) {
            inMemoryPositions.put(order.getAccountId(), Maps.newConcurrentMap());
        }
        if (!inMemoryPositions.get(order.getAccountId()).containsKey(order.getSymbol())) {
            inMemoryPositions.get(order.getAccountId()).put(order.getSymbol(), Maps.newConcurrentMap());
        }
        inMemoryPositions.get(order.getAccountId()).get(order.getSymbol()).put(position.getId(), position);
    }

    private void storeLimitOrder(Orders order) {
        inMemoryOrders.put(order.getId(), order);
    }
}
