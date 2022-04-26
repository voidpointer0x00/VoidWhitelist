package voidpointer.spigot.voidwhitelist.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import voidpointer.spigot.framework.di.Autowired;
import voidpointer.spigot.voidwhitelist.config.WhitelistConfig;
import voidpointer.spigot.voidwhitelist.event.WhitelistReloadEvent;
import voidpointer.spigot.voidwhitelist.task.KickTaskScheduler;

public final class WhitelistReloadListener implements Listener {
    @Autowired private static WhitelistConfig whitelistConfig;
    @Autowired private static KickTaskScheduler kickTaskScheduler;

    @EventHandler public void onReload(final @NonNull WhitelistReloadEvent event) {
        if (whitelistConfig.isWhitelistEnabled())
            kickTaskScheduler.schedule(Bukkit.getOnlinePlayers());
    }

    public void register(final Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
}
