package com.github.alwaysdarkk.currencies.listener;

import com.github.alwaysdarkk.currencies.cache.CurrencyUserCache;
import com.github.alwaysdarkk.currencies.data.user.CurrencyUser;
import com.github.alwaysdarkk.currencies.repository.CurrencyUserRepository;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nonnull;

@RequiredArgsConstructor
public class UserTrafficListener {

    private final CurrencyUserRepository userRepository;
    private final CurrencyUserCache userCache;

    public void onPlayerConnect(@Nonnull PlayerConnectEvent event) {
        final PlayerRef playerRef = event.getPlayerRef();
        this.userRepository.find(playerRef.getUuid()).thenAccept(optionalUser -> {
            if (optionalUser.isEmpty()) {
                final CurrencyUser user = new CurrencyUser(playerRef.getUuid(), playerRef.getUsername());
                this.userCache.insert(user);
                return;
            }

            final CurrencyUser user = optionalUser.get();
            if (!user.getUsername().equals(playerRef.getUsername())) {
                user.setUsername(playerRef.getUsername());
                user.setDirty(true);
            }

            this.userCache.insert(user);
        });
    }

    public void onPlayerDisconnect(@Nonnull PlayerDisconnectEvent event) {
        final PlayerRef playerRef = event.getPlayerRef();
        final CurrencyUser user = this.userCache.find(playerRef.getUuid());

        if (user == null) {
            return;
        }

        this.userCache.delete(user);
    }
}
