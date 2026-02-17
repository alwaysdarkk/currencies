package com.github.alwaysdarkk.currencies.data.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class CurrencyLeaderboardUser {
    private final UUID id;
    private final String username;
    private final int position;
    private final double amount;
}
