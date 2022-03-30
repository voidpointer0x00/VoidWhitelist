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
import voidpointer.spigot.framework.localemodule.Locale;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.Whitelistable;
import voidpointer.spigot.voidwhitelist.event.EventManager;
import voidpointer.spigot.voidwhitelist.event.WhitelistRemovedEvent;
import voidpointer.spigot.voidwhitelist.message.WhitelistMessage;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;
import voidpointer.spigot.voidwhitelist.uuid.UUIDFetcher;

import java.util.Optional;

public class RemoveCommand extends Command {
    public static final String NAME = "remove";
    public static final String PERMISSION = "whitelist.remove";
    public static final Integer MIN_ARGS = 1;

    @AutowiredLocale private static Locale locale;
    @Autowired private static WhitelistService whitelistService;
    @Autowired private static EventManager eventManager;
    @Autowired private static UUIDFetcher uniqueIdFetcher;

    public RemoveCommand() {
        super(NAME);
        super.setPermission(PERMISSION);
        super.setRequiredArgsNumber(MIN_ARGS);
        super.addOptions(UuidOptions.values());
    }

    @Override public void execute(final Args args) {
        final String name = args.get(0);
        uniqueIdFetcher.getUUID(name).thenAcceptAsync(uuidOptional -> {
            if (!uuidOptional.isPresent()) {
                locale.localize(WhitelistMessage.API_REQUEST_FAILED_DIRECT_UUID_NOT_IMPLEMENTED_YET)
                        .set("player", name)
                        .send(args.getSender());
                return;
            }

            final Optional<Whitelistable> whitelistable = whitelistService.find(uuidOptional.get()).join();
            if (!whitelistable.isPresent() || !whitelistable.get().isAllowedToJoin()) {
                locale.localize(WhitelistMessage.REMOVE_NOT_WHITELISTED)
                        .set("player", name)
                        .send(args.getSender());
                return;
            }
            whitelistService.remove(whitelistable.get());
            locale.localize(WhitelistMessage.REMOVED)
                    .set("player", name)
                    .send(args.getSender());
            eventManager.callEvent(new WhitelistRemovedEvent(whitelistable.get()));
        });
    }

    @Override protected void onNotEnoughArgs(final CommandSender sender, final Args args) {
        locale.localize(WhitelistMessage.REMOVE_HELP).send(sender);
    }

    @Override protected void onNoPermission(final CommandSender sender) {
        locale.localize(WhitelistMessage.NO_PERMISSION).send(sender);
    }
}
