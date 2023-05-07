package voidpointer.spigot.voidwhitelist.command.autowhitelist;

import org.bukkit.command.CommandSender;
import voidpointer.spigot.framework.di.Autowired;
import voidpointer.spigot.framework.localemodule.Locale;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.TimesAutoWhitelisted;
import voidpointer.spigot.voidwhitelist.command.Command;
import voidpointer.spigot.voidwhitelist.command.arg.Args;
import voidpointer.spigot.voidwhitelist.storage.AutoWhitelistService;
import voidpointer.spigot.voidwhitelist.uuid.UUIDFetchers;

import static voidpointer.spigot.voidwhitelist.message.WhitelistMessage.*;

final class SetCommand extends Command {
    @AutowiredLocale private static Locale locale;
    @Autowired(mapId="whitelistService")
    private static AutoWhitelistService autoWhitelistService;

    SetCommand() {
        super("set");

        super.setRequiredArgsNumber(2);
    }

    @SuppressWarnings("CodeBlock2Expr") /* expand to lambda */
    @Override public void execute(final Args args) {
        final int newTimesAutoWhitelisted;
        try {
            newTimesAutoWhitelisted = Integer.parseInt(args.get(1));
        } catch (final NumberFormatException numberFormatException) {
            locale.localize(AUTO_WHITELIST_SET_INVALID_INT)
                    .set("player", args.get(0)).set("given", args.get(1)).send(args.getSender());
            return;
        }
        UUIDFetchers.of(args.getDefinedOptions()).getUUID(args.get(0)).thenAcceptAsync(optionalUuid -> {
            optionalUuid.ifPresentOrElse(uuid -> {
                autoWhitelistService.update(TimesAutoWhitelisted.of(uuid, newTimesAutoWhitelisted))
                        .thenAccept(isUpdated -> {
                            locale.localize(isUpdated ? AUTO_WHITELIST_SET : AUTO_WHITELIST_SET_FAIL)
                                    .set("player-details", locale.localize(PLAYER_DETAILS))
                                    .set("player", args.get(0))
                                    .set("uuid", uuid)
                                    .set("new", newTimesAutoWhitelisted)
                                    .send(args.getSender());
                        });
            }, () -> {
                locale.localize(AUTO_UUID_FAIL_TRY_OFFLINE)
                        .set("cmd", getName())
                        .set("player", args.get(0))
                        .send(args.getSender());
            });
        });
    }

    @Override protected void onNotEnoughArgs(final CommandSender sender, final Args args) {
        locale.localize(AUTO_WHITELIST_SET_ARGS).set("given", args.size()).set("needed", getRequiredArgsNumber())
                .send(sender);
    }
}
