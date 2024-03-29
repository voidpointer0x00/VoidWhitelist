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
package voidpointer.spigot.voidwhitelist.command.arg;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Set;

import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableSet;

public final class Args {
    @Getter private final CommandSender sender;
    private final LinkedList<Arg> args = new LinkedList<>();
    private final LinkedList<Arg> undefinedOptions = new LinkedList<>();
    @Getter private Set<DefinedOption> definedOptions = emptySet();

    public Args(final @NonNull CommandSender sender, final @NonNull String[] rawArgs) {
        this.sender = sender;
        for (int index = 0; index < rawArgs.length; index++)
            new Arg(index, rawArgs[index]).ifOptionOrElse(undefinedOptions::add, args::add);
    }

    public Player getPlayer() {
        if (sender instanceof Player)
            return (Player) sender;
        else
            throw new ClassCastException("CommandSender isn't a Player instance.");
    }

    public void parseOptions(final Collection<DefinedOption> definedOptions) {
        if (definedOptions.isEmpty())
            return;
        HashSet<DefinedOption> options = new HashSet<>(this.definedOptions);
        Iterator<Arg> optionsIterator = undefinedOptions.iterator();
        while (optionsIterator.hasNext()) {
            Arg rawOption = optionsIterator.next();
            for (DefinedOption option : definedOptions) {
                if (!option.matches(rawOption.value))
                    continue;
                options.add(option);
                args.remove(rawOption);
                optionsIterator.remove();
            }
        }
        this.definedOptions = unmodifiableSet(options);
    }

    public boolean hasOption(final DefinedOption option) {
        return definedOptions.contains(option);
    }

    public boolean isPlayer() {
        return sender instanceof Player;
    }

    public String get(int index) {
        return args.get(index).value;
    }

    public String removeFirst() {
        return args.removeFirst().value;
    }

    public String getLast() {
        return args.getLast().value;
    }

    public Optional<Arg> getLastArg() {
        if (args.isEmpty()) {
            if (undefinedOptions.isEmpty())
                return Optional.empty();
            return Optional.of(undefinedOptions.getLast());
        } else if (undefinedOptions.isEmpty()) {
            return Optional.of(args.getLast());
        }
        return Optional.of(args.getLast().max(undefinedOptions.getLast()));
    }

    public boolean isEmpty() {
        return args.isEmpty();
    }

    public int size() {
        return args.size();
    }

    public int sizeWithOptions() {
        return args.size() + undefinedOptions.size();
    }
}
