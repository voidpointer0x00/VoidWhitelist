package voidpointer.spigot.voidwhitelist;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import voidpointer.spigot.framework.localemodule.config.TranslatedLocaleFileConfiguration;
import voidpointer.spigot.voidwhitelist.command.WhitelistCommand;
import voidpointer.spigot.voidwhitelist.config.WhitelistConfig;
import voidpointer.spigot.voidwhitelist.event.EventManager;
import voidpointer.spigot.voidwhitelist.event.WhitelistEnabledEvent;
import voidpointer.spigot.voidwhitelist.listener.LoginListener;
import voidpointer.spigot.voidwhitelist.listener.QuitListener;
import voidpointer.spigot.voidwhitelist.listener.WhitelistAddedListener;
import voidpointer.spigot.voidwhitelist.listener.WhitelistDisabledListener;
import voidpointer.spigot.voidwhitelist.listener.WhitelistEnabledListener;
import voidpointer.spigot.voidwhitelist.listener.WhitelistRemovedListener;
import voidpointer.spigot.voidwhitelist.message.WhitelistMessage;
import voidpointer.spigot.voidwhitelist.storage.StorageFactory;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;
import voidpointer.spigot.voidwhitelist.task.KickTask;

import java.util.Map;
import java.util.WeakHashMap;

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
        whitelistService = new StorageFactory(getDataFolder()).loadStorage(getLogger(), whitelistConfig);
        new WhitelistCommand(locale, whitelistService, whitelistConfig, eventManager).register(this);
        registerListeners();

        if (whitelistConfig.isWhitelistEnabled())
            eventManager.callAsyncEvent(new WhitelistEnabledEvent());

        // TODO online/offline mode UUID calculation
    }

    private void registerListeners() {
        Map<Player, KickTask> scheduledKickTasks = new WeakHashMap<>();
        new LoginListener(this, whitelistService, locale, whitelistConfig, scheduledKickTasks).register(this);
        new WhitelistEnabledListener(this, locale, whitelistService, scheduledKickTasks).register(this);
        new WhitelistDisabledListener(scheduledKickTasks).register(this);
        new WhitelistAddedListener(this, locale, scheduledKickTasks).register(this);
        new WhitelistRemovedListener(whitelistConfig, locale).register(this);
        new QuitListener(scheduledKickTasks).register(this);
    }
}
