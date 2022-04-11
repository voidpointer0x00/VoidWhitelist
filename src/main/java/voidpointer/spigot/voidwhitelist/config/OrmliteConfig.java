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
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import voidpointer.spigot.framework.localemodule.LocaleLog;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.storage.db.WhitelistableModel;

import java.io.File;
import java.sql.SQLException;
import java.util.UUID;

public final class OrmliteConfig {
    public static final String FILENAME = "database.yml";
    private static final String DEFAULT_DBMS = "h2";
    private static final String DEFAULT_HOST = "localhost";
    private static final String DBMS_PATH = "dbms";
    private static final String HOST_PATH = "host";
    private static final String PORT_PATH = "port";
    private static final String USER_PATH = "user";
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

    public Dao<WhitelistableModel, UUID> getWhitelistableDao() {
        try {
            return DaoManager.createDao(connectionSource, WhitelistableModel.class);
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
