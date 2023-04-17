package voidpointer.spigot.voidwhitelist.command.autowhitelist;

import voidpointer.spigot.framework.di.Autowired;
import voidpointer.spigot.framework.localemodule.Locale;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.command.Command;
import voidpointer.spigot.voidwhitelist.command.arg.Args;
import voidpointer.spigot.voidwhitelist.config.WhitelistConfig;
import voidpointer.spigot.voidwhitelist.date.Duration;

import static voidpointer.spigot.voidwhitelist.message.WhitelistMessage.*;

final class GetDurationCommand extends Command {
    @AutowiredLocale private static Locale locale;
    @Autowired private static WhitelistConfig whitelistConfig;

    GetDurationCommand() {
        super("get-duration");
    }

    @Override public void execute(final Args args) {
        final String rawDuration = whitelistConfig.getRawAutoDuration();
        locale.localize(AUTO_WHITELIST_GET_DURATION)
                .set("duration", rawDuration).set("exact", Duration.exactMillis(rawDuration).orElse(-1L))
                .send(args.getSender());
    }
}
