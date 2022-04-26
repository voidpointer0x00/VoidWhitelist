package voidpointer.spigot.voidwhitelist.task;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.checkerframework.checker.nullness.qual.NonNull;
import voidpointer.spigot.framework.di.Autowired;
import voidpointer.spigot.framework.localemodule.LocaleLog;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.Whitelistable;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.System.currentTimeMillis;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static voidpointer.spigot.voidwhitelist.message.WhitelistMessage.*;

public final class KickTaskScheduler {
    @AutowiredLocale private static LocaleLog locale;
    @Autowired(mapId="plugin") private static Plugin plugin;
    @Autowired private static WhitelistService whitelistService;
    private final Map<Player, BukkitTask> tasks = new ConcurrentHashMap<>();

    public void schedule(final @NonNull Iterable<Player> players) {
        for (final Player player : players) {
            if (!player.isOnline())
                continue;
            whitelistService.find(player.getUniqueId()).thenAcceptAsync(optionalWhitelistable -> {
                if (optionalWhitelistable.isPresent())
                    schedule(optionalWhitelistable.get());
                else
                    kickSynchronously(player);
            }).exceptionally(thrown -> {
                locale.warn("Error on kick schedule: {0}", thrown.getMessage());
                return null;
            });
        }
    }

    public void schedule(final @NonNull Whitelistable whitelistable) {
        final Optional<Player> player = whitelistable.findAssociatedOnlinePlayer();
        if (!player.isPresent())
            return;
        if (!whitelistable.isExpirable())
            return;
        if (!whitelistable.isAllowedToJoin()) {
            kickSynchronously(player.get());
            return;
        }
        long delay = whitelistable.getExpiresAt().getTime() - currentTimeMillis();
        kickSynchronously(player.get(), (delay > 0) ? (MILLISECONDS.toSeconds(delay) * 20L) : 0);
    }

    public void kickSynchronously(final @NonNull Player player) {
        cancel(player);
        tasks.put(player, plugin.getServer().getScheduler().runTask(plugin, () -> kick(player)));
    }

    public void kickSynchronously(final @NonNull Player player, final long delayInTicks) {
        cancel(player);
        tasks.put(player, plugin.getServer().getScheduler().runTaskLater(plugin, () -> kick(player), delayInTicks));
    }

    private void kick(final Player player) {
        player.kickPlayer(locale.localize(LOGIN_DISALLOWED).getRawMessage());
    }

    public void cancel(final @NonNull Player player) {
        BukkitTask removed = tasks.remove(player);
        if (removed != null)
            removed.cancel();
    }

    public void cancelAll() {
        tasks.values().forEach(BukkitTask::cancel);
        tasks.clear();
    }
}
