package voidpointer.spigot.voidwhitelist.config;

import lombok.NonNull;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class WhitelistConfig {
    private static final String WHITELIST_ENABLED_PATH = "whitelist.enabled";

    @NonNull private final JavaPlugin plugin;

    public WhitelistConfig(@NonNull final JavaPlugin plugin) {
        this.plugin = plugin;
        saveIfNotExists();
    }

    public boolean isWhitelistEnabled() {
        return plugin.getConfig().getBoolean(WHITELIST_ENABLED_PATH, false);
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
