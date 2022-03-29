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
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import voidpointer.spigot.framework.localemodule.Locale;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.event.WhitelistEnabledEvent;
import voidpointer.spigot.voidwhitelist.message.WhitelistMessage;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;
import voidpointer.spigot.voidwhitelist.task.KickTask;

import java.util.Map;

@RequiredArgsConstructor
public final class WhitelistEnabledListener implements Listener {
    @AutowiredLocale private static Locale locale;
    @NonNull private final Plugin plugin;
    @NonNull private final WhitelistService whitelistService;
    @NonNull private final Map<Player, KickTask> scheduledKickTasks;

    /**
     * Kick all non whitelisted online players and schedule kick tasks
     *  for those who's time will expire.
     */
    @EventHandler(priority=EventPriority.HIGHEST)
    public void onEnabled(final WhitelistEnabledEvent event) {
        String kickReason = locale.localize(WhitelistMessage.LOGIN_DISALLOWED).getRawMessage();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            whitelistService.find(onlinePlayer.getUniqueId()).thenAcceptAsync(whitelistable -> {
                if (!whitelistable.isPresent() || !whitelistable.get().isAllowedToJoin()) {
                    plugin.getServer().getScheduler().runTask(plugin, () -> onlinePlayer.kickPlayer(kickReason));
                } else if (whitelistable.get().isExpirable()) {
                    KickTask task = new KickTask(onlinePlayer, kickReason);
                    task.scheduleKick(plugin, whitelistable.get().getExpiresAt());
                    scheduledKickTasks.put(onlinePlayer, task);
                }
            });
        }
    }

    public void register() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
}
