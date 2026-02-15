package com.github.alwaysdarkk.currencies.cache;

import com.github.alwaysdarkk.currencies.data.user.CurrencyUser;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CurrencyUserCache {
    private final Map<UUID, CurrencyUser> userMap = new HashMap<>();

    public void insert(@Nonnull CurrencyUser user) {
        this.userMap.put(user.getId(), user);
    }

    public void delete(@Nonnull CurrencyUser user) {
        this.userMap.remove(user.getId());
    }

    public @Nullable CurrencyUser find(UUID uuid) {
        return this.userMap.get(uuid);
    }

    public @Nonnull Collection<CurrencyUser> findAll() {
        return this.userMap.values();
    }
}