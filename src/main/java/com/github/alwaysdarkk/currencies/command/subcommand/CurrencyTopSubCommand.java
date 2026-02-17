package com.github.alwaysdarkk.currencies.command.subcommand;

import com.github.alwaysdarkk.currencies.cache.CurrencyLeaderboardCache;
import com.github.alwaysdarkk.currencies.config.MessagesConfig;
import com.github.alwaysdarkk.currencies.data.currency.Currency;
import com.github.alwaysdarkk.currencies.data.user.CurrencyLeaderboardUser;
import com.github.alwaysdarkk.currencies.util.NumberUtil;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import fi.sulku.hytale.TinyMsg;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CurrencyTopSubCommand extends AbstractAsyncCommand {

    private final Currency currency;
    private final CurrencyLeaderboardCache leaderboardCache;

    public CurrencyTopSubCommand(@Nonnull Currency currency, @Nonnull CurrencyLeaderboardCache leaderboardCache) {
        super("top", "Shows the player ranking for this currency");

        this.currency = currency;
        this.leaderboardCache = leaderboardCache;
    }

    @Override
    protected boolean canGeneratePermission() {
        return false;
    }

    @Nonnull
    @Override
    protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext commandContext) {
        final List<CurrencyLeaderboardUser> users = this.leaderboardCache.find(currency);
        final Message message = Message.empty();

        final String headerText = MessagesConfig.LEADERBOARD_HEADER
                .param("color", currency.getColor())
                .param("currency-name", currency.getName())
                .getAnsiMessage();
        message.insert(TinyMsg.parse(headerText));

        users.forEach(user -> {
            final String userFormatText = MessagesConfig.LEADERBOARD_USER_FORMAT
                    .param("color", currency.getColor())
                    .param("currency-name", currency.getName())
                    .param("position", String.valueOf(user.getPosition()))
                    .param("player", user.getUsername())
                    .param("amount", NumberUtil.format(user.getAmount()))
                    .getAnsiMessage();
            message.insert(TinyMsg.parse(userFormatText));
        });

        final String footerText = MessagesConfig.LEADERBOARD_FOOTER.getAnsiMessage();
        message.insert(TinyMsg.parse(footerText));

        commandContext.sendMessage(message);

        return CompletableFuture.completedFuture(null);
    }
}
