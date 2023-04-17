package voidpointer.spigot.voidwhitelist.command.autowhitelist;

import voidpointer.spigot.framework.di.Autowired;
import voidpointer.spigot.framework.localemodule.Locale;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.command.Command;
import voidpointer.spigot.voidwhitelist.command.arg.Args;
import voidpointer.spigot.voidwhitelist.config.WhitelistConfig;

import static voidpointer.spigot.voidwhitelist.message.WhitelistMessage.*;

final class GetLimitCommand extends Command {
    @AutowiredLocale private static Locale locale;
    @Autowired private static WhitelistConfig whitelistConfig;

    GetLimitCommand() {
        super("get-limit");
    }

    @Override public void execute(final Args args) {
        locale.localize(AUTO_WHITELIST_GET_LIMIT).set("limit", whitelistConfig.getAutoLimit()).send(args.getSender());
    }
}
