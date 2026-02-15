package com.github.alwaysdarkk.currencies.data.user;

import com.github.alwaysdarkk.currencies.data.currency.Currency;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "currency_users")
public class CurrencyUser {

    @Id
    @Column(length = 16, columnDefinition = "BINARY(16)")
    private UUID id;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "user_currencies",
            joinColumns = @JoinColumn(name = "user_id"),
            indexes = @Index(columnList = "user_id")
    )
    @MapKeyColumn(name = "currency_id")
    @Column(name = "amount")
    private Map<String, Double> currencyMap = new HashMap<>();

    @Transient
    private boolean dirty = false;

    public CurrencyUser(@Nonnull UUID id) {
        this.id = id;
        this.currencyMap = new HashMap<>();
        this.dirty = true;
    }

    public void addAmount(@Nonnull Currency currency, double amount) {
        final double currentBalance = this.getAmount(currency);
        this.currencyMap.put(currency.getId(), currentBalance + amount);

        this.setDirty(true);
    }

    public void removeAmount(@Nonnull Currency currency, double amount) {
        final double currentBalance = this.getAmount(currency);
        final double newBalance = Math.max(currentBalance - amount, 0.0);

        this.currencyMap.put(currency.getId(), newBalance);
        this.setDirty(true);
    }

    public void setAmount(@Nonnull Currency currency, double amount) {
        this.currencyMap.put(currency.getId(), amount);
        this.setDirty(true);
    }

    public double getAmount(@Nonnull Currency currency) {
        return this.currencyMap.getOrDefault(currency.getId(), currency.getDefaultAmount());
    }
}