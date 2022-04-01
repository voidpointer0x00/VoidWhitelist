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

import org.bukkit.command.CommandSender;
import voidpointer.spigot.framework.di.Autowired;
import voidpointer.spigot.framework.localemodule.LocaleLog;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.Whitelistable;
import voidpointer.spigot.voidwhitelist.event.EventManager;
import voidpointer.spigot.voidwhitelist.event.WhitelistRemovedEvent;
import voidpointer.spigot.voidwhitelist.message.WhitelistMessage;
import voidpointer.spigot.voidwhitelist.net.DefaultUUIDFetcher;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;

import java.util.Optional;

public class RemoveCommand extends Command {
    public static final String NAME = "remove";
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
        DefaultUUIDFetcher.of(args.getOptions()).getUUID(name).thenAcceptAsync(uuidOptional -> {
            if (!uuidOptional.isPresent()) {
                locale.localize(WhitelistMessage.UUID_FAIL_TRY_OFFLINE)
                        .set("cmd", getName())
                        .set("player", name)
                        .set("date", null)
                        .send(args.getSender());
                return;
            }

            final Optional<Whitelistable> whitelistable = whitelistService.find(uuidOptional.get()).join();
            if (!whitelistable.isPresent() || !whitelistable.get().isAllowedToJoin()) {
                locale.localize(WhitelistMessage.REMOVE_NOT_WHITELISTED)
                        .set("player-details", locale.localize(WhitelistMessage.PLAYER_DETAILS))
                        .set("uuid", uuidOptional.get())
                        .set("player", name)
                        .send(args.getSender());
                return;
            }
            whitelistService.remove(whitelistable.get());
            locale.localize(WhitelistMessage.REMOVED)
                    .set("player-details", locale.localize(WhitelistMessage.PLAYER_DETAILS))
                    .set("uuid", uuidOptional.get())
                    .set("player", name)
                    .send(args.getSender());
            eventManager.callEvent(new WhitelistRemovedEvent(whitelistable.get()));
        }).whenCompleteAsync((res, th) -> {
            if (th != null)
                locale.warn("Couldn't remove a player from the whitelist", th);
        });
    }

    @Override protected void onNotEnoughArgs(final CommandSender sender, final Args args) {
        locale.localize(WhitelistMessage.REMOVE_HELP).send(sender);
    }
}
