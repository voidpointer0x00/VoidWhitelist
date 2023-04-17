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
        /* general commands */
        autoWhitelistCommands.addCommand(new EnableCommand());
        autoWhitelistCommands.addCommand(new DisableCommand());
        autoWhitelistCommands.addCommand(new IsOnCommand());
        /* configuration related commands */
        autoWhitelistCommands.addCommand(new GetDurationCommand());
        autoWhitelistCommands.addCommand(new SetDurationCommand());
        autoWhitelistCommands.addCommand(new GetLimitCommand());
        autoWhitelistCommands.addCommand(new SetLimitCommand());
        autoWhitelistCommands.addCommand(new GetStrategyCommand());
        autoWhitelistCommands.addCommand(new SetStrategyCommand());
        /* player related commands */
        autoWhitelistCommands.addCommand(new InfoCommand());
        autoWhitelistCommands.addCommand(new ResetCommand());
        autoWhitelistCommands.addCommand(new SetCommand());
        /* set appropriate permissions for sub commands */
        autoWhitelistCommands.getCommands().values()
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
