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
import voidpointer.spigot.framework.localemodule.LocalizedMessage;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.Whitelistable;
import voidpointer.spigot.voidwhitelist.message.WhitelistMessage;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;
import voidpointer.spigot.voidwhitelist.uuid.DefaultUUIDFetcher;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static voidpointer.spigot.voidwhitelist.message.WhitelistMessage.INFO_NOT_WHITELISTED;
import static voidpointer.spigot.voidwhitelist.message.WhitelistMessage.INFO_WHITELISTED;
import static voidpointer.spigot.voidwhitelist.message.WhitelistMessage.INFO_WHITELISTED_TEMP;
import static voidpointer.spigot.voidwhitelist.message.WhitelistMessage.PLAYER_DETAILS;

public final class InfoCommand extends Command {
    public static final String NAME = "info";
    public static final String PERMISSION = "whitelist.info";

    @AutowiredLocale private static Locale locale;
    @Autowired private static WhitelistService whitelistService;

    public InfoCommand() {
        super(NAME);
        super.setPermission(PERMISSION);
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
                        .set("player", args.get(0))
                        .set("date", null)
                        .send(args.getSender());
            }
            tellInfo(args, whitelistService.find(uuidOptional.get()).join(), uuidOptional.get());
        });
    }

    @Override protected void onNoPermission(final CommandSender sender) {
        locale.localize(WhitelistMessage.NO_PERMISSION).send(sender);
    }

    private CompletableFuture<Optional<UUID>> getUniqueId(final Args args) {
        if (args.isEmpty())
            return CompletableFuture.completedFuture(Optional.of(args.getPlayer().getUniqueId()));
        else
            return DefaultUUIDFetcher.of(args.getOptions()).getUUID(args.get(0));
    }

    private boolean isSelfConsole(final Args args) {
        return !args.isPlayer() && args.isEmpty();
    }

    private void tellInfo(final Args args, final Optional<Whitelistable> whitelistable, final UUID uuid) {
        LocalizedMessage message;
        if (!whitelistable.isPresent() || !whitelistable.get().isAllowedToJoin()) {
            message = locale.localize(INFO_NOT_WHITELISTED);
        } else if (whitelistable.get().isExpirable()) {
            message = locale.localize(INFO_WHITELISTED_TEMP)
                    .set("date", whitelistable.get().getExpiresAt().toString());
        } else {
            message = locale.localize(INFO_WHITELISTED);
        }
        message.set("player-details", locale.localize(PLAYER_DETAILS).getRawMessage())
                .set("player", args.get(0))
                .set("uuid", uuid.toString())
                .send(args.getSender());
    }
}
