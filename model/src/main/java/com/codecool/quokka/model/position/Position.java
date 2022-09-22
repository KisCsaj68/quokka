package com.codecool.quokka.model.position;

import java.math.BigDecimal;
import java.util.UUID;

public class Position {
    private int quantity;
    private UUID userId;
    private UUID assetId;
    private BigDecimal priceAtBuy;
    private BigDecimal priceAtSell;

    public Position(int quantity, UUID userId, UUID assetId, BigDecimal priceAtBuy, BigDecimal priceAtSell) {
        this.quantity = quantity;
        this.userId = userId;
        this.assetId = assetId;
        this.priceAtBuy = priceAtBuy;
        this.priceAtSell = priceAtSell;
    }

    public int getQuantity() {
        return quantity;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getAssetId() {
        return assetId;
    }

    public BigDecimal getPriceAtBuy() {
        return priceAtBuy;
    }

    public BigDecimal getPriceAtSell() {
        return priceAtSell;
    }
}
