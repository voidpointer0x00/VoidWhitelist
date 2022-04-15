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

import static voidpointer.spigot.voidwhitelist.message.WhitelistMessage.*;

public final class StatusCommand extends Command {
    public static final String NAME = "status";
    public static final String PERMISSION = "whitelist.status";

    @AutowiredLocale private static Locale locale;
    @Autowired private static WhitelistConfig config;

    public StatusCommand() {
        super(NAME);
        setPermission(PERMISSION);
    }

    @Override public void execute(final Args args) {
        locale.localize(config.isWhitelistEnabled() ? ENABLED : DISABLED).send(args.getSender());
    }
}
