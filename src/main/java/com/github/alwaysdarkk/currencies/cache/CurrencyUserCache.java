package com.github.alwaysdarkk.currencies.cache;

import com.github.alwaysdarkk.currencies.data.user.CurrencyUser;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CurrencyUserCache {
    private final ConcurrentMap<UUID, CurrencyUser> userMap = new ConcurrentHashMap<>();

    public void insert(@Nonnull CurrencyUser user) {
        this.userMap.put(user.getId(), user);
    }

    public void delete(@Nonnull CurrencyUser user) {
        this.userMap.remove(user.getId());
    }

    public @Nullable CurrencyUser find(@Nonnull UUID uuid) {
        return this.userMap.get(uuid);
    }

    public @Nonnull Collection<CurrencyUser> findAll() {
        return this.userMap.values();
    }
}