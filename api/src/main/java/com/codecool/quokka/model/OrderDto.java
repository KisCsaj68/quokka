package com.codecool.quokka.model;

import com.codecool.quokka.model.order.AssetOrder;
import com.codecool.quokka.model.order.OrderStatus;
import com.codecool.quokka.model.order.OrderType;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderDto {
    private int quantity;
    private String symbol;
    private OrderType type;
    private BigDecimal limit;

    public OrderDto(int qty, String symbol, OrderType type, BigDecimal limit) {
        this.quantity = qty;
        this.symbol = symbol;
        this.type = type;
        this.limit = limit;
    }

    public int getQty() {
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

    public AssetOrder toEntity(UUID accountId) {
        return new AssetOrder(this.getQty(), null, accountId, OrderStatus.OPEN, this.getType(), this.getLimit());
    }
}
