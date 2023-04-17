package voidpointer.spigot.voidwhitelist.command.autowhitelist;

import org.bukkit.command.CommandSender;
import voidpointer.spigot.framework.di.Autowired;
import voidpointer.spigot.framework.localemodule.Locale;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.command.Command;
import voidpointer.spigot.voidwhitelist.command.arg.Args;
import voidpointer.spigot.voidwhitelist.config.WhitelistConfig;
import voidpointer.spigot.voidwhitelist.date.Duration;

import static voidpointer.spigot.voidwhitelist.message.WhitelistMessage.*;

final class SetDurationCommand extends Command {
    @AutowiredLocale
    private static Locale locale;
    @Autowired
    private static WhitelistConfig whitelistConfig;

    SetDurationCommand() {
        super("set-duration");

        super.setRequiredArgsNumber(1);
    }

    @Override public void execute(final Args args) {
        if (!Duration.exactMillis(args.get(0)).isPresent()) { /* TODO java upgrade #isEmpty() */
            locale.localize(AUTO_WHITELIST_SET_INVALID_DURATION).set("given", args.get(0)).send(args.getSender());
            return;
        }
        final String previousDuration = whitelistConfig.setAutoDuration(args.get(0));
        locale.localize(AUTO_WHITELIST_SET_DURATION).set("old", previousDuration).set("new", args.get(0))
                .send(args.getSender());
    }

    @Override protected void onNotEnoughArgs(final CommandSender sender, final Args args) {
        locale.localize(AUTO_WHITELIST_SET_DURATION_ARGS).send(args.getSender());
    }
}
