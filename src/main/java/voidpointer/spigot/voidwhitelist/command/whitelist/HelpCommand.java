package voidpointer.spigot.voidwhitelist.command.whitelist;

import voidpointer.spigot.framework.localemodule.Locale;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.command.Command;
import voidpointer.spigot.voidwhitelist.command.arg.Args;

import static voidpointer.spigot.voidwhitelist.message.WhitelistMessage.WHITELIST_HELP;

final class HelpCommand extends Command {
    public static final String NAME = "help";

    @AutowiredLocale private static Locale locale;

    public HelpCommand() {
        super(NAME);
    }

    @Override public void execute(final Args args) {
        locale.localize(WHITELIST_HELP).send(args.getSender());
    }
}
