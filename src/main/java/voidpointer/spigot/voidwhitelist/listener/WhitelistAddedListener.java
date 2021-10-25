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
import voidpointer.spigot.voidwhitelist.VwPlayer;
import voidpointer.spigot.voidwhitelist.event.WhitelistAddedEvent;
import voidpointer.spigot.voidwhitelist.message.WhitelistMessage;
import voidpointer.spigot.voidwhitelist.task.KickTask;

import java.util.Map;

@RequiredArgsConstructor
public final class WhitelistAddedListener implements Listener {
    @NonNull private final Plugin plugin;
    @NonNull private final Locale locale;
    @NonNull private final Map<String, KickTask> scheduledKickTasks;

    @EventHandler(priority=EventPriority.MONITOR)
    public void onAdded(final WhitelistAddedEvent event) {
        if (scheduledKickTasks.containsKey(event.getNickname()))
            scheduledKickTasks.remove(event.getNickname()).cancel();

        Player player = Bukkit.getPlayer(event.getNickname());
        if ((null != player) && player.isOnline() && (VwPlayer.NEVER_EXPIRES != event.getExpiresAt())) {
            String kickMessage = locale.localizeColorized(WhitelistMessage.LOGIN_DISALLOWED).getRawMessage();
            KickTask kickTask = new KickTask(player, kickMessage);
            kickTask.scheduleKick(plugin, event.getExpiresAt());
            scheduledKickTasks.put(event.getNickname(), kickTask);
        }
    }

    public void register(final Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
}
