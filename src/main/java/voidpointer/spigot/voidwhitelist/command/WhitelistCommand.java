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
package voidpointer.spigot.voidwhitelist.command;

import org.bukkit.command.CommandSender;
import voidpointer.spigot.framework.localemodule.Locale;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.message.WhitelistMessage;

import java.util.List;

public final class WhitelistCommand extends Command {
    public static final String NAME = "whitelist";
    public static final int MIN_REQUIRED_ARGS = 1;

    @AutowiredLocale private static Locale locale;
    private final CommandManager whitelistCommands = new CommandManager();

    public WhitelistCommand() {
        super(NAME);

        whitelistCommands.addCommand(new AddCommand());
        whitelistCommands.addCommand(new RemoveCommand());
        whitelistCommands.addCommand(new EnableCommand());
        whitelistCommands.addCommand(new DisableCommand());
        whitelistCommands.addCommand(new InfoCommand());
        super.setRequiredArgsNumber(MIN_REQUIRED_ARGS);
    }

    @Override public void execute(final Args args) {
        final String subCommandName = args.get(0);

        args.getArgs().removeFirst();
        try {
            whitelistCommands.executeCommand(subCommandName, args);
        } catch (IllegalArgumentException illegalArgumentException) {
            locale.localize(WhitelistMessage.WHITELIST_HELP).send(args.getSender());
        }
    }

    @Override public List<String> tabComplete(final Args args) {
        return whitelistCommands.tabComplete(args);
    }

    @Override protected void onNotEnoughArgs(final CommandSender sender, final Args args) {
        locale.localize(WhitelistMessage.WHITELIST_HELP).send(sender);
    }

    @Override protected void onNoPermission(final CommandSender sender) {
        locale.localize(WhitelistMessage.NO_PERMISSION).send(sender);
    }
}
