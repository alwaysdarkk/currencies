package com.github.alwaysdarkk.currencies;

import com.github.alwaysdarkk.currencies.cache.CurrencyCache;
import com.github.alwaysdarkk.currencies.cache.CurrencyUserCache;
import com.github.alwaysdarkk.currencies.command.CurrencyCommand;
import com.github.alwaysdarkk.currencies.config.MainConfig;
import com.github.alwaysdarkk.currencies.data.currency.Currency;
import com.github.alwaysdarkk.currencies.listener.UserTrafficListener;
import com.github.alwaysdarkk.currencies.repository.CurrencyUserRepository;
import com.github.alwaysdarkk.currencies.repository.factory.RepositoryFactory;
import com.github.alwaysdarkk.currencies.repository.settings.RepositorySettings;
import com.hypixel.hytale.event.EventRegistry;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.util.Config;
import lombok.Getter;
import org.hibernate.SessionFactory;

import javax.annotation.Nonnull;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.github.alwaysdarkk.currencies.config.MainConfig.MAIN_CODEC;

@Getter
public class CurrenciesPlugin extends JavaPlugin {

    private static CurrenciesPlugin INSTANCE;

    private final Config<MainConfig> mainConfig = this.withConfig("config", MAIN_CODEC);

    private CurrencyUserCache userCache;
    private CurrencyUserRepository userRepository;

    private CurrencyCache currencyCache;
    private ScheduledExecutorService executorService;

    public CurrenciesPlugin(@Nonnull JavaPluginInit init) {
        super(init);
    }

    public static CurrenciesPlugin getInstance() {
        return INSTANCE;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void setup() {
        INSTANCE = this;

        this.userCache = new CurrencyUserCache();
        this.currencyCache = new CurrencyCache();

        this.mainConfig.save();

        final MainConfig config = this.mainConfig.get();
        for (Currency currency : config.getCurrencies()) {
            this.currencyCache.insert(currency);
            this.getCommandRegistry().registerCommand(new CurrencyCommand(currency, this.userCache));
        }

        final RepositorySettings repositorySettings = config.getRepositorySettings();
        final RepositoryFactory repositoryFactory = new RepositoryFactory();

        final SessionFactory sessionFactory = repositoryFactory.create(repositorySettings, this.getDataDirectory());
        this.userRepository = new CurrencyUserRepository(sessionFactory);

        final EventRegistry eventRegistry = this.getEventRegistry();
        final UserTrafficListener trafficListener = new UserTrafficListener(this.userRepository, this.userCache);

        eventRegistry.registerGlobal(PlayerConnectEvent.class, trafficListener::onPlayerConnect);
        eventRegistry.registerGlobal(PlayerDisconnectEvent.class, trafficListener::onPlayerDisconnect);

        this.executorService = Executors.newSingleThreadScheduledExecutor();

        final ScheduledFuture<Void> scheduledTask = (ScheduledFuture<Void>) this.executorService.scheduleAtFixedRate(
                () -> this.userRepository.bulkInsert(this.userCache.findAll()), 1, 1, TimeUnit.SECONDS);
        this.getTaskRegistry().registerTask(scheduledTask);
    }

    @Override
    protected void shutdown() {
        this.userRepository.shutdown();
    }
}
