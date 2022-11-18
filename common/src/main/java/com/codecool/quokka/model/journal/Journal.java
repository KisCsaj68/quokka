package com.codecool.quokka.model.journal;

import java.math.BigDecimal;
import java.util.UUID;

public class Journal {
    private JournalType type;
    private BigDecimal amount;
    private UUID userId;

    public Journal(JournalType type, BigDecimal amount, UUID userId) {
        this.type = type;
        this.amount = amount;
        this.userId = userId;
    }

    public JournalType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public UUID getUserId() {
        return userId;
    }
}
