package voidpointer.spigot.voidwhitelist.command;

import org.bukkit.Bukkit;

import java.util.*;
import java.util.stream.Collectors;

public final class CommandManager {
    private final Map<String, Command> commands = new HashMap<>();

    public Set<String> getCommandsNames() {
        return commands.keySet();
    }

    public void addCommand(final Command command) {
        final String commandName = command.getName();
        if (commands.containsKey(commandName)) {
            Bukkit.getLogger().severe("Cannot add \""+commandName+"\" command, duplicate found.");
            Bukkit.getLogger().severe("Command \""+commandName+"\" not added.");
            return;
        }

        this.commands.put(commandName, command);
        for (final String alias : command.getAliases()) {
            if (commands.containsKey(alias)) {
                Bukkit.getLogger().warning("Cannot add \""+alias+"\" alias, duplicate found.");
                continue;
            }
            commands.put(alias, command);
        }
    }

    public boolean hasCommand(final String commandName) {
        return commands.containsKey(commandName);
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
