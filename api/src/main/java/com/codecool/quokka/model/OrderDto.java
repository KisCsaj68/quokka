package com.codecool.quokka.model;

import com.codecool.quokka.model.assets.AssetType;
import com.codecool.quokka.model.order.OrderSide;
import com.codecool.quokka.model.order.Orders;
import com.codecool.quokka.model.order.OrderStatus;
import com.codecool.quokka.model.order.OrderType;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderDto {
    private int quantity;
    private String symbol;
    private OrderType type;
//    @JsonProperty("limit")
    private BigDecimal limit;
    private BigDecimal price;

    private AssetType assetType;
    @JsonProperty("side")
    private OrderSide orderSide;

    @JsonProperty("sell_position_id")
    private UUID sellPositionId;

    public OrderDto(int qty, String symbol, OrderType type, BigDecimal limit, BigDecimal price, AssetType assetType, OrderSide orderSide, UUID sellPositionId) {
        this.quantity = qty;
        this.symbol = symbol;
        this.type = type;
        this.limit = limit;
        this.price = price;
        this.assetType = assetType;
        this.orderSide = orderSide;
        this.sellPositionId = sellPositionId;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getSymbol() {
        return symbol;
    }

    public OrderType getType() {
        return type;
    }

    public BigDecimal getLimit() {
        return limit;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public AssetType getAssetType() {
        return assetType;
    }

    public UUID getSellPositionId() {
        return sellPositionId;
    }

    public Orders toEntity(UUID accountId) {
        return new Orders(this.getQuantity(), this.getSymbol(), accountId, OrderStatus.OPEN, this.getType(), this.getLimit(), this.getPrice(), this.getAssetType(), this.orderSide(), this.getSellPositionId());
    }

    private OrderSide orderSide() {
        return this.orderSide;
    }
}
