package com.github.alwaysdarkk.currencies.command;

import com.github.alwaysdarkk.currencies.cache.CurrencyUserCache;
import com.github.alwaysdarkk.currencies.command.subcommand.CurrencyAddSubCommand;
import com.github.alwaysdarkk.currencies.command.subcommand.CurrencyPaySubCommand;
import com.github.alwaysdarkk.currencies.command.subcommand.CurrencyRemoveSubCommand;
import com.github.alwaysdarkk.currencies.command.subcommand.CurrencySetSubCommand;
import com.github.alwaysdarkk.currencies.config.MessagesConfig;
import com.github.alwaysdarkk.currencies.data.currency.Currency;
import com.github.alwaysdarkk.currencies.data.user.CurrencyUser;
import com.github.alwaysdarkk.currencies.util.NumberUtil;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

public class CurrencyCommand extends AbstractAsyncCommand {

    private final Currency currency;
    private final CurrencyUserCache userCache;

    private final OptionalArg<PlayerRef> targetArg;

    public CurrencyCommand(@Nonnull Currency currency, @Nonnull CurrencyUserCache userCache) {
        super(currency.getId(), "Shows your or a player balance");

        this.currency = currency;
        this.userCache = userCache;

        this.targetArg = this.withOptionalArg("player", "target player", ArgTypes.PLAYER_REF);

        this.addSubCommand(new CurrencyAddSubCommand(currency, userCache));
        this.addSubCommand(new CurrencyRemoveSubCommand(currency, userCache));
        this.addSubCommand(new CurrencySetSubCommand(currency, userCache));
        this.addSubCommand(new CurrencyPaySubCommand(currency, userCache));
    }

    @Override
    protected boolean canGeneratePermission() {
        return false;
    }

    @Nonnull
    @Override
    protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext commandContext) {
        if (!this.targetArg.provided(commandContext)) {
            if (!commandContext.isPlayer()) {
                commandContext.sendMessage(MessagesConfig.ONLY_PLAYER);
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

                final Message message = MessagesConfig.PLAYER_BALANCE
                        .color(currency.getColor())
                        .param("balance", NumberUtil.formatWithSuffix(user.getAmount(currency)))
                        .param("currency-name", currency.getName());
                playerRef.sendMessage(message);
            });

            return CompletableFuture.completedFuture(null);
        }

        final PlayerRef playerRef = commandContext.get(this.targetArg);
        if (playerRef == null) {
            commandContext.sendMessage(MessagesConfig.PLAYER_NOT_FOUND);
            return CompletableFuture.completedFuture(null);
        }

        final CurrencyUser user = this.userCache.find(playerRef.getUuid());
        if (user == null) {
            return CompletableFuture.completedFuture(null);
        }

        final Message message = MessagesConfig.TARGET_BALANCE
                .color(currency.getColor())
                .param("player", playerRef.getUsername())
                .param("balance", NumberUtil.formatWithSuffix(user.getAmount(currency)))
                .param("currency-name", currency.getName());
        commandContext.sendMessage(message);

        return CompletableFuture.completedFuture(null);
    }
}
