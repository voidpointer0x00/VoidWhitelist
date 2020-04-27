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
package voidpointer.bukkit.whitelist.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import voidpointer.bukkit.whitelist.service.ScheduledKickService;

/** @author VoidPointer aka NyanGuyMF */
@RequiredArgsConstructor
public final class PlayerQuitListener implements Listener {
    @NonNull private final ScheduledKickService scheduledKickService;

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onDisconnected(final PlayerQuitEvent event) {
        denyScheduledKick(event.getPlayer());
    }

    private void denyScheduledKick(final Player player) {
        scheduledKickService.denyScheduledKick(player);
    }

    public void register(final Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
}
