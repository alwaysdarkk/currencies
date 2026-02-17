package com.github.alwaysdarkk.currencies.runnable;

import com.github.alwaysdarkk.currencies.cache.CurrencyCache;
import com.github.alwaysdarkk.currencies.cache.CurrencyLeaderboardCache;
import com.github.alwaysdarkk.currencies.repository.CurrencyUserRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CurrencyLeaderboardUpdateRunnable implements Runnable {

    private final CurrencyCache currencyCache;
    private final CurrencyUserRepository userRepository;
    private final CurrencyLeaderboardCache leaderboardCache;

    @Override
    public void run() {
        this.currencyCache.findAll().forEach(currency -> this.userRepository
                .findLeaderboard(currency)
                .thenAccept(users -> this.leaderboardCache.insert(currency, users)));
    }
}
