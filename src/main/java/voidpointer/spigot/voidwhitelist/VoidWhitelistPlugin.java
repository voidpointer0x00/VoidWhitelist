package voidpointer.spigot.voidwhitelist;

import org.bukkit.plugin.java.JavaPlugin;
import voidpointer.spigot.framework.localemodule.config.TranslatedLocaleFileConfiguration;
import voidpointer.spigot.voidwhitelist.command.WhitelistCommand;
import voidpointer.spigot.voidwhitelist.config.WhitelistConfig;
import voidpointer.spigot.voidwhitelist.event.EventManager;
import voidpointer.spigot.voidwhitelist.event.WhitelistAddedEvent;
import voidpointer.spigot.voidwhitelist.event.WhitelistDisabledEvent;
import voidpointer.spigot.voidwhitelist.event.WhitelistEnabledEvent;
import voidpointer.spigot.voidwhitelist.listener.*;
import voidpointer.spigot.voidwhitelist.message.WhitelistMessage;
import voidpointer.spigot.voidwhitelist.storage.StorageFactory;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;
import voidpointer.spigot.voidwhitelist.storage.serial.SerialWhitelistService;
import voidpointer.spigot.voidwhitelist.task.KickTask;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public final class VoidWhitelistPlugin extends JavaPlugin {
    private TranslatedLocaleFileConfiguration locale;
    private WhitelistService whitelistService;
    private WhitelistConfig whitelistConfig;
    private EventManager eventManager;

    @Override public void onLoad() {
        whitelistConfig = new WhitelistConfig(this);
        eventManager = new EventManager(this);

        locale = new TranslatedLocaleFileConfiguration(this, whitelistConfig.getLanguage());
        locale.addDefaults(WhitelistMessage.values());
        locale.save();
    }

    @Override public void onEnable() {
        whitelistService = new StorageFactory(getDataFolder())
                .loadStorage(getLogger(), whitelistConfig.getStorageMethod());
        new WhitelistCommand(locale, whitelistService, whitelistConfig, eventManager).register(this);
        registerListeners();

        if (whitelistConfig.isWhitelistEnabled())
            eventManager.callAsyncEvent(new WhitelistEnabledEvent());
    }

    @Override public void onDisable() {
        eventManager.callAsyncEvent(new WhitelistDisabledEvent());
    }

    private void registerListeners() {
        Map<String, KickTask> scheduledKickTasks = new ConcurrentHashMap<>();
        new LoginListener(this, whitelistService, locale, whitelistConfig, scheduledKickTasks).register(this);
        new WhitelistEnabledListener(this, locale, whitelistService, scheduledKickTasks).register(this);
        new WhitelistDisabledListener(scheduledKickTasks).register(this);
        new WhitelistAddedListener(this, locale, scheduledKickTasks).register(this);
        new WhitelistRemovedListener(whitelistConfig, locale).register(this);
        new QuitListener(scheduledKickTasks).register(this);
    }
}
