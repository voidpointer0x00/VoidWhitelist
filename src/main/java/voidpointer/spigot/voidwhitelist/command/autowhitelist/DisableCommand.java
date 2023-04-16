package voidpointer.spigot.voidwhitelist.command.autowhitelist;

import voidpointer.spigot.framework.di.Autowired;
import voidpointer.spigot.framework.localemodule.Locale;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.command.Command;
import voidpointer.spigot.voidwhitelist.command.arg.Args;
import voidpointer.spigot.voidwhitelist.config.WhitelistConfig;
import voidpointer.spigot.voidwhitelist.message.WhitelistMessage;

final class DisableCommand extends Command {
    @AutowiredLocale private static Locale locale;
    @Autowired private static WhitelistConfig whitelistConfig;

    DisableCommand() {
        super("off");
    }

    @Override public void execute(final Args args) {
        whitelistConfig.disableAutoWhitelist();
        locale.localize(WhitelistMessage.AUTO_WHITELIST_DISABLED).send(args.getSender());
    }
}
