package voidpointer.spigot.voidwhitelist.task;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import voidpointer.spigot.framework.di.Autowired;
import voidpointer.spigot.framework.localemodule.Locale;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.Whitelistable;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.System.currentTimeMillis;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static voidpointer.spigot.voidwhitelist.message.WhitelistMessage.*;

public final class KickTaskScheduler {
    @AutowiredLocale private static Locale locale;
    @Autowired private static Plugin plugin;
    private final Map<Player, KickTask> tasks = new ConcurrentHashMap<>();

    public void schedule(final @NonNull Iterable<Whitelistable> iterableWhitelistable) {
        for (final Whitelistable whitelistable : iterableWhitelistable)
            schedule(whitelistable);
    }

    public void schedule(final @NonNull Whitelistable whitelistable) {
        if (!whitelistable.isExpirable())
            return;
        final Optional<Player> player = whitelistable.findAssociatedOnlinePlayer();
        if (!player.isPresent())
            return;
        long delay = whitelistable.getExpiresAt().getTime() - currentTimeMillis();
        delay = (delay < 0) ? 0 : delay;
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> kick(player.get()),
                MILLISECONDS.toSeconds(delay) * 20L);
    }

    private void kick(final Player player) {
        player.kickPlayer(locale.localize(LOGIN_DISALLOWED).getRawMessage());
    }

    public void cancel(final @NonNull Player player) {
        KickTask removed = tasks.remove(player);
        if (removed != null)
            removed.cancel();
    }

    public void cancelAll() {
        tasks.values().forEach(KickTask::cancel);
        tasks.clear();
    }
}
