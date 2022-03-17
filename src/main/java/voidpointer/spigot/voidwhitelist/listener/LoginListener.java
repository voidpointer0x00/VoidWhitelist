package voidpointer.spigot.voidwhitelist.listener;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import voidpointer.spigot.framework.localemodule.Locale;
import voidpointer.spigot.voidwhitelist.Whitelistable;
import voidpointer.spigot.voidwhitelist.config.WhitelistConfig;
import voidpointer.spigot.voidwhitelist.message.WhitelistMessage;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;
import voidpointer.spigot.voidwhitelist.task.KickTask;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public final class LoginListener implements Listener {
    @NonNull private final Plugin plugin;
    @NonNull private final WhitelistService whitelistService;
    @NonNull private final Locale locale;
    @NonNull private final WhitelistConfig whitelistConfig;
    @NonNull private final Map<Player, KickTask> scheduledKickTaskMap;

    @EventHandler public void onAsyncPreLogin(final AsyncPlayerPreLoginEvent event) {
        if (!whitelistConfig.isWhitelistEnabled())
            return;

        Optional<Whitelistable> whitelistable = whitelistService.find(event.getUniqueId()).join();

        if ((!whitelistable.isPresent()) || !whitelistable.get().isAllowedToJoin())
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, getKickReason());
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onJoin(final PlayerJoinEvent event) {
        if (!whitelistConfig.isWhitelistEnabled())
            return;

        whitelistService.find(event.getPlayer().getUniqueId()).thenAcceptAsync(whitelistable -> {
            if ((!whitelistable.isPresent()) || !whitelistable.get().isExpirable())
                return;

            KickTask kickTask = new KickTask(event.getPlayer(), getKickReason());
            kickTask.scheduleKick(plugin, whitelistable.get().getExpiresAt());
            scheduledKickTaskMap.put(event.getPlayer(), kickTask);
        });
    }

    public void register(final JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private String getKickReason() {
        return locale.localizeColorized(WhitelistMessage.LOGIN_DISALLOWED).getRawMessage();
    }

}
