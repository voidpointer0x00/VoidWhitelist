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

import java.io.File;

public final class HibernateConfig {
    public static final String FILENAME = "database.yml";

    private final Plugin plugin;
    private final File configFile;
    private YamlConfiguration config;

    public HibernateConfig(final Plugin plugin) {
        this.plugin = plugin;
        configFile = new File(plugin.getDataFolder(), FILENAME);
        load();
    }

    private void load() {
        if (!configFile.exists())
            plugin.saveResource(FILENAME, true);
        config = YamlConfiguration.loadConfiguration(configFile);
    }
}
