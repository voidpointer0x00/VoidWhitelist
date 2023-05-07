package voidpointer.spigot.voidwhitelist.command.autowhitelist;

import org.bukkit.command.CommandSender;
import voidpointer.spigot.framework.di.Autowired;
import voidpointer.spigot.framework.localemodule.Locale;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.command.Command;
import voidpointer.spigot.voidwhitelist.command.arg.Args;
import voidpointer.spigot.voidwhitelist.config.WhitelistConfig;

import static voidpointer.spigot.voidwhitelist.message.WhitelistMessage.*;

final class SetLimitCommand extends Command {
    @AutowiredLocale private static Locale locale;
    @Autowired private static WhitelistConfig whitelistConfig;

    SetLimitCommand() {
        super("set-limit");

        super.setRequiredArgsNumber(1);
    }

    @Override public void execute(final Args args) {
        final int newLimit;
        try {
            newLimit = Integer.parseInt(args.get(0));
        } catch (NumberFormatException e) {
            locale.localize(AUTO_WHITELIST_SET_LIMIT_INT).set("given", args.get(0)).send(args.getSender());
            return;
        }
        final int previousLimit = whitelistConfig.setAutoLimit(newLimit);
        locale.localize(AUTO_WHITELIST_SET_LIMIT).set("old", previousLimit).set("new", newLimit).send(args.getSender());
    }

    @Override protected void onNotEnoughArgs(final CommandSender sender, final Args args) {
        locale.localize(AUTO_WHITELIST_SET_LIMIT_ARGS).send(args.getSender());
    }
}
