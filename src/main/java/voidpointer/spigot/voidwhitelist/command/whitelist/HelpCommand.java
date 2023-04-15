package voidpointer.spigot.voidwhitelist.command.whitelist;

import voidpointer.spigot.framework.localemodule.Locale;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.command.Command;
import voidpointer.spigot.voidwhitelist.command.arg.Args;

import static voidpointer.spigot.voidwhitelist.message.WhitelistMessage.*;

final class HelpCommand extends Command {
    public static final String NAME = "help";
    public static final String PERMISSION = "whitelist.help";

    @AutowiredLocale private static Locale locale;

    public HelpCommand() {
        super(NAME);
        setPermission(PERMISSION);
    }

    @Override public void execute(final Args args) {
        locale.localize(WHITELIST_HELP).send(args.getSender());
    }
}
