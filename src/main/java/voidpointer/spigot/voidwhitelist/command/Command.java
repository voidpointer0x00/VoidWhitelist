package voidpointer.spigot.voidwhitelist.command;

import lombok.*;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.List;

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
        plugin.getCommand(name).setExecutor(this);
        plugin.getCommand(name).setTabCompleter(this);
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

    protected abstract void onNoPermission(final CommandSender sender);
}
