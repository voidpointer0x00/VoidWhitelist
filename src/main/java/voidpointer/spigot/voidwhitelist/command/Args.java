package voidpointer.spigot.voidwhitelist.command;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public final class Args {
    @Getter
    private final CommandSender sender;
    @Getter
    private final List<String> args;

    public Args(final CommandSender sender, final String[] args) {
        this.sender = sender;
        this.args = Arrays.asList(args);
    }

    public Player getPlayer() {
        if (sender instanceof Player) {
            return (Player) sender;
        } else {
            throw new ClassCastException("CommandSender isn't a Player instance.");
        }
    }

    public boolean isPlayer() {
        return sender instanceof Player;
    }

    public String get(int index) {
        return args.get(index);
    }

    public boolean isEmpty() {
        return args.isEmpty();
    }

    public int size() {
        return args.size();
    }
}
