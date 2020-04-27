/*
 * Copyright (c) 2020 Vasiliy Petukhov <void.pointer@ya.ru>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 */
package voidpointer.bukkit.whitelist.command;

import voidpointer.bukkit.framework.command.SimpleSubCommandManager;
import voidpointer.bukkit.framework.event.EventManager;
import voidpointer.bukkit.framework.locale.Locale;
import voidpointer.bukkit.whitelist.config.WhitelistConfig;
import voidpointer.bukkit.whitelist.service.WhitelistableService;
import voidpointer.bukkit.whitelist.subcommand.WhitelistAddSubCommand;
import voidpointer.bukkit.whitelist.subcommand.WhitelistDisableSubCommand;
import voidpointer.bukkit.whitelist.subcommand.WhitelistEnableCommand;
import voidpointer.bukkit.whitelist.subcommand.WhitelistInfoSubCommand;
import voidpointer.bukkit.whitelist.subcommand.WhitelistReloadSubCommand;
import voidpointer.bukkit.whitelist.subcommand.WhitelistRemoveSubCommand;

/** @author VoidPointer aka NyanGuyMF */
public final class WhitelistCommand extends SimpleSubCommandManager {
    private static final String WHITELIST_COMMAND_NAME = "whitelist";

    public WhitelistCommand(
            final Locale locale,
            final WhitelistableService whitelistableService,
            final EventManager eventManager,
            final WhitelistConfig whitelistConfig
    ) {
        super(WHITELIST_COMMAND_NAME, locale);
        super.addSubCommand(new WhitelistInfoSubCommand(locale, whitelistableService));
        super.addSubCommand(new WhitelistAddSubCommand(locale, whitelistableService, eventManager));
        super.addSubCommand(new WhitelistRemoveSubCommand(locale, whitelistableService, eventManager));
        super.addSubCommand(new WhitelistReloadSubCommand(locale, whitelistConfig));
        super.addSubCommand(new WhitelistEnableCommand(locale, whitelistConfig));
        super.addSubCommand(new WhitelistDisableSubCommand(locale, whitelistConfig));
    }
}
