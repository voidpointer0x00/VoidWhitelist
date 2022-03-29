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

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import voidpointer.spigot.framework.di.Autowired;
import voidpointer.spigot.framework.localemodule.Locale;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.Whitelistable;
import voidpointer.spigot.voidwhitelist.date.EssentialsDateParser;
import voidpointer.spigot.voidwhitelist.event.EventManager;
import voidpointer.spigot.voidwhitelist.event.WhitelistAddedEvent;
import voidpointer.spigot.voidwhitelist.message.WhitelistMessage;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;
import voidpointer.spigot.voidwhitelist.uuid.UUIDFetcher;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public final class AddCommand extends Command {
    public static final String NAME = "add";
    public static final String PERMISSION = "whitelist.add";
    public static final int MIN_REQUIRED_ARGS = 1;

    @AutowiredLocale private static Locale locale;
    @Autowired private static WhitelistService whitelistService;
    @Autowired private static EventManager eventManager;
    @Autowired private static UUIDFetcher uniqueIdFetcher;

    public AddCommand() {
        super(NAME);
        super.setRequiredArgsNumber(MIN_REQUIRED_ARGS);
        super.setPermission(PERMISSION);
    }

    @Override public void execute(final Args args) {
        final Date expiresAt;
        if (hasExpiresAtArgument(args.size())) {
            final long whitelistTimePeriod = EssentialsDateParser.parseDate(args.get(1));
            if (EssentialsDateParser.WRONG_DATE_FORMAT == whitelistTimePeriod) {
                locale.localize(WhitelistMessage.WRONG_DATE_FORMAT).send(args.getSender());
                return;
            }
            expiresAt = new Date(whitelistTimePeriod);
        } else {
            expiresAt = Whitelistable.NEVER_EXPIRES;
        }
        uniqueIdFetcher.getUUID(args.get(0)).thenAcceptAsync(uuidOptional -> {
            if (!uuidOptional.isPresent()) {
                locale.localize(WhitelistMessage.API_REQUEST_FAILED_DIRECT_UUID_NOT_IMPLEMENTED_YET)
                        .set("player", args.get(0))
                        .send(args.getSender());
                return;
            }
            whitelistService.add(uuidOptional.get(), expiresAt).thenAcceptAsync(this::callWhitelistAddedEvent);

            if (expiresAt != Whitelistable.NEVER_EXPIRES)
                notifyAddedForever(args, expiresAt);
            else
                notifyAdded(args);
        });
    }

    private void notifyAddedForever(final Args args, final Date expiresAt) {
        locale.localize(WhitelistMessage.ADDED_TEMP)
                .set("player", args.get(0))
                .set("date", expiresAt.toString())
                .send(args.getSender());
    }

    private void notifyAdded(final Args args) {
        locale.localize(WhitelistMessage.ADDED)
                .set("player", args.get(0))
                .send(args.getSender());
    }

    @Override public List<String> tabComplete(final Args args) {
        if (args.size() == 1) {
            String presumptiveName = args.get(0);
            return Arrays.stream(Bukkit.getOfflinePlayers())
                    .filter(offlinePlayer -> (null != offlinePlayer.getName())
                            && offlinePlayer.getName().startsWith(presumptiveName))
                    .map(OfflinePlayer::getName)
                    .collect(Collectors.toList());
        } else if (args.size() > 1) {
            return Collections.emptyList();
        }
        return Arrays.stream(Bukkit.getOfflinePlayers())
                .map(OfflinePlayer::getName)
                .collect(Collectors.toList());
    }

    @Override protected void onNotEnoughArgs(final CommandSender sender, final Args args) {
        locale.localize(WhitelistMessage.ADD_HELP).send(sender);
    }

    @Override protected void onNoPermission(final CommandSender sender) {
        locale.localize(WhitelistMessage.NO_PERMISSION).send(sender);
    }

    private void callWhitelistAddedEvent(final Whitelistable whitelistable) {
        eventManager.callAsyncEvent(new WhitelistAddedEvent(whitelistable));
    }

    private boolean hasExpiresAtArgument(int argsNumber) {
        return MIN_REQUIRED_ARGS < argsNumber;
    }
}
