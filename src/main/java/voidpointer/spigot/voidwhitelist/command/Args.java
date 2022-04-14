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
import lombok.NonNull;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

public final class Args {
    @Getter private final CommandSender sender;
    @Getter private final LinkedList<String> args;
    @Getter private final Set<ArgOption> options = Collections.synchronizedSet(new HashSet<>());

    public Args(final @NonNull CommandSender sender, final @NonNull String[] args) {
        this.sender = sender;
        this.args = new LinkedList<>(Arrays.asList(args));
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
        Iterator<String> argsIterator = args.iterator();
        String arg;
        while (argsIterator.hasNext()) {
            arg = argsIterator.next();
            for (ArgOption option : argOptions) {
                if (!option.matches(arg))
                    continue;
                options.add(option);
                argsIterator.remove();
            }
        }
    }

    public boolean hasOption(final ArgOption option) {
        return options.contains(option);
    }

    public boolean isPlayer() {
        return sender instanceof Player;
    }

    public String get(int index) {
        return args.get(index);
    }

    public String getLast() {
        return args.getLast();
    }

    public boolean isEmpty() {
        return args.isEmpty();
    }

    public int size() {
        return args.size();
    }
}
