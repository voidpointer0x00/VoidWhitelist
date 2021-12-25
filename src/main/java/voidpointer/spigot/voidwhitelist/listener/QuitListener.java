package voidpointer.spigot.voidwhitelist.listener;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import voidpointer.spigot.voidwhitelist.task.KickTask;

import java.util.Map;

@RequiredArgsConstructor
public final class QuitListener implements Listener {
    @NonNull private final Map<Player, KickTask> scheduledKickTasks;

    @EventHandler public void onQuit(final PlayerQuitEvent event) {
        final String nickname = event.getPlayer().getName();
        if (scheduledKickTasks.containsKey(nickname)) {
            scheduledKickTasks.get(nickname).cancel();
            scheduledKickTasks.remove(nickname);
        }
    }

    public void register(final JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
}
