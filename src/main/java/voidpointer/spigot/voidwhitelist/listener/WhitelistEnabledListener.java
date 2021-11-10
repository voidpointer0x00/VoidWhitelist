package voidpointer.spigot.voidwhitelist.listener;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import voidpointer.spigot.framework.localemodule.Locale;
import voidpointer.spigot.voidwhitelist.event.WhitelistEnabledEvent;
import voidpointer.spigot.voidwhitelist.message.WhitelistMessage;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;
import voidpointer.spigot.voidwhitelist.task.KickTask;

import java.util.Map;

@RequiredArgsConstructor
public final class WhitelistEnabledListener implements Listener {
    @NonNull private final Plugin plugin;
    @NonNull private final Locale locale;
    @NonNull private final WhitelistService whitelistService;
    @NonNull private final Map<String, KickTask> scheduledKickTasks;

    /**
     * Kick all non whitelisted online players and schedule kick tasks
     *  for those who's time will expire.
     */
    @EventHandler(priority=EventPriority.HIGHEST)
    public void onEnabled(final WhitelistEnabledEvent event) {
        String kickReason = locale.localizeColorized(WhitelistMessage.LOGIN_DISALLOWED).getRawMessage();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            whitelistService.findVwPlayer(onlinePlayer.getName()).thenAccept(vwPlayer -> {
                if ((null == vwPlayer) || !vwPlayer.isAllowedToJoin()) {
                    plugin.getServer().getScheduler().runTask(plugin, () -> onlinePlayer.kickPlayer(kickReason));
                } else if (vwPlayer.isExpirable()) {
                    KickTask task = new KickTask(onlinePlayer, kickReason);
                    task.scheduleKick(plugin, vwPlayer.getExpiresAt());
                    scheduledKickTasks.put(onlinePlayer.getName(), task);
                }
            });
        }
    }

    public void register(final JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
}
