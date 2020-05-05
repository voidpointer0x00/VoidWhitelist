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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import lombok.NonNull;
import voidpointer.bukkit.framework.command.CommandArgs;
import voidpointer.bukkit.framework.command.SimpleCommand;
import voidpointer.bukkit.framework.command.validator.MinArgsValidator;
import voidpointer.bukkit.framework.command.validator.PermissionValidator;
import voidpointer.bukkit.framework.locale.Locale;
import voidpointer.bukkit.framework.locale.LocalizedMessage;
import voidpointer.bukkit.whitelist.Whitelistable;
import voidpointer.bukkit.whitelist.message.InfoMessage;
import voidpointer.bukkit.whitelist.message.UntilMessageFormatter;
import voidpointer.bukkit.whitelist.service.WhitelistableService;

/** @author VoidPointer aka NyanGuyMF */
public final class WhitelistInfoSubCommand extends SimpleCommand {
    private static final String INFO_COMMAND_NAME = "info";
    private static final String INFO_COMMAND_DISPLAY_NAME = "whitelist info";
    private static final String INFO_COMMAND_PERMISSION = "voidwhitelist.info";
    private static final int REQUIRED_ARGS = 1;
    private static final int PLAYER_NAME_INDEX = 0;

    @NonNull private final Locale locale;
    @NonNull private final UntilMessageFormatter untilMessageFormatter;
    @NonNull private final WhitelistableService whitelistableService;

    public WhitelistInfoSubCommand(final Locale locale, final WhitelistableService whitelistableService) {
        super(INFO_COMMAND_NAME);
        super.addValidator(new PermissionValidator(locale));
        super.addValidator(new MinArgsValidator(locale, REQUIRED_ARGS));
        this.locale = locale;
        untilMessageFormatter = new UntilMessageFormatter(locale);
        this.whitelistableService = whitelistableService;
    }

    @Override public boolean execute(final CommandArgs args) {
        final String playerName = args.get(PLAYER_NAME_INDEX);
        Whitelistable whitelistable = whitelistableService.findOrNew(playerName);

        LocalizedMessage message;
        if (!whitelistable.isWhitelisted())
            message = getNotWhitelistedMessage(whitelistable);
        else if (!whitelistable.hasUntil())
            message = getWhitelistedMessage(whitelistable);
        else if (whitelistable.isExpired())
            message = getExpiredMessage(whitelistable);
        else
            message = getUntilMessage(whitelistable);

        message.send(args.getSender());

        return true;
    }

    private LocalizedMessage getNotWhitelistedMessage(final Whitelistable whitelistable) {
        return locale.getLocalized(InfoMessage.INFO_NOT_WHITELISTED)
                .set("player", whitelistable.getName())
                .colorize()
                .multiline();
    }

    private LocalizedMessage getWhitelistedMessage(final Whitelistable whitelistable) {
        return locale.getLocalized(InfoMessage.INFO_WHITELISTED)
                .set("player", whitelistable.getName())
                .colorize()
                .multiline();
    }

    private LocalizedMessage getExpiredMessage(final Whitelistable whitelistable) {
        return locale.getLocalized(InfoMessage.INFO_EXPIRED)
                .set("player", whitelistable.getName())
                .set("until", untilMessageFormatter.format(whitelistable.getUntil()))
                .colorize()
                .multiline();
    }

    private LocalizedMessage getUntilMessage(final Whitelistable whitelistable) {
        return locale.getLocalized(InfoMessage.INFO_WHITELISTED_UNTIL)
                .set("player", whitelistable.getName())
                .set("until", untilMessageFormatter.format(whitelistable.getUntil()))
                .colorize()
                .multiline();
    }

    @Override public List<String> complete(final CommandArgs args) {
        return Arrays.stream(Bukkit.getOfflinePlayers()).parallel()
                .map(OfflinePlayer::getName)
                .collect(Collectors.toList());
    }

    @Override public String getDisplayName() {
        return INFO_COMMAND_DISPLAY_NAME;
    }

    @Override public String getPermission() {
        return INFO_COMMAND_PERMISSION;
    }
}
