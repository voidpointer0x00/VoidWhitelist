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
package voidpointer.spigot.voidwhitelist.listener;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import voidpointer.spigot.framework.di.Autowired;
import voidpointer.spigot.framework.localemodule.Locale;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.config.WhitelistConfig;
import voidpointer.spigot.voidwhitelist.event.WhitelistAddedEvent;
import voidpointer.spigot.voidwhitelist.task.KickTaskScheduler;

import java.util.Optional;

@RequiredArgsConstructor
public final class WhitelistAddedListener implements Listener {
    @AutowiredLocale private static Locale locale;
    @Autowired(mapId="plugin")
    private static Plugin plugin;
    @Autowired private static WhitelistConfig config;
    @Autowired private static KickTaskScheduler kickTaskScheduler;

    @EventHandler(priority=EventPriority.MONITOR)
    public void onAdded(final WhitelistAddedEvent event) {
        if (!config.isWhitelistEnabled())
            return;
        Optional<Player> player = event.getWhitelistable().findAssociatedOnlinePlayer();
        if (player.isEmpty() || !event.getWhitelistable().isExpirable())
            return;

        kickTaskScheduler.schedule(event.getWhitelistable());
    }

    public void register() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
}
