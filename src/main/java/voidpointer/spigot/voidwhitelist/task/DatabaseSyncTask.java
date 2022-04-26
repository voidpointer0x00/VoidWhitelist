package voidpointer.spigot.voidwhitelist.task;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import voidpointer.spigot.framework.di.Autowired;
import voidpointer.spigot.voidwhitelist.config.WhitelistConfig;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;

import java.util.Optional;

import static org.bukkit.Bukkit.getOnlinePlayers;

public final class DatabaseSyncTask extends BukkitRunnable {
    @Autowired private static WhitelistConfig whitelistConfig;
    @Autowired private static KickTaskScheduler kickTaskScheduler;
    @Autowired private static WhitelistService whitelistService;

    @Override public void run() {
        if (!whitelistConfig.isWhitelistEnabled())
            return;
        for (final Player onlinePlayer : getOnlinePlayers()) {
            whitelistService.find(onlinePlayer.getUniqueId()).thenAcceptAsync(optionalWhitelistable -> {
                if (!optionalWhitelistable.isPresent()) {
                    kickTaskScheduler.kickSynchronously(onlinePlayer);
                    return;
                }
                if (!optionalWhitelistable.get().isExpirable()) {
                    kickTaskScheduler.cancel(onlinePlayer);
                    return;
                }
                Optional<KickTask> kickTask = kickTaskScheduler.getTask(onlinePlayer);
                if (kickTask.isPresent()) {
                    if (kickTask.get().expiresAt != optionalWhitelistable.get().getExpiresAt().getTime())
                        kickTaskScheduler.schedule(optionalWhitelistable.get());
                } else {
                    kickTaskScheduler.schedule(optionalWhitelistable.get());
                }
            });
        }
    }
}
