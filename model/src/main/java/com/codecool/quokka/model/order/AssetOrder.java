package com.codecool.quokka.model.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
public class AssetOrder {
    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @JsonProperty("quantity")
    private int quantity;

    @JsonProperty("account_id")
    private UUID accountId;
    @JsonProperty("id")
    private UUID orderId;
    @JsonProperty("status")
    private OrderStatus status;
    @JsonProperty("type")
    private OrderType type;
    @JsonProperty("order_limit")
    private BigDecimal orderLimit;

    private UUID assetId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AssetOrder(int quantity, UUID assetId, UUID accountId, OrderStatus status, OrderType type, BigDecimal orderLimit) {
        this.quantity = quantity;
        this.assetId = assetId;
        this.accountId = accountId;
        this.status = status;
        this.type = type;
        this.orderLimit = orderLimit;
        this.orderId = UUID.randomUUID();
    }

    public AssetOrder(){}

    public int getQuantity() {
        return quantity;
    }

    public UUID getAssetId() {
        return assetId;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public OrderType getType() {
        return type;
    }

    public BigDecimal getOrderLimit() {
        return orderLimit;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void setType(OrderType type) {
        this.type = type;
    }

    public void setOrderLimit(BigDecimal limit) {
        this.orderLimit = limit;
    }

    public void setAssetId(UUID assetId) {
        this.assetId = assetId;
    }

    @Override
    public String toString() {
        return "Order{" +
                "quantity=" + quantity +
                ", assetId=" + assetId +
                ", accountId=" + accountId +
                ", id=" + orderId +
                ", status=" + status +
                ", type=" + type +
                ", limit=" + orderLimit +
                '}';
    }
}
