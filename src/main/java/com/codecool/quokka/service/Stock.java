package com.codecool.quokka.service;

import java.math.BigDecimal;

public class Stock {
    private final String symbol;
    private BigDecimal price;

    public Stock(String symbol, BigDecimal price) {
        this.symbol = symbol;
        this.price = price;
    }

    public Stock(String symbol, double price) {
        this.symbol = symbol;
        this.price = new BigDecimal(price);
    }

    public Stock(String symbol, float price) {
        this.symbol = symbol;
        this.price = new BigDecimal(price);
    }

    public String getSymbol() {
        return this.symbol;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }


}
