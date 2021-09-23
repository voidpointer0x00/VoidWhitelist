package voidpointer.spigot.voidwhitelist.config;

import lombok.NonNull;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class WhitelistConfig {
    public static final String DEFAULT_LANGUAGE = "en";
    private static final String WHITELIST_ENABLED_PATH = "whitelist.enabled";

    @NonNull private final JavaPlugin plugin;

    public WhitelistConfig(@NonNull final JavaPlugin plugin) {
        this.plugin = plugin;
        saveIfNotExists();
    }

    public boolean isWhitelistEnabled() {
        return plugin.getConfig().getBoolean(WHITELIST_ENABLED_PATH, false);
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
