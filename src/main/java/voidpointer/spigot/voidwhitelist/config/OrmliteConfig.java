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

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import voidpointer.spigot.framework.localemodule.LocaleLog;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;

import java.io.File;

import static java.lang.Integer.parseInt;
import static java.util.concurrent.TimeUnit.MINUTES;

public final class OrmliteConfig {
    public static final String FILENAME = "database.yml";
    private static final String DEFAULT_DBMS = "h2";
    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_DATABASE = "whitelist";
    private static final String DEFAULT_USER = "void";
    private static final String DEFAULT_PASSWORD = "password";
    private static final int DEFAULT_SYNC = 5;
    private static final String DBMS_PATH = "dbms";
    private static final String HOST_PATH = "host";
    private static final String PORT_PATH = "port";
    private static final String DATABASE_PATH = "database";
    private static final String USER_PATH = "username";
    private static final String PASSWORD_PATH = "password";
    private static final String SYNC_PATH = "sync-every-n-minute";

    @AutowiredLocale private static LocaleLog log;

    private final Plugin plugin;
    private final File configFile;
    private YamlConfiguration config;

    public OrmliteConfig(final Plugin plugin) {
        this.plugin = plugin;
        configFile = new File(plugin.getDataFolder(), FILENAME);
        load();
    }

    public boolean reload() {
        try {
            load();
            return true;
        } catch (final Exception exception) {
            log.warn("Unable to reload database (ORMLite) configuration: {0}", exception.getMessage());
            return false;
        }
    }

    public boolean isSyncEnabled() {
        return config.getLong(SYNC_PATH, DEFAULT_SYNC) > 0;
    }

    public long getSyncTimerInTicks() {
        return MINUTES.toSeconds(config.getLong(SYNC_PATH, DEFAULT_SYNC)) * 20L;
    }

    public String getHost() {
        return config.getString(HOST_PATH, DEFAULT_HOST);
    }

    public int getPort() {
        try {
            return parseInt(config.getString(PORT_PATH, "-1"));
        } catch (final NumberFormatException numberFormatException) {
            log.warn("Invalid port: {0}", config.getString(PORT_PATH));
            return -1;
        }
    }

    public String getDatabase() {
        return config.getString(DATABASE_PATH, DEFAULT_DATABASE);
    }

    public String getUser() {
        return config.getString(USER_PATH, DEFAULT_USER);
    }

    public String getPassword() {
        return config.getString(PASSWORD_PATH, DEFAULT_PASSWORD);
    }

    public String getDbms() {
        return config.getString(DBMS_PATH, DEFAULT_DBMS);
    }

    private void load() {
        if (!configFile.exists())
            plugin.saveResource(FILENAME, true);
        config = YamlConfiguration.loadConfiguration(configFile);
    }
}
