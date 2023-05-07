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
package voidpointer.spigot.voidwhitelist.command.whitelist;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import voidpointer.spigot.framework.di.Autowired;
import voidpointer.spigot.framework.localemodule.LocaleLog;
import voidpointer.spigot.framework.localemodule.Message;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.Whitelistable;
import voidpointer.spigot.voidwhitelist.command.Command;
import voidpointer.spigot.voidwhitelist.command.arg.Arg;
import voidpointer.spigot.voidwhitelist.command.arg.Args;
import voidpointer.spigot.voidwhitelist.command.arg.UuidOptions;
import voidpointer.spigot.voidwhitelist.date.Duration;
import voidpointer.spigot.voidwhitelist.event.EventManager;
import voidpointer.spigot.voidwhitelist.event.WhitelistAddedEvent;
import voidpointer.spigot.voidwhitelist.message.WhitelistMessage;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;
import voidpointer.spigot.voidwhitelist.uuid.UUIDFetchers;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static org.bukkit.Bukkit.getOfflinePlayers;
import static voidpointer.spigot.voidwhitelist.message.WhitelistMessage.ADD_FAIL;
import static voidpointer.spigot.voidwhitelist.message.WhitelistMessage.PLAYER_DETAILS;

public final class AddCommand extends Command {
    public static final String NAME = "add";
    public static final int MIN_ARGS = 1;

    @AutowiredLocale private static LocaleLog locale;
    @Autowired(mapId="whitelistService")
    private static WhitelistService whitelistService;
    @Autowired private static EventManager eventManager;

    public AddCommand() {
        super(NAME);
        super.setRequiredArgsNumber(MIN_ARGS);
        super.addOptions(UuidOptions.values());
    }

    @Override public void execute(final Args args) {
        final Date whitelistTimeDuration;
        if (hasExpiresAtArgument(args)) {
            final Optional<Date> optionalWhitelistTimeDuration = Duration.ofEssentialsDate(args.get(1));
            if (optionalWhitelistTimeDuration.isEmpty()) {
                locale.localize(WhitelistMessage.WRONG_DATE_FORMAT).send(args.getSender());
                return;
            }
            whitelistTimeDuration = optionalWhitelistTimeDuration.get();
        } else {
            whitelistTimeDuration = null;
        }

        //noinspection CodeBlock2Expr
        UUIDFetchers.of(args.getDefinedOptions()).getUUID(args.get(0)).thenAcceptAsync(uid -> uid.ifPresentOrElse(uuid -> {
            whitelistService.add(uuid, args.get(0), whitelistTimeDuration).whenCompleteAsync((res, th) -> {
                if (th != null) {
                    locale.warn("Couldn't add a player to the whitelist", th);
                    return;
                }
                res.ifPresentOrElse(whitelistable -> {
                    notifyAdded(args, whitelistTimeDuration, uuid,
                            whitelistTimeDuration == null ? WhitelistMessage.ADDED : WhitelistMessage.ADDED_TEMP);
                    callWhitelistAddedEvent(whitelistable);
                }, () -> notifyFail(args, uuid));
            });
        }, () -> locale.localize(WhitelistMessage.UUID_FAIL_TRY_OFFLINE)
                .set("cmd", getName())
                .set("player", args.get(0))
                .set("date", whitelistTimeDuration)
                .send(args.getSender())
        )).whenCompleteAsync((res, th) -> {
            if (th != null)
                locale.warn("Couldn't add a player to the whitelist", th);
        });
    }

    private boolean hasExpiresAtArgument(final Args args) {
        return MIN_ARGS < args.size();
    }

    private void notifyFail(final Args args, final UUID uuid) {
        locale.localize(ADD_FAIL)
                .set("player-details", locale.localize(PLAYER_DETAILS))
                .set("player", args.get(0))
                .set("uuid", uuid)
                .send(args.getSender());
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
