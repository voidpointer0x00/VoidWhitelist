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
import voidpointer.spigot.framework.di.Autowired;
import voidpointer.spigot.framework.localemodule.LocaleLog;
import voidpointer.spigot.voidwhitelist.gui.WhitelistGui;
import voidpointer.spigot.voidwhitelist.message.GuiMessage;
import voidpointer.spigot.voidwhitelist.net.Profile;

import java.util.ConcurrentModificationException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

import static java.lang.Math.round;
import static java.lang.String.format;

public final class AddProfileSkullTask extends BukkitRunnable {
    @Autowired private static LocaleLog locale;

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
        long percentage = round((profilesRequested - countDownLatch.getCount()) / (double) profilesRequested * 100);
        gui.getScreen().setTitle(format("§6VoidWhitelist %s",
                locale.localize(GuiMessage.WHITELIST_LOADING).set("percentage", percentage)));
    }

    private void stopLoading() {
        gui.getScreen().setTitle("§6VoidWhitelist");
        gui.update();
    }
}
