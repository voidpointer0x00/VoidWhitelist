package voidpointer.spigot.voidwhitelist.task;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import voidpointer.spigot.voidwhitelist.VwPlayer;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public final class KickTask extends BukkitRunnable {
    @NonNull private final WeakReference<Player> playerReference;
    @NonNull private final String kickReason;

    public KickTask(final Player player, final String kickReason) {
        this(new WeakReference<>(player), kickReason);
    }

    @Override public void run() {
        Player player = this.playerReference.get();
        if (player == null) {
            cancel();
            return;
        }

        player.kickPlayer(kickReason);
    }

    public KickTask scheduleKick(final Plugin plugin, final Date expiresAt) {
        if (VwPlayer.NEVER_EXPIRES == expiresAt)
            throw new IllegalStateException("Cannot schedule kick task for player with expiresAt == NEVER_EXPIRES");

        long delay = expiresAt.getTime() - System.currentTimeMillis();
        if (delay < 0)
            delay = 0;

        super.runTaskLater(plugin, TimeUnit.MILLISECONDS.toSeconds(delay) * 20L);

        return this;
    }
}
