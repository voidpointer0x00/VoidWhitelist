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

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import voidpointer.spigot.framework.localemodule.LocaleLog;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.command.arg.Args;
import voidpointer.spigot.voidwhitelist.command.arg.DefinedOption;
import voidpointer.spigot.voidwhitelist.message.WhitelistMessage;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static java.util.Collections.emptyList;
import static lombok.AccessLevel.PROTECTED;

@Getter
@RequiredArgsConstructor
public abstract class Command implements CommandExecutor, TabCompleter {
    public static final String EMPTY_PERMISSION = "";
    public static final Integer DEFAULT_REQUIRED_ARGS_NUMBER = 0;

    private static final List<String> EMPTY_ALIASES = emptyList();
    @AutowiredLocale private static LocaleLog localeLog;

    @Setter(PROTECTED)
    private @NonNull String permission = EMPTY_PERMISSION;

    private final String name;

    @Setter(PROTECTED)
    private int requiredArgsNumber = DEFAULT_REQUIRED_ARGS_NUMBER;

    private final Set<DefinedOption> options = new HashSet<>();

    @Override public final boolean onCommand(
            final @NonNull CommandSender sender,
            final org.bukkit.command.@NonNull Command command,
            final @NonNull String label,
            final String[] rawArgs) {
        Args args = new Args(sender, rawArgs);

        if (!isValidForExecution(args))
            return true;

        execute(args);

        return true;
    }

    public abstract void execute(final Args args);

    @Override public List<String> onTabComplete(
            final @NonNull CommandSender sender,
            final org.bukkit.command.@NonNull Command command,
            final @NonNull String alias,
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
        PluginCommand command = plugin.getCommand(name);
        if (command == null) {
            localeLog.warn("Plugin does not define a required \"{0}\" command.", name);
            return;
        }
        command.setExecutor(this);
        command.setTabCompleter(this);
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

    protected void onNoPermission(final CommandSender sender) {
        localeLog.localize(WhitelistMessage.NO_PERMISSION).send(sender);
    }

    protected final void addOptions(DefinedOption[] options) {
        Collections.addAll(this.options, options);
    }

    protected final List<String> completeOptionOrElse(final String optionStart,
                                                      final Function<String, List<String>> noPrefixAction) {
        if (!optionStart.startsWith("-"))
            return noPrefixAction.apply(optionStart);
        return completeOption0(optionStart);
    }

    /** @throws IllegalArgumentException if the given optionStart argument is not an option
     * (does not have a dash prefix) */
    protected final List<String> completeOption(final String optionStart) throws IllegalArgumentException {
        if (!optionStart.startsWith("-"))
            throw new IllegalArgumentException("Option must start with \"-\" prefix");
        return completeOption0(optionStart);
    }

    private List<String> completeOption0(final String optionStart) {
        if (options.isEmpty())
            return emptyList();
        final StringBuilder optionBuilder = new StringBuilder(optionStart);
        final StringBuilder prefix = new StringBuilder(2);
        for (int index = 0; (index < 2) && (index < optionStart.length()) && (optionStart.charAt(index) == '-');) {
            optionBuilder.deleteCharAt(0);
            prefix.append('-');
            index++;
        }
        List<String> completed = new LinkedList<>();
        final String optionWithoutPrefix = optionBuilder.toString();
        for (final DefinedOption availableOption : options)
            if (availableOption.getName().startsWith(optionWithoutPrefix))
                completed.add(prefix + availableOption.getName());
        return completed;
    }
}
