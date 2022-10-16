package com.codecool.quokka.model.order;

import com.codecool.quokka.model.assets.AssetType;
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
//    @JsonIgnore
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

    @Enumerated(EnumType.STRING)
    private AssetType assetType;

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

    public Orders(int quantity, String symbol, UUID accountId, OrderStatus status, OrderType type, BigDecimal orderLimit, BigDecimal price, AssetType assetType) {
        this.quantity = quantity;
        this.symbol = symbol;
        this.accountId = accountId;
        this.status = status;
        this.type = type;
        this.orderLimit = orderLimit;
        this.price = price;
        this.assetType = assetType;
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



    @Override
    public String toString() {
        return "Orders{" +
                "id=" + id +
                ", quantity=" + quantity +
                ", accountId=" + accountId +
                ", price=" + price +
                ", status=" + status +
                ", type=" + type +
                ", assetType=" + assetType +
                ", orderLimit=" + orderLimit +
                ", symbol='" + symbol + '\'' +
                '}';
    }
}
