package voidpointer.spigot.voidwhitelist.command.autowhitelist;

import voidpointer.spigot.framework.di.Autowired;
import voidpointer.spigot.framework.localemodule.Locale;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.command.Command;
import voidpointer.spigot.voidwhitelist.command.arg.Args;
import voidpointer.spigot.voidwhitelist.config.WhitelistConfig;

import static voidpointer.spigot.voidwhitelist.message.WhitelistMessage.*;

final class IsOnCommand extends Command {
    @AutowiredLocale private static Locale locale;
    @Autowired private static WhitelistConfig whitelistConfig;

    IsOnCommand() {
        super("is-on");
    }

    @Override public void execute(final Args args) {
        locale.localize(whitelistConfig.isAutoWhitelistEnabled() ? AUTO_WHITELIST_ENABLED : AUTO_WHITELIST_DISABLED)
                .send(args.getSender());
    }
}
