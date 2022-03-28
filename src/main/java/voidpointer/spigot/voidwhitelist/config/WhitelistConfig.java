package voidpointer.spigot.voidwhitelist.config;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import voidpointer.spigot.framework.localemodule.LocaleLog;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.storage.StorageMethod;

import java.io.File;

public final class WhitelistConfig {
    public static final String DEFAULT_LANGUAGE = "en";
    public static final StorageMethod DEFAULT_STORAGE_METHOD = StorageMethod.JSON;
    private static final String WHITELIST_ENABLED_PATH = "whitelist.enabled";
    private static final String UUID_MODE_PATH = "whitelist.uuid-mode";
    private static final String STORAGE_METHOD_PATH = "storage-method";

    @AutowiredLocale private static LocaleLog log;
    private final JavaPlugin plugin;

    public WhitelistConfig(final @NonNull JavaPlugin plugin) {
        this.plugin = plugin;
        saveIfNotExists();
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
