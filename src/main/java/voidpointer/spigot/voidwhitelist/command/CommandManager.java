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

import lombok.AccessLevel;
import lombok.Getter;
import voidpointer.spigot.framework.localemodule.LocaleLog;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.command.arg.Args;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

public final class CommandManager {
    @AutowiredLocale private static LocaleLog log;

    @Getter(AccessLevel.PUBLIC)
    private final Map<String, Command> commands = new HashMap<>();

    public void addCommand(final Command command) {
        final String commandName = command.getName();
        if (commands.containsKey(commandName)) {
            log.severe("Cannot add \"{0}\" command, duplicate found.", commandName);
            return;
        }

        this.commands.put(commandName, command);
        for (final String alias : command.getAliases()) {
            if (commands.containsKey(alias)) {
                log.warn("Cannot add \"{0}\" alias, duplicate found.", commandName);
                continue;
            }
            commands.put(alias, command);
        }
    }

    /**
     * @return {@code true} if the command is executed successfully,
     *          or {@code false} if command not found.
     */
    public boolean executeCommand(final String commandName, final Args args) {
        if (!commands.containsKey(commandName))
            return false;

        final Command command = commands.get(commandName);
        args.parseOptions(command.getOptions());
        if (command.isValidForExecution(args))
            command.execute(args);
        return true;
    }

    public List<String> tabComplete(final Args args) throws IllegalArgumentException {
        if (args.isEmpty() || args.get(0).isEmpty())
            return new ArrayList<>(commands.keySet());

        if (1 == args.sizeWithOptions()) {
            final String supposedCommand = args.get(0);
            return commands.keySet().stream()
                    .filter(command -> command.startsWith(supposedCommand))
                    .collect(Collectors.toList());
        }

        final String commandName = args.removeFirst();
        if (!commands.containsKey(commandName))
            return emptyList();
        return commands.get(commandName).tabComplete(args);
    }
}
