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

import org.bukkit.scheduler.BukkitRunnable;
import voidpointer.spigot.voidwhitelist.gui.WhitelistGui;
import voidpointer.spigot.voidwhitelist.net.Profile;

import java.util.ConcurrentModificationException;
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
            stopLoading();
            gui.update();
            cancel();
            return;
        }
        Profile profile;
        while ((profile = profiles.poll()) != null) {
            try {
                gui.addProfile(profile);
                countDownLatch.countDown();
                updateLoading();
            } catch (ConcurrentModificationException ignore) {
                profiles.add(profile);
            }
        }
        gui.update();
    }

    public boolean isLoading() {
        return countDownLatch.getCount() != 0;
    }

    private void updateLoading() {
        gui.getScreen().setTitle(String.format("§6VoidWhitelist§8§o Loading %d%%",
                Math.round((profilesRequested - countDownLatch.getCount()) / (double) profilesRequested * 100)));
    }

    private void stopLoading() {
        gui.getScreen().setTitle("§6VoidWhitelist");
        gui.update();
    }
}
