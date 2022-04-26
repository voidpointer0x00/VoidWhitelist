package voidpointer.spigot.voidwhitelist.task;

import org.bukkit.scheduler.BukkitRunnable;
import voidpointer.spigot.framework.di.Autowired;
import voidpointer.spigot.voidwhitelist.config.WhitelistConfig;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;

public final class DatabaseSyncTask extends BukkitRunnable {
    @Autowired private static WhitelistConfig whitelistConfig;
    @Autowired private static KickTaskScheduler kickTaskScheduler;
    @Autowired private static WhitelistService whitelistService;

    @Override public void run() {
        if (!whitelistConfig.isWhitelistEnabled())
            return;
        for (final KickTask kickTask : kickTaskScheduler.getTasks()) {
            whitelistService.find(kickTask.player.getUniqueId()).thenAcceptAsync(optionalWhitelistable -> {
                if (!optionalWhitelistable.isPresent()) {
                    kickTaskScheduler.kickSynchronously(kickTask.player);
                } else if (optionalWhitelistable.get().isExpirable()) {
                    if (kickTask.expiresAt != optionalWhitelistable.get().getExpiresAt().getTime())
                        kickTaskScheduler.schedule(optionalWhitelistable.get());
                }
            });
        }
    }
}
