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

import voidpointer.spigot.framework.di.Autowired;
import voidpointer.spigot.framework.localemodule.Locale;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.command.arg.Args;
import voidpointer.spigot.voidwhitelist.config.WhitelistConfig;
import voidpointer.spigot.voidwhitelist.event.EventManager;
import voidpointer.spigot.voidwhitelist.event.WhitelistDisabledEvent;
import voidpointer.spigot.voidwhitelist.message.WhitelistMessage;

public final class DisableCommand extends Command {
    public static final String NAME = "off";
    public static final String PERMISSION = "whitelist.disable";

    @AutowiredLocale private static Locale locale;
    @Autowired private static WhitelistConfig whitelistConfig;
    @Autowired private static EventManager eventManager;

    public DisableCommand() {
        super(NAME);
        super.setPermission(PERMISSION);
    }

    @Override public void execute(final Args args) {
        whitelistConfig.disableWhitelist();
        locale.localize(WhitelistMessage.DISABLED).send(args.getSender());
        eventManager.callAsyncEvent(new WhitelistDisabledEvent());
    }
}
