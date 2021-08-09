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
import voidpointer.spigot.voidwhitelist.storage.NotWhitelistedException;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;
import voidpointer.spigot.voidwhitelist.task.KickTask;

import java.util.Date;
import java.util.Map;

@RequiredArgsConstructor
public final class WhitelistEnabledListener implements Listener {
    @NonNull private final Plugin plugin;
    @NonNull private final Locale locale;
    @NonNull private final WhitelistService whitelistService;
    @NonNull private final Map<String, KickTask> scheduledKickTasks;

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onEnabled(final WhitelistEnabledEvent event) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            String kickReason = locale.localizeColorized(WhitelistMessage.LOGIN_DISALLOWED).getRawMessage();
            try {
                final Date expiresAt = whitelistService.getExpiresAt(onlinePlayer.getName());
                if (WhitelistService.NEVER_EXPIRES == expiresAt)
                    continue;
                KickTask task = new KickTask(onlinePlayer, kickReason);
                scheduledKickTasks.put(onlinePlayer.getName(), task.scheduleKick(plugin, expiresAt));
            } catch (final NotWhitelistedException ignored) {
                onlinePlayer.kickPlayer(kickReason);
                continue;
            }
        }
    }

    public void register(final JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
}
