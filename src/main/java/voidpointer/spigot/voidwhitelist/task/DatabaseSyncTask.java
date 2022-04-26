package voidpointer.spigot.voidwhitelist.task;

import org.bukkit.scheduler.BukkitRunnable;
import voidpointer.spigot.framework.di.Autowired;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;

public final class DatabaseSyncTask extends BukkitRunnable {
    @Autowired private static WhitelistService whitelistService;

    @Override public void run() {

    }
}
