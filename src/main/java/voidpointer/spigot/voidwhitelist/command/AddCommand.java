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

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import voidpointer.spigot.framework.di.Autowired;
import voidpointer.spigot.framework.localemodule.LocaleLog;
import voidpointer.spigot.framework.localemodule.Message;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.Whitelistable;
import voidpointer.spigot.voidwhitelist.command.arg.Arg;
import voidpointer.spigot.voidwhitelist.command.arg.Args;
import voidpointer.spigot.voidwhitelist.command.arg.UuidOptions;
import voidpointer.spigot.voidwhitelist.date.EssentialsDateParser;
import voidpointer.spigot.voidwhitelist.event.EventManager;
import voidpointer.spigot.voidwhitelist.event.WhitelistAddedEvent;
import voidpointer.spigot.voidwhitelist.message.WhitelistMessage;
import voidpointer.spigot.voidwhitelist.net.DefaultUUIDFetcher;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static org.bukkit.Bukkit.getOfflinePlayers;
import static voidpointer.spigot.voidwhitelist.Whitelistable.NEVER_EXPIRES;

public final class AddCommand extends Command {
    public static final String NAME = "add";
    public static final String PERMISSION = "whitelist.add";
    public static final int MIN_ARGS = 1;

    @AutowiredLocale private static LocaleLog locale;
    @Autowired private static WhitelistService whitelistService;
    @Autowired private static EventManager eventManager;

    public AddCommand() {
        super(NAME);
        super.setRequiredArgsNumber(MIN_ARGS);
        super.setPermission(PERMISSION);
        super.addOptions(UuidOptions.values());
    }

    @Override public void execute(final Args args) {
        final Optional<Date> expiresAt = parseExpiresAtAndWarnIfWrong(args);
        if (expiresAt == null)
            return;

        DefaultUUIDFetcher.of(args.getDefinedOptions()).getUUID(args.get(0)).thenAcceptAsync(uuidOptional -> {
            if (!uuidOptional.isPresent()) {
                locale.localize(WhitelistMessage.UUID_FAIL_TRY_OFFLINE)
                        .set("cmd", getName())
                        .set("player", args.get(0))
                        .set("date", expiresAt.orElse(null))
                        .send(args.getSender());
                return;
            }
            whitelistService.add(uuidOptional.get(), args.get(0), expiresAt.orElse(NEVER_EXPIRES))
                    .whenCompleteAsync((res, th) -> {
                        if (th != null) {
                            locale.warn("Couldn't add a player to the whitelist", th);
                            return;
                        }
                        if (expiresAt.isPresent())
                            notifyAdded(args, expiresAt.get(), uuidOptional.get(), WhitelistMessage.ADDED_TEMP);
                        else
                            notifyAdded(args, null, uuidOptional.get(), WhitelistMessage.ADDED);
                        res.ifPresent(this::callWhitelistAddedEvent);
                    });
        }).whenCompleteAsync((res, th) -> {
            if (th != null)
                locale.warn("Couldn't add a player to the whitelist", th);
        });
    }

    private Optional<Date> parseExpiresAtAndWarnIfWrong(final Args args) {
        if (hasExpiresAtArgument(args.size())) {
            final long whitelistTimePeriod = EssentialsDateParser.parseDate(args.get(1));
            if (EssentialsDateParser.WRONG_DATE_FORMAT == whitelistTimePeriod) {
                locale.localize(WhitelistMessage.WRONG_DATE_FORMAT).send(args.getSender());
                return null;
            }
            return Optional.of(new Date(whitelistTimePeriod));
        }
        return Optional.empty();
    }

    private boolean hasExpiresAtArgument(int argsNumber) {
        return MIN_ARGS < argsNumber;
    }

    private void notifyAdded(final Args args, final Date expiresAt, final UUID uuid, final Message message) {
        locale.localize(message)
                .set("player-details", locale.localize(WhitelistMessage.PLAYER_DETAILS))
                .set("player", args.get(0))
                .set("uuid", uuid)
                .set("date", expiresAt)
                .send(args.getSender());
    }

    @Override public List<String> tabComplete(final Args args) {
        Optional<Arg> last = args.getLastArg();
        if (last.isPresent() && last.get().isOption())
            return completeOption(last.get().value);
        if (args.isEmpty()) {
            return stream(getOfflinePlayers())
                    .map(OfflinePlayer::getName)
                    .collect(Collectors.toList());
        }
        if (args.size() == MIN_ARGS) {
            return stream(getOfflinePlayers())
                    .filter(offlinePlayer -> (null != offlinePlayer.getName())
                            && offlinePlayer.getName().startsWith(args.getLast()))
                    .map(OfflinePlayer::getName)
                    .collect(Collectors.toList());
        }
        return emptyList();
    }

    @Override protected void onNotEnoughArgs(final CommandSender sender, final Args args) {
        locale.localize(WhitelistMessage.ADD_HELP).send(sender);
    }

    private void callWhitelistAddedEvent(final Whitelistable whitelistable) {
        eventManager.callAsyncEvent(new WhitelistAddedEvent(whitelistable));
    }
}
