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
import voidpointer.spigot.voidwhitelist.gui.WhitelistGui;
import voidpointer.spigot.voidwhitelist.net.CachedProfileFetcher;
import voidpointer.spigot.voidwhitelist.net.Profile;
import voidpointer.spigot.voidwhitelist.task.AddProfileSkullTask;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class GuiCommand extends Command {
    private static final String NAME = "gui";
    private static final List<UUID> uuids = Arrays.asList(
            UUID.fromString("c55a15b5-896f-4c09-9c07-75ad36572aad"),
            UUID.fromString("069a79f4-44e9-4726-a5be-fca90e38aaf5"),
            UUID.fromString("61699b2e-d327-4a01-9f1e-0ea8c3f06bc6"),
            UUID.fromString("9c2ac958-5de9-45a8-8ca1-4122eb4c0b9e"),
            UUID.fromString("52ea9354-99ed-4b06-bec2-331e7c0f6f57"),
            UUID.fromString("c55a15b5-896f-4c09-9c07-75ad36572aad"),
            UUID.fromString("069a79f4-44e9-4726-a5be-fca90e38aaf5"),
            UUID.fromString("61699b2e-d327-4a01-9f1e-0ea8c3f06bc6"),
            UUID.fromString("9c2ac958-5de9-45a8-8ca1-4122eb4c0b9e"),
            UUID.fromString("52ea9354-99ed-4b06-bec2-331e7c0f6f57"),
            UUID.fromString("c55a15b5-896f-4c09-9c07-75ad36572aad"),
            UUID.fromString("069a79f4-44e9-4726-a5be-fca90e38aaf5"),
            UUID.fromString("61699b2e-d327-4a01-9f1e-0ea8c3f06bc6"),
            UUID.fromString("9c2ac958-5de9-45a8-8ca1-4122eb4c0b9e"),
            UUID.fromString("52ea9354-99ed-4b06-bec2-331e7c0f6f57"),
            UUID.fromString("c55a15b5-896f-4c09-9c07-75ad36572aad"),
            UUID.fromString("069a79f4-44e9-4726-a5be-fca90e38aaf5"),
            UUID.fromString("61699b2e-d327-4a01-9f1e-0ea8c3f06bc6"),
            UUID.fromString("9c2ac958-5de9-45a8-8ca1-4122eb4c0b9e"),
            UUID.fromString("52ea9354-99ed-4b06-bec2-331e7c0f6f57"));

    @AutowiredLocale private static LocaleLog log;
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
        ConcurrentLinkedQueue<Profile> profiles = CachedProfileFetcher.fetchProfiles(uuids);
        new AddProfileSkullTask(gui, profiles, uuids.size()).runTaskTimerAsynchronously(plugin, 0, 1L);
    }
}
