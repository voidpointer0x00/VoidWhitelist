/*
 *             DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
 *
 *  Copyright (C) 2022 Vasiliy Petukhov <void.pointer@ya.ru>
 *
 *  Everyone is permitted to copy and distribute verbatim or modified
 *  copies of this license document, and changing it is allowed as long
 *  as the name is changed.
 *
 *             DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
 *    TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION
 *
 *   0. You just DO WHAT THE FUCK YOU WANT TO.
 */
package voidpointer.spigot.voidwhitelist.task;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import voidpointer.spigot.voidwhitelist.Whitelistable;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public final class KickTask extends BukkitRunnable {
    @NonNull private final WeakReference<Player> playerReference;
    @NonNull private final String kickReason;

    public KickTask(final @NonNull Player player, final String kickReason) {
        this(new WeakReference<>(player), kickReason);
    }

    @Override public void run() {
        Player player = this.playerReference.get();
        if (player == null) {
            cancel();
            return;
        }

        assert player.isOnline();
        player.kickPlayer(kickReason);
    }

    public void scheduleKick(final Plugin plugin, final Date expiresAt) {
        if (!Whitelistable.isDateExpirable(expiresAt))
            throw new IllegalStateException("Cannot schedule kick task for a player without expirable date");

        long delay = expiresAt.getTime() - System.currentTimeMillis();
        if (delay < 0)
            delay = 0;

        super.runTaskLater(plugin, TimeUnit.MILLISECONDS.toSeconds(delay) * 20L);
    }
}
