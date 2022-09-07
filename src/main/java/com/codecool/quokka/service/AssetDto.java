package com.codecool.quokka.service;

import java.math.BigDecimal;

public class StockDto {
    private final String symbol;
    private final float price;

    public StockDto(String symbol, BigDecimal price) {
        this.symbol = symbol;
        this.price = price.floatValue();
    }

    public String getSymbol() {
        return symbol;
    }

    public float getPrice() {
        return price;
    }

}
