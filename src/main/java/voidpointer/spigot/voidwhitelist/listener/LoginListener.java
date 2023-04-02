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
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import voidpointer.spigot.framework.di.Autowired;
import voidpointer.spigot.framework.localemodule.LocaleLog;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.Whitelistable;
import voidpointer.spigot.voidwhitelist.config.WhitelistConfig;
import voidpointer.spigot.voidwhitelist.message.WhitelistMessage;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;
import voidpointer.spigot.voidwhitelist.task.KickTaskScheduler;

import java.util.Optional;

import static org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST;
import static voidpointer.spigot.voidwhitelist.message.KickReason.EXPIRED;
import static voidpointer.spigot.voidwhitelist.message.KickReason.NOT_ALLOWED;
import static voidpointer.spigot.voidwhitelist.net.CachedProfileFetcher.removeCachedProfile;

@RequiredArgsConstructor
public final class LoginListener implements Listener {
    @AutowiredLocale private static LocaleLog locale;
    @Autowired private static WhitelistService whitelistService;
    @Autowired private static WhitelistConfig whitelistConfig;
    @Autowired private static KickTaskScheduler kickTaskScheduler;

    @EventHandler(priority=EventPriority.NORMAL)
    public void disallowIfNotWhitelisted(final AsyncPlayerPreLoginEvent event) {
        if (!whitelistConfig.isWhitelistEnabled())
            return;

        Optional<Whitelistable> whitelistable = whitelistService.find(event.getUniqueId()).join();

        if (!whitelistable.isPresent())
            event.disallow(KICK_WHITELIST, locale.localize(WhitelistMessage.of(NOT_ALLOWED)).getRawMessage());
        else if (!whitelistable.get().isAllowedToJoin())
            event.disallow(KICK_WHITELIST, locale.localize(WhitelistMessage.of(EXPIRED)).getRawMessage());
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void scheduleKickOnJoin(final PlayerJoinEvent event) {
        if (!whitelistConfig.isWhitelistEnabled())
            return;

        whitelistService.find(event.getPlayer().getUniqueId()).thenAcceptAsync(whitelistable -> {
            if (!whitelistable.isPresent()) {
                locale.severe("No Whitelistable entity found for a player that passed PreLogin check {0}",
                        event.getPlayer().getUniqueId());
                return;
            }
            updateWhitelistableName(event.getPlayer(), whitelistable.get());
            kickTaskScheduler.schedule(whitelistable.get());
        }).whenCompleteAsync((res, th) -> {
            if (th != null)
                locale.warn("Couldn't schedule a KickTask on join event", th);
        });
    }

    public void register(final @NonNull Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private void updateWhitelistableName(final Player player, final Whitelistable whitelistable) {
        assert player.getUniqueId().equals(whitelistable.getUniqueId())
                : "UUID of the updating player must match Whitelistable";
        whitelistable.setName(player.getName());
        whitelistService.update(whitelistable).thenAcceptAsync(updatedOptional ->
                updatedOptional.ifPresent(updated -> removeCachedProfile(updated.getUniqueId())));
    }
}
