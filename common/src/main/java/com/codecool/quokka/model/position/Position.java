package com.codecool.quokka.model.position;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Position implements Serializable {
    @Id
    private UUID id;

    private int quantity;
    private UUID userId;
    private String symbol;
    private BigDecimal priceAtBuy;
    private BigDecimal priceAtSell;
    private UUID entryOrderId;
    private UUID exitOrderId;

    private Date buyAt;
    private Date sellAt;

    public Position(int quantity, UUID userId, String symbol, BigDecimal priceAtBuy, BigDecimal priceAtSell, Date today, UUID entryOrderId, UUID exitOrderId) {
        this.quantity = quantity;
        this.userId = userId;
        this.symbol = symbol;
        this.priceAtBuy = priceAtBuy;
        this.priceAtSell = priceAtSell;
        this.buyAt = today;
        this.id = UUID.randomUUID();
        this.entryOrderId = entryOrderId;
        this.exitOrderId = exitOrderId;
    }

    public UUID getId() {
        return id;
    }

    public int getQuantity() {
        return quantity;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getSymbol() {
        return symbol;
    }

    public BigDecimal getPriceAtBuy() {
        return priceAtBuy;
    }

    public BigDecimal getPriceAtSell() {
        return priceAtSell;
    }

    public UUID getEntryOrderId() {
        return entryOrderId;
    }

    public UUID getExitOrderId() {
        return exitOrderId;
    }

    public Date getBuyAt() {
        return buyAt;
    }

    public Date getSellAt() {
        return sellAt;
    }

    public void setPriceAtSell(BigDecimal priceAtSell) {
        this.priceAtSell = priceAtSell;
    }

    public void setEntryOrderId(UUID entryOrderId) {
        this.entryOrderId = entryOrderId;
    }

    public void setExitOrderId(UUID exitOrderId) {
        this.exitOrderId = exitOrderId;
    }

    public void setSellAt(Date sellAt) {
        this.sellAt = sellAt;
    }
}
