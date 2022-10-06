package com.codecool.quokka.model.assets;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Objects;

public class Asset {
    private final String symbol;
    private AssetType type;
    private int id;
    private BigDecimal open;

    public Asset(@JsonProperty("symbol") String symbol, @JsonProperty("open") String open) {
        this.symbol = symbol;
        this.open = BigDecimal.valueOf(Double.valueOf(open));
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
                ", open=" + open + '}';
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

    public BigDecimal getOpen() {
        return open;
    }

    public void setOpen(BigDecimal open) {
        this.open = open;
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
