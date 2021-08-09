package voidpointer.spigot.voidwhitelist.listener;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import voidpointer.spigot.framework.localemodule.Locale;
import voidpointer.spigot.voidwhitelist.config.WhitelistConfig;
import voidpointer.spigot.voidwhitelist.message.WhitelistMessage;
import voidpointer.spigot.voidwhitelist.storage.NotWhitelistedException;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;
import voidpointer.spigot.voidwhitelist.task.KickTask;

import java.util.Date;
import java.util.Map;

@RequiredArgsConstructor
public final class LoginListener implements Listener {
    @NonNull private final Plugin plugin;
    @NonNull private final WhitelistService whitelistService;
    @NonNull private final Locale locale;
    @NonNull private final WhitelistConfig whitelistConfig;
    @NonNull private final Map<String, KickTask> scheduledKickTaskMap;

    @EventHandler public void onLogin(final PlayerLoginEvent event) {
        if (!whitelistConfig.isWhitelistEnabled())
            return;

        final String nickname = event.getPlayer().getName();
        final String kickReason = locale.localizeColorized(WhitelistMessage.LOGIN_DISALLOWED).getRawMessage();

        if (!whitelistService.isWhitelisted(nickname)) {
            event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, kickReason);
            return;
        }

        try {
            final Date whitelistExpiresAt = whitelistService.getExpiresAt(nickname);
            if (WhitelistService.NEVER_EXPIRES == whitelistExpiresAt)
                return;
            KickTask task = new KickTask(event.getPlayer(), kickReason);
            scheduledKickTaskMap.put(nickname, task.scheduleKick(plugin, whitelistExpiresAt));
        } catch (final NotWhitelistedException ignored) {}
    }

    public void register(final JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
}
