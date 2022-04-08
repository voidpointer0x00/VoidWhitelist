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
import org.bukkit.plugin.Plugin;
import voidpointer.spigot.framework.localemodule.config.TranslatedLocaleFile;
import voidpointer.spigot.voidwhitelist.message.GuiMessage;

public final class GuiConfig {
    public static final String FILENAME_FORMAT = "gui-%s.yml";

    private final Plugin plugin;
    private final WhitelistConfig whitelistConfig;
    @Getter private TranslatedLocaleFile localeLog;

    public GuiConfig(final Plugin plugin, final WhitelistConfig whitelistConfig) {
        this.plugin = plugin;
        this.whitelistConfig = whitelistConfig;
        load();
    }

    private void load() {
        localeLog = new TranslatedLocaleFile(plugin, whitelistConfig.getLanguage(), FILENAME_FORMAT);
        localeLog.addDefaults(GuiMessage.values());
        localeLog.save();
    }
}
