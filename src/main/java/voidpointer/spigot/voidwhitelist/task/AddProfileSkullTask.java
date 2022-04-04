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
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import voidpointer.spigot.voidwhitelist.gui.WhitelistGui;
import voidpointer.spigot.voidwhitelist.net.Profile;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

public final class AddProfileSkullTask extends BukkitRunnable {
    private final WhitelistGui gui;
    private final ConcurrentLinkedQueue<Profile> profiles;
    private final int profilesRequested;
    private final CountDownLatch countDownLatch;

    public AddProfileSkullTask(final WhitelistGui gui, final ConcurrentLinkedQueue<Profile> profiles, final int profilesRequested) {
        this.gui = gui;
        this.profiles = profiles;
        this.profilesRequested = profilesRequested;
        countDownLatch = new CountDownLatch(profilesRequested);
    }

    @Override public void run() {
        if (isCancelled())
            return;
        if (countDownLatch.getCount() == 0) {
            cancel();
            return;
        }
        Profile profile;
        while ((profile = profiles.peek()) != null) {
            try {
                gui.addProfile(profile);
                profiles.poll();
                countDownLatch.countDown();
            } catch (ConcurrentModificationException ignore) {}
        }
        gui.update();
    }

    @Override public synchronized void cancel() throws IllegalStateException {
        super.cancel();
        gui.stopLoading();
    }

    @NonNull
    @Override
    public synchronized BukkitTask runTask(@NonNull final Plugin plugin) throws IllegalArgumentException, IllegalStateException {
        BukkitTask bukkitTask = super.runTask(plugin);
        gui.startLoading(countDownLatch, profilesRequested);
        return bukkitTask;
    }

    @NonNull
    @Override
    public synchronized BukkitTask runTaskAsynchronously(@NonNull final Plugin plugin) throws IllegalArgumentException, IllegalStateException {
        BukkitTask bukkitTask = super.runTaskAsynchronously(plugin);
        gui.startLoading(countDownLatch, profilesRequested);
        return bukkitTask;
    }

    @NonNull
    @Override
    public synchronized BukkitTask runTaskLater(@NonNull final Plugin plugin, final long delay) throws IllegalArgumentException, IllegalStateException {
        BukkitTask bukkitTask = super.runTaskLater(plugin, delay);
        gui.startLoading(countDownLatch, profilesRequested);
        return bukkitTask;
    }

    @NonNull
    @Override
    public synchronized BukkitTask runTaskLaterAsynchronously(@NonNull final Plugin plugin, final long delay) throws IllegalArgumentException, IllegalStateException {
        BukkitTask bukkitTask = super.runTaskLaterAsynchronously(plugin, delay);
        gui.startLoading(countDownLatch, profilesRequested);
        return bukkitTask;
    }

    @NonNull
    @Override
    public synchronized BukkitTask runTaskTimer(@NonNull final Plugin plugin, final long delay, final long period) throws IllegalArgumentException, IllegalStateException {
        BukkitTask bukkitTask = super.runTaskTimer(plugin, delay, period);
        gui.startLoading(countDownLatch, profilesRequested);
        return bukkitTask;
    }

    @NonNull
    @Override
    public synchronized BukkitTask runTaskTimerAsynchronously(@NonNull final Plugin plugin, final long delay, final long period) throws IllegalArgumentException, IllegalStateException {
        BukkitTask bukkitTask = super.runTaskTimerAsynchronously(plugin, delay, period);
        gui.startLoading(countDownLatch, profilesRequested);
        return bukkitTask;
    }
}
