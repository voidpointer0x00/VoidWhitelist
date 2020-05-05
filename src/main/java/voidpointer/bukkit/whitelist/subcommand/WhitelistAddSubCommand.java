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

import static java.util.Arrays.asList;
import static org.bukkit.Bukkit.getOfflinePlayers;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import lombok.NonNull;
import voidpointer.bukkit.framework.command.CommandArgs;
import voidpointer.bukkit.framework.command.SimpleCommand;
import voidpointer.bukkit.framework.command.validator.MinArgsValidator;
import voidpointer.bukkit.framework.command.validator.PermissionValidator;
import voidpointer.bukkit.framework.event.EventManager;
import voidpointer.bukkit.framework.locale.Locale;
import voidpointer.bukkit.framework.locale.LocalizedMessage;
import voidpointer.bukkit.whitelist.Whitelistable;
import voidpointer.bukkit.whitelist.date.EssentialsDateParser;
import voidpointer.bukkit.whitelist.date.MinecraftDateParser;
import voidpointer.bukkit.whitelist.event.WhitelistAddedEvent;
import voidpointer.bukkit.whitelist.message.ErrorMessage;
import voidpointer.bukkit.whitelist.message.UntilMessageFormatter;
import voidpointer.bukkit.whitelist.message.WhitelistMessage;
import voidpointer.bukkit.whitelist.service.WhitelistableService;

/** @author VoidPointer aka NyanGuyMF */
public final class WhitelistAddSubCommand extends SimpleCommand {
    private static final String ADD_COMMAND_NAME = "add";
    private static final String ADD_COMMAND_DISPLAY_NAME = "whitelist add";
    private static final String ADD_COMMAND_PERMISSION = "voidwhitelist.add";
    private static final int REQUIRED_ARGS = 1;
    private static final int WHITELISTABLE_NAME_INDEX = 0;
    private static final int DATE_INDEX = 1;

    @NonNull private final MinecraftDateParser dateParser = new EssentialsDateParser();
    @NonNull private final Locale locale;
    @NonNull private final UntilMessageFormatter untilMessageFormatter;
    @NonNull private final WhitelistableService whitelistableService;
    @NonNull private final EventManager eventManager;

    public WhitelistAddSubCommand(
            final Locale locale,
            final WhitelistableService whitelistableService,
            final EventManager eventManager
    ) {
        super(ADD_COMMAND_NAME);
        super.addValidator(new MinArgsValidator(locale, REQUIRED_ARGS));
        super.addValidator(new PermissionValidator(locale));
        this.locale = locale;
        untilMessageFormatter = new UntilMessageFormatter(locale);
        this.whitelistableService = whitelistableService;
        this.eventManager = eventManager;
    }

    @Override public boolean execute(final CommandArgs args) {
        String whitelistableName = args.get(WHITELISTABLE_NAME_INDEX);
        Whitelistable whitelistable = whitelistableService.findOrNew(whitelistableName);

        if (args.length() > 1) {
            long untilTimestamp = dateParser.parseDate(args.get(DATE_INDEX));
            if (untilTimestamp == -1) {
                onWrongDate(args);
                return true;
            }
            whitelistable.setUntil(new Date(untilTimestamp));
        } else if (whitelistable.hasUntil()) {
            /* in this case player is whitelisted forever. */
            whitelistable.setUntil(null);
        }

        if (!whitelistable.isWhitelisted())
            whitelistable.setWhitelisted(true);

        notifyIfUnknownPlayer(whitelistableName, args.getSender());
        whitelistableService.update(whitelistable);
        onAdded(whitelistableName, whitelistable, args.getSender());

        Event addedEvent = new WhitelistAddedEvent(
            args.getSender(), whitelistableName, whitelistable.getUntil()
        );
        eventManager.callEventAsync(addedEvent);

        return true;
    }

    private void onWrongDate(final CommandArgs args) {
        locale.getLocalized(ErrorMessage.INVALID_DATE)
                .colorize()
                .set("date", args.get(DATE_INDEX))
                .send(args.getSender());
    }

    private void notifyIfUnknownPlayer(final String playerName, final CommandSender receiver) {
        boolean isUnknown = asList(getOfflinePlayers()).parallelStream()
                .anyMatch(player -> player.getName().equals(playerName));
        if (!isUnknown) {
            locale.getLocalized(WhitelistMessage.WHITELIST_UNKNOWN_PLAYER)
                    .colorize()
                    .set("player", playerName)
                    .send(receiver);
        }
    }

    private void onAdded(
            final String playerName,
            final Whitelistable whitelistable,
            final CommandSender actor
    ) {
        LocalizedMessage message;
        if (whitelistable.hasUntil()) {
            message = locale.getLocalized(WhitelistMessage.PLAYER_ADDED_UNTIL)
                    .colorize()
                    .set("until", untilMessageFormatter.format(whitelistable.getUntil()));
        } else {
            message = locale.getLocalized(WhitelistMessage.PLAYER_ADDED)
                    .colorize();
        }
        message.set("player", playerName).send(actor);
    }

    @Override public List<String> complete(final CommandArgs args) {
        return Bukkit.getOnlinePlayers().parallelStream()
                .map(Player::getName)
                .collect(Collectors.toList());
    }

    @Override public String getPermission() {
        return ADD_COMMAND_PERMISSION;
    }

    @Override public String getDisplayName() {
        return ADD_COMMAND_DISPLAY_NAME;
    }
}
