package voidpointer.spigot.voidwhitelist.task;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import voidpointer.spigot.framework.di.Autowired;
import voidpointer.spigot.voidwhitelist.config.WhitelistConfig;
import voidpointer.spigot.voidwhitelist.message.KickReason;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;

import java.util.Optional;

import static org.bukkit.Bukkit.getOnlinePlayers;

/**
 * When this task is launched it check every currently
 *  online player on the server and synchronises
 *  their scheduled kick tasks with the database.
 */
public final class DatabaseSyncTask extends BukkitRunnable {
    @Autowired private static WhitelistConfig whitelistConfig;
    @Autowired private static KickTaskScheduler kickTaskScheduler;
    @Autowired(mapId="whitelistService")
    private static WhitelistService whitelistService;

    @Override public void run() {
        if (!whitelistConfig.isWhitelistEnabled())
            return;
        for (final Player onlinePlayer : getOnlinePlayers()) {
            whitelistService.find(onlinePlayer.getUniqueId()).thenAcceptAsync(optionalWhitelistable -> {
                if (optionalWhitelistable.isEmpty()) {
                    kickTaskScheduler.kickSynchronously(onlinePlayer, KickReason.NOT_ALLOWED);
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
