package com.github.alwaysdarkk.currencies.cache;

import com.github.alwaysdarkk.currencies.data.currency.Currency;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CurrencyCache {
    private final ConcurrentMap<String, Currency> currencyMap = new ConcurrentHashMap<>();

    public void insert(@Nonnull Currency currency) {
        this.currencyMap.put(currency.getId(), currency);
    }

    public void delete(@Nonnull Currency currency) {
        this.currencyMap.remove(currency.getId());
    }

    public @Nullable Currency find(@Nonnull String id) {
        return this.currencyMap.get(id);
    }

    public @Nonnull Collection<Currency> findAll() {
        return this.currencyMap.values();
    }
}
