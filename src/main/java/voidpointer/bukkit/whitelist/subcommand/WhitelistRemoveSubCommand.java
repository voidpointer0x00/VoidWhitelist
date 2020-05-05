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
package voidpointer.bukkit.whitelist.subcommand;

import java.util.List;
import java.util.stream.Collectors;

import lombok.NonNull;
import voidpointer.bukkit.framework.command.CommandArgs;
import voidpointer.bukkit.framework.command.SimpleCommand;
import voidpointer.bukkit.framework.command.validator.MinArgsValidator;
import voidpointer.bukkit.framework.command.validator.PermissionValidator;
import voidpointer.bukkit.framework.event.EventManager;
import voidpointer.bukkit.framework.locale.Locale;
import voidpointer.bukkit.whitelist.Whitelistable;
import voidpointer.bukkit.whitelist.event.WhitelistRemovedEvent;
import voidpointer.bukkit.whitelist.message.WhitelistMessage;
import voidpointer.bukkit.whitelist.service.WhitelistableService;

/** @author VoidPointer aka NyanGuyMF */
public final class WhitelistRemoveSubCommand extends SimpleCommand {
    private static final String REMOVE_COMMNAD_NAME = "remove";
    private static final String REMOVE_COMMAND_DISPLAY_NAME = "whitelist remove";
    private static final String REMOVE_COMMAND_PERMISSION = "voidwhitelist.remove";
    private static final int PLAYER_NAME_INDEX = 0;
    private static final int REQUIRED_ARGS = 1;

    @NonNull private final Locale locale;
    @NonNull private final WhitelistableService whitelistableService;
    @NonNull private final EventManager eventManager;

    public WhitelistRemoveSubCommand(
            final Locale locale,
            final WhitelistableService whitelistableService,
            final EventManager eventManager
    ) {
        super(REMOVE_COMMNAD_NAME);
        super.addValidator(new PermissionValidator(locale));
        super.addValidator(new MinArgsValidator(locale, REQUIRED_ARGS));
        this.locale = locale;
        this.whitelistableService = whitelistableService;
        this.eventManager = eventManager;
    }

    @Override public boolean execute(final CommandArgs args) {
        final String whitelistableName = args.get(PLAYER_NAME_INDEX);
        Whitelistable whitelistable = whitelistableService.findByName(whitelistableName);
        if (whitelistable == null) {
            onWhitelistableNotFound(whitelistableName, args);
            return true;
        }

        whitelistable.setWhitelisted(false);
        whitelistableService.update(whitelistable);
        onWhitelistableRemoved(whitelistableName, args);

        eventManager.callEvent(new WhitelistRemovedEvent(args.getSender(), whitelistableName));

        return true;
    }

    private void onWhitelistableNotFound(final String targetName, final CommandArgs args) {
        locale.getLocalized(WhitelistMessage.PLAYER_NOT_WHITELISTED)
                .colorize()
                .set("player", targetName)
                .send(args.getSender());
    }

    private void onWhitelistableRemoved(final String targetName, final CommandArgs args) {
        locale.getLocalized(WhitelistMessage.PLAYER_REMOVED)
                .colorize()
                .set("player", targetName)
                .send(args.getSender());
    }

    @Override public List<String> complete(final CommandArgs args) {
        return whitelistableService.findAll().parallelStream()
                .filter(whitelistable -> whitelistable.isWhitelistedAndNotExpired())
                .map(Whitelistable::getName)
                .collect(Collectors.toList());
    }

    @Override public String getPermission() {
        return REMOVE_COMMAND_PERMISSION;
    }

    @Override public String getDisplayName() {
        return REMOVE_COMMAND_DISPLAY_NAME;
    }
}
