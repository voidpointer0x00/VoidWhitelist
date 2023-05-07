package voidpointer.spigot.voidwhitelist.command.autowhitelist;

import voidpointer.spigot.framework.di.Autowired;
import voidpointer.spigot.framework.localemodule.Locale;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.command.Command;
import voidpointer.spigot.voidwhitelist.command.arg.Args;
import voidpointer.spigot.voidwhitelist.config.WhitelistConfig;

import static voidpointer.spigot.voidwhitelist.message.WhitelistMessage.AUTO_WHITELIST_GET_STRATEGY;

final class GetStrategyCommand extends Command {
    @AutowiredLocale private static Locale locale;
    @Autowired private static WhitelistConfig whitelistConfig;

    GetStrategyCommand() {
        super("get-strategy");
    }

    @Override public void execute(final Args args) {
        locale.localize(AUTO_WHITELIST_GET_STRATEGY)
                .set("strategy", whitelistConfig.getStrategyPredicate().getName())
                .send(args.getSender());
    }
}
