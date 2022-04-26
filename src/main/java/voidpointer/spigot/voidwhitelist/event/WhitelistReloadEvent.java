package voidpointer.spigot.voidwhitelist.event;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.checkerframework.checker.nullness.qual.NonNull;
import voidpointer.spigot.voidwhitelist.command.ReloadCommand;

/**
 * <p>Called asynchronously when reloading plugin.</p>
 *
 * @see ReloadCommand
 */
public final class WhitelistReloadEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /** The {@link CommandSender} that ran the reload operation. */
    @Getter private final @NonNull CommandSender sender;

    public WhitelistReloadEvent(final CommandSender sender) {
        super(true);
        this.sender = sender;
    }

    @Override public @NonNull HandlerList getHandlers() {
        return handlers;
    }
}
