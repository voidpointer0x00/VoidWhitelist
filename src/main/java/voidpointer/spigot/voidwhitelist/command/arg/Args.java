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
import java.util.Set;

import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableSet;

public final class Args {
    @Getter private final CommandSender sender;
    private final LinkedList<Arg> args = new LinkedList<>();
    private final LinkedList<Arg> undefinedOptions = new LinkedList<>();
    // TODO: ArgOption -> DefinedOption, options -> definedOptions
    @Getter private Set<ArgOption> options = emptySet();

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

    public void parseOptions(final Collection<ArgOption> argOptions) {
        if (argOptions.isEmpty())
            return;
        HashSet<ArgOption> options = new HashSet<>(this.options);
        Iterator<Arg> optionsIterator = undefinedOptions.iterator();
        while (optionsIterator.hasNext()) {
            Arg rawOption = optionsIterator.next();
            for (ArgOption option : argOptions) {
                if (!option.matches(rawOption.value))
                    continue;
                options.add(option);
                args.remove(rawOption);
                optionsIterator.remove();
            }
        }
        this.options = unmodifiableSet(options);
    }

    public boolean hasOption(final ArgOption option) {
        return options.contains(option);
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

    public boolean isEmpty() {
        return args.isEmpty();
    }

    public int size() {
        return args.size();
    }
}
