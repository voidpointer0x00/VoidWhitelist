/*
 *             DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
 *
 *  Copyright (C) 2022 Vasiliy Petukhov <void.pointer@ya.ru>
 *
 *  Everyone is permitted to copy and distribute verbatim or modified
 *  copies of this license document, and changing it is allowed as long
 *  as the name is changed.
 *
 *             DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
 *    TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION
 *
 *   0. You just DO WHAT THE FUCK YOU WANT TO.
 */
package voidpointer.spigot.voidwhitelist.config;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import lombok.RequiredArgsConstructor;
import voidpointer.spigot.framework.localemodule.LocaleLog;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;

import java.io.File;
import java.sql.SQLException;

import static java.lang.String.format;

@RequiredArgsConstructor
final class DbmsFactory {
    private static final int MYSQL_PORT = 3306;
    private static final int PSQL_PORT = 5432;

    @AutowiredLocale private static LocaleLog log;

    private final File dataFolder;

    public Dbms matchingOrDefault(final String dbmsName) {
        switch (dbmsName.toLowerCase()) {
            case "h2":
                return this::h2;
            case "mysql":
                return this::mysql;
            case "psql":
                return this::psql;
            default:
                log.info("Unknown DBMS named {0}, using default H2", dbmsName);
                return this::h2;
        }
    }

    private ConnectionSource h2(final OrmliteConfig ormliteConfig) {
        File h2File = new File(dataFolder, "h2");
        try {
            return new JdbcConnectionSource("jdbc:h2:" + h2File.getAbsolutePath());
        } catch (SQLException sqlException) {
            log.warn("Unable to establish database connection", sqlException);
            return null;
        }
    }

    private ConnectionSource mysql(final OrmliteConfig ormliteConfig) {
        final String host = ormliteConfig.getHost();
        final int port = ormliteConfig.getPort();
        final String database = ormliteConfig.getDatabase();
        final String user = ormliteConfig.getUser();
        final String password = ormliteConfig.getPassword();
        try {
            return new JdbcConnectionSource(mysqlConnectionUrl(host, port, database), user, password);
        } catch (final SQLException sqlException) {
            log.warn("Unable to establish database connection", sqlException);
            return null;
        }
    }

    private String mysqlConnectionUrl(final String host, final int port, final String database) {
        return format("jdbc:mysql://%s:%d/%s?autoReconnect=true", host, port != -1 ? port : MYSQL_PORT, database);
    }

    private ConnectionSource psql(final OrmliteConfig ormliteConfig) {
        final String host = ormliteConfig.getHost();
        final int port = ormliteConfig.getPort();
        final String database = ormliteConfig.getDatabase();
        final String user = ormliteConfig.getUser();
        final String password = ormliteConfig.getPassword();
        try {
            return new JdbcConnectionSource(psqlConnectionUrl(host, port, database), user, password);
        } catch (final SQLException sqlException) {
            log.warn("Unable to establish database connection", sqlException);
            return null;
        }
    }

    private String psqlConnectionUrl(final String host, final int port, final String database) {
        return format("jdbc:postgresql://%s:%d/%s?autoReconnect=true", host, port != -1 ? port : PSQL_PORT, database);
    }
}
