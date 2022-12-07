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

    public Position(int quantity, UUID userId, String symbol, BigDecimal priceAtBuy, BigDecimal priceAtSell, Date today) {
        this.quantity = quantity;
        this.userId = userId;
        this.symbol = symbol;
        this.priceAtBuy = priceAtBuy;
        this.priceAtSell = priceAtSell;
        this.buyAt = today;
        this.id = UUID.randomUUID();
    }

    public int getQuantity() {
        return quantity;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getAssetId() {
        return symbol;
    }

    public BigDecimal getPriceAtBuy() {
        return priceAtBuy;
    }

    public BigDecimal getPriceAtSell() {
        return priceAtSell;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
