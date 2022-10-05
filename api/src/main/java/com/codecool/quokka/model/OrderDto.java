package com.codecool.quokka.model;

import com.codecool.quokka.model.order.Orders;
import com.codecool.quokka.model.order.OrderStatus;
import com.codecool.quokka.model.order.OrderType;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderDto {
    private int quantity;
    private String symbol;
    private OrderType type;
    private BigDecimal limit;
    private BigDecimal price;

    public OrderDto(int qty, String symbol, OrderType type, BigDecimal limit, BigDecimal price) {
        this.quantity = qty;
        this.symbol = symbol;
        this.type = type;
        this.limit = limit;
        this.price = price;
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

    public Orders toEntity(UUID accountId) {
        return new Orders(this.getQuantity(), this.getSymbol(), accountId, OrderStatus.OPEN, this.getType(), this.getLimit(), this.getPrice());
    }
}
