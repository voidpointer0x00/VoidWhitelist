/*
 *             DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
 *
 *  Copyright (C) 2022 Vasiliy Petukhov <void.pointer@ya.ru>
 *
 *  Everyone is permitted to copy and distribute verbatim or modified
 *  copies of this license document, and changing it is allowed as long
 *  as the name is changed.
 *
 *             DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
 *    TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION
 *
 *   0. You just DO WHAT THE FUCK YOU WANT TO.
 */
package voidpointer.spigot.voidwhitelist.command.whitelist;

import org.bukkit.command.CommandSender;
import voidpointer.spigot.framework.localemodule.Locale;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.command.Command;
import voidpointer.spigot.voidwhitelist.command.CommandManager;
import voidpointer.spigot.voidwhitelist.command.arg.Args;

import java.util.List;

import static voidpointer.spigot.voidwhitelist.message.WhitelistMessage.*;

public final class WhitelistCommand extends Command {
    public static final String NAME = "whitelist";
    public static final int MIN_REQUIRED_ARGS = 1;

    @AutowiredLocale private static Locale locale;
    private final CommandManager whitelistCommands = new CommandManager();

    public WhitelistCommand() {
        super(NAME);

        super.setPermission("voidwhitelist." + NAME);
        super.setRequiredArgsNumber(MIN_REQUIRED_ARGS);

        whitelistCommands.addCommand(new AddCommand());
        whitelistCommands.addCommand(new RemoveCommand());
        whitelistCommands.addCommand(new EnableCommand());
        whitelistCommands.addCommand(new DisableCommand());
        whitelistCommands.addCommand(new InfoCommand());
        whitelistCommands.addCommand(new StatusCommand());
        whitelistCommands.addCommand(new GuiCommand());
        whitelistCommands.addCommand(new ImportJsonCommand());
        whitelistCommands.addCommand(new ExportCommand());
        whitelistCommands.addCommand(new HelpCommand());
        whitelistCommands.addCommand(new ReloadCommand());
        whitelistCommands.addCommand(new ReconnectCommand());

        whitelistCommands.getCommands().values()
                .forEach(cmd -> cmd.setPermission(getPermission() + "." + cmd.getName()));
    }

    @Override public void execute(final Args args) {
        final String subCommandName = args.removeFirst();

        if (!whitelistCommands.executeCommand(subCommandName, args))
            locale.localize(WHITELIST_UNKNOWN_COMMAND).send(args.getSender());
    }

    @Override public List<String> tabComplete(final Args args) {
        return whitelistCommands.tabComplete(args);
    }

    @Override protected void onNotEnoughArgs(final CommandSender sender, final Args args) {
        locale.localize(WHITELIST_NOT_ENOUGH_ARGS).send(sender);
    }
}
