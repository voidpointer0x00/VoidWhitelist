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

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import voidpointer.spigot.framework.di.Autowired;
import voidpointer.spigot.framework.localemodule.LocaleLog;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.Whitelistable;
import voidpointer.spigot.voidwhitelist.config.WhitelistConfig;
import voidpointer.spigot.voidwhitelist.message.WhitelistMessage;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;
import voidpointer.spigot.voidwhitelist.task.KickTask;

import java.util.Map;
import java.util.Optional;

import static voidpointer.spigot.voidwhitelist.net.CachedProfileFetcher.removeCachedProfile;

@RequiredArgsConstructor
public final class LoginListener implements Listener {
    @AutowiredLocale private static LocaleLog locale;
    @Autowired private static WhitelistService whitelistService;
    @Autowired private static WhitelistConfig whitelistConfig;
    @NonNull private final Plugin plugin;
    @NonNull private final Map<Player, KickTask> scheduledKickTaskMap;

    @EventHandler public void onAsyncPreLogin(final AsyncPlayerPreLoginEvent event) {
        if (!whitelistConfig.isWhitelistEnabled())
            return;

        Optional<Whitelistable> whitelistable = whitelistService.find(event.getUniqueId()).join();

        if (!whitelistable.isPresent() || !whitelistable.get().isAllowedToJoin())
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, getKickReason());
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onJoin(final PlayerJoinEvent event) {
        if (!whitelistConfig.isWhitelistEnabled())
            return;

        whitelistService.find(event.getPlayer().getUniqueId()).thenAcceptAsync(whitelistable -> {
            if (!whitelistable.isPresent())
                return;
            updateWhitelistableName(event.getPlayer(), whitelistable.get());
            if (!whitelistable.get().isExpirable())
                return;

            KickTask kickTask = new KickTask(event.getPlayer(), getKickReason());
            kickTask.scheduleKick(plugin, whitelistable.get().getExpiresAt());
            scheduledKickTaskMap.put(event.getPlayer(), kickTask);
        }).whenCompleteAsync((res, th) -> {
            if (th != null)
                locale.warn("Couldn't schedule a KickTask on join event", th);
        });
    }

    public void register() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private String getKickReason() {
        return locale.localize(WhitelistMessage.LOGIN_DISALLOWED).getRawMessage();
    }

    private void updateWhitelistableName(final Player player, final Whitelistable whitelistable) {
        assert player.getUniqueId().equals(whitelistable.getUniqueId()) : "UUID of player a whitelistable must match";
        whitelistable.setName(player.getName());
        whitelistService.update(whitelistable).thenAcceptAsync(updatedOptional ->
                updatedOptional.ifPresent(updated -> removeCachedProfile(updated.getUniqueId())));
    }
}
