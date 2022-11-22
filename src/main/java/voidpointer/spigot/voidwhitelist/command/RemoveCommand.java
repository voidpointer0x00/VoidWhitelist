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
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.Whitelistable;
import voidpointer.spigot.voidwhitelist.command.arg.Arg;
import voidpointer.spigot.voidwhitelist.command.arg.Args;
import voidpointer.spigot.voidwhitelist.command.arg.UuidOptions;
import voidpointer.spigot.voidwhitelist.event.EventManager;
import voidpointer.spigot.voidwhitelist.event.WhitelistRemovedEvent;
import voidpointer.spigot.voidwhitelist.message.WhitelistMessage;
import voidpointer.spigot.voidwhitelist.net.DefaultUUIDFetcher;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.bukkit.Bukkit.getOfflinePlayers;
import static voidpointer.spigot.voidwhitelist.message.WhitelistMessage.*;

public class RemoveCommand extends Command {
    public static final String NAME = "remove";
    public static final List<String> ALIASES = singletonList("rem");
    public static final String PERMISSION = "whitelist.remove";
    public static final Integer MIN_ARGS = 1;

    @AutowiredLocale private static LocaleLog locale;
    @Autowired private static WhitelistService whitelistService;
    @Autowired private static EventManager eventManager;

    public RemoveCommand() {
        super(NAME);
        super.setPermission(PERMISSION);
        super.setRequiredArgsNumber(MIN_ARGS);
        super.addOptions(UuidOptions.values());
    }

    @Override public void execute(final Args args) {
        final String name = args.get(0);
        DefaultUUIDFetcher.of(args.getDefinedOptions()).getUUID(name).thenAcceptAsync(uuidOptional -> {
            if (uuidOptional.isEmpty()) {
                locale.localize(WhitelistMessage.UUID_FAIL_TRY_OFFLINE)
                        .set("cmd", getName())
                        .set("player", name)
                        .set("date", null)
                        .send(args.getSender());
                return;
            }

            final Optional<Whitelistable> whitelistable = whitelistService.find(uuidOptional.get()).join();
            if (whitelistable.isEmpty()) {
                locale.localize(WhitelistMessage.REMOVE_NOT_WHITELISTED)
                        .set("player-details", locale.localize(PLAYER_DETAILS))
                        .set("uuid", uuidOptional.get())
                        .set("player", name)
                        .send(args.getSender());
                return;
            }
            boolean isRemoved = whitelistService.remove(whitelistable.get()).join();
            if (isRemoved) {
                notifyRemoved(args.getSender(), uuidOptional.get(), name);
                eventManager.callEvent(new WhitelistRemovedEvent(whitelistable.get()));
            } else {
                notifyNotRemoved(args.getSender(), uuidOptional.get(), name);
            }
        }).whenCompleteAsync((res, th) -> {
            if (th != null)
                locale.warn("Couldn't remove a player from the whitelist", th);
        });
    }

    private void notifyRemoved(final CommandSender sender, final UUID uuid, final String name) {
        locale.localize(REMOVED)
                .set("player-details", locale.localize(PLAYER_DETAILS))
                .set("uuid", uuid)
                .set("player", name)
                .send(sender);
    }

    private void notifyNotRemoved(final CommandSender sender, final UUID uuid, final String name) {
        locale.localize(REMOVE_FAIL)
                .set("player-details", locale.localize(PLAYER_DETAILS))
                .set("uuid", uuid)
                .set("player", name)
                .send(sender);
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

    @Override public List<String> getAliases() {
        return ALIASES;
    }

    @Override protected void onNotEnoughArgs(final CommandSender sender, final Args args) {
        locale.localize(WhitelistMessage.REMOVE_HELP).send(sender);
    }
}
