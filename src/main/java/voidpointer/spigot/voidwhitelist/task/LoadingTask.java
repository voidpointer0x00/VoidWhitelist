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

import lombok.RequiredArgsConstructor;
import org.bukkit.scheduler.BukkitRunnable;
import voidpointer.spigot.framework.localemodule.Locale;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.gui.WhitelistGui;

import java.util.concurrent.CountDownLatch;

@RequiredArgsConstructor
public final class LoadingTask extends BukkitRunnable {
    @AutowiredLocale private static Locale locale;
    private final WhitelistGui whitelistGui;
    private final CountDownLatch countDownLatch;
    private final double countDownStart;

    @Override public void run() {
        if (isCancelled())
            return;
        whitelistGui.getGui().setTitle(getLoadingPercentage());
        // it will be updated as soon as it gets a new item
    }

    private String getLoadingPercentage() {
        final String format = "§6VoidWhitelist§8§o Loading %d%%";
        double percentage = (countDownStart - countDownLatch.getCount()) / countDownStart * 100;
        return String.format(format, Math.round(percentage));
    }
}
