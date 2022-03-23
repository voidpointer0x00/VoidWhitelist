package voidpointer.spigot.voidwhitelist.listener;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import voidpointer.spigot.framework.localemodule.Locale;
import voidpointer.spigot.voidwhitelist.config.WhitelistConfig;
import voidpointer.spigot.voidwhitelist.event.WhitelistRemovedEvent;
import voidpointer.spigot.voidwhitelist.message.WhitelistMessage;

@RequiredArgsConstructor
public final class WhitelistRemovedListener implements Listener {
    @NonNull private final WhitelistConfig whitelistConfig;
    @NonNull private final Locale locale;

    /**
     * Kick the removed player it they're online.
     */
    @EventHandler public void onRemoved(final WhitelistRemovedEvent event) {
        if (!whitelistConfig.isWhitelistEnabled())
            return;

        Player player = Bukkit.getPlayer(event.getWhitelistable().getUniqueId());
        if ((player != null) && player.isOnline())
            player.kickPlayer(locale.localize(WhitelistMessage.YOU_WERE_REMOVED).getRawMessage());
    }

    public void register(final JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
}
