package voidpointer.spigot.voidwhitelist.config;

import lombok.NonNull;
import org.bukkit.plugin.java.JavaPlugin;
import voidpointer.spigot.voidwhitelist.storage.StorageMethod;

import java.io.File;

public class WhitelistConfig {
    public static final String DEFAULT_LANGUAGE = "en";
    public static final StorageMethod DEFAULT_STORAGE_METHOD = StorageMethod.JSON;
    private static final String WHITELIST_ENABLED_PATH = "whitelist.enabled";
    private static final String STORAGE_METHOD_PATH = "storage-method";

    @NonNull private final JavaPlugin plugin;

    public WhitelistConfig(@NonNull final JavaPlugin plugin) {
        this.plugin = plugin;
        saveIfNotExists();
    }

    public boolean isWhitelistEnabled() {
        return plugin.getConfig().getBoolean(WHITELIST_ENABLED_PATH, false);
    }

    public StorageMethod getStorageMethod() {
        if (plugin.getConfig().isSet(STORAGE_METHOD_PATH)) {
            String storageMethodName = plugin.getConfig().getString(STORAGE_METHOD_PATH);
            for (StorageMethod storageMethod : StorageMethod.values()) {
                if (storageMethod.toString().equalsIgnoreCase(storageMethodName))
                    return storageMethod;
            }
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
        if (!new File(plugin.getDataFolder(), "config.yml").exists()) {
            plugin.saveDefaultConfig();
        }
    }
}
