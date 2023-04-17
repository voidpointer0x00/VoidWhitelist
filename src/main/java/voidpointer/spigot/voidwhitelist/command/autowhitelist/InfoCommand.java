package voidpointer.spigot.voidwhitelist.command.autowhitelist;

import org.bukkit.command.CommandSender;
import voidpointer.spigot.framework.di.Autowired;
import voidpointer.spigot.framework.localemodule.Locale;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.command.Command;
import voidpointer.spigot.voidwhitelist.command.arg.Args;
import voidpointer.spigot.voidwhitelist.storage.AutoWhitelistService;
import voidpointer.spigot.voidwhitelist.uuid.UUIDFetchers;

import static voidpointer.spigot.voidwhitelist.message.WhitelistMessage.*;

final class InfoCommand extends Command {
    @AutowiredLocale
    private static Locale locale;
    @Autowired(mapId="whitelistService")
    private static AutoWhitelistService autoWhitelistService;

    InfoCommand() {
        super("info");

        super.setRequiredArgsNumber(1);
    }

    @Override public void execute(final Args args) {
        UUIDFetchers.of(args.getDefinedOptions()).getUUID(args.get(0)).thenAcceptAsync(optionalUuid -> {
            if (!optionalUuid.isPresent()) { /* TODO: Java upgrade #isEmpty() */
                locale.localize(AUTO_UUID_FAIL_TRY_OFFLINE)
                        .set("cmd", getName())
                        .set("player", args.get(0))
                        .send(args.getSender());
                return;
            }
            autoWhitelistService.getTimesAutoWhitelisted(optionalUuid.get()).thenAcceptAsync(optionalTimesAutoWhitelist -> {
                if (!optionalTimesAutoWhitelist.isPresent()) { /* TODO: Java upgrade #isEmpty() */
                    locale.localize(AUTO_WHITELIST_INFO_NOT_FOUND)
                            .set("player-details", locale.localize(PLAYER_DETAILS))
                            .set("player", args.get(0))
                            .set("uuid", optionalUuid.get())
                            .send(args.getSender());
                    return;
                }
                locale.localize(AUTO_WHITELIST_INFO)
                        .set("player-details", locale.localize(PLAYER_DETAILS))
                        .set("player", args.get(0))
                        .set("uuid", optionalUuid.get())
                        .set("times-auto-whitelisted", optionalTimesAutoWhitelist.get().get())
                        .send(args.getSender());
            });
        });
    }

    @Override protected void onNotEnoughArgs(final CommandSender sender, final Args args) {
        locale.localize(AUTO_WHITELIST_INFO_ARGS).send(args.getSender());
    }
}
