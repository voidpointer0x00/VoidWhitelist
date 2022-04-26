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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import voidpointer.spigot.framework.di.Autowired;
import voidpointer.spigot.voidwhitelist.event.WhitelistDisabledEvent;
import voidpointer.spigot.voidwhitelist.task.KickTaskScheduler;

@RequiredArgsConstructor
public final class WhitelistDisabledListener implements Listener {
    @Autowired private static KickTaskScheduler kickTaskScheduler;

    @EventHandler public void onDisabled(final WhitelistDisabledEvent event) {
        kickTaskScheduler.cancelAll();
    }

    public void register(final JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
}
