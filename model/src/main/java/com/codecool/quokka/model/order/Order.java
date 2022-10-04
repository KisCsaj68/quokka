package com.codecool.quokka.model.order;

import java.math.BigDecimal;
import java.util.UUID;

public class Order {
    private int quantity;
    private UUID assetId;
    private UUID userId;
    private UUID id;
    private OrderStatus status;
    private OrderType type;
    private BigDecimal limit;

    public Order(int quantity, UUID assetId, UUID userId, OrderStatus status, OrderType type, BigDecimal limit) {
        this.quantity = quantity;
        this.assetId = assetId;
        this.userId = userId;
        this.status = status;
        this.type = type;
        this.limit = limit;
        this.id = UUID.randomUUID();
    }

    public int getQuantity() {
        return quantity;
    }

    public UUID getAssetId() {
        return assetId;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getId() {
        return id;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public OrderType getType() {
        return type;
    }

    public BigDecimal getLimit() {
        return limit;
    }
}
