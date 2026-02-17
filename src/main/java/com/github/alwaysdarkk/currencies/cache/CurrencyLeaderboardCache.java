package com.github.alwaysdarkk.currencies.cache;

import com.github.alwaysdarkk.currencies.data.currency.Currency;
import com.github.alwaysdarkk.currencies.data.user.CurrencyLeaderboardUser;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CurrencyLeaderboardCache {
    private final ConcurrentMap<String, List<CurrencyLeaderboardUser>> leaderboardMap = new ConcurrentHashMap<>();

    public void insert(@Nonnull Currency currency, @Nonnull List<CurrencyLeaderboardUser> users) {
        this.leaderboardMap.put(currency.getId(), users);
    }

    public void delete(@Nonnull Currency currency) {
        this.leaderboardMap.remove(currency.getId());
    }

    public @Nonnull List<CurrencyLeaderboardUser> find(@Nonnull Currency currency) {
        return this.leaderboardMap.getOrDefault(currency.getId(), new ArrayList<>());
    }
}