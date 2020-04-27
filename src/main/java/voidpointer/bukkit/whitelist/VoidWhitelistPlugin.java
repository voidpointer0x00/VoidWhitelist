/*
 * Copyright (c) 2020 Vasiliy Petukhov <void.pointer@ya.ru>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 */
package voidpointer.bukkit.whitelist;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.NonNull;
import voidpointer.bukkit.framework.VoidPointerFrameworkPlugin;
import voidpointer.bukkit.framework.api.VoidPointerFramework;
import voidpointer.bukkit.framework.config.db.PluginDatabaseConfig;
import voidpointer.bukkit.framework.dependency.DependencyManager;
import voidpointer.bukkit.framework.event.EventManager;
import voidpointer.bukkit.framework.locale.PluginYamlLocale;
import voidpointer.bukkit.whitelist.command.WhitelistCommand;
import voidpointer.bukkit.whitelist.config.WhitelistConfig;
import voidpointer.bukkit.whitelist.db.OrmLiteWhitelistableDatabaseManager;
import voidpointer.bukkit.whitelist.db.WhitelistableDatabaseManager;
import voidpointer.bukkit.whitelist.listener.PlayerLoginListener;
import voidpointer.bukkit.whitelist.listener.PlayerQuitListener;
import voidpointer.bukkit.whitelist.listener.WhitelistAddedListener;
import voidpointer.bukkit.whitelist.listener.WhitelistRemovedListener;
import voidpointer.bukkit.whitelist.service.DaoWhitelistableService;
import voidpointer.bukkit.whitelist.service.ScheduledKickService;
import voidpointer.bukkit.whitelist.service.WhitelistableKickService;
import voidpointer.bukkit.whitelist.service.WhitelistableService;

/** @author VoidPointer aka NyanGuyMF */
public class VoidWhitelistPlugin extends JavaPlugin {
    @NonNull private WhitelistConfig pluginConfig;
    @NonNull private PluginDatabaseConfig databaseConfig;
    @NonNull private PluginYamlLocale locale;
    @NonNull private VoidPointerFramework voidPointerFramework;
    @NonNull private DependencyManager dependencyManager;
    @NonNull private WhitelistableDatabaseManager databaseManager;
    @NonNull private WhitelistableService whitelistableService;
    @NonNull private ScheduledKickService scheduledKickService;
    @NonNull private EventManager eventManager;

    private boolean hasLoadErrors = false;

    @Override public void onLoad() {
        loadVoidPointerFramework();
        loadConfiguration();
        if (!establishDatabaseConnection())
            hasLoadErrors = true;
        loadServices();
    }

    private void loadVoidPointerFramework() {
        voidPointerFramework = super.getPlugin(VoidPointerFrameworkPlugin.class).getFramework();
        dependencyManager = voidPointerFramework.getDependencyManager(this);
        eventManager = voidPointerFramework.getEventManager(this);
        voidPointerFramework.requireOrmLite(this);
    }

    private void loadConfiguration() {
        WhitelistConfig pluginConfig = new WhitelistConfig(this);
        this.pluginConfig = pluginConfig;
        pluginConfig.load().join();
        PluginYamlLocale locale = new PluginYamlLocale(this);
        locale.loadLocalized(pluginConfig.getLanguage());
        this.locale = locale;
        databaseConfig = new PluginDatabaseConfig(this);
        databaseConfig.load().join();
    }

    private boolean establishDatabaseConnection() {
        databaseManager = new OrmLiteWhitelistableDatabaseManager(databaseConfig, dependencyManager);
        return databaseManager.connect().join();
    }

    private void loadServices() {
        whitelistableService = new DaoWhitelistableService(databaseManager.getPlayerModelDao());
        scheduledKickService = new WhitelistableKickService(whitelistableService, locale, this);
    }

    @Override public void onEnable() {
        if (hasLoadErrors) {
            onHasReloadErrors();
            return;
        }

        new Thread(() -> kickOnlineButNotWhitelisted(), "WhitelistKickThread").start();
        registerListeners();
        registerCommand();
    }

    private void onHasReloadErrors() {
        super.getServer().getPluginManager().disablePlugin(this);
    }

    private void kickOnlineButNotWhitelisted() {
        scheduledKickService.kickNowOrLater(Bukkit.getOnlinePlayers());
    }

    private void registerListeners() {
        new PlayerLoginListener(
                locale,
                whitelistableService,
                scheduledKickService,
                pluginConfig
        ).register(this);
        new PlayerQuitListener(scheduledKickService).register(this);
        new WhitelistAddedListener(scheduledKickService).register(this);
        new WhitelistRemovedListener(locale).register(this);
    }

    private void registerCommand() {
        new WhitelistCommand(
            locale,
            whitelistableService,
            eventManager,
            pluginConfig
        ).register(this);
    }

    @Override public void onDisable() {
        if (databaseManager != null)
            databaseManager.close();
    }
}
