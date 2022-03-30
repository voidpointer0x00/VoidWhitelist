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
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public abstract class Command implements CommandExecutor, TabCompleter {
    public static final String EMPTY_PERMISSION = "";
    public static final Integer DEFAULT_REQUIRED_ARGS_NUMBER = 0;

    private static final List<String> EMPTY_ALIASES = Collections.emptyList();

    @Getter
    @Setter(AccessLevel.PROTECTED)
    @NonNull
    private String permission = EMPTY_PERMISSION;

    @Getter
    @NonNull
    private final String name;

    @Getter
    @Setter(AccessLevel.PROTECTED)
    private int requiredArgsNumber = DEFAULT_REQUIRED_ARGS_NUMBER;

    @Getter
    private final Set<ArgOption> options = new HashSet<>();

    @Override public final boolean onCommand(
            final CommandSender sender,
            final org.bukkit.command.Command command,
            final String label,
            final String[] rawArgs) {
        Args args = new Args(sender, rawArgs);

        if (!isValidForExecution(args))
            return true;

        execute(args);

        return true;
    }

    public abstract void execute(final Args args);

    @Override public List<String> onTabComplete(
            final CommandSender sender,
            final org.bukkit.command.Command command,
            final String alias,
            final String[] rawArgs) {
        Args args = new Args(sender, rawArgs);

        if (!isValidForExecution(args))
            return null;

        return tabComplete(args);
    }

    public List<String> getAliases() {
        return EMPTY_ALIASES;
    }

    public List<String> tabComplete(final Args args) {
        return null;
    }

    public final void register(final JavaPlugin plugin) {
        try {
            plugin.getCommand(name).setExecutor(this);
            plugin.getCommand(name).setTabCompleter(this);
        } catch (NullPointerException npe) {
            throw new UnsupportedOperationException("Plugin doesn't define command: " + name);
        }
    }

    protected final boolean isValidForExecution(final Args args) {
        if (!(permission.equals(EMPTY_PERMISSION) || args.getSender().hasPermission(permission))) {
            onNoPermission(args.getSender());
            return false;
        } else if (args.size() < requiredArgsNumber) {
            onNotEnoughArgs(args.getSender(), args);
            return false;
        } else {
            return true;
        }
    }

    protected void onNotEnoughArgs(final CommandSender sender, final Args args) {
        if (requiredArgsNumber > DEFAULT_REQUIRED_ARGS_NUMBER)
            throw new NotImplementedException("Subtype must implement onNotEnoughArgs() if args are required");
    }

    // TODO: use locale DI
    protected abstract void onNoPermission(final CommandSender sender);

    protected final void addOptions(ArgOption[] options) {
        for (ArgOption option : options)
            this.options.add(option);
    }
}
