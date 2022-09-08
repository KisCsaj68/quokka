package com.codecool.quokka.service.assets;

import com.codecool.quokka.model.AssetType;

import java.math.BigDecimal;

public class AssetDto {
    private final String symbol;
    private final float price;
    private final String type;

    public AssetDto(String symbol, BigDecimal price, AssetType type) {
        this.symbol = symbol;
        this.price = price.floatValue();
        this.type = type.name();
    }

    public String getType() {
        return type;
    }

    public String getSymbol() {
        return symbol;
    }

    public float getPrice() {
        return price;
    }

}
