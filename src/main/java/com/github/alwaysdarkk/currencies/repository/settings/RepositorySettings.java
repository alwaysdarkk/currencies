package com.github.alwaysdarkk.currencies.repository.settings;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import lombok.Data;

@Data
public class RepositorySettings {
    public static final BuilderCodec<RepositorySettings> REPOSITORY_CODEC = BuilderCodec.builder(
                    RepositorySettings.class, RepositorySettings::new)
            .append(new KeyedCodec<>("Type", Codec.STRING), RepositorySettings::setType, RepositorySettings::getType)
            .add()
            .append(
                    new KeyedCodec<>("Address", Codec.STRING),
                    RepositorySettings::setAddress,
                    RepositorySettings::getAddress)
            .add()
            .append(
                    new KeyedCodec<>("Username", Codec.STRING),
                    RepositorySettings::setUsername,
                    RepositorySettings::getUsername)
            .add()
            .append(
                    new KeyedCodec<>("Password", Codec.STRING),
                    RepositorySettings::setPassword,
                    RepositorySettings::getPassword)
            .add()
            .append(
                    new KeyedCodec<>("Database", Codec.STRING),
                    RepositorySettings::setDatabase,
                    RepositorySettings::getDatabase)
            .add()
            .append(new KeyedCodec<>("File", Codec.STRING), RepositorySettings::setFile, RepositorySettings::getFile)
            .add()
            .build();

    private String type = "h2";
    private String address = "localhost:3306";
    private String username = "root";
    private String password = "";
    private String database = "test";
    private String file = "database/data";
}
