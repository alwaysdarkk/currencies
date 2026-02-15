package com.github.alwaysdarkk.currencies.repository.factory;

import com.github.alwaysdarkk.currencies.data.user.CurrencyUser;
import com.github.alwaysdarkk.currencies.repository.settings.RepositorySettings;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.BatchSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.JdbcSettings;
import org.hibernate.cfg.SchemaToolingSettings;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.Properties;

public class RepositoryFactory {
    public @Nonnull SessionFactory create(@Nonnull RepositorySettings settings, @Nonnull Path path) {
        final HikariConfig hikariConfig = this.getHikariConfig(settings, path);
        final Properties properties = new Properties();

        properties.put(JdbcSettings.JAKARTA_NON_JTA_DATASOURCE, new HikariDataSource(hikariConfig));

        properties.put(JdbcSettings.SHOW_SQL, "false");
        properties.put(JdbcSettings.FORMAT_SQL, "false");

        properties.put(BatchSettings.STATEMENT_BATCH_SIZE, "50");
        properties.put(BatchSettings.ORDER_INSERTS, "true");
        properties.put(BatchSettings.ORDER_UPDATES, "true");

        properties.put(SchemaToolingSettings.HBM2DDL_AUTO, "update");

        return new Configuration()
                .addProperties(properties)
                .addAnnotatedClass(CurrencyUser.class)
                .buildSessionFactory();
    }

    private @Nonnull HikariConfig getHikariConfig(@Nonnull RepositorySettings settings, @Nonnull Path path) {
        final boolean isMysql = settings.getType().equalsIgnoreCase("mysql");
        final String h2Path = path.toAbsolutePath().toString().replace('\\', '/');

        final String jdbcUrl = isMysql
                ? "jdbc:mysql://" + settings.getAddress() + "/" + settings.getDatabase()
                : "jdbc:h2:" + h2Path + "/" + settings.getFile() + ";MODE=MySQL;DB_CLOSE_DELAY=-1";

        final HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(jdbcUrl);
        hikariConfig.setUsername(settings.getUsername());
        hikariConfig.setPassword(settings.getPassword());

        hikariConfig.setMaximumPoolSize(15);
        hikariConfig.setMinimumIdle(5);
        hikariConfig.setIdleTimeout(300000);
        hikariConfig.setMaxLifetime(600000);
        hikariConfig.setConnectionTimeout(10000);

        if (isMysql) {
            hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");

            hikariConfig.addDataSourceProperty("rewriteBatchedStatements", "true");
            hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
            hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
            hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            hikariConfig.addDataSourceProperty("useServerPrepStmts", "true");

            hikariConfig.addDataSourceProperty("elideSetAutoCommits", "true");
            hikariConfig.addDataSourceProperty("maintainTimeStats", "false");
            hikariConfig.addDataSourceProperty("useLocalSessionState", "true");
            hikariConfig.addDataSourceProperty("cacheResultSetMetadata", "true");
            hikariConfig.addDataSourceProperty("cacheServerConfiguration", "true");
        } else {
            hikariConfig.setDriverClassName("org.h2.Driver");
        }

        return hikariConfig;
    }
}
