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
import org.bukkit.plugin.Plugin;
import voidpointer.spigot.framework.localemodule.Locale;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.event.WhitelistAddedEvent;
import voidpointer.spigot.voidwhitelist.message.WhitelistMessage;
import voidpointer.spigot.voidwhitelist.task.KickTask;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public final class WhitelistAddedListener implements Listener {
    @AutowiredLocale private static Locale locale;
    @NonNull private final Plugin plugin;
    @NonNull private final Map<Player, KickTask> scheduledKickTasks;

    @EventHandler(priority=EventPriority.MONITOR)
    public void onAdded(final WhitelistAddedEvent event) {
        Optional<Player> player = event.getWhitelistable().findAssociatedOnlinePlayer();
        if (!player.isPresent())
            return;

        scheduledKickTasks.remove(player.get());

        if (event.getWhitelistable().isExpirable()) {
            String kickMessage = locale.localize(WhitelistMessage.LOGIN_DISALLOWED).getRawMessage();
            KickTask kickTask = new KickTask(player.get(), kickMessage);
            kickTask.scheduleKick(plugin, event.getWhitelistable().getExpiresAt());
            scheduledKickTasks.put(player.get(), kickTask);
        }
    }

    public void register() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
}
