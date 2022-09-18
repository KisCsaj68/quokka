package com.codecool.quokka.model.assets;

import java.math.BigDecimal;
import java.util.Objects;

public class Asset {
    private final String symbol;
    private AssetType type;
    private int id;
    private BigDecimal price;

    public Asset(String symbol, String price) {
        this.symbol = symbol;
        this.price = new BigDecimal(price);
    }


    public AssetType getType() {
        return this.type;
    }

    public void setType(AssetType type) {
        this.type = type;
    }

    public void setType(String type) {
        this.type = AssetType.valueOf(type.toUpperCase());
    }

    @Override
    public String toString() {
        return "Asset{" +
                "symbol='" + symbol + '\'' +
                ", type=" + type +
                ", id=" + id +
                ", price=" + price + '}';
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
        Asset asset = (Asset) o;
        return this.symbol.equals(asset.getSymbol()) && this.type.equals(asset.getType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.symbol, this.type);
    }
}
