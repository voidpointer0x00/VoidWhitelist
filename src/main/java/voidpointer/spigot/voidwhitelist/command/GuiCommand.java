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
package voidpointer.spigot.voidwhitelist.command;

import org.bukkit.plugin.java.JavaPlugin;
import voidpointer.spigot.framework.di.Autowired;
import voidpointer.spigot.framework.localemodule.LocaleLog;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.Whitelistable;
import voidpointer.spigot.voidwhitelist.gui.WhitelistGui;
import voidpointer.spigot.voidwhitelist.net.Profile;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;
import voidpointer.spigot.voidwhitelist.task.AddProfileSkullTask;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import static voidpointer.spigot.voidwhitelist.net.CachedProfileFetcher.fetchProfiles;

public final class GuiCommand extends Command {
    private static final String NAME = "gui";

    @AutowiredLocale private static LocaleLog log;
    @Autowired static WhitelistService whitelistService;
    @Autowired(mapId="plugin")
    private static JavaPlugin plugin;

    public GuiCommand() {
        super(NAME);
    }

    @Override public void execute(final Args args) {
        if (!args.isPlayer()) {
            args.getSender().sendMessage("I hate console, you can't use this command! >:C");
            return;
        }
        WhitelistGui gui = new WhitelistGui();
        gui.show(args.getPlayer());
        whitelistService.findAll(gui.availableProfileSlots()).thenAcceptAsync(whitelistableSet -> {
            if (whitelistableSet.isEmpty())
                return;
            ConcurrentLinkedQueue<Profile> profiles = fetchProfiles(whitelistableSet.stream()
                    .map(Whitelistable::getUniqueId)
                    .collect(Collectors.toList()));
            new AddProfileSkullTask(gui, profiles, whitelistableSet.size())
                    .runTaskTimerAsynchronously(plugin, 0, 1L);
        });
    }
}
