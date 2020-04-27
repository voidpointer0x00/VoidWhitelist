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
package voidpointer.bukkit.whitelist.service;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import voidpointer.bukkit.framework.locale.Locale;
import voidpointer.bukkit.framework.locale.Message;
import voidpointer.bukkit.whitelist.Whitelistable;
import voidpointer.bukkit.whitelist.message.KickMessage;

/**
 * This implementation for {@link ScheduledKickService} cached all the
 *      later kicks using {@link Cache} class.
 *
 * @author VoidPointer aka NyanGuyMF
 */
@RequiredArgsConstructor
public final class WhitelistableKickService implements ScheduledKickService {
    private static final long TICKS_PER_SECOND = 20L;
    private final Cache<Player, BukkitTask> laterKicks = CacheBuilder.newBuilder().build();

    @NonNull private final WhitelistableService whitelistService;
    @NonNull private final Locale locale;
    @NonNull private final Plugin plugin;

    @Override public void kickNowOrLater(final Collection<? extends Player> players) {
        Date now = new Date();
        players.parallelStream().map(player -> {
            Whitelistable whitelistable = whitelistService.findOrNew(player.getName());
            return new AbstractMap.SimpleEntry<>(player, whitelistable);
        }).forEach(pair -> {
            if (!pair.getValue().isWhitelisted()) {
                syncKickNowWithMessage(pair.getKey(), KickMessage.NOT_WHITELISTED);
                return;
            }
            if (!pair.getValue().hasUntil()) {
                /* the player is whitelisted forever */
                return;
            }
            kickLater(pair.getKey(), now, pair.getValue().getUntil());
        });
    }

    private void syncKickNowWithMessage(final Player player, final Message message) {
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            kickNowWithMessage(player, message);
        });
    }

    private void kickNowWithMessage(final Player player, final Message message) {
        player.kickPlayer(locale.getLocalized(message).colorize().multiline().getValue());
    }

    @Override public void kickLater(final Player player, final Date kickDate) {
        kickLater(player, new Date(), kickDate);
    }

    private void kickLater(final Player player, final Date now, final Date expiresAt) {
        long secondsDelay = getKickDelayInSeconds(now, expiresAt);
        scheduleKick(player, secondsDelay);
    }

    private long getKickDelayInSeconds(final Date now, final Date kickDate) {
        if (now.before(kickDate))
            return TimeUnit.MILLISECONDS.toSeconds(kickDate.getTime() - now.getTime());
        else
            return 0L;
    }

    private void scheduleKick(final Player player, final long secondsDelay) {
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            kickNowWithMessage(player, KickMessage.WHITELIST_EXPIRED);
        }, secondsDelay * TICKS_PER_SECOND);
    }

    @Override public void denyScheduledKick(final Player player) {
        laterKicks.asMap().remove(player);
    }
}
