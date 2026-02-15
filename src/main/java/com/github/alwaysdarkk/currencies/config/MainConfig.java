package com.github.alwaysdarkk.currencies.config;

import com.github.alwaysdarkk.currencies.data.currency.Currency;
import com.github.alwaysdarkk.currencies.repository.settings.RepositorySettings;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import lombok.Data;

import static com.github.alwaysdarkk.currencies.data.currency.Currency.CURRENCY_CODEC;
import static com.github.alwaysdarkk.currencies.repository.settings.RepositorySettings.REPOSITORY_CODEC;

@Data
public class MainConfig {
    public static final BuilderCodec<MainConfig> MAIN_CODEC = BuilderCodec.builder(MainConfig.class, MainConfig::new)
            .append(
                    new KeyedCodec<>("Currencies", new ArrayCodec<>(CURRENCY_CODEC, Currency[]::new)),
                    MainConfig::setCurrencies,
                    MainConfig::getCurrencies).add()
            .append(
                    new KeyedCodec<>("Repository", REPOSITORY_CODEC),
                    MainConfig::setRepositorySettings,
                    MainConfig::getRepositorySettings).add()
            .build();

    private Currency[] currencies = new Currency[0];
    private RepositorySettings repositorySettings = new RepositorySettings();
}