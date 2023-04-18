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
import lombok.val;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import voidpointer.spigot.framework.di.Autowired;
import voidpointer.spigot.framework.localemodule.LocaleLog;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.TimesAutoWhitelisted;
import voidpointer.spigot.voidwhitelist.Whitelistable;
import voidpointer.spigot.voidwhitelist.config.WhitelistConfig;
import voidpointer.spigot.voidwhitelist.message.KickReason;
import voidpointer.spigot.voidwhitelist.message.WhitelistMessage;
import voidpointer.spigot.voidwhitelist.storage.AutoWhitelistService;
import voidpointer.spigot.voidwhitelist.task.KickTaskScheduler;

import java.util.Date;
import java.util.Optional;

import static org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST;
import static voidpointer.spigot.voidwhitelist.message.KickReason.EXPIRED;
import static voidpointer.spigot.voidwhitelist.message.KickReason.NOT_ALLOWED;
import static voidpointer.spigot.voidwhitelist.net.CachedProfileFetcher.removeCachedProfile;

@RequiredArgsConstructor
public final class LoginListener implements Listener {
    @AutowiredLocale private static LocaleLog locale;
    @Autowired(mapId="whitelistService")
    private static AutoWhitelistService autoWhitelistService;
    @Autowired private static WhitelistConfig whitelistConfig;
    @Autowired private static KickTaskScheduler kickTaskScheduler;

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onAsyncPreLogin(final AsyncPlayerPreLoginEvent event) {
        /*  This listener disallows login if the connecting user is not on the whitelist,
         * and due to either configuration or exceeding auto-whitelist.max-repeats limit
         * we cannot automatically add the user to the whitelist. */
        if (!whitelistConfig.isWhitelistEnabled())
            return;

        Optional<Whitelistable> user = autoWhitelistService.find(event.getUniqueId()).join();
        Optional<KickReason> optionalKickReason = getKickReasonFor(user.orElse(null));
        if (!optionalKickReason.isPresent())
            return;

        if (!whitelistConfig.isAutoWhitelistEnabled()
                || whitelistConfig.getStrategyPredicate().negate().test(event.getUniqueId())
                || (whitelistConfig.getAutoLimit() == 0)) {
            disallow(event, optionalKickReason.get());
            return;
        }
        val timesAutoWhitelisted = autoWhitelistService.getTimesAutoWhitelisted(event.getUniqueId()).join();
        if (user.isPresent() && timesAutoWhitelisted.isPresent()
                && timesAutoWhitelisted.get().isExceeded(whitelistConfig.getAutoLimit())) {
                disallow(event, optionalKickReason.get());
                return;
        }
        final Optional<Date> autoDuration = whitelistConfig.getAutoDuration();
        if (!autoDuration.isPresent()) {
            locale.warn("Could not apply auto-whitelist to user {0} because of invalid duration {1}",
                    event.getUniqueId(), whitelistConfig.getRawAutoDuration());
            disallow(event, optionalKickReason.get());
            return;
        }
        autoWhitelistService.add(event.getUniqueId(), event.getName(), autoDuration.get(),
                timesAutoWhitelisted.map(TimesAutoWhitelisted::get).orElse(0) + 1).thenApplyAsync(whitelistable -> {
            if (!whitelistable.isPresent()) {
                locale.warn("Automatic whitelisting of {0} failed", event.getUniqueId());
                return whitelistable;
            }
            locale.info("Automatically whitelisted {0} ({1}) until {2}", event.getUniqueId(), event.getName(),
                    autoDuration.get());
            return whitelistable;
        });
    }

    private Optional<KickReason> getKickReasonFor(final @Nullable Whitelistable whitelistable) {
        if (whitelistable == null)
            return Optional.of(NOT_ALLOWED);
        else if (!whitelistable.isAllowedToJoin())
            return Optional.of(EXPIRED);
        else
            return Optional.empty();
    }

    private void disallow(final AsyncPlayerPreLoginEvent event, final KickReason reason) {
        event.disallow(KICK_WHITELIST, locale.localize(WhitelistMessage.of(reason)).getRawMessage());
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void scheduleKickOnJoin(final PlayerJoinEvent event) {
        if (!whitelistConfig.isWhitelistEnabled())
            return;

        autoWhitelistService.find(event.getPlayer().getUniqueId()).thenAcceptAsync(whitelistable -> {
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
        autoWhitelistService.update(whitelistable).thenAcceptAsync(updatedOptional ->
                updatedOptional.ifPresent(updated -> removeCachedProfile(updated.getUniqueId())));
    }
}
