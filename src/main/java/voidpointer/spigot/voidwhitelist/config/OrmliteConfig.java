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

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import voidpointer.spigot.framework.localemodule.LocaleLog;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.storage.db.WhitelistableModel;

import java.io.File;
import java.sql.SQLException;
import java.util.UUID;

import static java.lang.Integer.parseInt;

public final class OrmliteConfig {
    public static final String FILENAME = "database.yml";
    private static final String DEFAULT_DBMS = "h2";
    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_DATABASE = "whitelist";
    private static final String DEFAULT_USER = "void";
    private static final String DEFAULT_PASSWORD = "password";
    private static final String DBMS_PATH = "dbms";
    private static final String HOST_PATH = "host";
    private static final String PORT_PATH = "port";
    private static final String DATABASE_PATH = "database";
    private static final String USER_PATH = "username";
    private static final String PASSWORD_PATH = "password";

    @AutowiredLocale private static LocaleLog log;

    private final Plugin plugin;
    private final File configFile;
    private YamlConfiguration config;
    private ConnectionSource connectionSource;

    public OrmliteConfig(final Plugin plugin) {
        this.plugin = plugin;
        configFile = new File(plugin.getDataFolder(), FILENAME);
        load();
    }

    String getHost() {
        if (!config.isSet(HOST_PATH))
            config.set(HOST_PATH, DEFAULT_HOST);
        return config.getString(HOST_PATH);
    }

    int getPort() {
        try {
            return parseInt(config.getString(PORT_PATH, "-1"));
        } catch (final NumberFormatException numberFormatException) {
            log.warn("Invalid port: {0}", config.getString(PORT_PATH));
            return -1;
        }
    }

    String getDatabase() {
        if (!config.isSet(DATABASE_PATH))
            config.set(DATABASE_PATH, DEFAULT_DATABASE);
        return config.getString(DATABASE_PATH);
    }

    String getUser() {
        if (!config.isSet(USER_PATH))
            config.set(USER_PATH, DEFAULT_USER);
        return config.getString(USER_PATH);
    }

    String getPassword() {
        if (!config.isSet(PASSWORD_PATH))
            config.set(PASSWORD_PATH, DEFAULT_PASSWORD);
        return config.getString(PASSWORD_PATH);
    }

    public Dao<WhitelistableModel, UUID> getWhitelistableDao() {
        try {
            Dao<WhitelistableModel, UUID> dao = DaoManager.createDao(connectionSource, WhitelistableModel.class);
            TableUtils.createTableIfNotExists(connectionSource, WhitelistableModel.class);
            return dao;
        } catch (SQLException sqlException) {
            log.warn("Unable to create Dao object", sqlException);
            return null;
        }
    }

    private void load() {
        if (!configFile.exists())
            plugin.saveResource(FILENAME, true);
        config = YamlConfiguration.loadConfiguration(configFile);
        createConnectionSource();
    }

    private void createConnectionSource() {
        DbmsFactory dbmsFactory = new DbmsFactory(plugin);
        Dbms dbms = dbmsFactory.matchingOrDefault(getDbms());
        connectionSource = dbms.newConnectionSource(this);
    }

    private String getDbms() {
        if (config.isSet(DBMS_PATH))
            return config.getString(DBMS_PATH);
        config.set(DBMS_PATH, DEFAULT_DBMS);
        return DEFAULT_DBMS;
    }
}
