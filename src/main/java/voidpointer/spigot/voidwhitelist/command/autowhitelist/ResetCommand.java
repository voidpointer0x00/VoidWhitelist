package voidpointer.spigot.voidwhitelist.command.autowhitelist;

import org.bukkit.command.CommandSender;
import voidpointer.spigot.framework.di.Autowired;
import voidpointer.spigot.framework.localemodule.Locale;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.TimesAutoWhitelisted;
import voidpointer.spigot.voidwhitelist.command.Command;
import voidpointer.spigot.voidwhitelist.command.arg.Args;
import voidpointer.spigot.voidwhitelist.command.arg.UuidOptions;
import voidpointer.spigot.voidwhitelist.storage.AutoWhitelistService;
import voidpointer.spigot.voidwhitelist.uuid.UUIDFetchers;

import static voidpointer.spigot.voidwhitelist.message.WhitelistMessage.*;

final class ResetCommand extends Command {
    @AutowiredLocale
    private static Locale locale;
    @Autowired(mapId="whitelistService")
    private static AutoWhitelistService autoWhitelistService;

    ResetCommand() {
        super("reset");

        super.setRequiredArgsNumber(1);
        super.addOptions(UuidOptions.values());
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
            autoWhitelistService.update(TimesAutoWhitelisted.zero(optionalUuid.get())).thenAccept(isUpdated -> {
                locale.localize(isUpdated ? AUTO_WHITELIST_RESET : AUTO_WHITELIST_RESET_FAIL)
                        .set("player-details", locale.localize(PLAYER_DETAILS))
                        .set("player", args.get(0))
                        .set("uuid", optionalUuid.get())
                        .send(args.getSender());
            });
        });
    }

    @Override protected void onNotEnoughArgs(final CommandSender sender, final Args args) {
        locale.localize(AUTO_WHITELIST_RESET_ARGS).send(sender);
    }
}
