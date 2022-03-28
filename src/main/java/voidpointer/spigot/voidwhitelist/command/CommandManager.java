package voidpointer.spigot.voidwhitelist.command;

import voidpointer.spigot.framework.localemodule.LocaleLog;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class CommandManager {
    @AutowiredLocale private static LocaleLog log;
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

    public void executeCommand(final String commandName, final Args args) throws IllegalArgumentException {
        if (!commands.containsKey(commandName))
            throw new IllegalArgumentException("CommandManager does not contain \"" + commandName + "\" command.");

        final Command command = commands.get(commandName);
        if (command.isValidForExecution(args))
            command.execute(args);
    }

    public List<String> tabComplete(final Args args) throws IllegalArgumentException {
        if (args.isEmpty())
            return new ArrayList<>(commands.keySet());

        if (1 == args.size()) {
            final String supposedCommand = args.get(0);
            return commands.keySet().stream()
                    .filter(command -> command.startsWith(supposedCommand))
                    .collect(Collectors.toList());
        }

        final String commandName = args.get(0);
        if (!commands.containsKey(commandName))
            return null;
        args.getArgs().removeFirst();
        return commands.get(commandName).tabComplete(args);
    }
}
