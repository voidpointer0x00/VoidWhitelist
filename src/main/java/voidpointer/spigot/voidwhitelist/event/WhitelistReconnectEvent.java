package voidpointer.spigot.voidwhitelist.event;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.checkerframework.checker.nullness.qual.NonNull;
import voidpointer.spigot.voidwhitelist.command.ReconnectCommand;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService.ConnectionResult;

/**
 * <p>Called asynchronously after reconnecting to a database
 *      using new credentials or reloading file based storage.</p>
 *
 * @see ReconnectCommand
 */
public final class WhitelistReconnectEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Getter private final ConnectionResult result;

    /** The {@link CommandSender} that ran the reconnect operation. */
    @Getter private final CommandSender sender;

    public WhitelistReconnectEvent(final @NonNull ConnectionResult result, final @NonNull CommandSender sender) {
        super(true);
        this.result = result;
        this.sender = sender;
    }

    @Override public @NonNull HandlerList getHandlers() {
        return handlers;
    }
}
