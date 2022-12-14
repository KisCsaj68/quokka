package com.codecool.quokka.oms.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class FilledOrder {
    @JsonProperty("account_id")
    private UUID accountId;
    @JsonProperty("order_id")
    private UUID orderId;
    @JsonProperty("limit_price")
    private float limitPrice;
    @JsonProperty("symbol")
    private String symbol;
    @JsonProperty("filled_price")
    private float filledPrice;
    @JsonProperty("sell_position_id")
    private UUID sellPositionId;

    public FilledOrder(UUID accountId, UUID orderId, float limitPrice, String symbol, float filledPrice, UUID sellPositionId) {
        this.accountId = accountId;
        this.orderId = orderId;
        this.limitPrice = limitPrice;
        this.symbol = symbol;
        this.filledPrice = filledPrice;
        this.sellPositionId = sellPositionId;
    }

    public FilledOrder(){}

    public UUID getAccountId() {
        return accountId;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public float getLimitPrice() {
        return limitPrice;
    }

    public String getSymbol() {
        return symbol;
    }

    public float getFilledPrice() {
        return filledPrice;
    }

    public UUID getSellPositionId() {
        return sellPositionId;
    }

    @Override
    public String toString() {
        return "FilledOrder{" +
                "accountId=" + accountId +
                ", orderId=" + orderId +
                ", limitPrice=" + limitPrice +
                ", symbol='" + symbol + '\'' +
                ", filledPrice=" + filledPrice +
                ", sellPositionId=" + sellPositionId +
                '}';
    }
}
