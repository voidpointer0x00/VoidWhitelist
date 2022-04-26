package voidpointer.spigot.voidwhitelist.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import voidpointer.spigot.framework.di.Autowired;
import voidpointer.spigot.framework.localemodule.Locale;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.config.WhitelistConfig;
import voidpointer.spigot.voidwhitelist.event.WhitelistReconnectEvent;
import voidpointer.spigot.voidwhitelist.task.KickTaskScheduler;

import static org.bukkit.Bukkit.getOnlinePlayers;
import static voidpointer.spigot.voidwhitelist.message.WhitelistMessage.*;

public final class WhitelistReconnectListener implements Listener {
    @AutowiredLocale private static Locale locale;
    @Autowired(mapId="plugin") private static Plugin plugin;
    @Autowired private static WhitelistConfig whitelistConfig;
    @Autowired private static KickTaskScheduler kickTaskScheduler;

    @EventHandler public void onReconnect(final @NonNull WhitelistReconnectEvent event) {
        if (!whitelistConfig.isWhitelistEnabled())
            return;
        if (event.getResult().isSuccess())
            kickTaskScheduler.schedule(getOnlinePlayers());
        else
            plugin.getServer().getScheduler().runTask(plugin, this::kickAll);
    }

    private void kickAll() {
        kickTaskScheduler.cancelAll();
        final String reason = locale.localize(RECONNECT_FAIL_KICK).getRawMessage();
        getOnlinePlayers().forEach(player -> player.kickPlayer(reason));
    }

    public void register(final Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
}
