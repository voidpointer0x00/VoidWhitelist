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
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import voidpointer.spigot.framework.di.Autowired;
import voidpointer.spigot.framework.localemodule.LocaleLog;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.event.WhitelistEnabledEvent;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;
import voidpointer.spigot.voidwhitelist.task.KickTaskScheduler;

@RequiredArgsConstructor
public final class WhitelistEnabledListener implements Listener {
    @AutowiredLocale private static LocaleLog locale;
    @Autowired private static KickTaskScheduler kickTaskScheduler;
    @Autowired private static WhitelistService whitelistService;
    @Autowired(mapId="plugin")
    private static Plugin plugin;

    /**
     * Kick all non whitelisted online players and schedule kick tasks
     *  for those who's time will expire.
     */
    @EventHandler(priority=EventPriority.HIGHEST)
    public void onEnabled(final WhitelistEnabledEvent event) {
        for (final Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            whitelistService.find(onlinePlayer.getUniqueId()).thenAcceptAsync(whitelistable -> {
                if (whitelistable.isPresent()) // TODO Java 16 migrate to #ifPresetOrElse()
                    kickTaskScheduler.schedule(whitelistable.get());
                else
                    kickTaskScheduler.kickSynchronously(onlinePlayer);
            }).whenComplete((res, th) -> {
                if (th != null)
                    locale.warn("Couldn't schedule a kick task on whitelist enable event", th);
            });
        }
    }

    public void register() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
}
