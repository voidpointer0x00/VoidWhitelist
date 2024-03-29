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
import voidpointer.spigot.voidwhitelist.config.migration.WhitelistConfigMigrationRepository;
import voidpointer.spigot.voidwhitelist.date.Duration;
import voidpointer.spigot.voidwhitelist.storage.StorageMethod;

import java.io.File;
import java.util.Date;
import java.util.Optional;

public final class WhitelistConfig {
    public static final String DEFAULT_LANGUAGE = "en";
    public static final StorageMethod DEFAULT_STORAGE_METHOD = StorageMethod.JSON;
    public static final String AUTO_WL_PATH = "auto-whitelist";
    public static final String AUTO_WL_ENABLED_PATH = "auto-whitelist.enabled";
    public static final String AUTO_WL_DURATION_PATH = "auto-whitelist.duration";
    public static final String AUTO_WL_LIMIT_PATH = "auto-whitelist.limit";
    public static final String AUTO_WL_STRATEGY_PATH = "auto-whitelist.strategy";
    private static final String WHITELIST_ENABLED_PATH = "whitelist.enabled";
    private static final String UUID_MODE_PATH = "whitelist.uuid-mode";
    private static final String STORAGE_METHOD_PATH = "storage-method";

    @AutowiredLocale private static LocaleLog log;
    private final JavaPlugin plugin;

    public WhitelistConfig(final @NonNull JavaPlugin plugin) {
        this.plugin = plugin;
        saveIfNotExists();
    }

    /**
     * Detects if newly-featured configuration properties are missing
     *  and runs an appropriate migration for them.
     */
    public void runMigrations() {
        WhitelistConfigMigrationRepository.getAllMigrations().forEach(migration -> {
            if (!migration.isUpToDate(plugin.getConfig())) {
                /* this method is called at plugin load stage, in which LocaleLog is not
                 *  initialized yet, so we have report any information this way. */
                plugin.getLogger().info("Running config migration " + migration.getMigrationName());
                migration.run(plugin.getConfig());
                plugin.saveConfig();
            }
        });
    }

    public void reload() {
        plugin.reloadConfig();
    }

    public boolean isWhitelistEnabled() {
        return plugin.getConfig().getBoolean(WHITELIST_ENABLED_PATH, false);
    }

    public boolean isAutoWhitelistEnabled() {
        return plugin.getConfig().getBoolean(AUTO_WL_ENABLED_PATH, false);
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
            final String storageMethodName = plugin.getConfig().getString(STORAGE_METHOD_PATH);
            if ("serial".equalsIgnoreCase(storageMethodName))
                /* backwards compatibility (though I can hardly imagine someone actually used serial) */ {
                return StorageMethod.JSON;
            }
            for (StorageMethod storageMethod : StorageMethod.values())
                if (storageMethod.toString().equalsIgnoreCase(storageMethodName))
                    return storageMethod;
            reportUnknown(STORAGE_METHOD_PATH, DEFAULT_STORAGE_METHOD.toString().toLowerCase());
        } else {
            plugin.getConfig().set(STORAGE_METHOD_PATH, DEFAULT_STORAGE_METHOD.toString().toLowerCase());
        }
        return DEFAULT_STORAGE_METHOD;
    }

    public String getLanguage() {
        return plugin.getConfig().getString("language", DEFAULT_LANGUAGE);
    }

    public StrategyPredicate getStrategyPredicate() {
        return StrategyPredicate.getOrDefault(plugin.getConfig().getString(AUTO_WL_STRATEGY_PATH, "all"));
    }

    public int getAutoLimit() {
        return plugin.getConfig().getInt(AUTO_WL_LIMIT_PATH, 0);
    }

    /** @return previous limit. */
    public int setAutoLimit(final int newLimit) {
        final int previousLimit = plugin.getConfig().getInt(AUTO_WL_LIMIT_PATH);
        plugin.getConfig().set(AUTO_WL_LIMIT_PATH, newLimit);
        plugin.saveConfig();
        return previousLimit;
    }

    public Optional<Date> getAutoDuration() {
        return Duration.ofEssentialsDate(getRawAutoDuration());
    }

    /** @return previous duration. */
    public String setAutoDuration(final String newDuration) {
        final String previousDuration = getRawAutoDuration();
        plugin.getConfig().set(AUTO_WL_DURATION_PATH, newDuration);
        plugin.saveConfig();
        return previousDuration;
    }

    public String getRawAutoDuration() {
        return plugin.getConfig().getString(AUTO_WL_DURATION_PATH, "7d");
    }

    public void enableWhitelist() {
        plugin.getConfig().set(WHITELIST_ENABLED_PATH, true);
        plugin.saveConfig();
    }

    public void disableWhitelist() {
        plugin.getConfig().set(WHITELIST_ENABLED_PATH, false);
        plugin.saveConfig();
    }

    public void enableAutoWhitelist() {
        plugin.getConfig().set(AUTO_WL_ENABLED_PATH, true);
        plugin.saveConfig();
    }

    public void disableAutoWhitelist() {
        plugin.getConfig().set(AUTO_WL_ENABLED_PATH, false);
        plugin.saveConfig();
    }

    private void saveIfNotExists() {
        if (!new File(plugin.getDataFolder(), "config.yml").exists())
            plugin.saveResource("config.yml", true);
    }

    private boolean isOnline(UUIDMode uuidMode) {
        return switch (uuidMode) {
            case ONLINE -> true;
            case OFFLINE -> false;
            default -> Bukkit.getOnlineMode();
        };
    }

    private void reportUnknown(final String property, final String defaultValue) {
        log.warn("Property «{0}» is not set; using default value «{1}» instead", property, defaultValue);
    }

    public StrategyPredicate setAutoWhitelistStrategy(final StrategyPredicate strategy) {
        final StrategyPredicate previousStrategy = getStrategyPredicate();
        plugin.getConfig().set(AUTO_WL_STRATEGY_PATH, strategy.getName());
        plugin.saveConfig();
        return previousStrategy;
    }
}
