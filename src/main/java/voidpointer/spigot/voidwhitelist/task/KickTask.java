package voidpointer.spigot.voidwhitelist.task;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.nullness.qual.NonNull;
import voidpointer.spigot.framework.di.Autowired;
import voidpointer.spigot.framework.localemodule.Locale;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;

import static java.lang.System.currentTimeMillis;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static lombok.AccessLevel.PRIVATE;
import static voidpointer.spigot.voidwhitelist.message.WhitelistMessage.*;

@RequiredArgsConstructor(access=PRIVATE)
final class KickTask extends BukkitRunnable {
    @AutowiredLocale private static Locale locale;
    @Autowired(mapId="plugin") private static Plugin plugin;

    public final @NonNull Player player;
    public final long expiresAt;

    @Override public void run() {
        player.kickPlayer(locale.localize(LOGIN_DISALLOWED).getRawMessage());
    }

    public static KickTask schedule(final @NonNull Player player, final long expiresAt) {
        KickTask kickTask = new KickTask(player, expiresAt);
        final long delay = expiresAt - currentTimeMillis();
        kickTask.runTaskLater(plugin, (delay > 0 ? delay : MILLISECONDS.toSeconds(delay)) * 20L);
        return kickTask;
    }
}
