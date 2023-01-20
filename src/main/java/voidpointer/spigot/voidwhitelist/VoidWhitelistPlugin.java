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
package voidpointer.spigot.voidwhitelist;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import voidpointer.spigot.framework.di.Dependency;
import voidpointer.spigot.framework.di.Injector;
import voidpointer.spigot.framework.localemodule.LocaleLog;
import voidpointer.spigot.framework.localemodule.annotation.LocaleAnnotationResolver;
import voidpointer.spigot.framework.localemodule.annotation.PluginLocale;
import voidpointer.spigot.framework.localemodule.config.TranslatedLocaleFile;
import voidpointer.spigot.voidwhitelist.command.WhitelistCommand;
import voidpointer.spigot.voidwhitelist.config.GuiConfig;
import voidpointer.spigot.voidwhitelist.config.WhitelistConfig;
import voidpointer.spigot.voidwhitelist.event.EventManager;
import voidpointer.spigot.voidwhitelist.event.WhitelistEnabledEvent;
import voidpointer.spigot.voidwhitelist.listener.LoginListener;
import voidpointer.spigot.voidwhitelist.listener.QuitListener;
import voidpointer.spigot.voidwhitelist.listener.WhitelistAddedListener;
import voidpointer.spigot.voidwhitelist.listener.WhitelistDisabledListener;
import voidpointer.spigot.voidwhitelist.listener.WhitelistEnabledListener;
import voidpointer.spigot.voidwhitelist.listener.WhitelistReconnectListener;
import voidpointer.spigot.voidwhitelist.listener.WhitelistReloadListener;
import voidpointer.spigot.voidwhitelist.listener.WhitelistRemovedListener;
import voidpointer.spigot.voidwhitelist.message.WhitelistMessage;
import voidpointer.spigot.voidwhitelist.storage.StorageFactory;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;
import voidpointer.spigot.voidwhitelist.task.KickTaskScheduler;
import voidpointer.spigot.voidwhitelist.uuid.UUIDFetchers;

import java.io.File;

@NoArgsConstructor
public final class VoidWhitelistPlugin extends JavaPlugin {
    @PluginLocale(defaultMessages=WhitelistMessage.class)
    private static TranslatedLocaleFile locale;
    @Dependency private static WhitelistConfig whitelistConfig;
    @Dependency private static LocaleLog guiLocale;
    @Dependency private static WhitelistService whitelistService;
    @Dependency private static EventManager eventManager;
    @Dependency private static StorageFactory storageFactory;
    @Dependency private static KickTaskScheduler kickTaskScheduler;
    @Dependency(id="plugin")
    private static VoidWhitelistPlugin instance;

    // for tests
    VoidWhitelistPlugin(final JavaPluginLoader loader, final PluginDescriptionFile description, final File dataFolder, final File file) {
        super(loader, description, dataFolder, file);
    }

    @Override public void onLoad() {
        instance = this;
        whitelistConfig = new WhitelistConfig(this);
        guiLocale = new GuiConfig(this, whitelistConfig).getLocaleLog();
        eventManager = new EventManager(this);
        UUIDFetchers.updateMode(whitelistConfig.isUUIDModeOnline());

        getLogger().info("Resolving locale dependencies...");
        LocaleAnnotationResolver.resolve(this);
        getLogger().info("Plugin loaded.");
    }

    @Override public void onEnable() {
        storageFactory = new StorageFactory(this);
        whitelistService = storageFactory.loadStorage(whitelistConfig.getStorageMethod());
        kickTaskScheduler = new KickTaskScheduler();
        Injector.inject(this);
        new WhitelistCommand().register(this);
        registerListeners();

        if (whitelistConfig.isWhitelistEnabled())
            eventManager.callAsyncEvent(new WhitelistEnabledEvent());
        getLogger().info("Plugin enabled.");
    }

    @Override public void onDisable() {
        whitelistService.shutdown();
    }

    /** Changes the {@link WhitelistService}, but doesn't inject it. */
    public void changeWhitelistService(final @NonNull WhitelistService whitelistService) {
        VoidWhitelistPlugin.whitelistService = whitelistService;
    }

    private void registerListeners() {
        new LoginListener().register(this);
        new WhitelistEnabledListener().register(this);
        new WhitelistDisabledListener().register(this);
        new WhitelistReloadListener().register(this);
        new WhitelistReconnectListener().register(this);
        new WhitelistAddedListener().register();
        new WhitelistRemovedListener().register(this);
        new QuitListener().register(this);
    }

    private String getLanguage() {
        return whitelistConfig.getLanguage();
    }
}
