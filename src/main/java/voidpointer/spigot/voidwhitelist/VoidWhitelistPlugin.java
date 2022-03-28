package voidpointer.spigot.voidwhitelist;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import voidpointer.spigot.framework.localemodule.annotation.LocaleAnnotationResolver;
import voidpointer.spigot.framework.localemodule.annotation.PluginLocale;
import voidpointer.spigot.framework.localemodule.config.TranslatedLocaleFile;
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
import voidpointer.spigot.voidwhitelist.uuid.OfflineUUIDFetcher;
import voidpointer.spigot.voidwhitelist.uuid.OnlineUUIDFetcher;
import voidpointer.spigot.voidwhitelist.uuid.UUIDFetcher;

import java.util.Map;
import java.util.WeakHashMap;

public final class VoidWhitelistPlugin extends JavaPlugin {
    @PluginLocale(defaultMessages=WhitelistMessage.class)
    private static TranslatedLocaleFile locale;
    private WhitelistService whitelistService;
    private WhitelistConfig whitelistConfig;
    private EventManager eventManager;
    private UUIDFetcher uniqueIdFetcher;

    @Override public void onLoad() {
        whitelistConfig = new WhitelistConfig(this);
        eventManager = new EventManager(this);
        uniqueIdFetcher = whitelistConfig.isUUIDModeOnline()
                ? new OnlineUUIDFetcher()
                : new OfflineUUIDFetcher();

        LocaleAnnotationResolver.resolve(this);
    }

    @Override public void onEnable() {
        whitelistService = new StorageFactory(getDataFolder()).loadStorage(whitelistConfig);
        new WhitelistCommand(whitelistService, whitelistConfig, eventManager, uniqueIdFetcher)
                .register(this);
        registerListeners();

        if (whitelistConfig.isWhitelistEnabled())
            eventManager.callAsyncEvent(new WhitelistEnabledEvent());
    }

    private void registerListeners() {
        Map<Player, KickTask> scheduledKickTasks = new WeakHashMap<>();
        new LoginListener(this, whitelistService, whitelistConfig, scheduledKickTasks).register();
        new WhitelistEnabledListener(this, whitelistService, scheduledKickTasks).register();
        new WhitelistDisabledListener(scheduledKickTasks).register(this);
        new WhitelistAddedListener(this, scheduledKickTasks).register();
        new WhitelistRemovedListener(whitelistConfig).register(this);
        new QuitListener(scheduledKickTasks).register(this);
    }

    private String getLanguage() {
        return whitelistConfig.getLanguage();
    }
}
