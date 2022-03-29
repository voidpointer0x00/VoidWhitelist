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

import org.bukkit.command.CommandSender;
import voidpointer.spigot.framework.di.Autowired;
import voidpointer.spigot.framework.localemodule.Locale;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.config.WhitelistConfig;
import voidpointer.spigot.voidwhitelist.event.EventManager;
import voidpointer.spigot.voidwhitelist.event.WhitelistEnabledEvent;
import voidpointer.spigot.voidwhitelist.message.WhitelistMessage;

import java.util.Collections;
import java.util.List;

public final class EnableCommand extends Command {
    public static final String NAME = "enable";
    public static final List<String> ALIASES = Collections.singletonList("on");
    public static final String PERMISSION = "whitelist.enable";

    @AutowiredLocale private static Locale locale;
    @Autowired private static WhitelistConfig whitelistConfig;
    @Autowired private static EventManager eventManager;

    public EnableCommand() {
        super(NAME);
        super.setPermission(PERMISSION);
    }

    @Override public void execute(final Args args) {
        whitelistConfig.enableWhitelist();
        locale.localize(WhitelistMessage.ENABLED).send(args.getSender());
        eventManager.callAsyncEvent(new WhitelistEnabledEvent());
    }

    @Override public List<String> getAliases() {
        return ALIASES;
    }

    @Override protected void onNoPermission(final CommandSender sender) {
        locale.localize(WhitelistMessage.NO_PERMISSION).send(sender);
    }
}
