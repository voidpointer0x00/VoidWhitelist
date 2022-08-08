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

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import voidpointer.spigot.framework.localemodule.LocaleLog;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.date.EssentialsDateParser;
import voidpointer.spigot.voidwhitelist.storage.StorageMethod;

import java.io.File;
import java.util.Date;

import static voidpointer.spigot.voidwhitelist.Whitelistable.NEVER_EXPIRES;
import static voidpointer.spigot.voidwhitelist.date.EssentialsDateParser.WRONG_DATE_FORMAT;

public final class WhitelistConfig {
    public static final String DEFAULT_LANGUAGE = "en";
    public static final StorageMethod DEFAULT_STORAGE_METHOD = StorageMethod.JSON;
    private static final String DEFAULT_AUTO_WHITELIST_TIME = "1mon";
    private static final String WHITELIST_ENABLED_PATH = "whitelist.enabled";
    private static final String UUID_MODE_PATH = "whitelist.uuid-mode";
    private static final String STORAGE_METHOD_PATH = "storage-method";
    private static final String AUTO_WHITELIST_NEW_PLAYERS = "auto-whitelist-new-players";
    private static final String AUTO_WHITELIST_TIME = "auto-whitelist-time";

    @AutowiredLocale private static LocaleLog log;
    private final JavaPlugin plugin;
    private final boolean runtimeSupportsFloodGate;

    public WhitelistConfig(final @NonNull JavaPlugin plugin) {
        this.plugin = plugin;
        saveIfNotExists();
        runtimeSupportsFloodGate = plugin.getServer().getPluginManager().getPlugin("floodgate") != null;
    }

    public void reload() {
        plugin.reloadConfig();
    }

    public boolean runtimeSupportsFloodgate() {
        return runtimeSupportsFloodGate;
    }

    public boolean autoWhitelistNewPlayers() {
        // TODO actually implement the feature
        if (!plugin.getConfig().isSet(AUTO_WHITELIST_NEW_PLAYERS)) {
            plugin.getConfig().set(AUTO_WHITELIST_NEW_PLAYERS, false);
            plugin.getConfig().addDefault(AUTO_WHITELIST_TIME, "1mon");
            plugin.saveConfig();
        }
        return plugin.getConfig().getBoolean(AUTO_WHITELIST_NEW_PLAYERS);
    }

    public Date getAutoWhitelistTime() {
        // TODO actually implement the feature
        if (!plugin.getConfig().isSet(AUTO_WHITELIST_TIME)) {
            plugin.getConfig().set(AUTO_WHITELIST_TIME, DEFAULT_AUTO_WHITELIST_TIME);
            plugin.saveConfig();
        }
        final String autoWhitelistTime = plugin.getConfig().getString(AUTO_WHITELIST_TIME);
        long autoWhitelistTimestamp = EssentialsDateParser.parseDate(autoWhitelistTime);
        if ((autoWhitelistTimestamp == WRONG_DATE_FORMAT) || (autoWhitelistTimestamp == 0)) {
            return NEVER_EXPIRES;
        }
        return new Date(autoWhitelistTimestamp);
    }

    public boolean isWhitelistEnabled() {
        return plugin.getConfig().getBoolean(WHITELIST_ENABLED_PATH, false);
    }

    public boolean isUUIDModeOnline() {
        if (plugin.getConfig().isSet(UUID_MODE_PATH)) {
            String uuidModeName = plugin.getConfig().getString(UUID_MODE_PATH);
            for (UUIDMode uuidMode : UUIDMode.values()) {
                if (uuidMode.toString().equalsIgnoreCase(uuidModeName))
                    return isOnline(uuidMode);
            }
            reportUnknown(UUID_MODE_PATH, UUIDMode.AUTO.toString().toLowerCase());
        } else {
            plugin.getConfig().set(UUID_MODE_PATH, UUIDMode.AUTO.toString().toLowerCase());
        }
        return Bukkit.getOnlineMode();
    }

    public StorageMethod getStorageMethod() {
        if (plugin.getConfig().isSet(STORAGE_METHOD_PATH)) {
            String storageMethodName = plugin.getConfig().getString(STORAGE_METHOD_PATH);
            for (StorageMethod storageMethod : StorageMethod.values()) {
                if (storageMethod.toString().equalsIgnoreCase(storageMethodName))
                    return storageMethod;
            }
            reportUnknown(STORAGE_METHOD_PATH, DEFAULT_STORAGE_METHOD.toString().toLowerCase());
        } else {
            plugin.getConfig().set(STORAGE_METHOD_PATH, DEFAULT_STORAGE_METHOD.toString().toLowerCase());
        }
        return DEFAULT_STORAGE_METHOD;
    }

    public String getLanguage() {
        return plugin.getConfig().getString("language", DEFAULT_LANGUAGE);
    }

    public void enableWhitelist() {
        plugin.getConfig().set(WHITELIST_ENABLED_PATH, true);
        plugin.saveConfig();
    }

    public void disableWhitelist() {
        plugin.getConfig().set(WHITELIST_ENABLED_PATH, false);
        plugin.saveConfig();
    }

    private void saveIfNotExists() {
        if (!new File(plugin.getDataFolder(), "config.yml").exists())
            plugin.saveResource("config.yml", true);
    }

    private boolean isOnline(UUIDMode uuidMode) {
        switch (uuidMode) {
            case ONLINE:
                return true;
            case OFFLINE:
                return false;
            case AUTO:
            default:
                return Bukkit.getOnlineMode();
        }
    }

    private void reportUnknown(final String property, final String defaultValue) {
        log.warn("Property «{0}» is not set; using default value «{1}» instead", property, defaultValue);
    }
}
