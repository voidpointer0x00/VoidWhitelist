package voidpointer.spigot.voidwhitelist.command.autowhitelist;

import voidpointer.spigot.framework.localemodule.Locale;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.command.Command;
import voidpointer.spigot.voidwhitelist.command.arg.Args;
import voidpointer.spigot.voidwhitelist.message.WhitelistMessage;

final class HelpCommand extends Command {
    @AutowiredLocale private static Locale locale;

    HelpCommand() {
        super("help");
    }

    @Override public void execute(final Args args) {
        locale.localize(WhitelistMessage.AUTO_WHITELIST_HELP);
    }
}
