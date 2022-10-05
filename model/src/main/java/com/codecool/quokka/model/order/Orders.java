package com.codecool.quokka.model.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Orders {
    @Id
    @JsonIgnore
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @JsonProperty("quantity")
    private int quantity;

    private UUID accountId;
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    @JsonProperty("type")
    @Enumerated(EnumType.STRING)
    private OrderType type;
    @JsonProperty("order_limit")
    private BigDecimal orderLimit;

    @JsonProperty("symbol")
    private String symbol;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Orders(int quantity, String symbol, UUID accountId, OrderStatus status, OrderType type, BigDecimal orderLimit, BigDecimal price) {
        this.quantity = quantity;
        this.symbol = symbol;
        this.accountId = accountId;
        this.status = status;
        this.type = type;
        this.orderLimit = orderLimit;
        this.price = price;

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

    public void setOrderLimit(BigDecimal limit) {
        this.orderLimit = limit;
    }


    @Override
    public String toString() {
        return "Order{" +
                "quantity=" + quantity +
                ", symbol=" + symbol +
                ", accountId=" + accountId +
                ", id=" + id +
                ", status=" + status +
                ", type=" + type +
                ", limit=" + orderLimit +
                '}';
    }
}
