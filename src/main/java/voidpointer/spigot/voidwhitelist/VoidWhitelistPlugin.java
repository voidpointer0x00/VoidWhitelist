package voidpointer.spigot.voidwhitelist;

import org.bukkit.plugin.java.JavaPlugin;
import voidpointer.spigot.framework.localemodule.config.LocaleFileConfiguration;
import voidpointer.spigot.voidwhitelist.command.WhitelistCommand;
import voidpointer.spigot.voidwhitelist.config.WhitelistConfig;
import voidpointer.spigot.voidwhitelist.event.WhitelistDisabledEvent;
import voidpointer.spigot.voidwhitelist.event.WhitelistEnabledEvent;
import voidpointer.spigot.voidwhitelist.listener.LoginListener;
import voidpointer.spigot.voidwhitelist.listener.QuitListener;
import voidpointer.spigot.voidwhitelist.listener.WhitelistDisabledListener;
import voidpointer.spigot.voidwhitelist.listener.WhitelistEnabledListener;
import voidpointer.spigot.voidwhitelist.message.WhitelistMessage;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;
import voidpointer.spigot.voidwhitelist.storage.serial.SerialWhitelistService;
import voidpointer.spigot.voidwhitelist.task.KickTask;

import java.util.Map;
import java.util.TreeMap;

public final class VoidWhitelistPlugin extends JavaPlugin {
    private LocaleFileConfiguration locale;
    private WhitelistService whitelistService;
    private WhitelistConfig whitelistConfig;

    @Override public void onLoad() {
        this.whitelistConfig = new WhitelistConfig(this);

        locale = new LocaleFileConfiguration(this);
        locale.addDefaults(WhitelistMessage.values());
        locale.save();
    }

    @Override public void onEnable() {
        whitelistService = new SerialWhitelistService(getDataFolder());
        new WhitelistCommand(locale, whitelistService, whitelistConfig).register(this);
        registerListeners();

        if (whitelistConfig.isWhitelistEnabled())
            getServer().getPluginManager().callEvent(new WhitelistEnabledEvent());
    }

    @Override public void onDisable() {
        getServer().getPluginManager().callEvent(new WhitelistDisabledEvent());
    }

    private void registerListeners() {
        Map<String, KickTask> scheduledKickTasks = new TreeMap<>();
        new LoginListener(this, whitelistService, locale, whitelistConfig, scheduledKickTasks).register(this);
        new WhitelistEnabledListener(this, locale, whitelistService, scheduledKickTasks).register(this);
        new WhitelistDisabledListener(scheduledKickTasks).register(this);
        new QuitListener(scheduledKickTasks).register(this);
    }
}
