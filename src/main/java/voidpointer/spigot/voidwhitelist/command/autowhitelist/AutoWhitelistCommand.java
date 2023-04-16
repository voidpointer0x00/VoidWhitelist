package voidpointer.spigot.voidwhitelist.command.autowhitelist;

import org.bukkit.command.CommandSender;
import voidpointer.spigot.framework.localemodule.Locale;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.command.Command;
import voidpointer.spigot.voidwhitelist.command.CommandManager;
import voidpointer.spigot.voidwhitelist.command.arg.Args;

import java.util.List;

import static voidpointer.spigot.voidwhitelist.message.WhitelistMessage.*;

public final class AutoWhitelistCommand extends Command {
    public static final String NAME = "auto-whitelist";

    @AutowiredLocale private static Locale locale;

    private final CommandManager autoWhitelistCommands = new CommandManager();

    public AutoWhitelistCommand() {
        super(NAME);

        super.setPermission("voidwhitelist.auto-whitelist");
        super.setRequiredArgsNumber(1);

        autoWhitelistCommands.addCommand(new HelpCommand());
        autoWhitelistCommands.addCommand(new IsOnCommand());
        autoWhitelistCommands.addCommand(new ResetCommand());

        autoWhitelistCommands.getCommands().values() /* set appropriate permissions for sub commands */
                .forEach(cmd -> cmd.setPermission(getPermission() + '.' + cmd.getName()));
    }

    @Override public void execute(final Args args) {
        final String subCommandName = args.removeFirst();
        if (!autoWhitelistCommands.executeCommand(subCommandName, args))
            locale.localize(AUTO_WHITELIST_UNKNOWN_COMMAND).send(args.getSender());
    }

    @Override public List<String> tabComplete(final Args args) {
        return autoWhitelistCommands.tabComplete(args);
    }

    @Override protected void onNotEnoughArgs(final CommandSender sender, final Args args) {
        locale.localize(AUTO_WHITELIST_NOT_ENOUGH_ARGS).send(sender);
    }
}
