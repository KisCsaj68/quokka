package com.codecool.quokka.service;

import java.math.BigDecimal;
import java.util.Objects;

public class Stock {
    private final String symbol;
    private int id;
    private BigDecimal price;

    public Stock(String symbol, BigDecimal price, int id) {
        this.id = id;
        this.symbol = symbol;
        this.price = price;
    }

    public Stock(String symbol, BigDecimal price) {
        this.symbol = symbol;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stock stock = (Stock) o;
        return this.id == stock.getId() && Objects.equals(this.symbol, stock.getSymbol());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.symbol, this.id);
    }
}
