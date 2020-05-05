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

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.Plugin;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import voidpointer.bukkit.framework.locale.Locale;
import voidpointer.bukkit.whitelist.Whitelistable;
import voidpointer.bukkit.whitelist.config.WhitelistConfig;
import voidpointer.bukkit.whitelist.message.KickMessage;
import voidpointer.bukkit.whitelist.service.ScheduledKickService;
import voidpointer.bukkit.whitelist.service.WhitelistableService;

/** @author VoidPointer aka NyanGuyMF */
@RequiredArgsConstructor
public final class PlayerLoginListener implements Listener {
    @NonNull private final Locale locale;
    @NonNull private final WhitelistableService whitelistableService;
    @NonNull private final ScheduledKickService scheduledKickService;
    @NonNull private final WhitelistConfig pluginConfig;

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onLogin(final PlayerLoginEvent event) {
        if (!pluginConfig.isEnabled())
            return;

        Whitelistable whitelistable = whitelistableService.findOrNew(event.getPlayer().getName());
        if (!whitelistable.isWhitelisted()) {
            event.disallow(
                PlayerLoginEvent.Result.KICK_WHITELIST,
                locale.getLocalized(KickMessage.NOT_WHITELISTED)
                        .multiline().colorize().getValue()
            );
        } else if (whitelistable.hasUntil()) {
            scheduledKickService.kickLater(event.getPlayer(), whitelistable.getUntil());
        } else {
            /* nothing to do, player is whitelisted forever. */
        }
    }

    public void register(final Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
}
