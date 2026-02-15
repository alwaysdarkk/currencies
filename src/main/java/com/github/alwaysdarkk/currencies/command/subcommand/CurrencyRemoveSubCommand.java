package com.github.alwaysdarkk.currencies.command.subcommand;

import com.github.alwaysdarkk.currencies.cache.CurrencyUserCache;
import com.github.alwaysdarkk.currencies.config.MessagesConfig;
import com.github.alwaysdarkk.currencies.data.currency.Currency;
import com.github.alwaysdarkk.currencies.data.user.CurrencyUser;
import com.github.alwaysdarkk.currencies.util.NumberUtil;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

public class CurrencyRemoveSubCommand extends AbstractAsyncCommand {

    private final Currency currency;
    private final CurrencyUserCache userCache;

    private final RequiredArg<PlayerRef> targetArg;
    private final RequiredArg<Double> amountArg;

    public CurrencyRemoveSubCommand(@Nonnull Currency currency, @Nonnull CurrencyUserCache userCache) {
        super("remove", "Remove balance of a player");

        this.currency = currency;
        this.userCache = userCache;

        this.targetArg = this.withRequiredArg("player", "target player", ArgTypes.PLAYER_REF);
        this.amountArg = this.withRequiredArg("amount", "amount to remove", ArgTypes.DOUBLE)
                .addValidator(Validators.greaterThan(0.0));

        this.requirePermission("currencies.commands.remove");
    }

    @Nonnull
    @Override
    protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext commandContext) {
        final PlayerRef playerRef = commandContext.get(this.targetArg);
        if (playerRef == null) {
            commandContext.sendMessage(MessagesConfig.PLAYER_NOT_FOUND);
            return CompletableFuture.completedFuture(null);
        }

        final CurrencyUser user = this.userCache.find(playerRef.getUuid());
        if (user == null) {
            return CompletableFuture.completedFuture(null);
        }

        final double amount = this.amountArg.get(commandContext);
        if (NumberUtil.isInvalid(amount) || amount > user.getAmount(currency)) {
            commandContext.sendMessage(MessagesConfig.INVALID_AMOUNT);
            return CompletableFuture.completedFuture(null);
        }

        user.removeAmount(currency, amount);

        final Message message = MessagesConfig.REMOVE_BALANCE.color(currency.getColor())
                .param("player", playerRef.getUsername())
                .param("amount", NumberUtil.formatWithSuffix(amount))
                .param("currency-name", currency.getName());
        commandContext.sendMessage(message);

        return CompletableFuture.completedFuture(null);
    }
}
