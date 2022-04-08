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

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import voidpointer.spigot.framework.di.Autowired;
import voidpointer.spigot.framework.localemodule.LocaleLog;
import voidpointer.spigot.framework.localemodule.config.LocaleSection;
import voidpointer.spigot.voidwhitelist.message.GuiMessage;

import java.io.File;
import java.io.IOException;

import static java.util.Objects.requireNonNull;
import static voidpointer.spigot.framework.localemodule.config.LocaleFile.MESSAGES_PATH;

public final class GuiConfig {
    public static final String FILENAME = "gui.yml";

    @Autowired(mapId="plugin")
    private static Plugin plugin;
    private final File configFile;
    private YamlConfiguration config;
    @Getter private LocaleLog localeLog;

    public GuiConfig() {
        configFile = new File(plugin.getDataFolder(), FILENAME);
        load();
    }

    private void load() {
        if (!configFile.exists())
            plugin.saveResource(FILENAME, true);
        assert configFile.exists() : FILENAME + " must exist";
        config = YamlConfiguration.loadConfiguration(configFile);
        if (!config.isSet(MESSAGES_PATH))
            config.createSection(MESSAGES_PATH);
        ConfigurationSection messagesSection = config.getConfigurationSection(MESSAGES_PATH);
        requireNonNull(messagesSection, MESSAGES_PATH + " config section must exist in " + FILENAME);
        localeLog = new LocaleSection(plugin, messagesSection);
        localeLog.addDefaults(GuiMessage.values());
        save();
    }

    private void save() {
        try {
            save0();
        } catch (final IOException ioException) {
            localeLog.warn("Unable to save " + FILENAME, ioException);
        }
    }

    private void save0() throws IOException {
        config.save(configFile);
    }
}
