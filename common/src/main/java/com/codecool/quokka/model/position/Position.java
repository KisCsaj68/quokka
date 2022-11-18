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
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private int quantity;
    private UUID userId;
    private String symbol;
    private BigDecimal priceAtBuy;
    private BigDecimal priceAtSell;

    private Date buyAt;
    private Date sellAt;

    public Position(int quantity, UUID userId, String symbol, BigDecimal priceAtBuy, BigDecimal priceAtSell, Date today) {
        this.quantity = quantity;
        this.userId = userId;
        this.symbol = symbol;
        this.priceAtBuy = priceAtBuy;
        this.priceAtSell = priceAtSell;
        this.buyAt = today;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
