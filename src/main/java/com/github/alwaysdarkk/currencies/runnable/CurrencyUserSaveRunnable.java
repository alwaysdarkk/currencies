package com.github.alwaysdarkk.currencies.runnable;

import com.github.alwaysdarkk.currencies.cache.CurrencyUserCache;
import com.github.alwaysdarkk.currencies.repository.CurrencyUserRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CurrencyUserSaveRunnable implements Runnable {

    private final CurrencyUserRepository userRepository;
    private final CurrencyUserCache userCache;

    @Override
    public void run() {
        this.userRepository.bulkInsert(this.userCache.findAll());
    }
}
