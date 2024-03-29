package voidpointer.spigot.voidwhitelist.task;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import voidpointer.spigot.framework.di.Autowired;
import voidpointer.spigot.framework.localemodule.LocaleLog;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.Whitelistable;
import voidpointer.spigot.voidwhitelist.message.KickReason;
import voidpointer.spigot.voidwhitelist.message.WhitelistMessage;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static voidpointer.spigot.voidwhitelist.message.KickReason.EXPIRED;
import static voidpointer.spigot.voidwhitelist.message.KickReason.NOT_ALLOWED;

public final class KickTaskScheduler {
    @AutowiredLocale private static LocaleLog locale;
    @Autowired(mapId="plugin")
    private static Plugin plugin;
    @Autowired(mapId="whitelistService")
    private static WhitelistService whitelistService;
    private final Map<Player, KickTask> tasks = new ConcurrentHashMap<>();

    public Optional<KickTask> getTask(final Player player) {
        return Optional.ofNullable(tasks.get(player));
    }

    public void schedule(final @NonNull Iterable<? extends Player> players) {
        for (final Player player : players) {
            if (!player.isOnline())
                continue;
            whitelistService.find(player.getUniqueId()).thenAcceptAsync(optionalWhitelistable -> {
                if (optionalWhitelistable.isPresent())
                    schedule(optionalWhitelistable.get());
                else
                    kickSynchronously(player, NOT_ALLOWED);
            }).exceptionally(thrown -> {
                locale.warn("Error on kick schedule: {0}", thrown.getMessage());
                return null;
            });
        }
    }

    public void schedule(final @NonNull Whitelistable whitelistable) {
        final Optional<Player> player = whitelistable.findAssociatedOnlinePlayer();
        if (!(player.isPresent() && whitelistable.isExpirable()))
            return;
        if (!whitelistable.isAllowedToJoin()) {
            kickSynchronously(player.get(), EXPIRED);
            return;
        }
        kickSynchronously(player.get(), whitelistable.getExpiresAt().getTime());
    }

    public void kickSynchronously(final @NonNull Player player, final KickReason kickReason) {
        cancel(player);
        plugin.getServer().getScheduler().runTask(plugin, () ->
                player.kickPlayer(locale.localize(WhitelistMessage.of(kickReason)).getRawMessage()));
    }

    private void kickSynchronously(final @NonNull Player player, final long expiresAt) {
        cancel(player);
        tasks.put(player, KickTask.schedule(player, expiresAt));
    }

    public void cancel(final @NonNull Player player) {
        final KickTask removed = tasks.remove(player);
        if (removed != null)
            removed.cancel();
    }

    public void cancelAll() {
        tasks.values().forEach(KickTask::cancel);
        tasks.clear();
    }
}
