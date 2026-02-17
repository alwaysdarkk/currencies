package com.github.alwaysdarkk.currencies.command.subcommand;

import com.github.alwaysdarkk.currencies.cache.CurrencyUserCache;
import com.github.alwaysdarkk.currencies.config.MessagesConfig;
import com.github.alwaysdarkk.currencies.data.currency.Currency;
import com.github.alwaysdarkk.currencies.data.user.CurrencyUser;
import com.github.alwaysdarkk.currencies.util.NumberUtil;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import fi.sulku.hytale.TinyMsg;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

public class CurrencyPaySubCommand extends AbstractAsyncCommand {

    private final Currency currency;
    private final CurrencyUserCache userCache;

    private final RequiredArg<PlayerRef> targetArg;
    private final RequiredArg<Double> amountArg;

    public CurrencyPaySubCommand(@Nonnull Currency currency, @Nonnull CurrencyUserCache userCache) {
        super("pay", "Send balance for a player");

        this.currency = currency;
        this.userCache = userCache;

        this.targetArg = this.withRequiredArg("player", "target player", ArgTypes.PLAYER_REF);
        this.amountArg = this.withRequiredArg("amount", "amount to add", ArgTypes.DOUBLE)
                .addValidator(Validators.greaterThan(0.0));
    }

    @Override
    protected boolean canGeneratePermission() {
        return false;
    }

    @Nonnull
    @Override
    protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext commandContext) {
        if (!commandContext.isPlayer()) {
            final String message = MessagesConfig.ONLY_PLAYER.getAnsiMessage();
            commandContext.sendMessage(TinyMsg.parse(message));
            return CompletableFuture.completedFuture(null);
        }

        final Player player = (Player) commandContext.sender();
        final Ref<EntityStore> playerReference = player.getReference();

        if (playerReference == null) {
            return CompletableFuture.completedFuture(null);
        }

        final Store<EntityStore> playerStore = playerReference.getStore();
        final World world = playerStore.getExternalData().getWorld();

        world.execute(() -> {
            final PlayerRef playerRef = playerStore.getComponent(playerReference, PlayerRef.getComponentType());
            if (playerRef == null) {
                return;
            }

            final CurrencyUser user = this.userCache.find(playerRef.getUuid());
            if (user == null) {
                return;
            }

            if (!currency.isPayEnable()) {
                final String message = MessagesConfig.PAY_DISABLE.getAnsiMessage();
                playerRef.sendMessage(TinyMsg.parse(message));
                return;
            }

            final PlayerRef targetRef = commandContext.get(this.targetArg);
            if (targetRef == null) {
                final String message = MessagesConfig.PLAYER_NOT_FOUND.getAnsiMessage();
                playerRef.sendMessage(TinyMsg.parse(message));
                return;
            }

            if (targetRef.getUuid() == playerRef.getUuid()) {
                final String message = MessagesConfig.SAME_PLAYER.getAnsiMessage();
                playerRef.sendMessage(TinyMsg.parse(message));
                return;
            }

            final CurrencyUser targetUser = this.userCache.find(targetRef.getUuid());
            if (targetUser == null) {
                return;
            }

            final double amount = this.amountArg.get(commandContext);
            if (NumberUtil.isInvalid(amount) || amount > user.getAmount(currency)) {
                final String message = MessagesConfig.INVALID_AMOUNT.getAnsiMessage();
                commandContext.sendMessage(TinyMsg.parse(message));
                return;
            }

            user.removeAmount(currency, amount);
            targetUser.addAmount(currency, amount);

            final String playerMessage = MessagesConfig.PLAYER_PAY
                    .param("color", currency.getColor())
                    .param("player", targetRef.getUsername())
                    .param("amount", NumberUtil.formatWithSuffix(amount))
                    .param("currency-name", currency.getName())
                    .getAnsiMessage();
            playerRef.sendMessage(TinyMsg.parse(playerMessage));

            final String targetMessage = MessagesConfig.TARGET_PAY
                    .param("color", currency.getColor())
                    .param("player", playerRef.getUsername())
                    .param("amount", NumberUtil.formatWithSuffix(amount))
                    .param("currency-name", currency.getName())
                    .getAnsiMessage();
            targetRef.sendMessage(TinyMsg.parse(targetMessage));
        });

        return CompletableFuture.completedFuture(null);
    }
}
