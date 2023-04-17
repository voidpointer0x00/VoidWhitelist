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
import voidpointer.spigot.framework.di.Autowired;
import voidpointer.spigot.framework.localemodule.LocaleLog;
import voidpointer.spigot.framework.localemodule.LocalizedMessage;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.Whitelistable;
import voidpointer.spigot.voidwhitelist.command.Command;
import voidpointer.spigot.voidwhitelist.command.arg.Arg;
import voidpointer.spigot.voidwhitelist.command.arg.Args;
import voidpointer.spigot.voidwhitelist.command.arg.UuidOptions;
import voidpointer.spigot.voidwhitelist.message.WhitelistMessage;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;
import voidpointer.spigot.voidwhitelist.uuid.UUIDFetchers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static org.bukkit.Bukkit.getOfflinePlayers;
import static voidpointer.spigot.voidwhitelist.message.WhitelistMessage.*;

public final class InfoCommand extends Command {
    public static final String NAME = "info";

    @AutowiredLocale private static LocaleLog locale;
    @Autowired(mapId="whitelistService")
    private static WhitelistService whitelistService;

    public InfoCommand() {
        super(NAME);
        super.addOptions(UuidOptions.values());
    }

    @Override public void execute(final Args args) {
        if (isSelfConsole(args)) {
            locale.localize(WhitelistMessage.CONSOLE_WHITELISTED).send(args.getSender());
            return;
        }

        getUniqueId(args).thenAcceptAsync(uuidOptional -> {
            if (!uuidOptional.isPresent()) {
                locale.localize(WhitelistMessage.UUID_FAIL_TRY_OFFLINE)
                        .set("cmd", getName())
                        .set("player", args.isEmpty() ? args.getPlayer().getDisplayName() : args.get(0))
                        .set("date", null)
                        .send(args.getSender());
                return;
            }
            tellInfo(args, whitelistService.find(uuidOptional.get()).join(), uuidOptional.get());
        }).whenCompleteAsync((res, th) -> {
            if (th != null)
                locale.warn("Couldn't get information about player", th);
        });
    }

    private CompletableFuture<Optional<UUID>> getUniqueId(final Args args) {
        if (args.isEmpty())
            return CompletableFuture.completedFuture(Optional.of(args.getPlayer().getUniqueId()));
        else
            return UUIDFetchers.of(args.getDefinedOptions()).getUUID(args.get(0));
    }

    private boolean isSelfConsole(final Args args) {
        return !args.isPlayer() && args.isEmpty();
    }

    private void tellInfo(final Args args, final Optional<Whitelistable> whitelistable, final UUID uuid) {
        LocalizedMessage message;
        if (!whitelistable.isPresent() || !whitelistable.get().isAllowedToJoin())
            message = locale.localize(INFO_NOT_WHITELISTED);
        else if (whitelistable.get().isExpirable())
            message = locale.localize(INFO_WHITELISTED_TEMP).set("date", whitelistable.get().getExpiresAt());
        else
            message = locale.localize(INFO_WHITELISTED);
        message.set("player-details", locale.localize(PLAYER_DETAILS))
                .set("player", args.isEmpty() ? args.getPlayer().getDisplayName() : args.get(0))
                .set("uuid", uuid.toString())
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
        if (args.size() == 1) {
            return stream(getOfflinePlayers())
                    .filter(offlinePlayer -> (null != offlinePlayer.getName())
                            && offlinePlayer.getName().startsWith(args.getLast()))
                    .map(OfflinePlayer::getName)
                    .collect(Collectors.toList());
        }
        return emptyList();
    }
}
