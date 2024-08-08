package com.voxelations.common;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.voxelations.common.config.ConfigService;
import com.voxelations.common.config.internal.SQLConfig;
import com.voxelations.common.event.EventBus;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class CommonModule extends AbstractModule {

    @Provides
    @Singleton
    public ConfigService provideConfigService() throws IOException {
        return new ConfigService();
    }

    @Provides
    @Singleton
    public EventBus provideEventBus() {
        return new EventBus();
    }

    @Provides
    @Singleton
    public HikariConfig provideHikariConfig(ConfigService configService) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setPoolName("mc-commons");
        hikariConfig.setMaximumPoolSize(4);
        hikariConfig.setMinimumIdle(4);
        hikariConfig.setMaxLifetime(TimeUnit.MINUTES.toMillis(30));
        hikariConfig.setConnectionTimeout(TimeUnit.SECONDS.toMillis(5));

        SQLConfig sqlConfig = configService.get(SQLConfig.class);
        hikariConfig.setJdbcUrl(sqlConfig.getJdbcUrl());
        hikariConfig.setUsername(sqlConfig.getUsername());
        hikariConfig.setPassword(sqlConfig.getPassword());

        String driverClass = switch (sqlConfig.getJdbcUrl().split(":")[1]) {
            case "sqlite" -> "org.sqlite.JDBC";
            case "postgresql" -> "org.postgresql.Driver";
            case "mysql", "mariadb" -> "com.mysql.jdbc.Driver";
            default -> throw new IllegalStateException("Unsupported database driver: " + sqlConfig.getJdbcUrl().split(":")[1]);
        };
        hikariConfig.setDriverClassName(driverClass);

        return hikariConfig;
    }

    @Provides
    @Singleton
    public HikariDataSource provideHikariDataSource(HikariConfig hikariConfig) {
        HikariDataSource hikariDataSource = new HikariDataSource(hikariConfig);
        hikariConfig.validate();
        return hikariDataSource;
    }

    @Provides
    @Singleton
    public DSLContext provideDSLContext(HikariDataSource hikariDataSource) {
        return DSL.using(hikariDataSource, switch (hikariDataSource.getDriverClassName()) {
            case "org.sqlite.JDBC" -> SQLDialect.SQLITE;
            case "org.postgresql.Driver" -> SQLDialect.POSTGRES;
            case "org.mariadb.jdbc.Driver" -> SQLDialect.MARIADB;
            case "com.mysql.cj.jdbc.Driver" -> SQLDialect.MYSQL;
            default -> throw new IllegalStateException("Unsupported database driver: " + hikariDataSource.getDriverClassName());
        });
    }
}
