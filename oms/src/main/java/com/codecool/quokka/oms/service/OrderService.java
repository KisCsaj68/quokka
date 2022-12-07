package com.codecool.quokka.oms.service;

import com.codecool.quokka.model.assets.Asset;
import com.codecool.quokka.model.mqconfig.Config;
import com.codecool.quokka.model.order.Orders;
import com.codecool.quokka.model.order.OrderStatus;
import com.codecool.quokka.model.position.Position;
import com.google.common.collect.Maps;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@ConfigurationProperties
public class OrderService {

    private RabbitTemplate rabbitTemplate;
    private RestTemplate restTemplate;

    private Map<UUID, Orders> inMemoryOrders;

    // Store position by user -> {symbol: {positionId: position}}
    private Map<UUID, Map<String, Map<Long, Position>>> inMemoryPositions;

    @Value("${quokka.service.assetcache.address}${quokka.service.assetcache.endpoint}")
    private String assetCacheURL;

    @Autowired
    public OrderService(RabbitTemplate template) {
        this.rabbitTemplate = template;
        this.inMemoryOrders = new ConcurrentHashMap<>();
        this.inMemoryPositions = new ConcurrentHashMap<>();
        this.restTemplate = new RestTemplate();
    }

    public ResponseEntity createOrder(Orders order) {
        System.out.println(order);
        order.setStatus(OrderStatus.OPEN);
        // Send open order to persister via RMQ
        rabbitTemplate.convertAndSend(Config.EXCHANGE, Config.ORDER_ROUTING_KEY, order);
        switch (order.getType()) {
            case LIMIT -> handleLimitOrder();
            case MARKET -> handleMarketOrder(order);
        }
//        handleMarketOrder();
//        // Ask the actual price from assetcache port 8000.
//        Asset asset = restTemplate.getForObject(assetCacheURL + order.getAssetType().toString().toLowerCase() + "/" + order.getSymbol(), Asset.class);
//        // Fill the price to the order and update the order in DB.
//        order.setPrice(asset.getOpen());
//        order.setStatus(OrderStatus.FILLED);
//        rabbitTemplate.convertAndSend(Config.EXCHANGE, Config.ORDER_ROUTING_KEY, order);
//        // Create position, send to persister RMQ
//        Position position = new Position(order.getQuantity(), order.getAccountId(), order.getSymbol(), order.getPrice(), null, new Date());
//        rabbitTemplate.convertAndSend(Config.EXCHANGE, Config.POSITION_ROUTING_KEY, position);
        // Store both Entity in-memory
        //TODO
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    private void handleLimitOrder() {
    }

    private void handleMarketOrder(Orders order) {
        // Ask the actual price from assetcache port 8000.
        Asset asset = restTemplate.getForObject(assetCacheURL + order.getAssetType().toString().toLowerCase() + "/" + order.getSymbol(), Asset.class);
        // Fill the price to the order and update the order in DB.
        order.setPrice(asset.getOpen());
        order.setStatus(OrderStatus.FILLED);
        rabbitTemplate.convertAndSend(Config.EXCHANGE, Config.ORDER_ROUTING_KEY, order);

        // Create position and persist db + in-memory
        Position position = new Position(order.getQuantity(), order.getAccountId(), order.getSymbol(), order.getPrice(), null, new Date());
        storePosition(position, order);
    }

   /**
    * Push the Position to RabbitMQ first(for consistency) and stores it in-memory.
    */
    private void storePosition(Position position, Orders order) {
        rabbitTemplate.convertAndSend(Config.EXCHANGE, Config.POSITION_ROUTING_KEY, position);
        if (!inMemoryPositions.containsKey(order.getAccountId())) {
            inMemoryPositions.put(order.getAccountId(), Maps.newConcurrentMap());
        }
        if (!inMemoryPositions.get(order.getAccountId()).containsKey(order.getSymbol())) {
            inMemoryPositions.get(order.getAccountId()).put(order.getSymbol(), Maps.newConcurrentMap());
        }
        inMemoryPositions.get(order.getAccountId()).get(order.getSymbol()).put(position.getId(), position);
    }
}
