package voidpointer.spigot.voidwhitelist.config.migration;

import org.bukkit.configuration.ConfigurationSection;

public interface ConfigMigration {
    default String getMigrationName() {
        return getClass().getSimpleName();
    }

    boolean isUpToDate(final ConfigurationSection config);

    void run(final ConfigurationSection config);
}
