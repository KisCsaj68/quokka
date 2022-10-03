package com.codecool.quokka.model.order;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.UUID;

public class Order {
    @JsonProperty("quantity")
    private int quantity;

    @JsonProperty("account_id")
    private UUID accountId;
    @JsonProperty("id")
    private UUID id;
    @JsonProperty("status")
    private OrderStatus status;
    @JsonProperty("type")
    private OrderType type;
    @JsonProperty("limit")
    private BigDecimal limit;

    private UUID assetId;

    public Order(int quantity, UUID assetId, UUID accountId, OrderStatus status, OrderType type, BigDecimal limit) {
        this.quantity = quantity;
        this.assetId = assetId;
        this.accountId = accountId;
        this.status = status;
        this.type = type;
        this.limit = limit;
        this.id = UUID.randomUUID();
    }

    public Order(){}

    public int getQuantity() {
        return quantity;
    }

    public UUID getAssetId() {
        return assetId;
    }

    public UUID getAccountId() {
        return accountId;
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

    @Override
    public String toString() {
        return "Order{" +
                "quantity=" + quantity +
                ", assetId=" + assetId +
                ", accountId=" + accountId +
                ", id=" + id +
                ", status=" + status +
                ", type=" + type +
                ", limit=" + limit +
                '}';
    }
}
