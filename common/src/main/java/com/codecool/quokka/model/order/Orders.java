package com.codecool.quokka.model.order;

import com.codecool.quokka.model.assets.AssetType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
public class Orders implements Serializable {
    @Id
    private UUID id;

    @JsonProperty("quantity")
    private int quantity;

    @JsonProperty("account")
    private UUID accountId;

    @JsonProperty("sell_position_id")
    private UUID sellPositionId;

    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @JsonProperty("type")
    @Enumerated(EnumType.STRING)
    private OrderType type;


    @JsonProperty("asset_type")
    @Enumerated(EnumType.STRING)
    private AssetType assetType;

    @JsonProperty("side")
    @Enumerated(EnumType.STRING)
    private OrderSide orderSide;

    @JsonProperty("limit")
    private BigDecimal orderLimit;

    @JsonProperty("symbol")
    private String symbol;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Orders(int quantity, String symbol, UUID accountId, OrderStatus status, OrderType type, BigDecimal orderLimit, BigDecimal price, AssetType assetType, OrderSide side, UUID sellPositionId) {
        this.quantity = quantity;
        this.symbol = symbol;
        this.accountId = accountId;
        this.status = status;
        this.type = type;
        this.orderSide = side;
        this.orderLimit = orderLimit;
        this.price = price;
        this.assetType = assetType;
        this.id = UUID.randomUUID();
        this.sellPositionId = sellPositionId;
    }

    public Orders() {
        this.id = UUID.randomUUID();
    }

    public int getQuantity() {
        return quantity;
    }

    public UUID getAccountId() {
        return accountId;
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

    public void setOrderLimit(BigDecimal orderLimit) {
        this.orderLimit = orderLimit;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void setType(OrderType type) {
        this.type = type;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public AssetType getAssetType() {
        return assetType;
    }

    public void setAssetType(AssetType assetType) {
        this.assetType = assetType;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public UUID getSellPositionId() {
        return sellPositionId;
    }

    public void setSellPositionId(UUID sellPositionId) {
        this.sellPositionId = sellPositionId;
    }

    public OrderSide getOrderSide() {
        return orderSide;
    }



    @Override
    public String toString() {
        return "Orders{" +
                "id=" + id +
                ", quantity=" + quantity +
                ", accountId=" + accountId +
                ", sellPositionId=" + sellPositionId +
                ", price=" + price +
                ", status=" + status +
                ", type=" + type +
                ", assetType=" + assetType +
                ", orderSide=" + orderSide +
                ", orderLimit=" + orderLimit +
                ", symbol='" + symbol + '\'' +
                '}';
    }
}
