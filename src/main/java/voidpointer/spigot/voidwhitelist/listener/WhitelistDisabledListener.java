package voidpointer.spigot.voidwhitelist.listener;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import voidpointer.spigot.voidwhitelist.event.WhitelistDisabledEvent;
import voidpointer.spigot.voidwhitelist.task.KickTask;

import java.util.Map;

@RequiredArgsConstructor
public final class WhitelistDisabledListener implements Listener {
    @NonNull private final Map<String, KickTask> scheduledKickTasks;

    @EventHandler public void onDisabled(final WhitelistDisabledEvent event) {
        scheduledKickTasks.values().stream().forEach(BukkitRunnable::cancel);
        scheduledKickTasks.clear();
    }

    public void register(final JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
}
